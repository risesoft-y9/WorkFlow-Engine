package net.risesoft.controller.mobile;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.risesoft.api.org.PositionApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Tenant;
import net.risesoft.model.user.UserInfo;
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
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.PositionRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
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
 * @author 10858
 *
 */
@RestController
@RequestMapping("/mobile/document")
@Slf4j
public class MobileDocumentController {

    protected final Logger log = LoggerFactory.getLogger(MobileDocumentController.class);

    @Autowired
    private PersonApi personManager;

    @Autowired
    private PositionRoleApi positionRoleApi;

    @Autowired
    private TenantApi tenantManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private Document4PositionApi documentManager;

    @Autowired
    private ItemOpinionFrameBindApi itemOpinionFrameBindManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private AssociatedFile4PositionApi associatedFileManager;

    @Autowired
    private Draft4PositionApi draftManager;

    @Autowired
    private ItemRole4PositionApi itemRoleManager;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private TodoTaskApi todoTaskManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

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
    public void add(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = documentManager.add(Y9LoginUserHolder.getTenantId(), positionId, itemId, true);
            // 意见框？？？？？？？？？？
            String formIds = (String)map.get("formId");
            String taskDefKey = (String)map.get("taskDefKey");
            String processDefinitionId = (String)map.get("processDefinitionId");
            String formId[] = formIds.split(SysVariables.COMMA);
            List<Map<String, Object>> fieldDefineList = new ArrayList<Map<String, Object>>();
            List<Map<String, Object>> opinionFrameList = new ArrayList<Map<String, Object>>();
            List<ItemOpinionFrameBindModel> bindList = itemOpinionFrameBindManager.findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(tenantId, positionId, itemId, processDefinitionId, taskDefKey);
            for (ItemOpinionFrameBindModel bind : bindList) {
                Map<String, Object> opinionFrameMap = new HashMap<String, Object>(16);
                opinionFrameMap.put("hasRole", false);
                opinionFrameMap.put("opinionFrameMark", bind.getOpinionFrameMark());
                opinionFrameMap.put("opinionFrameName", bind.getOpinionFrameName());
                List<String> roleIds = bind.getRoleIds();
                for (String roleId : roleIds) {
                    Boolean hasRole = positionRoleApi.hasRole(tenantId, roleId, positionId);
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
                listMap = formDataManager.getFormFieldDefine(tenantId, formId[i]);
                fieldDefineMap.put(formId[i], listMap);
                fieldDefineList.add(fieldDefineMap);
            }
            map.put("opinionFrame", opinionFrameList);
            map.put("fieldDefine", fieldDefineList);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            e.printStackTrace();
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取表单初始化的数据
     * @param tenantId
     * @param userId
     * @param positionId
     * @param response
     */
    @RequestMapping(value = "/getFormInitData")
    public void getFormInitData(@RequestHeader("auth-tenantId") String tenantId,
                                @RequestHeader("auth-userId") String userId,
                                @RequestHeader("auth-positionId") String positionId,
                                HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("msg", "获取表单初始化数据失败");
        map.put("success", false);
        try{
            Person person = personManager.getPerson(tenantId,userId);
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sdf.format(date);
            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
            String year = yearsdf.format(date);
            String second = sesdf.format(date);
            String itemNumber = "〔" + year + "〕" + second + "号";
            OrgUnit parent = positionApi.getParent(tenantId, positionId);
            Tenant tenant = tenantManager.getById(tenantId);
            /** 办件表单数据初始化 **/
            map.put("deptName", parent.getName());// 创建部门
            map.put("userName", person.getName());// 创建人
            map.put("createDate", nowDate);// 创建日期
            map.put("mobile", person.getMobile());// 联系电话
            map.put("number", itemNumber);// 编号
            map.put("tenantName", tenant.getName());// 租户名称
            map.put("tenantId", tenant.getId());// 租户名称
            map.put("number", itemNumber);// 编号
            /** 办件表单数据初始化 **/
            map.put("zihao", second + "号");// 编号
            map.put("msg", "获取表单初始化数据成功");
            map.put("success", true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void delAssociatedFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String processSerialNumber, String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        boolean b = associatedFileManager.deleteAssociatedFile(tenantId, processSerialNumber, processInstanceId);
        map.put(UtilConsts.SUCCESS, b);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void documentDetail(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processSerialNumber, @RequestParam String taskId, @RequestParam String itembox,
        @RequestParam String itemId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            if (StringUtils.isNotBlank(processInstanceId)) {// 打开办件
                map = documentManager.edit(tenantId, positionId, itembox, taskId, processInstanceId, itemId, true);
            } else {// 打开草稿
                map = draftManager.openDraft4Position(tenantId, positionId, itemId, processSerialNumber, true);
            }
            String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
            String processDefinitionId = (String)map.get("processDefinitionId");
            String taskDefKey = (String)map.get("taskDefKey");
            String formIds = (String)map.get("formId");
            String formNames = (String)map.get("formName");
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void documentDetailByTaskId(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String taskId, @RequestParam String itembox, HttpServletResponse response, Model model) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "获取失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personManager.getPerson(tenantId, userId));
            if (StringUtils.isNotBlank(taskId)) {// 打开办件
                TaskModel taskModel = taskManager.findById(tenantId, taskId);
                if (taskModel != null && taskModel.getId() != null) {
                    ProcessParamModel processParamModel = processParamManager.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId());
                    String itemId = processParamModel.getItemId();
                    map = documentManager.edit(tenantId, positionId, itembox, taskId, taskModel.getProcessInstanceId(), itemId, true);
                    String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
                    String processDefinitionId = (String)map.get("processDefinitionId");
                    String taskDefKey = (String)map.get("taskDefKey");
                    String formIds = (String)map.get("formId");
                    String formNames = (String)map.get("formName");
                    String processSerialNumber = processParamModel.getProcessSerialNumber();
                    DocumentUtil documentUtil = new DocumentUtil();
                    Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, taskModel.getProcessInstanceId(), taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
                    map.putAll(dataMap);
                    map.put("msg", "获取数据成功");
                    map.put(UtilConsts.SUCCESS, true);
                } else {
                    todoTaskManager.deleteTodoTaskByTaskId(tenantId, taskId);
                }
            }
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void findPermUser(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam String processInstanceId, @RequestParam(required = false) String id, @RequestParam String itemId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        if (StringUtils.isBlank(processDefinitionId) || StringUtils.isBlank(itemId)) {
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
            return;
        } else {
            item = itemRoleManager.findPermUser(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId, processDefinitionId, taskDefKey, principalType, id, processInstanceId);
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
            return;
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
    public void forwarding(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String itemId, @RequestParam(required = false) String temp_Ids,
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
                draftManager.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey, number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "发生异常");
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            }
            if (StringUtils.isNotBlank(temp_Ids)) {
                List<String> TempIdList = Y9Util.stringToList(temp_Ids, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : TempIdList) {
                    formDataManager.saveFormData(tenantId, formId, formJsonData);
                }
            }
            Map<String, Object> variables = new HashMap<String, Object>(16);
            if (processDefinitionKey.equals(Y9Context.getProperty("y9.app.workOrder.processDefinitionKey"))) {
                String startRouteToTaskId = Y9Context.getProperty("y9.app.workOrder.inNetrouteToTaskId");
                map = documentManager.saveAndForwardingByTaskKey(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, startRouteToTaskId, variables);
            } else {
                map = documentManager.saveAndForwarding(tenantId, positionId, processInstanceId, taskId, sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid, routeToTaskId, variables);
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    @RequestMapping("/getAssociatedFileList")
    @ResponseBody
    public void getAssociatedFileList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processSerialNumber, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        map = associatedFileManager.getAssociatedFileList(tenantId, processSerialNumber);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void getByTaskId(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String taskId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("msg", "获取成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (StringUtils.isNotBlank(taskId)) {
                TaskModel taskModel = taskManager.findById(tenantId, taskId);
                ProcessParamModel processParam = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                if (taskModel != null && taskModel.getId() != null) {
                    map.put("itembox", ItemBoxTypeEnum.TODO.getValue());
                    map.put("processSerialNumber", processParam.getProcessSerialNumber());
                    map.put("itemId", processParam.getItemId());
                } else {
                    HistoricProcessInstanceModel hpi = historicProcessManager.getById(tenantId, processInstanceId);
                    if (hpi == null) {
                        map.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    } else {
                        taskId = "";
                        map.put("itembox", new HashMap<String, String>(16));
                        List<TaskModel> taskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
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
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void getTabMap(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processDefinitionKey, @RequestParam String processDefinitionId, @RequestParam String taskId,
        @RequestParam String taskDefKey, @RequestParam String itemId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            map = documentManager.docUserChoise(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId, processDefinitionKey, processDefinitionId, taskId, taskDefKey, processInstanceId);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void saveAssociatedFile(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processSerialNumber, @RequestParam(required = false) String processInstanceIds,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        boolean b = associatedFileManager.saveAssociatedFile(tenantId, positionId, processSerialNumber, processInstanceIds);
        map.put(UtilConsts.SUCCESS, b);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
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
    public void saveFormData(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String temp_Ids, @RequestParam String processInstanceId, @RequestParam String processSerialNumber,
        @RequestParam String processDefinitionKey, @RequestParam String itemId, @RequestParam String formJsonData, HttpServletRequest request, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
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
                draftManager.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey, number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, title, number, level, false);
            if (!map1.isSuccess()) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "发生异常");
                Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
                return;
            }
            if (StringUtils.isNotBlank(temp_Ids)) {
                List<String> TempIdList = Y9Util.stringToList(temp_Ids, SysVariables.COMMA);
                LOGGER.debug("****************表单数据：{}*******************", formJsonData);
                for (String formId : TempIdList) {
                    formDataManager.saveFormData(tenantId, formId, formJsonData);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}