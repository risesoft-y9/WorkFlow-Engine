package net.risesoft.controller.mobile;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemOpinionFrameBindApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.itemadmin.OpenDataModel;
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
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 新建，发送，详情，关联流程接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/document")
@Slf4j
public class MobileDocumentController {

    protected final Logger log = LoggerFactory.getLogger(MobileDocumentController.class);

    private final PersonApi personApi;

    private final PositionRoleApi positionRoleApi;

    private final TenantApi tenantApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final Document4PositionApi document4PositionApi;

    private final ItemOpinionFrameBindApi itemOpinionFrameBindApi;

    private final TaskApi taskApi;

    private final AssociatedFile4PositionApi associatedFile4PositionApi;

    private final Draft4PositionApi draft4PositionApi;

    private final ItemRole4PositionApi itemRole4PositionApi;

    private final FormDataApi formDataApi;

    private final ProcessParamApi processParamApi;

    private final ProcessParamService processParamService;

    private final TodoTaskApi todotaskApi;

    private final HistoricProcessApi historicProcessApi;

    private final DepartmentApi departmentApi;

    /**
     * 新建公文
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     */
    @ResponseBody
    @RequestMapping(value = "/add")
    public void add(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            OpenDataModel model =
                document4PositionApi.add(Y9LoginUserHolder.getTenantId(), positionId, itemId, true).getData();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);

