package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.IdentityApi;
import net.risesoft.api.processadmin.ProcessDoingApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.IdentityLinkModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoingService;
import net.risesoft.service.HandleFormDataService;
import net.risesoft.service.UtilService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DoingServiceImpl implements DoingService {

    private static final String PROCESSDEFINITIONID_KEY = "processDefinitionId";
    private static final String PROCESSINSTANCEID_KEY = "processInstanceId";
    private static final String ITEMNAME_KEY = "itemName";
    private static final String TASKNAME_KEY = "taskName";
    private static final String TASKCREATETIME_KEY = "taskCreateTime";
    private static final String TASKASSIGNEE_KEY = "taskAssignee";
    private static final String SERIALNUMBER_KEY = "serialNumber";
    private final ProcessDoingApi processDoingApi;
    private final TaskApi taskApi;
    private final ItemApi itemApi;
    private final OrgUnitApi orgUnitApi;
    private final ProcessParamApi processParamApi;
    private final HandleFormDataService handleFormDataService;
    private final IdentityApi identityApi;
    private final UtilService utilService;

    /**
     * 获取任务列表
     */
    private List<TaskModel> getTaskList(String tenantId, String processInstanceId) {
        try {
            return this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        } catch (Exception e) {
            LOGGER.warn("获取任务列表失败，processInstanceId: {}", processInstanceId, e);
            return new ArrayList<>();
        }
    }

    /**
     * 获取流程参数
     */
    private ProcessParamModel getProcessParam(String tenantId, String processInstanceId) {
        try {
            return this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        } catch (Exception e) {
            LOGGER.warn("获取流程参数失败，processInstanceId: {}", processInstanceId, e);
            return null;
        }
    }

    /**
     * 处理流程参数数据
     */
    private ProcessParamData handleProcessParamData(ProcessParamModel processParam) {
        String processSerialNumber = "";
        String documentTitle;
        String level = "";
        String number = "";
        if (null == processParam) {
            documentTitle = "流程参数表数据不存在";
        } else {
            documentTitle = StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
            level = processParam.getCustomLevel();
            number = processParam.getCustomNumber();
            processSerialNumber = processParam.getProcessSerialNumber();
        }

        return new ProcessParamData(processSerialNumber, documentTitle, level, number);
    }

    /**
     * 设置基础数据
     */
    private void setBasicData(Map<String, Object> mapTemp, ProcessParamData paramData, String processDefinitionId,
        String processInstanceId, String itemName) {
        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, paramData.processSerialNumber);
        mapTemp.put(PROCESSDEFINITIONID_KEY, processDefinitionId);
        mapTemp.put(PROCESSINSTANCEID_KEY, processInstanceId);
        mapTemp.put(ITEMNAME_KEY, itemName);
        mapTemp.put(SysVariables.DOCUMENT_TITLE, paramData.documentTitle);
        mapTemp.put(SysVariables.LEVEL, paramData.level);
        mapTemp.put(SysVariables.NUMBER, paramData.number);
    }

    /**
     * 处理单个流程实例，转换为前端需要的数据格式
     *
     * @param piModel 流程实例
     * @param itemName 事项名称
     * @param isSearch 是否为搜索模式
     * @param serialNumber 序号
     * @return 转换后的数据Map
     */
    private Map<String, Object> processSingleInstance(ProcessInstanceModel piModel, String itemName, boolean isSearch,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String processInstanceId = piModel.getId();
            String processDefinitionId = piModel.getProcessDefinitionId();
            // 获取任务列表和流程参数
            List<TaskModel> taskList = getTaskList(tenantId, processInstanceId);
            ProcessParamModel processParam = getProcessParam(tenantId, processInstanceId);
            // 处理流程参数数据
            ProcessParamData paramData = handleProcessParamData(processParam);
            // 设置基础数据
            setBasicData(mapTemp, paramData, processDefinitionId, processInstanceId, itemName);
            // 设置任务相关数据
            if (!taskList.isEmpty()) {
                TaskModel firstTask = taskList.get(0);
                mapTemp.put(TASKNAME_KEY, firstTask.getName());
                if (isSearch) {
                    mapTemp.put(TASKCREATETIME_KEY, Y9DateTimeUtils.formatDateTimeMinute(firstTask.getCreateTime()));
                } else {
                    Date endTime = piModel.getEndTime();
                    String taskCreateTime =
                        piModel.getEndTime() != null ? Y9DateTimeUtils.formatDateTimeMinute(endTime) : "";
                    mapTemp.put(TASKCREATETIME_KEY, taskCreateTime);
                }
                mapTemp.put(TASKASSIGNEE_KEY, this.getAssigneeNames(taskList));
            }
            // 设置公共数据
            utilService.setPublicData(mapTemp, processInstanceId, taskList, ItemBoxTypeEnum.DOING);
        } catch (Exception e) {
            LOGGER.error("获取在办列表失败{}", piModel.getId(), e);
        }
        mapTemp.put(SERIALNUMBER_KEY, serialNumber + 1);
        return mapTemp;
    }

    /**
     * 处理流程实例列表，转换为前端需要的数据格式
     *
     * @param piModelList 流程实例列表
     * @param itemName 事项名称
     * @param isSearch 是否为搜索模式
     * @param page 页码
     * @param rows 每页条数
     * @return 转换后的数据列表
     */
    private List<Map<String, Object>> processInstanceList(List<ProcessInstanceModel> piModelList, String itemId,
        String itemName, boolean isSearch, int page, int rows) {
        List<Map<String, Object>> items = new ArrayList<>();
        List<String> processSerialNumbers = new ArrayList<>();
        int serialNumber = (page - 1) * rows;
        for (ProcessInstanceModel piModel : piModelList) {
            Map<String, Object> mapTemp = processSingleInstance(piModel, itemName, isSearch, serialNumber);
            // 收集流程序列号用于表单数据处理
            Object processSerialNumberObj = mapTemp.get(SysVariables.PROCESS_SERIAL_NUMBER);
            if (processSerialNumberObj != null && !processSerialNumberObj.toString().isEmpty()) {
                processSerialNumbers.add(processSerialNumberObj.toString());
            }
            items.add(mapTemp);
            serialNumber += 1;
        }
        // 执行表单数据处理
        handleFormDataService.execute(itemId, items, processSerialNumbers);
        return items;
    }

    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        try {
            String positionId = Y9LoginUserHolder.getPositionId();
            String tenantId = Y9LoginUserHolder.getTenantId();
            // 获取事项信息
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            String itemName = item.getName();
            Y9Page<ProcessInstanceModel> piPage;
            boolean isSearch = StringUtils.isNotBlank(searchTerm);
            // 根据是否有搜索条件获取数据
            if (isSearch) {
                piPage = this.processDoingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
            } else {
                piPage = this.processDoingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId,
                    positionId, processDefinitionKey, page, rows);
            }
            // 处理数据列表
            List<Map<String, Object>> items =
                processInstanceList(piPage.getRows(), itemId, itemName, isSearch, page, rows);
            return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办件列表失败", e);
            return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
        }
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @param taskList 任务列表
     * @return 办理人名称字符串
     */
    private String getAssigneeNames(List<TaskModel> taskList) {
        if (taskList.isEmpty()) {
            return "";
        }
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> assigneeNameList = new ArrayList<>();
        for (TaskModel task : taskList) {
            String name = getTaskAssigneeName(task, tenantId);
            if (name != null && !name.isEmpty()) {
                assigneeNameList.add(name);
            }
        }
        return formatAssigneeNames(assigneeNameList, taskList.size());
    }

    /**
     * 获取任务办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 办理人名称
     */
    private String getTaskAssigneeName(TaskModel task, String tenantId) {
        if (StringUtils.isNotBlank(task.getAssignee())) {
            return getAssignedTaskAssigneeName(task, tenantId);
        } else {
            return getUnassignedTaskAssigneeNames(task, tenantId);
        }
    }

    /**
     * 格式化办理人名称列表
     *
     * @param assigneeNames 办理人名称列表
     * @param totalCount 总任务数
     * @return 格式化后的字符串
     */
    private String formatAssigneeNames(List<String> assigneeNames, int totalCount) {
        if (assigneeNames.isEmpty()) {
            return "";
        }
        int maxDisplayCount = 5;
        StringBuilder result = new StringBuilder();
        // 如果只有一个办理人，直接返回
        if (assigneeNames.size() == 1) {
            return assigneeNames.get(0);
        }
        // 如果办理人数不超过最大显示数，全部显示
        if (assigneeNames.size() <= maxDisplayCount) {
            return String.join("、", assigneeNames);
        }
        // 超过最大显示数，截取并添加"等"提示
        for (int i = 0; i < maxDisplayCount; i++) {
            if (result.length() > 0) {
                result.append("、");
            }
            result.append(assigneeNames.get(i));
        }
        result.append("等，共").append(totalCount).append("人");
        return result.toString();
    }

    /**
     * 处理已分配的任务，获取办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 办理人名称，如果获取失败返回null
     */
    private String getAssignedTaskAssigneeName(TaskModel task, String tenantId) {
        String assignee = task.getAssignee();
        if (StringUtils.isNotBlank(assignee)) {
            OrgUnit personTemp = this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
            if (personTemp != null) {
                return personTemp.getName();
            }
        }
        return null;
    }

    /**
     * 处理未分配的任务，获取候选办理人名称
     *
     * @param task 任务对象
     * @param tenantId 租户ID
     * @return 候选办理人名称列表
     */
    private String getUnassignedTaskAssigneeNames(TaskModel task, String tenantId) {
        StringBuilder assigneeNames = new StringBuilder();
        List<IdentityLinkModel> identityLinks =
            this.identityApi.getIdentityLinksForTask(tenantId, task.getId()).getData();

        if (!identityLinks.isEmpty()) {
            int maxDisplayCount = Math.min(identityLinks.size(), 5);
            for (int j = 0; j < maxDisplayCount; j++) {
                IdentityLinkModel identityLink = identityLinks.get(j);
                String assigneeId = identityLink.getUserId();
                OrgUnit ownerUser =
                    this.orgUnitApi.getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId).getData();
                if (assigneeNames.length() > 0) {
                    assigneeNames.append("、");
                }
                assigneeNames.append(ownerUser.getName());
            }

            if (identityLinks.size() > 5) {
                assigneeNames.append("等，共").append(identityLinks.size()).append("人");
            }
        }
        return assigneeNames.toString();
    }

    /**
     * 流程参数数据封装类
     */
    private static class ProcessParamData {
        String processSerialNumber;
        String documentTitle;
        String level;
        String number;

        ProcessParamData(String processSerialNumber, String documentTitle, String level, String number) {
            this.processSerialNumber = processSerialNumber;
            this.documentTitle = documentTitle;
            this.level = level;
            this.number = number;
        }
    }
}