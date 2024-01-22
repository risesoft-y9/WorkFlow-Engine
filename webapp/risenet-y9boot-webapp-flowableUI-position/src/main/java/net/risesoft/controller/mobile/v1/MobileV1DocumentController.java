package net.risesoft.controller.mobile.v1;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.permission.PositionRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Tenant;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.DocumentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 新建，发送，详情，关联文件接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequestMapping("/mobile/v1/document")
@Slf4j
public class MobileV1DocumentController {

    protected final Logger log = LoggerFactory.getLogger(MobileV1DocumentController.class);

    @Autowired
    private PersonApi personApi;

    @Autowired
    private PositionRoleApi positionRoleApi;

    @Autowired
    private TenantApi tenantApi;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private Document4PositionApi document4PositionApi;

    @Autowired
    private ItemOpinionFrameBindApi itemOpinionFrameBindApi;

    @Autowired
    private TaskApi taskApi;

    @Autowired
    private AssociatedFile4PositionApi associatedFile4PositionApi;

    @Autowired
    private Draft4PositionApi draft4PositionApi;

    @Autowired
    private ItemRole4PositionApi itemRole4PositionApi;

    @Autowired
    private FormDataApi formDataApi;

    @Autowired
    private ProcessParamApi processParamApi;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private TodoTaskApi todotaskApi;

    @Autowired
    private HistoricProcessApi historicProcessApi;

    @Autowired
    private DepartmentApi departmentApi;