            String formIds = model.getFormId();
            String taskDefKey = model.getTaskDefKey();
            String processDefinitionId = model.getProcessDefinitionId();
            String[] formId = formIds.split(SysVariables.COMMA);
            List<Map<String, Object>> fieldDefineList = new ArrayList<>();
            List<Map<String, Object>> opinionFrameList = new ArrayList<>();
            List<ItemOpinionFrameBindModel> bindList =
                itemOpinionFrameBindApi.findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(tenantId, positionId,
                    itemId, processDefinitionId, taskDefKey).getData();
            for (ItemOpinionFrameBindModel bind : bindList) {
                Map<String, Object> opinionFrameMap = new HashMap<>(16);
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
            for (String s : formId) {// 获取表单定义字段
                Map<String, Object> fieldDefineMap = new HashMap<>(16);
                List<FormFieldDefineModel> listMap;
                listMap = formDataApi.getFormFieldDefine(tenantId, s).getData();
                fieldDefineMap.put(s, listMap);
                fieldDefineList.add(fieldDefineMap);
            }
            map.put("opinionFrame", opinionFrameList);
            map.put("fieldDefine", fieldDefineList);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            LOGGER.error("获取数据失败", e);
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 删除的流程实例id
     */
    @RequestMapping("/delAssociatedFile")
    @ResponseBody
    public void delAssociatedFile(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processInstanceId,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        Y9Result<Object> y9Result =
            associatedFile4PositionApi.deleteAssociatedFile(tenantId, processSerialNumber, processInstanceId);
        map.put(UtilConsts.SUCCESS, y9Result.isSuccess());
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @RequestMapping(value = "/documentDetail")
    public void documentDetail(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String itembox, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            OpenDataModel model;
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            if (StringUtils.isNotBlank(processInstanceId)) {// 打开办件
                model = document4PositionApi
                    .edit(tenantId, positionId, itembox, taskId, processInstanceId, itemId, true).getData();
            } else {// 打开草稿
                model = draft4PositionApi.openDraft4Position(tenantId, positionId, itemId, processSerialNumber, true)
                    .getData();
            }
            String activitiUser = model.getActivitiUser();
            String processDefinitionId = model.getProcessDefinitionId();
            String taskDefKey = model.getTaskDefKey();
            String formIds = model.getFormId();
            String formNames = model.getFormName();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber,
                processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            map.put("msg", "获取数据成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取数据失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 待办获取公文信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param itembox 办件状态，待办：todo,在办：doing,办结：done
     */
    @RequestMapping(value = "/documentDetailByTaskId")
    public void documentDetailByTaskId(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam @NotBlank @NotBlank String taskId, @RequestParam(required = false) String itembox,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("msg", "获取失败");
        map.put(UtilConsts.SUCCESS, false);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.get(tenantId, userId).getData());
            if (StringUtils.isNotBlank(taskId)) {// 打开办件
                TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
                if (taskModel != null && taskModel.getId() != null) {
                    ProcessParamModel processParamModel =
                        processParamApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId()).getData();
                    String itemId = processParamModel.getItemId();
                    OpenDataModel model = document4PositionApi
                        .edit(tenantId, positionId, itembox, taskId, taskModel.getProcessInstanceId(), itemId, true)
                        .getData();
                    String activitiUser = model.getActivitiUser();
                    String processDefinitionId = model.getProcessDefinitionId();
                    String taskDefKey = model.getTaskDefKey();
                    String formIds = model.getFormId();
                    String formNames = model.getFormName();
                    String processSerialNumber = processParamModel.getProcessSerialNumber();
                    String str = Y9JsonUtil.writeValueAsString(model);
                    map = Y9JsonUtil.readHashMap(str);
                    DocumentUtil documentUtil = new DocumentUtil();
                    Map<String,
                        Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber,
                            taskModel.getProcessInstanceId(), taskDefKey, taskId, itembox, activitiUser, formIds,
                            formNames);
                    map.putAll(dataMap);
                    map.put("msg", "获取数据成功");
                    map.put(UtilConsts.SUCCESS, true);
                } else {
                    todotaskApi.deleteTodoTaskByTaskId(tenantId, taskId);
                }
            }
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取数据失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @RequestMapping("/findPermUser")
    @ResponseBody
    public void findPermUser(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String id, @RequestParam String itemId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        if (StringUtils.isBlank(processDefinitionId) || StringUtils.isBlank(itemId)) {
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
        } else {
            item = itemRole4PositionApi.findPermUser(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId,
                processDefinitionId, taskDefKey, principalType, id, processInstanceId).getData();
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
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
     */
    @RequestMapping("/findPermUserByName")
    @ResponseBody
    public void findPermUserByName(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam Integer principalType, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String name, @RequestParam String itemId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        if (StringUtils.isBlank(processDefinitionId) || StringUtils.isBlank(itemId)) {
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
        } else {
            item = itemRole4PositionApi.findPermUserByName(Y9LoginUserHolder.getTenantId(), userId, positionId, name,
                principalType, itemId, processDefinitionId, taskDefKey, processInstanceId).getData();
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
        }
    }

    /**
     * 发送，同时保存表单数据
     *
     * @param tenantId 租户id
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
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/forwarding")
    public void forwarding(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam String temp_Ids, @RequestParam(required = false) String taskId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam @NotBlank String userChoice, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String routeToTaskId,
        @RequestParam(required = false) String processInstanceId, @RequestParam @NotBlank String formJsonData,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9Result<String> y9Result = null;
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送成功");
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = "";
            String number = "";
            String level = "";
            if (mapFormJsonData != null) {
                title = (String)mapFormJsonData.get("title");
                number = (String)mapFormJsonData.get("number");
                level = (String)mapFormJsonData.get("level");
                if (StringUtils.isBlank(level)) {
                    level = (String)mapFormJsonData.get("workLevel");
                }
            }

            if (StringUtils.isBlank(taskId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey,
                    number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId,
                title, number, level, false);
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
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            Map<String, Object> variables = new HashMap<>(16);
            y9Result = document4PositionApi.saveAndForwarding(tenantId, positionId, processInstanceId, taskId,
                sponsorHandle, itemId, processSerialNumber, processDefinitionKey, userChoice, sponsorGuid,
                routeToTaskId, variables);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送失败");
            LOGGER.error("发送失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
    }

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param formId 表单Id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     */
    @RequestMapping("/getAllFieldPerm")
    @ResponseBody
    public void getAllFieldPerm(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam @NotBlank String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam @NotBlank String formId,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Result<List<FieldPermModel>> y9Result =
            formDataApi.getAllFieldPerm(tenantId, userId, formId, taskDefKey, processDefinitionId);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result.getData()));
    }

    /**
     * 获取关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     */
    @RequestMapping("/getAssociatedFileList")
    @ResponseBody
    public void getAssociatedFileList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String processSerialNumber,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Result<List<AssociatedFileModel>> y9Result =
            associatedFile4PositionApi.getAssociatedFileAllList(tenantId, positionId, processSerialNumber);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
    }

    /**
     * 获取办件状态
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/getByTaskId")
    public void getByTaskId(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "获取成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (StringUtils.isNotBlank(taskId)) {
                TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
                ProcessParamModel processParam =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                if (taskModel != null && taskModel.getId() != null) {
                    map.put("itembox", ItemBoxTypeEnum.TODO.getValue());
                    map.put("processSerialNumber", processParam.getProcessSerialNumber());
                    map.put("itemId", processParam.getItemId());
                } else {
                    HistoricProcessInstanceModel hpi =
                        historicProcessApi.getById(tenantId, processInstanceId).getData();
                    if (hpi == null) {
                        map.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    } else {
                        taskId = "";
                        map.put("itembox", new HashMap<String, String>(16));
                        List<TaskModel> taskList =
                            taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
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
            LOGGER.error("获取办件状态失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取表单初始化的数据
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     */
    @RequestMapping(value = "/getFormInitData")
    public void getFormInitData(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        map.put("msg", "获取表单初始化数据失败");
        map.put("success", false);
        try {
            Person person = personApi.get(tenantId, userId).getData();
            Position position = positionApi.get(tenantId, positionId).getData();
            Date date = new Date();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String nowDate = sdf.format(date);
            SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
            SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
            String year = yearsdf.format(date);
            String second = sesdf.format(date);
            String itemNumber = "〔" + year + "〕" + second + "号";
            OrgUnit parent = orgUnitApi.getParent(tenantId, positionId).getData();
            Tenant tenant = tenantApi.getById(tenantId).getData();
            /* 办件表单数据初始化 **/
            map.put("deptName", parent.getName());// 创建部门
            map.put("userName", person.getName());// 创建人
            map.put("positionName", position.getName());// 创建岗位
            map.put("duty", position.getName().split("（")[0]);// 职务
            map.put("createDate", nowDate);// 创建日期
            map.put("mobile", person.getMobile());// 联系电话
            map.put("tenantName", tenant.getName());// 租户名称
            map.put("tenantId", tenant.getId());// 租户名称
            map.put("number", itemNumber);// 编号
            map.put("sign", "");// 签名
            PersonExt personExt = personApi.getPersonExtByPersonId(tenantId, userId).getData();
            if (personExt != null && personExt.getSign() != null) {
                map.put("sign", personExt.getSign());// 签名
            }
            List<OrgUnit> leaders = departmentApi
                .listDepartmentPropOrgUnits(tenantId, parent.getId(), DepartmentPropCategoryEnum.LEADER.getValue())
                .getData();
            map.put("deptLeader", "未配置");// 岗位所在部门领导
            if (!leaders.isEmpty()) {
                List<Person> personLeaders =
                    positionApi.listPersonsByPositionId(tenantId, leaders.get(0).getId()).getData();
                map.put("deptLeader",
                    personLeaders.isEmpty() ? leaders.get(0).getName() : personLeaders.get(0).getName());
            }
            /* 办件表单数据初始化 **/
            map.put("zihao", second + "号");// 编号
            map.put("msg", "获取表单初始化数据成功");
            map.put("success", true);
        } catch (Exception e) {
            LOGGER.error("获取表单初始化数据失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @ResponseBody
    @RequestMapping("/getTabMap")
    public void getTabMap(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam(required = false) String taskId,
        @RequestParam(required = false) String taskDefKey, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            DocUserChoiseModel model =
                document4PositionApi.docUserChoise(Y9LoginUserHolder.getTenantId(), userId, positionId, itemId,
                    processDefinitionKey, processDefinitionId, taskId, taskDefKey, processInstanceId).getData();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            LOGGER.error("获取数据失败", e);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 保存关联流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程实例编号
     * @param processInstanceIds 关联的流程实例ids
     */
    @RequestMapping("/saveAssociatedFile")
    @ResponseBody
    public void saveAssociatedFile(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String processInstanceIds, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        Y9Result<Object> y9Result = associatedFile4PositionApi.saveAssociatedFile(tenantId, positionId,
            processSerialNumber, processInstanceIds);
        map.put(UtilConsts.SUCCESS, y9Result.isSuccess());
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param temp_Ids 表单ids
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param processInstanceId 流程实例id
     * @param formJsonData 表单数据json字符串
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/saveFormData")
    public void saveFormData(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String temp_Ids,
        @RequestParam(required = false) String processInstanceId, @RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String processDefinitionKey, @RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String formJsonData, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = "";
            String number = "";
            String level = "";
            if (mapFormJsonData != null) {
                title = (String)mapFormJsonData.get("title");
                number = (String)mapFormJsonData.get("number");
                level = (String)mapFormJsonData.get("level");
                if (StringUtils.isBlank(level)) {
                    level = (String)mapFormJsonData.get("workLevel");
                }
            }

            if (StringUtils.isBlank(processInstanceId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey,
                    number, level, title);
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId,
                title, number, level, false);
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
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            LOGGER.error("保存失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 发送，同时保存表单数据
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param temp_Ids 表单ids
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param formJsonData 表单数据json字符串
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping("/submitTo")
    public void submitTo(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String temp_Ids, @RequestParam(required = false) String taskId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String formJsonData,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9Result<Object> y9Result = null;
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "发送成功");
            Y9LoginUserHolder.setTenantId(tenantId);
            formJsonData = formJsonData.replace("\n", "\\n");
            formJsonData = formJsonData.replace("\r", "\\r");
            Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formJsonData, Map.class);
            String title = "";
            String number = "";
            String level = "";
            if (mapFormJsonData != null) {
                title = (String)mapFormJsonData.get("title");
                number = (String)mapFormJsonData.get("number");
                level = (String)mapFormJsonData.get("level");
                if (StringUtils.isBlank(level)) {
                    level = (String)mapFormJsonData.get("workLevel");
                }
            }

            String processInstanceId = "";
            if (StringUtils.isBlank(taskId)) {// 保存草稿
                draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, "", number, level,
                    title);
            } else {
                TaskModel task = taskApi.findById(tenantId, taskId).getData();
                if (null != task) {
                    processInstanceId = task.getProcessInstanceId();
                }
            }
            Y9Result<String> map1 = processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId,
                title, number, level, false);
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
                    formDataApi.saveFormData(tenantId, formId, formJsonData);
                }
            }
            y9Result = document4PositionApi.saveAndSubmitTo(tenantId, positionId, taskId, itemId, processSerialNumber);
            if (!y9Result.isSuccess()) {
                map.put(UtilConsts.SUCCESS, false);
                map.put("msg", "发送失败");
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "发送失败");
            LOGGER.error("发送失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }
}