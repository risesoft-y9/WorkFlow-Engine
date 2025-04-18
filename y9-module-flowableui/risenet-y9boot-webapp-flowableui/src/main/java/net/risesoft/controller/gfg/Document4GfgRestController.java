package net.risesoft.controller.gfg;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
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
import net.risesoft.api.itemadmin.SecretLevelRecordApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.platform.OrgTypeEnum;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.DocUserChoiseModel;
import net.risesoft.model.itemadmin.DocumentDetailModel;
import net.risesoft.model.itemadmin.ItemButtonModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.ItemSystemListModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.SecretLevelModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.itemadmin.SignTaskConfigModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Organization;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.service.fgw.HTKYService;
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

    private final ProcessDefinitionApi processDefinitionApi;

    private final OrgUnitApi orgUnitApi;

    private final PositionRoleApi positionRoleApi;

    private final DepartmentApi departmentApi;

    private final Y9Properties y9Config;

    private final ProcessTodoApi processTodoApi;

    private final AsyncUtilService asyncUtilService;

    private final SignDeptDetailApi signDeptDetailApi;

    private final SignDeptInfoApi signDeptInfoApi;

    private final ActRuDetailApi actRuDetailApi;

    private final RoleApi roleApi;

    private final ButtonOperationApi buttonOperationApi;

    private final SecretLevelRecordApi secretLevelRecordApi;

    private final PositionApi positionApi;

    private final PersonApi personApi;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private HTKYService htkyService;

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
     * 取消上会
     *
     * @param processSerialNumbers 流程编号
     * @param fields 字段
     * @param tableName 表名
     * @return Y9Result<Boolean>
     */
    @PostMapping(value = "/cancelShangHui")
    public Y9Result<Object> cancelShangHui(@RequestParam String[] processSerialNumbers, @RequestParam String fields,
        @RequestParam String tableName) {
        String[] field = fields.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String processSerialNumber : processSerialNumbers) {
            String sql = "select  " + field[0] + " from " + tableName + " where guid = '" + processSerialNumber
                + "' and " + field[0] + " = '1'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() > 0) {// 已上会
                sql = "update " + tableName + " set " + field[0] + "= '0'," + field[1] + "='"
                    + Y9LoginUserHolder.getUserInfo().getName() + "'," + field[2] + "='"
                    + Y9LoginUserHolder.getUserInfo().getPersonId() + "'," + field[3] + "='" + sdf.format(new Date())
                    + "' where guid = '" + processSerialNumber + "'";
                jdbcTemplate.execute(sql);
                // 调用第三方接口

            }
        }
        return Y9Result.success();
    }

    @PostMapping(value = "/check4Batch")
    public Y9Result<List<TargetModel>> check4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            StringBuilder msg = new StringBuilder();
            List<String> list = new ArrayList<>();
            List<TaskModel> signList = new ArrayList<>();
            List<TaskModel> taskList = new ArrayList<>();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
                if (null == task) {
                    ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
                    if (StringUtils.isBlank(msg)) {
                        msg.append(ppModel.getTitle());
                    } else {
                        msg.append(",").append(ppModel.getTitle());
                    }
                } else {
                    taskList.add(task);
                    if (StringUtils.isBlank(task.getAssignee())) {
                        signList.add(task);
                    }
                    if (!list.contains(task.getTaskDefinitionKey())) {
                        list.add(task.getTaskDefinitionKey());
                    }
                }
            });
            if (!signList.isEmpty()) {
                return Y9Result.failure("不能批量发送，存在未签收的待办");
            }
            if (StringUtils.isNotBlank(msg)) {
                return Y9Result.failure("不能批量发送，以下待办已处理：" + msg);
            }
            if (list.size() > 1) {
                return Y9Result.failure("不能批量发送，存在不同的办理环节");
            }
            List<TargetModel> routeToTasks = processDefinitionApi
                .getTargetNodes(tenantId, taskList.get(0).getProcessDefinitionId(),
                    taskList.get(0).getTaskDefinitionKey())
                .getData().stream()
                .filter(m -> !"退回".equals(m.getTaskDefName()) && !"Exclusive Gateway".equals(m.getTaskDefName()))
                .collect(Collectors.toList());
            return Y9Result.success(routeToTasks);
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验失败，发生异常");
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
     * 获取传签件办件数据
     *
     * @param processSerialNumber 流程序列号
     * @return
     */
    @GetMapping(value = "/editCopy")
    public Y9Result<DocumentDetailModel> editCopy(@RequestParam @NotBlank String processSerialNumber) {
        try {
            DocumentDetailModel model = documentApi.editCopy(Y9LoginUserHolder.getTenantId(),
                Y9LoginUserHolder.getPositionId(), processSerialNumber, false).getData();
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
    public Y9Result<DocumentDetailModel> editDoing(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDoing(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, false, ItemBoxTypeEnum.DOING).getData();
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
    @GetMapping(value = "/editDoing4Admin")
    public Y9Result<DocumentDetailModel> editDoing4Admin(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDoing(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, true, ItemBoxTypeEnum.MONITORDOING).getData();
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
    public Y9Result<DocumentDetailModel> editDone(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDone(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, false, ItemBoxTypeEnum.DONE).getData();
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
    @GetMapping(value = "/editDone4Admin")
    public Y9Result<DocumentDetailModel> editDone4Admin(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String documentId) {
        try {
            DocumentDetailModel model =
                documentApi.editDone(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
                    processInstanceId, documentId, true, ItemBoxTypeEnum.MONITORDONE).getData();
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
     * 办件批量发送
     *
     * @param taskIdAndProcessSerialNumbers 任务id和流程序列号集合
     * @param userChoice 收件人
     * @param routeToTaskId 发送路由，任务key
     * @param sponsorHandle 是否主办办理
     * @param sponsorGuid 主办人id
     * @return Y9Result<Map < String, Object>>
     */
    @PostMapping(value = "/forwarding4Batch")
    public Y9Result<String> forwarding4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers,
        @RequestParam @NotBlank String userChoice, @RequestParam @NotBlank String routeToTaskId,
        @RequestParam(required = false) String sponsorHandle, @RequestParam(required = false) String sponsorGuid,
        @RequestParam(required = false) String dueDate, @RequestParam(required = false) String description) {
        AtomicInteger success = new AtomicInteger();
        AtomicInteger fail = new AtomicInteger();
        Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
            String[] tpArr = tp.split(":");
            try {
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), tpArr[1]).getData();
                processParamModel.setDueDate(null);
                if (StringUtils.isNotBlank(dueDate)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    processParamModel.setDueDate(sdf.parse(dueDate));
                }
                processParamModel.setDescription(description);
                processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);

                Y9Result<String> y9Result = documentApi.forwarding(Y9LoginUserHolder.getTenantId(),
                    Y9LoginUserHolder.getPositionId(), tpArr[0], userChoice, routeToTaskId, sponsorHandle, sponsorGuid);
                if (y9Result.isSuccess()) {
                    success.getAndIncrement();
                } else {
                    fail.getAndIncrement();
                }
            } catch (ParseException e) {
                fail.getAndIncrement();
            }
        });
        return Y9Result.successMsg("发送成功" + success.get() + "条，发送失败" + fail.get() + "条");
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
     * 获取部门信息
     * 
     * @param deptId
     * @return
     */
    @GetMapping(value = "/getDeptInfo")
    public Y9Result<Department> getDeptInfo(@RequestParam @NotBlank String deptId) {
        Department dept = departmentApi.get(Y9LoginUserHolder.getTenantId(), deptId).getData();
        return Y9Result.success(dept);
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
     * 获取岗位信息
     *
     * @param positionId
     * @return
     */
    @GetMapping(value = "/getPositionInfo")
    public Y9Result<Position> getPositionInfo(@RequestParam @NotBlank String positionId) {
        Position position = positionApi.get(Y9LoginUserHolder.getTenantId(), positionId).getData();
        return Y9Result.success(position);
    }

    /**
     * 获取密级等级记录
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List < SecretLevelModel>>
     */
    @GetMapping(value = "/getSecretLevelRecord")
    public Y9Result<List<SecretLevelModel>> getSecretLevelRecord(@RequestParam @NotBlank String processSerialNumber) {
        return secretLevelRecordApi.getRecord(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

    /**
     * 获取上会状态
     *
     * @param processSerialNumber 流程编号
     * @param fields 字段
     * @param tableName 表名
     * @return Y9Result<Boolean>
     */
    @PostMapping(value = "/getShangHuiStatus")
    public Y9Result<Boolean> getShangHuiStatus(@RequestParam String processSerialNumber, @RequestParam String fields,
        @RequestParam String tableName) {
        String[] field = fields.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select  " + field[0] + " from " + tableName + " where guid = '" + processSerialNumber + "' and "
            + field[0] + " = '1'";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        if (list.size() == 0) {// 未上会
            return Y9Result.success(false);
        }
        return Y9Result.success(true);
    }

    /**
     * 获取条码号数据
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<String>
     */
    @PostMapping(value = "/getTmhData")
    public Y9Result<String> getTmhData(@RequestParam @NotBlank String processSerialNumber) {
        // 调用第三方接口
        String tmh = htkyService.getTMH(processSerialNumber);
        return Y9Result.success(tmh);
    }

    /**
     * 获取当前登录用户信息
     *
     * @return Y9Result<Person>
     */
    @GetMapping(value = "/getUserInfo")
    public Y9Result<Person> getUserInfo() {
        Person person =
            personApi.get(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPersonId()).getData();
        return Y9Result.success(person);
    }

    /**
     * 获取正文管理员角色
     *
     * @param roleId 正文管理员角色id
     * @return Y9Result<Boolean>
     */
    @GetMapping(value = "/getWordManager")
    public Y9Result<Boolean> getWordManager(@RequestParam String roleId) {
        List<OrgUnit> list =
            roleApi.listOrgUnitsById(Y9LoginUserHolder.getTenantId(), roleId, OrgTypeEnum.POSITION).getData();
        for (OrgUnit orgUnit : list) {
            if (orgUnit.getId().equals(Y9LoginUserHolder.getPositionId())) {
                return Y9Result.success(true);
            }
        }
        return Y9Result.success(false);
    }

    /**
     * 当前节点是[主办司局秘书转交]，表单中[委内会签]如果存在部门且还没有走过会签子流程则只能选择[送会签]路由
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/isMust2SignDept")
    public Y9Result<Boolean> isMust2SignDept(@RequestParam @NotBlank String processSerialNumber) {
        try {
            List<SignDeptDetailModel> sddList = signDeptDetailApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
            if (!sddList.isEmpty()) {
                return Y9Result.success(false);
            }
            List<SignDeptModel> sdiList =
                signDeptInfoApi.getSignDeptList(Y9LoginUserHolder.getTenantId(), "0", processSerialNumber).getData();
            return Y9Result.success(!sdiList.isEmpty());
        } catch (Exception e) {
            LOGGER.error("获取编辑办件数据失败", e);
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
     * 插入推送双杨数据
     *
     * @param processSerialNumbers
     * @param eventtype
     * @return
     */
    @PostMapping(value = "/savePushData")
    public Y9Result<Object> savePushData(@RequestParam String[] processSerialNumbers, @RequestParam String eventtype) {
        for (String processSerialNumber : processSerialNumbers) {
            ProcessParamModel process = processParamApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
            String sql =
                "insert into PUSHDATA (ID,EVENTTYPE,CREATEDATE,PROCESSSERIALNUMBER,PROCESSINSTANCEID,TSZT) values (?,?,?,?,?,?)";
            Object[] args = {Y9IdGenerator.genId(), eventtype, new Date(), processSerialNumber,
                process.getProcessInstanceId(), "0"};
            jdbcTemplate.update(sql, args);
        }
        return Y9Result.success();
    }

    /**
     * 保存密级等级记录
     *
     * @param processSerialNumber 流程编号
     * @param secretLevel 秘密等级
     * @param secretBasis 秘密依据
     * @param secretItem 秘密事项
     * @param tableName 表名
     * @param fieldName 字段名
     * @param description 描述
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveSecretLevelRecord")
    public Y9Result<String> saveSecretLevelRecord(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String secretLevel, @RequestParam @NotBlank String secretBasis,
        @RequestParam @NotBlank String secretItem, @RequestParam @NotBlank String tableName,
        @RequestParam @NotBlank String fieldName, @RequestParam(required = false) String description) {
        try {
            secretLevelRecordApi.saveRecord(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
                processSerialNumber, secretLevel, secretBasis, secretItem, description, tableName, fieldName);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存上会
     *
     * @param processSerialNumbers 流程编号
     * @param fields 字段
     * @param tableName 表名
     * @return Y9Result<Boolean>
     */
    @PostMapping(value = "/saveShangHui")
    public Y9Result<Object> saveShangHui(@RequestParam String[] processSerialNumbers, @RequestParam String fields,
        @RequestParam String tableName) {
        String[] field = fields.split(",");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (String processSerialNumber : processSerialNumbers) {
            String sql = "select  " + field[0] + " from " + tableName + " where guid = '" + processSerialNumber
                + "' and " + field[0] + " = '1'";
            List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
            if (list.size() == 0) {// 未上会
                sql = "update " + tableName + " set " + field[0] + "= '1'," + field[1] + "='"
                    + Y9LoginUserHolder.getUserInfo().getName() + "'," + field[2] + "='"
                    + Y9LoginUserHolder.getUserInfo().getPersonId() + "'," + field[3] + "='" + sdf.format(new Date())
                    + "' where guid = '" + processSerialNumber + "'";
                jdbcTemplate.execute(sql);
                // 调用第三方接口

            }
        }
        return Y9Result.success();
    }

    @PostMapping(value = "/sign4Batch")
    public Y9Result<String> sign4Batch(@RequestParam String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        try {
            StringBuilder msg = new StringBuilder();
            StringBuilder msg4sign = new StringBuilder();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
                ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
                if (null == task) {
                    if (StringUtils.isBlank(msg)) {
                        msg.append(ppModel.getTitle());
                    } else {
                        msg.append(",").append(ppModel.getTitle());
                    }
                } else {
                    if (StringUtils.isNotBlank(task.getAssignee())) {
                        if (StringUtils.isBlank(msg4sign)) {
                            msg4sign.append(ppModel.getTitle());
                        } else {
                            msg4sign.append(",").append(ppModel.getTitle());
                        }
                    }
                }
            });
            if (StringUtils.isNotBlank(msg4sign)) {
                return Y9Result.failure("不能批量签收，以下待办已签收：" + msg4sign);
            }
            if (StringUtils.isNotBlank(msg)) {
                return Y9Result.failure("不能批量签收，以下待办已处理：" + msg);
            }
            AtomicInteger success = new AtomicInteger();
            AtomicInteger fail = new AtomicInteger();
            Arrays.stream(taskIdAndProcessSerialNumbers).forEach(tp -> {
                String[] tpArr = tp.split(":");
                Y9Result<Object> y9Result = taskApi.claim(tenantId, positionId, tpArr[0]);
                if (y9Result.isSuccess()) {
                    success.getAndIncrement();
                } else {
                    fail.getAndIncrement();
                }

            });
            return Y9Result.successMsg("签收成功" + success.get() + "条，签收失败" + fail.get() + "条");
        } catch (Exception e) {
            LOGGER.error("校验失败", e);
        }
        return Y9Result.failure("校验失败，发生异常");
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
