package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.entity.ProcessParam;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemDataTransferService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
public class ItemDataTransferServiceImpl implements ItemDataTransferService {

    private final JdbcTemplate jdbcTemplate4Tenant;

    private final RuntimeApi runtimeApi;

    private final TaskApi taskApi;

    private final RepositoryApi repositoryApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessParamService processParamService;

    public ItemDataTransferServiceImpl(
        @Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        RuntimeApi runtimeApi,
        TaskApi taskApi,
        RepositoryApi repositoryApi,
        OrgUnitApi orgUnitApi,
        ProcessParamService processParamService) {
        this.jdbcTemplate4Tenant = jdbcTemplate4Tenant;
        this.runtimeApi = runtimeApi;
        this.taskApi = taskApi;
        this.repositoryApi = repositoryApi;
        this.orgUnitApi = orgUnitApi;
        this.processParamService = processParamService;
    }

    @Override
    public Y9Result<String> dataTransfer(String processDefinitionId, String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionId.split(":")[0]).getData();
        String latestProcessDefinitionId = processDefinition.getId();
        // 迁移所有
        if (StringUtils.isEmpty(processInstanceId)) {
            executeSql0(latestProcessDefinitionId, processDefinitionId);
        } else {// 迁移单个实例
            executeSql(latestProcessDefinitionId, processInstanceId);
        }
        return Y9Result.successMsg("迁移成功");
    }

    private void executeSql(String latestProcessDefinitionId, String processInstanceId) {
        updateTableByProcessInstanceId("ACT_HI_ACTINST", latestProcessDefinitionId, processInstanceId);
        updateTableByProcessInstanceId("ACT_HI_PROCINST", latestProcessDefinitionId, processInstanceId);
        updateTableByProcessInstanceId("ACT_HI_TASKINST", latestProcessDefinitionId, processInstanceId);
        updateTableByProcessInstanceId("ACT_RU_ACTINST", latestProcessDefinitionId, processInstanceId);
        updateTableByProcessInstanceId("ACT_RU_EXECUTION", latestProcessDefinitionId, processInstanceId);
        updateTableByProcessInstanceId("ACT_RU_TASK", latestProcessDefinitionId, processInstanceId);
    }

    /**
     * 根据流程实例ID更新表中的流程定义ID
     *
     * @param tableName 表名
     * @param latestProcessDefinitionId 新的流程定义ID
     * @param processInstanceId 流程实例ID
     */
    @SuppressWarnings("java:S2077")
    private void updateTableByProcessInstanceId(String tableName, String latestProcessDefinitionId,
        String processInstanceId) {
        String sql = "UPDATE " + tableName + " SET PROC_DEF_ID_ = ? WHERE PROC_INST_ID_ = ?";
        jdbcTemplate4Tenant.update(sql, latestProcessDefinitionId, processInstanceId);
    }

    private void executeSql0(String latestProcessDefinitionId, String processDefinitionId) {
        updateTableByProcessDefinitionId("ACT_HI_ACTINST", latestProcessDefinitionId, processDefinitionId);
        updateTableByProcessDefinitionId("ACT_HI_PROCINST", latestProcessDefinitionId, processDefinitionId);
        updateTableByProcessDefinitionId("ACT_HI_TASKINST", latestProcessDefinitionId, processDefinitionId);
        updateTableByProcessDefinitionId("ACT_RU_ACTINST", latestProcessDefinitionId, processDefinitionId);
        updateTableByProcessDefinitionId("ACT_RU_EXECUTION", latestProcessDefinitionId, processDefinitionId);
        updateTableByProcessDefinitionId("ACT_RU_TASK", latestProcessDefinitionId, processDefinitionId);
    }

    /**
     * 根据流程定义ID更新表中的流程定义ID
     *
     * @param tableName 表名
     * @param latestProcessDefinitionId 新的流程定义ID
     * @param processDefinitionId 原始流程定义ID
     */
    @SuppressWarnings("java:S2077")
    private void updateTableByProcessDefinitionId(String tableName, String latestProcessDefinitionId,
        String processDefinitionId) {
        String sql = "UPDATE " + tableName + " SET PROC_DEF_ID_ = ? WHERE PROC_DEF_ID_ = ?";
        jdbcTemplate4Tenant.update(sql, latestProcessDefinitionId, processDefinitionId);
    }

    private String getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        int i = 0;
        if (!taskList.isEmpty()) {
            for (TaskModel task : taskList) {
                String assignee = task.getAssignee();
                if (i < 5) {
                    if (StringUtils.isNotBlank(assignee)) {
                        OrgUnit personTemp = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                        if (personTemp != null) {
                            // 并行时，领导选取时存在顺序，因此这里也存在顺序
                            assigneeNames = Y9Util.genCustomStr(assigneeNames, personTemp.getName(), "、");
                            i += 1;
                        }
                    }
                }
            }
            boolean b = taskList.size() > 5;
            if (b) {
                assigneeNames += "等，共" + taskList.size() + "人";
            }
        }
        return assigneeNames;
    }

    @Override
    public Y9Page<Map<String, Object>> pageByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId,
        Integer page, Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        Y9Page<ProcessInstanceModel> piPage =
            runtimeApi.getProcessInstancesByDefId(tenantId, processDefinitionId, page, rows);
        List<ProcessInstanceModel> list = piPage.getRows();
        ObjectMapper objectMapper = new ObjectMapper();
        List<ProcessInstanceModel> pList = objectMapper.convertValue(list, new TypeReference<>() {});
        ProcessParam processParam;
        Map<String, Object> mapTemp;
        for (ProcessInstanceModel processInstance : pList) {
            mapTemp = new HashMap<>(16);
            try {
                String processInstanceId = processInstance.getId();
                mapTemp.put("processInstanceId", processInstanceId);
                mapTemp.put("startTime", Y9DateTimeUtils.formatDateTime(processInstance.getStartTime()));
                processParam = processParamService.findByProcessInstanceId(processInstanceId);
                mapTemp.put("title", StringUtils.isBlank(processParam.getTitle()) ? "未定义标题" : processParam.getTitle());
                mapTemp.put("number",
                    StringUtils.isBlank(processParam.getCustomNumber()) ? "" : processParam.getCustomNumber());
                mapTemp.put("startorName", processParam.getStartorName());
                List<TaskModel> taskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                String assigneeNames = getAssigneeIdsAndAssigneeNames(taskList);
                mapTemp.put("assigneeNames", assigneeNames);
            } catch (Exception e) {
                e.printStackTrace();
            }
            items.add(mapTemp);
        }
        return Y9Page.success(page, piPage.getTotalPages(), piPage.getTotal(), items, "获取列表成功");
    }

}
