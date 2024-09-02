package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
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
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemDataTransferService;
import net.risesoft.service.ProcessParamService;
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

    public ItemDataTransferServiceImpl(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate4Tenant,
        RuntimeApi runtimeApi, TaskApi taskApi, RepositoryApi repositoryApi, OrgUnitApi orgUnitApi,
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

    private final void executeSql(String latestProcessDefinitionId, String processInstanceId) {
        String sql1 = "UPDATE ACT_HI_ACTINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql1);

        String sql2 = "UPDATE ACT_HI_PROCINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql2);

        String sql3 = "UPDATE ACT_HI_TASKINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql3);

        String sql4 = "UPDATE ACT_RU_ACTINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql4);

        String sql5 = "UPDATE ACT_RU_EXECUTION SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql5);

        String sql6 = "UPDATE ACT_RU_TASK SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_INST_ID_ = '" + processInstanceId + "';";
        jdbcTemplate4Tenant.execute(sql6);
    }

    private final void executeSql0(String latestProcessDefinitionId, String processDefinitionId) {
        String sql1 = "UPDATE ACT_HI_ACTINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_DEF_ID_ = '" + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql1);

        String sql2 = "UPDATE ACT_HI_PROCINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_DEF_ID_ = '" + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql2);

        String sql3 = "UPDATE ACT_HI_TASKINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_DEF_ID_ = '" + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql3);

        String sql4 = "UPDATE ACT_RU_ACTINST SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_DEF_ID_ = '" + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql4);

        String sql5 = "UPDATE ACT_RU_EXECUTION SET PROC_DEF_ID_ = '" + latestProcessDefinitionId
            + "' WHERE PROC_DEF_ID_ = '" + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql5);

        String sql6 = "UPDATE ACT_RU_TASK SET PROC_DEF_ID_ = '" + latestProcessDefinitionId + "' WHERE PROC_DEF_ID_ = '"
            + processDefinitionId + "';";
        jdbcTemplate4Tenant.execute(sql6);

    }

    private final String getAssigneeIdsAndAssigneeNames(List<TaskModel> taskList) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String assigneeNames = "";
        int i = 0;
        if (taskList.size() > 0) {
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
        List<ProcessInstanceModel> pList =
            objectMapper.convertValue(list, new TypeReference<List<ProcessInstanceModel>>() {});

        ProcessParam processParam = null;
        Map<String, Object> mapTemp = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (ProcessInstanceModel processInstance : pList) {
            mapTemp = new HashMap<>(16);
            try {
                String processInstanceId = processInstance.getId();
                mapTemp.put("processInstanceId", processInstanceId);
                mapTemp.put("startTime", sdf.format(processInstance.getStartTime()));
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
