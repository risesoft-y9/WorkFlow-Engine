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
import net.risesoft.api.processadmin.ProcessDoingApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
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
    private final ProcessParamApi processParamApi;
    private final HandleFormDataService handleFormDataService;
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
                mapTemp.put(TASKASSIGNEE_KEY, utilService.getAssigneeNames(taskList, null));
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
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            String itemName = item.getName();
            Y9Page<ProcessInstanceModel> piPage;
            boolean isSearch = StringUtils.isNotBlank(searchTerm);
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