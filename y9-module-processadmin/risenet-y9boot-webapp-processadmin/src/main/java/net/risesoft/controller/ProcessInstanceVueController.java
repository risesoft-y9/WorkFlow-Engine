package net.risesoft.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomIdentityService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.task.api.Task;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/processInstance")
public class ProcessInstanceVueController {

    private final RuntimeService runtimeService;

    protected final HistoryService historyService;

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    private final TransactionWordApi transactionWordApi;

    private final Attachment4PositionApi attachment4PositionApi;

    private final ProcessParamApi processParamApi;

    private final CustomHistoricProcessService customHistoricProcessService;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final CustomIdentityService customIdentityService;

    private final CustomTaskService customTaskService;

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceId
     * @param type
     * @param reason
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam(required = true) String processInstanceId, @RequestParam(required = false) String type, @RequestParam(required = false) String reason) {
        // UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        // // 删除人不一定是当前正在处理流程的人员
        // String assignee = userInfo.getPersonId();
        // // 用户直接删除时，设置用户删除原因
        // if (TYPE_DELETE.equals(type)) {
        // if (StringUtils.isBlank(reason)) {
        // reason = "delete:删除流程实例";
        // } else {
        // // 为防止reason中出现英文顿号，这里将它们替换成中文顿号
        // reason.replace(SysVariables.COLON, "：");
        // // 为防止reason中出现英文逗号，这里将它们替换成中文逗号
        // reason.replace(SysVariables.COMMA, "，");
        // reason = "delete:" + reason;
        // }
        // // 当删除流程实例的时候，保存删除人的guid
        // reason = reason + SysVariables.COMMA + "operator" + SysVariables.COLON + assignee;
        // }
        // // 用户拒收时，设置用户拒收原因
        // if (TYPE_REJECT.equals(type)) {
        // reason = "reject:" + reason;
        // }
        // runtimeService.deleteProcessInstance(processInstanceId, reason);
        // return Y9Result.successMsg("删除成功");
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel = null;
        List<String> list = new ArrayList<String>();
        try {
            processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = customHistoricProcessService.removeProcess4Position(processInstanceId);
            if (b) {
                // 批量删除附件表
                attachment4PositionApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
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
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAllProcessList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getAllProcessList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            retMap = officeDoneInfo4PositionApi.searchAllList(tenantId, searchName, itemId, userName, state, year, page, rows);
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            List<OfficeDoneInfoModel> hpiModelList = (List<OfficeDoneInfoModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<OfficeDoneInfoModel> hpiList = objectMapper.convertValue(hpiModelList, new TypeReference<List<OfficeDoneInfoModel>>() {});
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp = null;
            for (OfficeDoneInfoModel hpim : hpiList) {
                mapTemp = new HashMap<String, Object>(16);
                String processInstanceId = hpim.getProcessInstanceId();
                try {
                    String processDefinitionId = hpim.getProcessDefinitionId();
                    String startTime = hpim.getStartTime().substring(0, 16);
                    String processSerialNumber = hpim.getProcessSerialNumber();
                    String documentTitle = StringUtils.isBlank(hpim.getTitle()) ? "无标题" : hpim.getTitle();
                    String level = hpim.getUrgency();
                    String number = hpim.getDocNumber();
                    String completer = hpim.getUserComplete();
                    mapTemp.put("itemName", hpim.getItemName());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put(SysVariables.DOCUMENTTITLE, documentTitle);
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("processDefinitionId", processDefinitionId);
                    mapTemp.put("processDefinitionKey", hpim.getProcessDefinitionKey());
                    mapTemp.put("startTime", startTime);
                    mapTemp.put("endTime", StringUtils.isBlank(hpim.getEndTime()) ? "--" : hpim.getEndTime().substring(0, 16));
                    mapTemp.put("taskDefinitionKey", "");
                    mapTemp.put("taskAssignee", completer);
                    mapTemp.put("creatUserName", hpim.getCreatUserName());
                    mapTemp.put("itemId", hpim.getItemId());
                    mapTemp.put("level", level == null ? "" : level);
                    mapTemp.put("number", number == null ? "" : number);
                    mapTemp.put("itembox", ItemBoxTypeEnum.DONE.getValue());
                    if (StringUtils.isBlank(hpim.getEndTime())) {
                        List<Task> taskList = customTaskService.findByProcessInstanceId(processInstanceId);
                        List<String> listTemp = getAssigneeIdsAndAssigneeNames1(taskList);
                        String taskIds = listTemp.get(0), assigneeIds = listTemp.get(1), assigneeNames = listTemp.get(2);
                        mapTemp.put("taskDefinitionKey", taskList.get(0).getTaskDefinitionKey());
                        mapTemp.put("taskId", listTemp.get(3).equals(ItemBoxTypeEnum.DOING.getValue()) ? taskIds : listTemp.get(4));
                        mapTemp.put("taskAssigneeId", assigneeIds);
                        mapTemp.put("taskAssignee", assigneeNames);
                        mapTemp.put("itembox", listTemp.get(3));
                    }
                    mapTemp.put("suspended", "--");
                    ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                    if (processInstance != null) {
                        mapTemp.put("suspended", processInstance.isSuspended());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), items, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    private List<String> getAssigneeIdsAndAssigneeNames1(List<Task> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        String taskIds = "", assigneeIds = "", assigneeNames = "", itembox = ItemBoxTypeEnum.DOING.getValue(), taskId = "";
        List<String> list = new ArrayList<String>();
        int i = 0;
        if (taskList.size() > 0) {
            for (Task task : taskList) {
                if (StringUtils.isEmpty(taskIds)) {
                    taskIds = task.getId();
                    String assignee = task.getAssignee();
                    if (StringUtils.isNotBlank(assignee)) {
                        assigneeIds = assignee;
                        Position personTemp = positionApi.get(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = personTemp.getName();
                        }
                        i += 1;
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
                            taskId = task.getId();
                        }
                    } else {// 处理单实例未签收的当前办理人显示
                        List<IdentityLink> iList = customIdentityService.getIdentityLinksForTask(taskId);
                        if (!iList.isEmpty()) {
                            int j = 0;
                            for (IdentityLink identityLink : iList) {
                                String assigneeId = identityLink.getUserId();
                                Position ownerUser = positionApi.get(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
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
                            Position personTemp = positionApi.get(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            }
                            i += 1;
                        }
                        if (assignee.contains(userId)) {
                            itembox = ItemBoxTypeEnum.TODO.getValue();
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
        list.add(itembox);
        list.add(taskId);
        return list;
    }

    /**
     * 获取流程实例列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/runningList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> runningList(@RequestParam(required = false) String processInstanceId, @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        long totalCount = 0;
        List<ProcessInstance> processInstanceList = null;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList = runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).orderByStartTime().desc().listPage((page - 1) * rows, rows);
        }
        Position position = null;
        OrgUnit orgUnit = null;
        Map<String, Object> map = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            map = new HashMap<>(16);
            map.put("processInstanceId", processInstanceId);
            map.put("processDefinitionId", processInstance.getProcessDefinitionId());
            map.put("processDefinitionName", processInstance.getProcessDefinitionName());
            map.put("startTime", processInstance.getStartTime() == null ? "" : sdf.format(processInstance.getStartTime()));
            try {
                map.put("activityName", runtimeService.createActivityInstanceQuery().processInstanceId(processInstanceId).orderByActivityInstanceStartTime().desc().list().get(0).getActivityName());
                map.put("suspended", processInstance.isSuspended());
                map.put("startUserName", "无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    String[] userIdAndDeptId = processInstance.getStartUserId().split(":");
                    if (userIdAndDeptId.length == 1) {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        orgUnit = orgUnitApi.getParent(tenantId, position.getId()).getData();
                        if (null != position) {
                            map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                        }
                    } else {
                        position = positionApi.get(tenantId, userIdAndDeptId[0]).getData();
                        if (null != position) {
                            orgUnit = orgUnitApi.getOrgUnit(tenantId, processInstance.getStartUserId().split(":")[1]).getData();
                            if (null == orgUnit) {
                                map.put("startUserName", position.getName());
                            } else {
                                map.put("startUserName", position.getName() + "(" + orgUnit.getName() + ")");
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            items.add(map);
        }
        int totalpages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalpages, totalCount, items, "获取列表成功");
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processInstanceId 流程实例
     * @return
     */
    @RequestMapping(value = "/switchSuspendOrActive", method = RequestMethod.POST, produces = "application/json")
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
            e.printStackTrace();
        }
        return Y9Result.failure("操作失败");
    }
}
