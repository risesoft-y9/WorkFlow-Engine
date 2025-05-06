package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomIdentityService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/processInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessInstanceVueController {

    protected final HistoryService historyService;

    private final RuntimeService runtimeService;

    private final OrgUnitApi orgUnitApi;

    private final TransactionWordApi transactionWordApi;

    private final AttachmentApi attachmentApi;

    private final ProcessParamApi processParamApi;

    private final CustomHistoricProcessService customHistoricProcessService;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final CustomIdentityService customIdentityService;

    private final CustomTaskService customTaskService;

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel;
        List<String> list = new ArrayList<>();
        try {
            processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = customHistoricProcessService.removeProcess(processInstanceId);
            if (b) {
                // 批量删除附件表
                attachmentApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除流程实例失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    private List<String> getAssigneeIdsAndAssigneeNames1(List<Task> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itemBox = ItemBoxTypeEnum.DOING.getValue(),
            taskId = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        if (!taskList.isEmpty()) {
            for (Task task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        OrgUnit orgUnitTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (orgUnitTemp != null) {
                            assigneeNames = orgUnitTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itemBox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLink> iList = customIdentityService.listIdentityLinksForTaskByTaskId(taskId);
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLink identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                OrgUnit ownerUser = orgUnitApi
                                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                                if (j < 5) {
                                    assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
                                    assigneeIds = Y9Util.genCustomStr(assigneeIds, assigneeId, SysVariables.COMMA);
                                } else {
                                    assigneeNames = assigneeNames + "等，共" + iList.size() + "人";
                                    break;
                                }
                                j++;
                            }
                        }
                    }
                } else {
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        if (i < 5) {
                            assigneeIds = Y9Util.genCustomStr(assigneeIds, assignee, SysVariables.COMMA);
                            OrgUnit orgUnitTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (orgUnitTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, orgUnitTemp.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(userId)) {
                            itemBox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        }
        list.add(taskIds);
        list.add(assigneeIds);
        list.add(assigneeNames);
        list.add(itemBox);
        list.add(taskId);
        return list;
    }

    /**
     * 获取流程实例列表（包括办结，未办结）
     *
     * @param searchName 标题，编号
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 状态
     * @param year 年度
     * @param page 页面
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/getAllProcessList")
    public Y9Page<Map<String, Object>> pageAllProcessList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9Page<OfficeDoneInfoModel> y9Page;
        try {
            y9Page = officeDoneInfoApi.searchAllList(tenantId, searchName, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<>();
            List<OfficeDoneInfoModel> hpiModelList = y9Page.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            for (OfficeDoneInfoModel officeDoneInfoModel : hpiList) {
                mapTemp = new HashMap<>(16);
                String processInstanceId = officeDoneInfoModel.getProcessInstanceId();
                try {
                    String processDefinitionId = officeDoneInfoModel.getProcessDefinitionId();
                    String startTime = officeDoneInfoModel.getStartTime().substring(0, 16);
                    String processSerialNumber = officeDoneInfoModel.getProcessSerialNumber();
                    String documentTitle =
                        StringUtils.isBlank(officeDoneInfoModel.getTitle()) ? "无标题" : officeDoneInfoModel.getTitle();
                    String level = officeDoneInfoModel.getUrgency();
                    String number = officeDoneInfoModel.getDocNumber();
                    String completer = officeDoneInfoModel.getUserComplete();
                    mapTemp.put("itemName", officeDoneInfoModel.getItemName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", officeDoneInfoModel.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", StringUtils.isBlank(officeDoneInfoModel.getEndTime()) ? "--"
                        : officeDoneInfoModel.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", officeDoneInfoModel.getCreatUserName());
                    mapTemp.put("itemId", officeDoneInfoModel.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(officeDoneInfoModel.getEndTime())) {
                        List<Task> taskList = customTaskService.listByProcessInstanceId(processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1),
                            assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId",
                            listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                    mapTemp.put("suspended", "--");
                    ProcessInstance processInstance =
                        runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                    if (processInstance != null) {
                        mapTemp.put("suspended", processInstance.isSuspended());
                    }
                } catch (Exception e) {
                    LOGGER.error("获取流程实例列表失败processInstanceId:{}, e:{}", processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, y9Page.getTotalPages(), y9Page.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取流程实例列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 获取流程实例列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/runningList")
    public Y9Page<Map<String, Object>> pageRunningList(@RequestParam(required = false) String processInstanceId,
        @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        long totalCount;
        List<ProcessInstance> processInstanceList;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId)
                .orderByStartTime().desc().listPage((page - 1) * rows, rows);
        }
        OrgUnit orgUnit;
        OrgUnit parent;
        Map<String, Object> map;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            map = new HashMap<>(16);
            map.put("processInstanceId", processInstanceId);
            map.put("processDefinitionId", processInstance.getProcessDefinitionId());
            map.put("processDefinitionName", processInstance.getProcessDefinitionName());
            map.put("startTime",
                processInstance.getStartTime() == null ? "" : sdf.format(processInstance.getStartTime()));
            try {
                map.put("activityName",
                    runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId)
                        .orderByActivityInstanceStartTime().desc().list().get(0).getActivityName());
                map.put("suspended", processInstance.isSuspended());
                map.put("startUserName", "无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    orgUnit =
                        orgUnitApi.getOrgUnitPersonOrPosition(tenantId, processInstance.getStartUserId()).getData();
                    parent = orgUnitApi.getParent(tenantId, orgUnit.getId()).getData();
                    map.put("startUserName", orgUnit.getName() + "(" + parent.getName() + ")");
                }
            } catch (Exception e) {
                LOGGER.error("获取流程实例列表失败processInstanceId:{}, e:{}", processInstanceId, e);
            }
            items.add(map);
        }
        int totalPages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalPages, totalCount, items, "获取列表成功");
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processInstanceId 流程实例
     * @return Y9Result<String>
     */
    @PostMapping(value = "/switchSuspendOrActive")
    public Y9Result<String> switchSuspendOrActive(@RequestParam String state, @RequestParam String processInstanceId) {
        try {
            if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
                runtimeService.activateProcessInstanceById(processInstanceId);
                return Y9Result.successMsg("已激活ID为[" + processInstanceId + "]的流程实例。");
            } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
                runtimeService.suspendProcessInstanceById(processInstanceId);
                return Y9Result.successMsg("已挂起ID为[" + processInstanceId + "]的流程实例。");
            }
        } catch (Exception e) {
            LOGGER.error("挂起、激活流程实例失败", e);
        }
        return Y9Result.failure("操作失败");
    }
}
