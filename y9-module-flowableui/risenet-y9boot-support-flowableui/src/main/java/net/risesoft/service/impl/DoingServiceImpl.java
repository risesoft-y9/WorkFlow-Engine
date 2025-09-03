package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
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
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class DoingServiceImpl implements DoingService {

    private final ProcessDoingApi processDoingApi;

    private final TaskApi taskApi;

    private final ItemApi itemApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamApi processParamApi;

    private final HandleFormDataService handleFormDataService;

    private final IdentityApi identityApi;

    private final UtilService utilService;

    @Override
    public Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<ProcessInstanceModel> piPage;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            List<String> processSerialNumbers = new ArrayList<>();
            if (StringUtils.isBlank(searchTerm)) {
                piPage = this.processDoingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId,
                    positionId, processDefinitionKey, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processDefinitionId, processInstanceId = "", processSerialNumber = "", documentTitle, level = "",
                    number = "";
                for (ProcessInstanceModel piModel : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        processInstanceId = piModel.getId();
                        processDefinitionId = piModel.getProcessDefinitionId();
                        Date endTime = piModel.getEndTime();
                        String taskCreateTime = piModel.getEndTime() != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        if (null == processParam) {
                            documentTitle = "流程参数表数据不存在";
                        } else {
                            documentTitle =
                                StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                            level = processParam.getCustomLevel();
                            number = processParam.getCustomNumber();
                            processSerialNumber = processParam.getProcessSerialNumber();
                            processSerialNumbers.add(processSerialNumber);
                        }
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskAssignee", this.getAssigneeNames(taskList));
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        utilService.setPublicData(mapTemp, processInstanceId, taskList, ItemBoxTypeEnum.DOING);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败{}", processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
                handleFormDataService.execute(itemId, items, processSerialNumbers);
            } else {
                piPage = this.processDoingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                String processDefinitionId, processInstanceId, processSerialNumber = "", documentTitle, level = "",
                    number = "";
                for (ProcessInstanceModel piModel : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    processInstanceId = piModel.getId();
                    processDefinitionId = piModel.getProcessDefinitionId();
                    try {
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        if (null == processParam) {
                            documentTitle = "流程参数表数据不存在";
                        } else {
                            documentTitle =
                                StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                            level = processParam.getCustomLevel();
                            number = processParam.getCustomNumber();
                            processSerialNumber = processParam.getProcessSerialNumber();
                            processSerialNumbers.add(processSerialNumber);
                        }
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskAssignee", this.getAssigneeNames(taskList));
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        utilService.setPublicData(mapTemp, processInstanceId, taskList, ItemBoxTypeEnum.DOING);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败{}", processInstanceId, e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
                handleFormDataService.execute(itemId, items, processSerialNumbers);
            }
            return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 当并行的时候，会获取到多个task，为了并行时当前办理人显示多人，而不是显示多条记录，需要分开分别进行处理
     *
     * @return String
     */
    private String getAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        int i = 0;
        if (!taskList.isEmpty()) {
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
                                    .getOrgUnitPersonOrPosition(Y9LoginUserHolder.getTenantId(), assigneeId)
                                    .getData();
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
                            OrgUnit personTemp =
                                this.orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                            if (personTemp != null) {
                                assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");// 并行时，领导选取时存在顺序，因此这里也存在顺序
                                i += 1;
                            }
                        }
                    }
                }
            }
            if (taskList.size() > 5) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        } else {
            /*
             * List<HistoricActivityInstance> historicActivityInstanceList =
             * historyService.createHistoricActivityInstanceQuery().
             * processInstanceId(processInstanceId).activityType(SysVariables.
             * CALLACTIVITY).list(); if (historicActivityInstanceList != null &&
             * historicActivityInstanceList.size() > 0) { Map<String, Object> record =
             * setTodoElement(historicActivityInstanceList.get(0), existAssigneeId,
             * existAssigneeName); items.add(record); }
             */
        }
        return assigneeNames;
    }

    @Override
    public Y9Page<Map<String, Object>> list4Mobile(String itemId, String searchTerm, Integer page, Integer rows) {
        Y9Page<ProcessInstanceModel> piPage;
        try {
            List<Map<String, Object>> items = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = this.itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid(), itemName = item.getName();
            if (StringUtils.isBlank(searchTerm)) {
                piPage = this.processDoingApi.getListByUserIdAndProcessDefinitionKeyOrderBySendTime(tenantId,
                    positionId, processDefinitionKey, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                for (ProcessInstanceModel piModel : hpiModelList) {// 以办理时间排序
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = piModel.getId();
                        String processDefinitionId = piModel.getProcessDefinitionId();
                        Date endTime = piModel.getEndTime();
                        String taskCreateTime = piModel.getEndTime() != null ? sdf.format(endTime) : "";
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", taskCreateTime);
                        mapTemp.put("taskAssignee", this.getAssigneeNames(taskList));
                        mapTemp.put("itemName", itemName);
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        utilService.setPublicData(mapTemp, processInstanceId, taskList, ItemBoxTypeEnum.DOING);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            } else {
                piPage = this.processDoingApi.searchListByUserIdAndProcessDefinitionKey(tenantId, positionId,
                    processDefinitionKey, searchTerm, page, rows);
                List<ProcessInstanceModel> hpiModelList = piPage.getRows();
                int serialNumber = (page - 1) * rows;
                Map<String, Object> mapTemp;
                ProcessParamModel processParam;
                for (ProcessInstanceModel piModel : hpiModelList) {
                    mapTemp = new HashMap<>(16);
                    try {
                        String processInstanceId = piModel.getId();
                        String processDefinitionId = piModel.getProcessDefinitionId();
                        List<TaskModel> taskList =
                            this.taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        processParam =
                            this.processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                        String processSerialNumber = processParam.getProcessSerialNumber();
                        String documentTitle =
                            StringUtils.isBlank(processParam.getTitle()) ? "无标题" : processParam.getTitle();
                        String level = processParam.getCustomLevel();
                        String number = processParam.getCustomNumber();
                        mapTemp.put(SysVariables.PROCESS_SERIAL_NUMBER, processSerialNumber);
                        mapTemp.put("processInstanceId", processInstanceId);
                        mapTemp.put("processDefinitionId", processDefinitionId);
                        mapTemp.put("itemName", itemName);
                        mapTemp.put("taskName", taskList.get(0).getName());
                        mapTemp.put("taskCreateTime", sdf.format(taskList.get(0).getCreateTime()));
                        mapTemp.put("taskAssignee", this.getAssigneeNames(taskList));
                        mapTemp.put(SysVariables.DOCUMENT_TITLE, documentTitle);
                        mapTemp.put(SysVariables.LEVEL, level);
                        mapTemp.put(SysVariables.NUMBER, number);
                        utilService.setPublicData(mapTemp, processInstanceId, taskList, ItemBoxTypeEnum.DOING);
                    } catch (Exception e) {
                        LOGGER.error("获取在办列表失败", e);
                    }
                    mapTemp.put("serialNumber", serialNumber + 1);
                    serialNumber += 1;
                    items.add(mapTemp);
                }
            }
            return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办列表失败", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}