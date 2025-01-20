package net.risesoft.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.DocumentDetailModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 发送，办结相关
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/document/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class Document4GfgRestController {

    private final ItemApi itemApi;

    private final DocumentApi documentApi;

    private final ButtonOperationService buttonOperationService;

    private final ProcessParamApi processParamApi;

    private final ChaoSongApi chaoSongApi;

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final OrgUnitApi orgUnitApi;

    private final PositionRoleApi positionRoleApi;

    private final DepartmentApi departmentApi;

    private final Y9Properties y9Config;

    private final ProcessTodoApi processTodoApi;

    private final AsyncUtilService asyncUtilService;

    private final SignDeptDetailApi signDeptDetailApi;

    private final ActRuDetailApi actRuDetailApi;

    private final RoleApi roleApi;

    private final ButtonOperationApi buttonOperationApi;

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/add")
    public Y9Result<Map<String, Object>> add(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map;
        try {
            OpenDataModel model = documentApi.add(tenantId, Y9LoginUserHolder.getPositionId(), itemId, false).getData();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);
            map.put("tenantId", tenantId);
            map.put("userId", Y9LoginUserHolder.getPositionId());
            map.put("userName", Y9LoginUserHolder.getPosition().getName());
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取新建办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/addWithStartTaskDefKey")
    public Y9Result<DocumentDetailModel> addWithStartTaskDefKey(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String startTaskDefKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            DocumentDetailModel model = documentApi
                .addWithStartTaskDefKey(tenantId, Y9LoginUserHolder.getPositionId(), itemId, startTaskDefKey, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取新建办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 流程办结
     *
     * @param taskId 任务id
     * @param infoOvert 办结数据是否在数据中心公开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/complete")
    public Y9Result<String> complete(@RequestParam @NotBlank String taskId,
        @RequestParam(required = false) String infoOvert) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("任务已办结，请刷新待办列表。");
            }
            boolean isSubProcessChildNode = processDefinitionApi.isSubProcessChildNode(Y9LoginUserHolder.getTenantId(),
                task.getProcessDefinitionId(), task.getTaskDefinitionKey()).getData();
            // 不是子流程，正常办结
            if (!isSubProcessChildNode) {
                buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
                return Y9Result.successMsg("办结成功");
            }
            // 是子流程，判断是不是最后一个办结的，是就找办理人，设置发送
            ProcessParamModel processParamModel = processParamApi
                .findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), task.getProcessInstanceId()).getData();
            List<SignDeptDetailModel> doingList =
                signDeptDetailApi
                    .findByProcessSerialNumberAndStatus(Y9LoginUserHolder.getTenantId(),
                        processParamModel.getProcessSerialNumber(), SignDeptDetailStatusEnum.DOING.getValue())
                    .getData();
            if (doingList.size() > 1) {
                buttonOperationService.complete(taskId, "办结", "已办结", infoOvert);
            } else {
                List<TaskModel> taskList = taskApi
                    .findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), task.getProcessInstanceId()).getData();
                List<SignDeptDetailModel> deleteList = signDeptDetailApi
                    .findByProcessSerialNumberAndStatus(Y9LoginUserHolder.getTenantId(),
                        processParamModel.getProcessSerialNumber(), SignDeptDetailStatusEnum.DELETED.getValue())
                    .getData();
                deleteList.parallelStream().forEach(sdd -> {
                    taskList.stream().filter(t -> t.getExecutionId().equals(sdd.getExecutionId())).findFirst()
                        .ifPresent(t -> {
                            try {
                                buttonOperationApi.specialComplete(Y9LoginUserHolder.getTenantId(),
                                    Y9LoginUserHolder.getPositionId(), t.getId(), "减签后的特殊办结");
                                sdd.setStatus(SignDeptDetailStatusEnum.DELETED_DONE.getValue());
                                signDeptDetailApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(),
                                    Y9LoginUserHolder.getPositionId(), sdd);
                            } catch (Exception e) {
                                throw new RuntimeException(e);
                            }
                        });
                });
                buttonOperationService.complete4Sub(taskId, "办结", "已办结");
            }
            /*
            通过是否有领导签名来表示正常办结
             */
            doingList.forEach(sdd -> {
                if (sdd.getExecutionId().equals(task.getExecutionId())) {
                    sdd.setStatus(StringUtils.isBlank(sdd.getDeptManager())
                        ? SignDeptDetailStatusEnum.ROLLBACK.getValue() : SignDeptDetailStatusEnum.DONE.getValue());
                    signDeptDetailApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                        sdd);
                }
            });
            return Y9Result.successMsg("办结成功");
        } catch (Exception e) {
            LOGGER.error("流程办结失败", e);
        }
        return Y9Result.failure("办结失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param itembox 办件状态
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/edit")
    public Y9Result<OpenDataModel> edit(@RequestParam @NotBlank String itembox,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String itemId) {
        if (itembox.equals("monitorDone") || itembox.equals("monitorRecycle")) {
            itembox = ItemBoxTypeEnum.DONE.getValue();
        }
        try {
            OpenDataModel model = documentApi.edit(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                itembox, taskId, processInstanceId, itemId, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/editDoing")
    public Y9Result<DocumentDetailModel> editDoing(@RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDoing(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), processInstanceId, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/editDone")
    public Y9Result<DocumentDetailModel> editDone(@RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDone(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), processInstanceId, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取回收站办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/editRecycle")
    public Y9Result<DocumentDetailModel> editRecycle(@RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi.editRecycle(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), processInstanceId, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/editTodo")
    public Y9Result<DocumentDetailModel> editTodo(@RequestParam @NotBlank String taskId) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("当前待办已处理！");
            }
            DocumentDetailModel model = documentApi
                .editTodo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId, false).getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取待办按钮
     * 
     * @param taskId 任务id
     * @return {@code Y9Result<List<ItemButtonModel>>} 通用请求返回对象 - data是按钮集合
     */
    @GetMapping(value = "/getButtons")
    public Y9Result<List<ItemButtonModel>> getButtons(@RequestParam @NotBlank String taskId) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("当前待办已处理！");
            }
            List<ItemButtonModel> buttonList = documentApi
                .getButtons(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId).getData();
            return Y9Result.success(buttonList, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办件发送
     *
     * @param itemId 事项id
     * @param sponsorHandle 是否主办办理
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param userChoice 收件人
     * @param sponsorGuid 主办人id
     * @param routeToTaskId 发送路由，任务key
     * @return Y9Result<Map < String, Object>>
     */
    @PostMapping(value = "/forwarding")
    public Y9Result<Map<String, Object>> forwarding(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String taskId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String userChoice,
        @RequestParam(required = false) String sponsorGuid, @RequestParam @NotBlank String routeToTaskId,
        @RequestParam(required = false) String dueDate, @RequestParam(required = false) String description) {
        Map<String, Object> map = new HashMap<>();
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            ProcessParamModel processParamModel = processParamApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
            processParamModel.setDueDate(null);
            if (StringUtils.isNotBlank(dueDate)) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    processParamModel.setDueDate(sdf.parse(dueDate));
                } catch (ParseException e) {
                    LOGGER.error("办理期限转换失败{}", dueDate);
                }
            }
            processParamModel.setDescription(description);
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
            Y9Result<String> y9Result = documentApi.forwarding(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), taskId, userChoice, routeToTaskId, sponsorHandle, sponsorGuid);
            if (y9Result.isSuccess()) {
                map.put("processInstanceId", y9Result.getData());
                // 生成流水号
                asyncUtilService.generateNumber(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
                    itemId, processSerialNumber, task);
                return Y9Result.success(map, y9Result.getMsg());
            } else {
                return Y9Result.failure(y9Result.getMsg());
            }
        } catch (Exception e) {
            LOGGER.error("发送失败", e);
        }
        return Y9Result.failure("发送失败，发生异常");
    }

    /**
     * 获取所有开始节点(事项管理-事项配置-路由配置进行授权)
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemStartNodeRoleModel>>
     */
    @GetMapping(value = "/getAllStartTaskDefKey")
    public Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam @NotBlank String itemId) {
        return documentApi.getAllStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
            itemId);
    }

    /**
     * 获取主办司局（取组织下的为委办局的部门）
     *
     * @return Y9Result<List < Department>>
     */
    @GetMapping(value = "/getBureau")
    public Y9Result<List<Department>> getBureau() {
        Organization organization =
            orgUnitApi.getOrganization(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId()).getData();
        List<Department> deptList =
            departmentApi.listByParentId(Y9LoginUserHolder.getTenantId(), organization.getId()).getData();
        deptList = deptList.stream().filter(Department::isBureau).collect(Collectors.toList());
        return Y9Result.success(deptList);
    }

    /**
     * 获取事项列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemList")
    public Y9Result<Map<String, Object>> getItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            List<ItemListModel> listMap =
                itemApi.getItemList(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData();
            map.put("itemMap", listMap);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData());
            // int followCount = officeFollowApi.getFollowCount(tenantId, Y9LoginUserHolder.getPositionId());
            // map.put("followCount", followCount);
            // 公共角色
            boolean b = positionRoleApi.hasPublicRole(tenantId, "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
            map.put("monitorManage", b);

            boolean b1 = positionRoleApi.hasRole(tenantId, "itemAdmin", "", "人事统计角色", Y9LoginUserHolder.getPositionId())
                .getData();
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项系统
     *
     * @return Y9Result<List < ItemSystemListModel>>
     */
    @GetMapping(value = "/getItemSystem")
    public Y9Result<List<ItemSystemListModel>> getItemSystem() {
        return itemApi.getItemSystem(Y9LoginUserHolder.getTenantId());
    }

    /**
     * 获取事项和事项对应的系统列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemSystemList")
    public Y9Result<Map<String, Object>> getItemSystemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            List<Map<String, Object>> list = new ArrayList<>();
            List<ItemModel> listMap = itemApi.getAllItem(Y9LoginUserHolder.getTenantId()).getData();
            for (ItemModel itemModel : listMap) {
                Map<String, Object> newmap = new HashMap<>(16);
                newmap.put("systemName", itemModel.getSystemName());
                newmap.put("systemCnName", itemModel.getSysLevel());
                if (!list.contains(newmap)) {
                    list.add(newmap);
                }
            }
            for (Map<String, Object> nmap : list) {
                long todoCount = processTodoApi
                    .getTodoCountByUserIdAndSystemName(tenantId, positionId, (String)nmap.get("systemName")).getData();
                nmap.put("todoCount", todoCount);
                List<ItemModel> itemList = new ArrayList<>();
                for (ItemModel itemModel : listMap) {
                    if (nmap.get("systemName").equals(itemModel.getSystemName())) {
                        itemList.add(itemModel);
                    }
                }
                nmap.put("itemList", itemList);
            }

            map.put("systemList", list);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData());
            // 公共角色
            boolean b = positionRoleApi.hasPublicRole(tenantId, "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
            map.put("monitorManage", b);

            boolean b1 = false;
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项系统列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取办件发起人领导
     *
     * @param processInstanceId 流程实例id
     * @param roleId 司局领导角色id
     * @return Y9Result<Boolean>
     */
    @GetMapping(value = "/getLeader")
    public Y9Result<Boolean> getLeader(@RequestParam String processInstanceId, @RequestParam String roleId) {
        ProcessParamModel processParam =
            processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
        String startor = processParam.getStartor();
        List<OrgUnit> list =
            roleApi.listOrgUnitsById(Y9LoginUserHolder.getTenantId(), roleId, OrgTypeEnum.POSITION).getData();
        OrgUnit bureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), startor).getData();
        for (OrgUnit orgUnit : list) {
            OrgUnit newbureau = orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), orgUnit.getId()).getData();
            // 判断当前人是司局领导，且跟起草人司局相同
            if (orgUnit.getId().equals(Y9LoginUserHolder.getPositionId()) && bureau != null && newbureau != null
                && newbureau.getId().equals(bureau.getId())) {
                return Y9Result.success(true);
            }
        }
        return Y9Result.success(false);
    }

    /**
     * 获取协办人员办理情况
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getParallelNames")
    public Y9Result<Map<String, Object>> getParallelNames(@RequestParam @NotBlank String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        String parallelDoing = "";
        Map<String, Object> map = new HashMap<>(16);
        map.put("isParallel", false);
        int i = 0;
        List<TaskModel> list = null;
        try {
            TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
            String multiInstance = processDefinitionApi
                .getNodeType(tenantId, taskModel.getProcessDefinitionId(), taskModel.getTaskDefinitionKey()).getData();
            if (multiInstance.equals(SysVariables.PARALLEL)) {// 并行
                map.put("isParallel", true);
                list = taskApi.findByProcessInstanceId(tenantId, taskModel.getProcessInstanceId(), true).getData();
                for (TaskModel task : list) {
                    if (i < 5) {
                        String assigneeId = task.getAssignee();
                        OrgUnit employee = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assigneeId).getData();
                        if (employee != null && !employee.getId().equals(positionId)) { // 协办人员
                            if (StringUtils.isBlank(parallelDoing)) {
                                parallelDoing = employee.getName();
                            } else {
                                parallelDoing = parallelDoing + "、" + employee.getName();
                            }
                            i++;
                        }
                    }
                }
            }
            map.put("parallelDoing", parallelDoing);
            map.put("count", list != null ? list.size() - 1 : 0);// 减去主办任务数
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取协办人员办理情况失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 批量恢复待办
     *
     * @param processInstanceIds 流程实例ids ，逗号隔开
     * @param desc 原因
     * @return Y9Result<String>
     */
    @PostMapping(value = "/multipleResumeToDo")
    public Y9Result<String> multipleResumeToDo(@RequestParam @NotBlank String processInstanceIds,
        @RequestParam(required = false) String desc) {
        try {
            buttonOperationService.multipleResumeToDo(processInstanceIds, desc);
            return Y9Result.successMsg("恢复成功");
        } catch (Exception e) {
            LOGGER.error("恢复待办失败", e);
        }
        return Y9Result.failure("恢复失败");
    }

    /**
     * 获取签收任务配置（用于判断是否直接发送）
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/signTaskConfig")
    public Y9Result<SignTaskConfigModel> signTaskConfig(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processDefinitionId, @RequestParam @NotBlank String taskDefinitionKey,
        @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return documentApi.signTaskConfig(tenantId, Y9LoginUserHolder.getPositionId(), itemId, processDefinitionId,
            taskDefinitionKey, processSerialNumber);
    }

    /**
     * 办件发送
     *
     * @param itemId 事项id
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/submitTo")
    public Y9Result<Object> submitTo(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskId, @RequestParam @NotBlank String processSerialNumber) {
        return documentApi.saveAndSubmitTo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), taskId,
            itemId, processSerialNumber);
    }

    /**
     * 获取用户选人发送界面数据
     *
     * @param itemId 事项id
     * @param routeToTask 任务路由
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @return Y9Result<DocUserChoiseModel>
     */
    @GetMapping(value = "/userChoiseData")
    public Y9Result<DocUserChoiseModel> userChoiseData(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String routeToTask, @RequestParam @NotBlank String processDefinitionId,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String processInstanceId) {
        return documentApi.docUserChoise(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), itemId, "", processDefinitionId, taskId, routeToTask, processInstanceId);
    }
}
