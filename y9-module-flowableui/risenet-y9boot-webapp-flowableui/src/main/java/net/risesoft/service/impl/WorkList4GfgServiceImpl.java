package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ItemAllApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ItemDoingApi;
import net.risesoft.api.itemadmin.ItemDoneApi;
import net.risesoft.api.itemadmin.ItemHaveDoneApi;
import net.risesoft.api.itemadmin.ItemRecycleApi;
import net.risesoft.api.itemadmin.ItemTodoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.itemadmin.UrgeInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkDayService;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WorkList4GfgServiceImpl implements WorkList4GfgService {

    private final ItemApi itemApi;

    private final VariableApi variableApi;

    private final HistoricTaskApi historicTaskApi;

    private final ProcessParamApi processParamApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final FormDataApi formDataApi;

    private final ItemTodoApi itemTodoApi;

    private final ItemDoingApi itemDoingApi;

    private final ItemDoneApi itemDoneApi;

    private final ItemRecycleApi itemRecycleApi;

    private final ItemHaveDoneApi itemHaveDoneApi;

    private final ItemAllApi itemAllApi;

    private final TaskApi taskApi;

    private final OrgUnitApi orgUnitApi;

    private final IdentityApi identityApi;

    private final TaskRelatedApi taskRelatedApi;

    private final WorkDayService workDayService;

    private final SignDeptDetailApi signDeptDetailApi;

    private final UrgeInfoApi urgeInfoApi;

    private final HistoricTaskApi historictaskApi;

    @Override
    public Y9Page<Map<String, Object>> allList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemAllApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("children", List.of());
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("executionId", ardModel.getExecutionId());
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList = new ArrayList<>();
                    mapTemp.putAll(
                        this.getTaskNameAndUserName(processParam, taskList, ardModel.isSub(), taskId,
                            signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    /*
                     * 催办信息
                     */
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO.getValue())) {
                        mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                        taskRelatedList = this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                        /*
                         * 红绿灯
                         */
                        if (null != ardModel.getDueDate()) {
                            taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                        }
                        taskRelatedList =
                            taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                                .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    } else {
                        if (!ardModel.isEnded()) {
                            mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                        } else {
                            mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                        }
                        mapTemp.put("children", ardModel.isSub() ? List.of() : this.getChildren(processSerialNumber,
                            mapTemp,
                            taskList, urgeInfoList4All, signDeptDetailList));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                } catch (Exception e) {
                    LOGGER.error("获取已办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel) {
        Y9Page<ActRuDetailModel> itemPage;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            itemPage = this.itemTodoApi.findByUserId(tenantId, positionId, queryParamModel);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (queryParamModel.getPage() - 1) * queryParamModel.getRows();
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam =
                        this.processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("number", processParam.getCustomNumber());
                    mapTemp.put("title", processParam.getTitle());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(queryParamModel.getPage(), itemPage.getTotalPages(), itemPage.getTotal(), items,
                "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办列表失败", e);
        }
        return Y9Page.success(queryParamModel.getPage(), 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            OrgUnit bureau = this.orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemDoingApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.putAll(
                        this.getTaskNameAndUserName(processParam, taskList, ardModel.isSub(), taskId,
                            signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);

                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4DuBan(String itemId, Integer days, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Date currentDate = new Date();
            String endDate = this.workDayService.getDate(currentDate, days);
            if (StringUtils.isBlank(endDate)) {
                return Y9Page.failure(0, 0, 0, new ArrayList<>(), "未设置日历", 500);
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            String startDate;
            switch (days) {
                case 3:
                    startDate = sdf.format(currentDate);
                    break;
                case 5:
                    startDate = this.workDayService.getDate(currentDate, 4);
                    break;
                case 7:
                    startDate = this.workDayService.getDate(currentDate, 6);
                    break;
                case 10:
                    startDate = this.workDayService.getDate(currentDate, 8);
                    break;
                default:
                    startDate = "2025-01-01";
            }
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemDoingApi.findBySystemName4DuBan(tenantId, startDate, endDate, item.getSystemName(), page,
                    rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList = new ArrayList<>();
                    mapTemp.putAll(this.getTaskNameAndUserName(processParam, taskList, false, "", signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                    /*
                     *催办信息
                     */
                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    urgeInfoList =
                        urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put("children",
                        this.getChildren(processSerialNumber, mapTemp, taskList, urgeInfoList4All, signDeptDetailList));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 获取流程的会签信息
     *
     * @param processSerialNumber 流程序列号
     * @param parentMap 主办流程相关数据
     * @param taskList 当前流程正在运行的所有任务
     * @param urgeInfoList4All 当前流程的所有催办信息
     * @return List<Map < String, Object>> 会签信息
     */
    private List<Map<String, Object>> getChildren(String processSerialNumber, Map<String, Object> parentMap,
        List<TaskModel> taskList, List<UrgeInfoModel> urgeInfoList4All, List<SignDeptDetailModel> signDeptDetailList) {
        List<Map<String, Object>> childrenList = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        if (signDeptDetailList.isEmpty()) {
            signDeptDetailList = this.signDeptDetailApi
                .findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        }
        signDeptDetailList.forEach(sdd -> {
            List<String> taskNameAndAssigneeNames = this.getTaskNameAndAssigneeNames(taskList, sdd.getExecutionId());
            Map<String, Object> childrenMap = new HashMap<>(parentMap);
            childrenMap.put("id", sdd.getId());
            childrenMap.put("isSub", true);
            childrenMap.put("serialNumber", count.incrementAndGet());
            childrenMap.put("taskName", taskNameAndAssigneeNames.get(0));
            childrenMap.put("taskAssignee", taskNameAndAssigneeNames.get(1));
            childrenMap.put("children", List.of());
            childrenMap.put("status", sdd.getStatus());
            childrenMap.put("bureauName", sdd.getDeptName());
            childrenMap.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
            childrenMap.put("executionId", sdd.getExecutionId());
            List<UrgeInfoModel> subUrgeInfoList = urgeInfoList4All.stream()
                .filter(urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(sdd.getExecutionId()))
                .collect(Collectors.toList());
            List<TaskRelatedModel> subTaskRelatedList = new ArrayList<>();
            if (!subUrgeInfoList.isEmpty()) {
                subTaskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                    Y9JsonUtil.writeValueAsString(subUrgeInfoList)));
            }
            childrenMap.put(SysVariables.TASKRELATEDLIST, subTaskRelatedList);
            childrenList.add(childrenMap);
        });
        return childrenList;
    }

    private Map<String, Object> getTaskNameAndUserName(ProcessParamModel processParam, List<TaskModel> taskList,
        boolean isSignDept, String taskId, List<SignDeptDetailModel> signDeptDetailList) {
        Map<String, Object> map = new HashMap<>();
        String tenantId = Y9LoginUserHolder.getTenantId(), processInstanceId = processParam.getProcessInstanceId();
        String userName = "", taskName = "";
        if (!taskList.isEmpty()) {
            /*
             * 当前节点如果是子流程的节点
             */
            boolean currentTaskIsSubNode = this.processDefinitionApi.isSubProcessChildNode(tenantId,
                taskList.get(0).getProcessDefinitionId(), taskList.get(0).getTaskDefinitionKey()).getData();
            if (currentTaskIsSubNode) {
                if (!isSignDept) {
                    /*
                     * 非会签司局看到的是送会签的人和发送会签的节点
                     */
                    String mainSender = this.variableApi
                        .getVariableByProcessInstanceId(tenantId, processInstanceId, SysVariables.MAINSENDER).getData();
                    if (signDeptDetailList.isEmpty()) {
                        signDeptDetailList =
                            this.signDeptDetailApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(),
                                processParam.getProcessSerialNumber()).getData();
                    }
                    taskName =
                        this.historictaskApi.getById(tenantId, signDeptDetailList.get(0).getTaskId()).getData()
                            .getName();
                    userName = StringUtils.isBlank(mainSender) ? "无" : Y9JsonUtil.readValue(mainSender, String.class);
                } else {
                    /*
                     * 会签司局看到的是子流程的当前办理人和办理节点
                     */
                    List<String> listTemp = this.getAssigneeIdsAndAssigneeNames4SignDept(taskList, taskId);
                    taskName = listTemp.get(0);
                    userName = listTemp.get(1);
                }
            } else {
                List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                taskName = taskList.get(0).getName();
                userName = listTemp.get(0);
            }
        } else {
            taskName = "已办结";
            userName = processParam.getCompleter();
        }
        map.put("taskName", taskName);
        map.put("taskAssignee", userName);
        return map;
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4Dept(String itemId, boolean isBureau, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = this.orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (isBureau) {
                itemPage = this.itemDoingApi.findByDeptIdAndSystemName(tenantId, bureau.getId(), true,
                    item.getSystemName(),
                    page, rows);
            } else {
                itemPage = this.itemDoingApi.findByDeptIdAndSystemName(tenantId, position.getParentId(), false,
                    item.getSystemName(), page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    boolean isSignDept = signDeptDetailList.stream()
                        .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
                    mapTemp
                        .putAll(this.getTaskNameAndUserName(processParam, taskList, isSignDept, taskId,
                            signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                    /*
                     * 催办信息
                     */
                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    if (isSignDept) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put("children", isSignDept ? List.of()
                        : this.getChildren(processSerialNumber, mapTemp, taskList, urgeInfoList4All,
                            signDeptDetailList));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doingList4All(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage = null;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = this.itemDoingApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            } else {
                itemPage =
                    this.itemDoingApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    List<SignDeptDetailModel> signDeptDetailList =
                        this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp
                        .putAll(this.getTaskNameAndUserName(processParam, taskList, false, taskId, signDeptDetailList));
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);

                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    urgeInfoList =
                        urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);

                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DOING.getValue());
                    mapTemp.put("children",
                        this.getChildren(processSerialNumber, mapTemp, taskList, urgeInfoList4All, signDeptDetailList));
                } catch (Exception e) {
                    LOGGER.error("获取在办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList4Dept(String itemId, boolean isBureau, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = this.orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (isBureau) {
                itemPage = this.itemDoneApi.findByDeptIdAndSystemName(tenantId, bureau.getId(), true,
                    item.getSystemName(),
                    page, rows);
            } else {
                itemPage = this.itemDoneApi.findByDeptIdAndSystemName(tenantId, position.getParentId(), false,
                    item.getSystemName(), page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                    List<SignDeptDetailModel> signDeptDetailList =
                        this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    boolean isSignDept = signDeptDetailList.stream()
                        .anyMatch(signDeptDetailModel -> signDeptDetailModel.getDeptId().equals(bureau.getId()));
                    /*
                     * 催办信息
                     */
                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    if (isSignDept) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put("children", isSignDept ? List.of()
                        : this.getChildren(processSerialNumber, mapTemp, List.of(), urgeInfoList4All,
                            signDeptDetailList));
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> doneList4All(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = this.itemDoneApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            } else {
                itemPage =
                    this.itemDoneApi.searchBySystemName(tenantId, item.getSystemName(), searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    urgeInfoList =
                        urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                    mapTemp.put("children",
                        this.getChildren(processSerialNumber, mapTemp, List.of(), urgeInfoList4All, List.of()));
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = this.orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
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
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(assigneeNames);
        return list;
    }

    /**
     * 返回当前人参与过的子流程的任务
     *
     * @param taskList 当前子流程所有任务
     * @param taskId 当前人参与过的任务
     * @return List<String>
     */
    private List<String> getAssigneeIdsAndAssigneeNames4SignDept(List<TaskModel> taskList, String taskId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskName = "", assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        HistoricTaskInstanceModel hisTask = this.historicTaskApi.getById(tenantId, taskId).getData();
        for (TaskModel task : taskList) {
            if (!task.getExecutionId().equals(hisTask.getExecutionId())) {
                continue;
            }
            taskName = task.getName();
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = this.orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
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
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }

        list.add(StringUtils.isBlank(taskName) ? "会签结束" : taskName);
        list.add(StringUtils.isBlank(assigneeNames) ? "无" : assigneeNames);
        return list;
    }

    /**
     * 返回会签流程的当前办理人和当前办理环节
     *
     * @param taskList 当前流程正在运行的所有任务
     * @param executionId 会签流程的执行id
     * @return List<String>
     */
    private List<String> getTaskNameAndAssigneeNames(List<TaskModel> taskList, String executionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String taskName = "", assigneeNames = "";
        List<String> list = new ArrayList<>();
        int i = 0;
        for (TaskModel task : taskList) {
            if (!task.getExecutionId().equals(executionId)) {
                continue;
            }
            taskName = task.getName();
            if (StringUtils.isEmpty(assigneeNames)) {
                String assignee = task.getAssignee();
                if (StringUtils.isNotBlank(assignee)) {
                    OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    if (personTemp != null) {
                        assigneeNames = personTemp.getName();
                        i += 1;
                    }
                } else {// 处理单实例未签收的当前办理人显示
                    List<IdentityLinkModel> iList =
                        this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();
                    if (!iList.isEmpty()) {
                        int j = 0;
                        for (IdentityLinkModel identityLink : iList) {
                            String assigneeId = identityLink.getUserId();
                            OrgUnit ownerUser = this.orgUnitApi
                                .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                            if (j < 5) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, ownerUser.getName(), "、");
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
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
        }
        if (taskList.size() > 5) {
            assigneeNames += "等，共" + taskList.size() + "人";
        }
        list.add(StringUtils.isBlank(taskName) ? "会签结束" : taskName);
        list.add(StringUtils.isBlank(assigneeNames) ? "无" : assigneeNames);
        return list;
    }

    @Override
    public Y9Page<Map<String, Object>> haveDoneList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemHaveDoneApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("id", processSerialNumber);
                    mapTemp.put("isSub", false);
                    mapTemp.put("serialNumber", ++serialNumber);
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("executionId", ardModel.getExecutionId());
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, StringUtils.isBlank(processParam.getCompleter())
                        ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
                    mapTemp.put("processDefinitionId", ardModel.getProcessDefinitionId());
                    List<SignDeptDetailModel> signDeptDetailList =
                        this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.putAll(
                        this.getTaskNameAndUserName(processParam, taskList, ardModel.isSub(), taskId,
                            signDeptDetailList));
                    List<UrgeInfoModel> urgeInfoList4All =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    List<UrgeInfoModel> urgeInfoList = urgeInfoList4All;
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    List<TaskRelatedModel> taskRelatedList = new ArrayList<>();
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put("children", ardModel.isSub() ? List.of()
                        : this.getChildren(processSerialNumber, mapTemp, taskList, urgeInfoList4All,
                            signDeptDetailList));
                } catch (Exception e) {
                    LOGGER.error("获取已办列表失败" + processInstanceId, e);
                }
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Result<List<Map<String, Object>>> getSignDeptDetailList(String processSerialNumber) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ProcessParamModel processParam =
                this.processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            Map<String, Object> mapTemp = new HashMap<>();
            mapTemp.put("id", processSerialNumber);
            mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
            mapTemp.put("systemCNName", processParam.getSystemCnName());
            mapTemp.put("bureauName", processParam.getHostDeptName());
            mapTemp.put("itemId", processParam.getItemId());
            mapTemp.put("processInstanceId", processParam.getProcessInstanceId());
            mapTemp.putAll(this.formDataApi.getData(tenantId, processParam.getItemId(), processSerialNumber).getData());
            mapTemp.put(SysVariables.ITEMBOX, StringUtils.isBlank(processParam.getCompleter())
                ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
            List<SignDeptDetailModel> signDeptDetailList =
                this.signDeptDetailApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            List<TaskModel> finalTaskList =
                this.taskApi.findByProcessInstanceId(tenantId, processParam.getProcessInstanceId()).getData();
            AtomicInteger count = new AtomicInteger(0);
            List<Map<String, Object>> childrenList = new ArrayList<>();
            signDeptDetailList.forEach(sdd -> {
                List<String> taskNameAndAssigneeNames =
                    this.getTaskNameAndAssigneeNames(finalTaskList, sdd.getExecutionId());
                Map<String, Object> childrenMap = new HashMap<>(mapTemp);
                childrenMap.put("id", sdd.getId());
                childrenMap.put("serialNumber", count.incrementAndGet());
                childrenMap.put("taskName", taskNameAndAssigneeNames.get(0));
                childrenMap.put("taskAssignee", taskNameAndAssigneeNames.get(1));
                childrenMap.put("children", List.of());
                childrenMap.put("status", sdd.getStatus());
                childrenMap.put("bureauName", sdd.getDeptName());
                childrenList.add(childrenMap);
            });
            return Y9Result.success(childrenList, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Result.success(List.of(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemRecycleApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskName", taskList.get(0).getName());
                    List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                    mapTemp.put("taskAssignee", listTemp.get(0));
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());

                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                } catch (Exception e) {
                    LOGGER.error("获取回收站列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList4Dept(String itemId, boolean isBureau, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            Position position = Y9LoginUserHolder.getPosition();
            OrgUnit bureau = this.orgUnitApi.getBureau(tenantId, positionId).getData();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (isBureau) {
                itemPage = this.itemRecycleApi.findByDeptIdAndSystemName(tenantId, bureau.getId(), true,
                    item.getSystemName(), page, rows);
            } else {
                itemPage = this.itemRecycleApi.findByDeptIdAndSystemName(tenantId, position.getParentId(), false,
                    item.getSystemName(), page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", "已办结");
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("completer",
                        StringUtils.isBlank(processParam.getCompleter()) ? "无" : processParam.getCompleter());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                } catch (Exception e) {
                    LOGGER.error("获取部门回收站列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> recycleList4All(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                this.itemRecycleApi.findBySystemName(tenantId, item.getSystemName(), page, rows);
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                    processParam = this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    List<TaskModel> taskList =
                        this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    mapTemp.put("taskName", taskList.get(0).getName());
                    List<String> listTemp = this.getAssigneeIdsAndAssigneeNames(taskList);
                    mapTemp.put("taskAssignee", listTemp.get(0));
                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.DONE.getValue());
                } catch (Exception e) {
                    LOGGER.error("获取回收站列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList(String itemId, String searchMapStr, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage =
                    this.itemTodoApi.findByUserIdAndSystemName(tenantId, positionId, item.getSystemName(), page, rows);
            } else {
                itemPage = this.itemTodoApi.searchByUserIdAndSystemName(tenantId, positionId, item.getSystemName(),
                    searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam =
                        this.processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    @Override
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(String itemId, String taskDefKey, String searchMapStr,
        Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage;
            if (StringUtils.isBlank(searchMapStr)) {
                itemPage = this.itemTodoApi.findByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, page, rows);
            } else {
                itemPage = this.itemTodoApi.searchByUserIdAndSystemNameAndTaskDefKey(tenantId, positionId,
                    item.getSystemName(), taskDefKey, searchMapStr, page, rows);
            }
            List<ActRuDetailModel> list = itemPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<ActRuDetailModel> taslList = objectMapper.convertValue(list, new TypeReference<>() {});
            List<Map<String, Object>> items = new ArrayList<>();
            int serialNumber = (page - 1) * rows;
            Map<String, Object> mapTemp;
            ProcessParamModel processParam;
            String processInstanceId;
            Map<String, Object> formData;
            for (ActRuDetailModel ardModel : taslList) {
                mapTemp = new HashMap<>(16);
                String taskId = ardModel.getTaskId();
                processInstanceId = ardModel.getProcessInstanceId();
                try {
                    String processSerialNumber = ardModel.getProcessSerialNumber();
                    processParam =
                        this.processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    mapTemp.put("actRuDetailId", ardModel.getId());
                    mapTemp.put("systemCNName", processParam.getSystemCnName());
                    mapTemp.put("bureauName", processParam.getHostDeptName());
                    mapTemp.put("taskName", ardModel.getTaskDefName());
                    mapTemp.put("itemId", processParam.getItemId());
                    mapTemp.put("processInstanceId", processInstanceId);
                    mapTemp.put("taskId", taskId);
                    mapTemp.put("taskAssignee", ardModel.getAssigneeName());
                    /*
                     * 暂时取表单所有字段数据
                     */
                    formData = this.formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
                    mapTemp.putAll(formData);
                    List<TaskRelatedModel> taskRelatedList =
                        this.taskRelatedApi.findByTaskId(tenantId, taskId).getData();
                    if (ardModel.isStarted()) {
                        taskRelatedList.add(0, new TaskRelatedModel(TaskRelatedEnum.NEWTODO.getValue(), "新"));
                    }
                    /*
                     * 红绿灯
                     */
                    if (null != ardModel.getDueDate()) {
                        taskRelatedList.add(this.workDayService.getLightColor(new Date(), ardModel.getDueDate()));
                    }
                    taskRelatedList = taskRelatedList.stream().filter(t -> Integer.parseInt(t.getInfoType()) < Integer
                        .parseInt(TaskRelatedEnum.ACTIONNAME.getValue())).collect(Collectors.toList());
                    List<UrgeInfoModel> urgeInfoList =
                        this.urgeInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                    if (ardModel.isSub()) {
                        urgeInfoList = urgeInfoList.stream().filter(
                            urgeInfo -> urgeInfo.isSub() && urgeInfo.getExecutionId().equals(ardModel.getExecutionId()))
                            .collect(Collectors.toList());
                    } else {
                        urgeInfoList =
                            urgeInfoList.stream().filter(urgeInfo -> !urgeInfo.isSub()).collect(Collectors.toList());
                    }
                    if (!urgeInfoList.isEmpty()) {
                        taskRelatedList.add(new TaskRelatedModel(TaskRelatedEnum.URGE.getValue(),
                            Y9JsonUtil.writeValueAsString(urgeInfoList)));
                    }
                    mapTemp.put(SysVariables.TASKRELATEDLIST, taskRelatedList);
                    mapTemp.put(SysVariables.ITEMBOX, ItemBoxTypeEnum.TODO.getValue());
                    mapTemp.put(SysVariables.PROCESSSERIALNUMBER, processSerialNumber);
                } catch (Exception e) {
                    LOGGER.error("获取待办列表失败" + processInstanceId, e);
                }
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取待办异常", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