    /**
     * 新建公文
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param request
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/add")
    public Y9Result<Map<String, Object>> add(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = document4PositionApi.add(Y9LoginUserHolder.getTenantId(), positionId, itemId, true);
            // 意见框？？？？？？？？？？
            String formIds = (String)map.get("formId");
            String taskDefKey = (String)map.get("taskDefKey");
            String processDefinitionId = (String)map.get("processDefinitionId");
            String formId[] = formIds.split(SysVariables.COMMA);
            List<Map<String, Object>> fieldDefineList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> opinionFrameList = new ArrayList<Map<String, Object>>();
            List<ItemOpinionFrameBindModel> bindList = itemOpinionFrameBindApi.findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(tenantId, positionId, itemId, processDefinitionId, taskDefKey);
            for (ItemOpinionFrameBindModel bind : bindList) {
                Map<String, Object> opinionFrameMap = new HashMap<String, Object>(16);
                opinionFrameMap.put("hasRole", false);
                opinionFrameMap.put("opinionFrameMark", bind.getOpinionFrameMark());
                opinionFrameMap.put("opinionFrameName", bind.getOpinionFrameName());
                List<String> roleIds = bind.getRoleIds();
                for (String roleId : roleIds) {
                    Boolean hasRole = positionRoleApi.hasRole(tenantId, roleId, positionId).getData();
                    if (hasRole) {
                        opinionFrameMap.put("hasRole", hasRole);
                        break;
                    }
                }
                opinionFrameList.add(opinionFrameMap);
            }
            for (int i = 0; i < formId.length; i++) {// 获取表单定义字段
                Map<String, Object> fieldDefineMap = new HashMap<String, Object>(16);
                List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
                listMap = formDataApi.getFormFieldDefine(tenantId, formId[i]);
                fieldDefineMap.put(formId[i], listMap);
                fieldDefineList.add(fieldDefineMap);
            }
            map.put("opinionFrame", opinionFrameList);
            map.put("fieldDefine", fieldDefineList);
            return Y9Result.success(map, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 删除的流程实例id
     * @param response
     */
    @RequestMapping("/delAssociatedFile")
    @ResponseBody
    public Y9Result<String> delAssociatedFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String processSerialNumber, String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = associatedFile4PositionApi.deleteAssociatedFile(tenantId, processSerialNumber, processInstanceId);
        if (b) {
            return Y9Result.successMsg("删除成功");
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 办件，草稿获取公文信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，待办：todo,在办：doing,办结：done
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/documentDetail")
    public Y9Result<Map<String, Object>> documentDetail(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processSerialNumber, @RequestParam String taskId,
        @RequestParam String itembox, @RequestParam String itemId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            if (StringUtils.isNotBlank(processInstanceId)) {// 打开办件
                map = document4PositionApi.edit(tenantId, positionId, itembox, taskId, processInstanceId, itemId, true);
            } else {// 打开草稿
                map = draft4PositionApi.openDraft4Position(tenantId, positionId, itemId, processSerialNumber, true);
            }
            String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
            String processDefinitionId = (String)map.get("processDefinitionId");
            String taskDefKey = (String)map.get("taskDefKey");
            String formIds = (String)map.get("formId");
            String formNames = (String)map.get("formName");
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            return Y9Result.success(map, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

    /**
     * 待办获取公文信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param itembox 办件状态，待办：todo,在办：doing,办结：done
     * @param response
     * @param model
     */
    @RequestMapping(value = "/documentDetailByTaskId")
    public Y9Result<Map<String, Object>> documentDetailByTaskId(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String taskId, @RequestParam String itembox, HttpServletResponse response,
        Model model) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "获取失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.getPerson(tenantId, userId).getData());
            if (StringUtils.isNotBlank(taskId)) {// 打开办件
                TaskModel taskModel = taskApi.findById(tenantId, taskId);
                if (taskModel != null && taskModel.getId() != null) {
                    ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId());
                    String itemId = processParamModel.getItemId();
                    map = document4PositionApi.edit(tenantId, positionId, itembox, taskId, taskModel.getProcessInstanceId(), itemId, true);
                    String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
                    String processDefinitionId = (String)map.get("processDefinitionId");
                    String taskDefKey = (String)map.get("taskDefKey");
                    String formIds = (String)map.get("formId");
                    String formNames = (String)map.get("formName");
                    String processSerialNumber = processParamModel.getProcessSerialNumber();
                    DocumentUtil documentUtil = new DocumentUtil();
                    Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, taskModel.getProcessInstanceId(), taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
                    map.putAll(dataMap);
                } else {
                    todotaskApi.deleteTodoTaskByTaskId(tenantId, taskId);
                }
            }
            return Y9Result.success(map, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

    /**
     * 获取发送人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @param id 父节点id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping("/findPermUser")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findPermUser(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam String processInstanceId, @RequestParam(required = false) String id, @RequestParam String itemId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(processDefinitionId) || StringUtils.isBlank(itemId)) {
            return Y9Result.success(item, "获取数据异常");
        } else {
            item = itemRole4PositionApi.findPermUser(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
            return Y9Result.success(item, "获取数据成功");
        }
    }

    /**
     * 发送选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param principalType 选人类型
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @param name 搜索内容
     * @param response
     */
    @RequestMapping("/findPermUserByName")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> findPermUserByName(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam Integer principalType, @RequestParam String processInstanceId, @RequestParam(required = false) String name, @RequestParam String itemId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(processDefinitionId) || StringUtils.isBlank(itemId)) {
            return Y9Result.success(item, "获取数据异常");
        } else {
            item = itemRole4PositionApi.findPermUserByName(Y9LoginUserHolder.getTenantId(), userId, positionId, name, principalType, itemId, processDefinitionId, taskDefKey, processInstanceId);
            return Y9Result.success(item, "获取数据成功");
        }
    }

    /**
     * 发送，同时保存表单数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param temp_Ids 表单ids
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param userChoice 选择人员
     * @param sponsorGuid 主办人id，并行区分主协办设值，其他为""
     * @param sponsorHandle 是否主办办理
     * @param routeToTaskId 任务路由key
     * @param processInstanceId 流程实例id
     * @param formJsonData 表单数据json字符串
     * @param response
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/forwarding")
    public Y9Result<Map<String, Object>> forwarding(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String temp_Ids,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processDefinitionKey, @RequestParam(required = false) String userChoice, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String routeToTaskId, @RequestParam(required = false) String processInstanceId, @RequestParam String formJsonData, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送成功");
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = (String)mapFormJsonData.get("title");
            String number = (String)mapFormJsonData.get("number");
            String level = (String)mapFormJsonData.get("level");
            if (StringUtils.isBlank(level)) {
                level = (String)mapFormJsonData.get("workLevel");
            }
            if (StringUtils.isBlank(taskId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey, number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            if (StringUtils.isNotBlank(temp_Ids)) {
                List<String> TempIdList = Y9Util.stringToList(temp_Ids, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : TempIdList) {
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            Map<String, Object> variables = new HashMap<String, Object>(16);
            if (processDefinitionKey.equals(Y9Context.getProperty("y9.app.workOrder.processDefinitionKey"))) {
                String startRouteToTaskId = Y9Context.getProperty("y9.app.workOrder.inNetrouteToTaskId");
                map = document4PositionApi.saveAndForwardingByTaskKey(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
            } else {
                map = document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            }
            if ((boolean)map.get("success")) {
                return Y9Result.success(map, (String)map.get("msg"));
            }
            return Y9Result.failure((String)map.get("msg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");
    }

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param formId 表单Id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @param response
     */
    @RequestMapping("/getAllFieldPerm")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getAllFieldPerm(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String formId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> list = formDataApi.getAllFieldPerm(tenantId, userId, formId, taskDefKey, processDefinitionId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取关联文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping("/getAssociatedFileList")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getAssociatedFileList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processSerialNumber,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        map = associatedFile4PositionApi.getAssociatedFileList(tenantId, processSerialNumber);
        if ((boolean)map.get("success")) {
            return Y9Result.success((List<Map<String, Object>>)map.get("rows"), "获取成功");
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取办件状态
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/getByTaskId")
    public Y9Result<Map<String, Object>> getByTaskId(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String taskId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (StringUtils.isNotBlank(taskId)) {
                TaskModel taskModel = taskApi.findById(tenantId, taskId);
                ProcessParamModel processParam = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                if (taskModel != null && taskModel.getId() != null) {
                    map.put("itembox", ItemBoxTypeEnum.TODO.getValue());
                    map.put("processSerialNumber", processParam.getProcessSerialNumber());
                    map.put("itemId", processParam.getItemId());
                } else {
                    HistoricProcessInstanceModel hpi = historicProcessApi.getById(tenantId, processInstanceId);
                    if (hpi == null) {
                        map.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    } else {
                        taskId = "";
                        map.put("itembox", new HashMap<String, String>(16));
                        List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                        for (TaskModel task : taskList) {
                            taskId = Y9Util.genCustomStr(taskId, task.getId(), SysVariables.COMMA);
                        }
                        map.put("taskId", taskId);
                    }
                    if (processParam == null) {
                        map.put("itembox", "delete");
                    } else {
                        map.put("processSerialNumber", processParam.getProcessSerialNumber());
                        map.put("itemId", processParam.getItemId());
                    }
                }
            }
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取表单初始化的数据
     *
     * @param tenantId
     * @param userId
     * @param positionId
     * @param response
     */
    @RequestMapping(value = "/getFormInitData")
    public Y9Result<Map<String, Object>> getFormInitData(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "获取表单初始化数据失败");
        map.put("success", false);
        try {
            Person person = personApi.getPerson(tenantId, userId).getData();
            Position position = positionApi.getPosition(tenantId, positionId).getData();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sdf.format(date);
            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
            String year = yearsdf.format(date);
            String second = sesdf.format(date);
            String itemNumber = "〔" + year + "〕" + second + "号";
            OrgUnit parent = positionApi.getParent(tenantId, positionId).getData();
            Tenant tenant = tenantApi.getById(tenantId).getData();
            /** 办件表单数据初始化 **/
            map.put("deptName", parent.getName());// 创建部门
            map.put("userName", person.getName());// 创建人
            map.put("positionName", position.getName());// 创建岗位
            map.put("duty", position.getName().split("（")[0]);// 职务
            map.put("createDate", nowDate);// 创建日期
            map.put("mobile", person.getMobile());// 联系电话
            map.put("number", itemNumber);// 编号
            map.put("tenantName", tenant.getName());// 租户名称
            map.put("tenantId", tenant.getId());// 租户名称
            map.put("number", itemNumber);// 编号
            map.put("sign", "");// 签名
            PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, userId).getData();
            if (personExt != null && personExt.getSign() != null) {
                map.put("sign", personExt.getSign());// 签名
            }
            List<OrgUnit> leaders = departmentApi.listLeaders(tenantId, parent.getId()).getData();
            map.put("deptLeader", "未配置");// 岗位所在部门领导
            if (!leaders.isEmpty()) {
                List<Person> personLeaders = positionApi.listPersons(tenantId, leaders.get(0).getId()).getData();
                map.put("deptLeader", personLeaders.isEmpty() ? leaders.get(0).getName() : personLeaders.get(0).getName());
            }
            /** 办件表单数据初始化 **/
            map.put("zihao", second + "号");// 编号
            return Y9Result.success(map, "获取表单初始化数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取发送选人类型
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processDefinitionKey 流程定义key
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskDefKey 任务key
     * @param itemId 事项id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @ResponseBody
    @RequestMapping("/getTabMap")
    public Y9Result<Map<String, Object>> getTabMap(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionKey, @RequestParam String processDefinitionId,
        @RequestParam String taskId, @RequestParam String taskDefKey, @RequestParam String itemId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            map = document4PositionApi.docUserChoise(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId, processDefinitionKey, processDefinitionId, taskId, taskDefKey, processInstanceId);
            return Y9Result.success(map, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 保存关联文件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程实例编号
     * @param processInstanceIds 关联的流程实例ids
     * @param response
     */
    @RequestMapping("/saveAssociatedFile")
    @ResponseBody
    public Y9Result<String> saveAssociatedFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String processInstanceIds, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        boolean b = associatedFile4PositionApi.saveAssociatedFile(tenantId, positionId, processSerialNumber, processInstanceIds);
        if (b) {
            return Y9Result.success("保存成功");
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param temp_Ids 表单ids
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param processInstanceId 流程实例id
     * @param formJsonData 表单数据json字符串
     * @param response
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/saveFormData")
    public Y9Result<String> saveFormData(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String temp_Ids, @RequestParam String processInstanceId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey, @RequestParam String itemId, @RequestParam String formJsonData, HttpServletRequest request, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = (String)mapFormJsonData.get("title");
            String number = (String)mapFormJsonData.get("number");
            String level = (String)mapFormJsonData.get("level");
            if (StringUtils.isBlank(level)) {
                level = (String)mapFormJsonData.get("workLevel");
            }
            if (StringUtils.isBlank(processInstanceId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey, number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            if (StringUtils.isNotBlank(temp_Ids)) {
                List<String> TempIdList = Y9Util.stringToList(temp_Ids, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : TempIdList) {
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            return Y9Result.success("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 发送，同时保存表单数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param temp_Ids 表单ids
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param formJsonData 表单数据json字符串
     * @param response
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/submitTo")
    public Y9Result<String> submitTo(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = true) String itemId, @RequestParam(required = true) String temp_Ids,
        @RequestParam(required = true) String taskId, @RequestParam(required = true) String processSerialNumber, @RequestParam String formJsonData, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = (String)mapFormJsonData.get("title");
            String number = (String)mapFormJsonData.get("number");
            String level = (String)mapFormJsonData.get("level");
            if (StringUtils.isBlank(level)) {
                level = (String)mapFormJsonData.get("workLevel");
            }
            String processInstanceId = "";
            if (StringUtils.isBlank(taskId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, "", number, level, title);
            } else {
                TaskModel task = taskApi.findById(tenantId, taskId);
                if (null != task) {
                    processInstanceId = task.getProcessInstanceId();
                }
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                return Y9Result.failure("发生异常");
            }
            if (StringUtils.isNotBlank(temp_Ids)) {
                List<String> TempIdList = Y9Util.stringToList(temp_Ids, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : TempIdList) {
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            map = document4PositionApi.saveAndSubmitTo(tenantId, positionId, taskId, itemId, processSerialNumber);
            if ((boolean)map.get("success")) {
                return Y9Result.success((String)map.get("msg"));
            }
            return Y9Result.failure((String)map.get("msg"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发送失败");
    }
}