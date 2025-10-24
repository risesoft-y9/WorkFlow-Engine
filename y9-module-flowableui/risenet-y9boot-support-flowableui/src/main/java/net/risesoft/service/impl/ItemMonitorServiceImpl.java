package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.form.FormDataApi;
import net.risesoft.api.itemadmin.worklist.ItemMonitorApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.ItemMonitorService;
import net.risesoft.service.UtilService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemMonitorServiceImpl implements ItemMonitorService {

    private final TaskApi taskApi;

    private final ItemApi itemApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final ItemMonitorApi itemMonitorApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final FormDataApi formDataApi;

    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> pageTodoList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageDoingList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageDoneList(String itemId, Integer page, Integer rows) {
        return null;
    }

    @Override
    public Y9Page<Map<String, Object>> pageAllList(String itemId, Integer page, Integer rows) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            Y9Page<ActRuDetailModel> itemPage =
                itemMonitorApi.findBySystemName(tenantId, positionId, item.getSystemName(), page, rows);
            List<Map<String, Object>> items = processActRuDetails(itemPage.getRows(), tenantId, itemId, page, rows);
            return Y9Page.success(page, itemPage.getTotalPages(), itemPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取监控列表异常", e);
            return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
        }
    }

    /**
     * 处理 ActRuDetailModel 列表
     */
    private List<Map<String, Object>> processActRuDetails(List<ActRuDetailModel> actRuDetails, String tenantId,
        String itemId, Integer page, Integer rows) {
        List<Map<String, Object>> items = new ArrayList<>();
        int serialNumber = (page - 1) * rows;
        for (ActRuDetailModel ardModel : actRuDetails) {
            Map<String, Object> itemMap = processSingleActRuDetail(ardModel, tenantId, itemId, ++serialNumber);
            items.add(itemMap);
        }

        return items;
    }

    /**
     * 处理单个 ActRuDetailModel
     */
    private Map<String, Object> processSingleActRuDetail(ActRuDetailModel ardModel, String tenantId, String itemId,
        int serialNumber) {
        Map<String, Object> mapTemp = new HashMap<>(16);
        String processInstanceId = ardModel.getProcessInstanceId();
        try {
            String processSerialNumber = ardModel.getProcessSerialNumber();
            mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
            ProcessParamModel processParam =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            // 处理任务信息
            handleTaskInfo(mapTemp, tenantId, processInstanceId);
            // 处理流程参数相关信息
            handleProcessParamInfo(mapTemp, processParam, tenantId);
            // 处理表单数据
            handleFormData(mapTemp, tenantId, itemId, processSerialNumber);
            // 设置状态
            handleItemBoxStatus(mapTemp, ardModel, processParam);
        } catch (Exception e) {
            LOGGER.error("处理流程实例失败{}", processInstanceId, e);
        }
        mapTemp.put("serialNumber", serialNumber);
        return mapTemp;
    }

    /**
     * 处理任务相关信息
     */
    private void handleTaskInfo(Map<String, Object> mapTemp, String tenantId, String processInstanceId) {
        try {
            List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (taskList != null && !taskList.isEmpty()) {
                TaskModel firstTask = taskList.get(0);
                boolean isSubProcessChildNode =
                    processDefinitionApi
                        .isSubProcessChildNode(tenantId, firstTask.getProcessDefinitionId(),
                            firstTask.getTaskDefinitionKey())
                        .getData();
                if (isSubProcessChildNode) {
                    // 针对SubProcess
                    mapTemp.put("taskName", "送会签");
                    mapTemp.put("taskAssignee", "");
                } else {
                    mapTemp.put("taskName", firstTask.getName());
                    mapTemp.put("taskAssignee", utilService.getAssigneeNames(taskList, null));
                }
            }
        } catch (Exception e) {
            LOGGER.warn("处理任务信息失败, processInstanceId: {}", processInstanceId, e);
        }
    }

    /**
     * 处理流程参数相关信息
     */
    private void handleProcessParamInfo(Map<String, Object> mapTemp, ProcessParamModel processParam, String tenantId) {
        if (processParam != null) {
            mapTemp.put("systemCNName", processParam.getSystemCnName());
            mapTemp.put("itemId", processParam.getItemId());
            mapTemp.put("processInstanceId", processParam.getProcessInstanceId());
            try {
                String startor = processParam.getStartor();
                if (StringUtils.isNotBlank(startor)) {
                    mapTemp.put("bureauName", orgUnitApi.getBureau(tenantId, startor).getData().getName());
                }
            } catch (Exception e) {
                LOGGER.warn("获取发起单位信息失败, startor: {}", processParam.getStartor(), e);
            }
        }
    }

    /**
     * 处理表单数据
     */
    private void handleFormData(Map<String, Object> mapTemp, String tenantId, String itemId,
        String processSerialNumber) {
        try {
            // 暂时取表单所有字段数据
            Map<String, Object> formData = formDataApi.getData(tenantId, itemId, processSerialNumber).getData();
            if (formData != null) {
                mapTemp.putAll(formData);
            }
        } catch (Exception e) {
            LOGGER.warn("获取表单数据失败, processSerialNumber: {}", processSerialNumber, e);
        }
    }

    /**
     * 处理状态设置
     */
    private void handleItemBoxStatus(Map<String, Object> mapTemp, ActRuDetailModel ardModel,
        ProcessParamModel processParam) {
        if (Objects.equals(ardModel.getStatus(), ActRuDetailStatusEnum.TODO)) {
            mapTemp.put(SysVariables.ITEM_BOX, ItemBoxTypeEnum.TODO.getValue());
        } else {
            mapTemp.put(SysVariables.ITEM_BOX, StringUtils.isBlank(processParam.getCompleter())
                ? ItemBoxTypeEnum.DOING.getValue() : ItemBoxTypeEnum.DONE.getValue());
        }
    }

    @Override
    public Map<String, Object> pageRecycleList(String itemId, Integer page, Integer rows) {
        return null;
    }
}
