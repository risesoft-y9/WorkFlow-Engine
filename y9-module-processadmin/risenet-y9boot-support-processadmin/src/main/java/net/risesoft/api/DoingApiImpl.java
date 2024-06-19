package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.DoingApi;
import net.risesoft.service.CustomDoingService;
import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * 在办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/doing")
public class DoingApiImpl implements DoingApi {

    private final CustomDoingService customDoingService;

    private final HistoryService historyService;

    /**
     * 根据人员id获取在办件统计
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getCountByUserId(@RequestParam String tenantId, @RequestParam String userId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.getCountByUserId(userId);
    }

    /**
     * 根据人员Id获取用户的在办任务(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserId(@RequestParam String tenantId, @RequestParam String userId, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.getListByUserId(userId, page, rows);
    }

    /**
     * 根据人员Id,事项ID获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.getListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, page, rows);
    }

    /**
     * 获取已办件列表，按办理的时间排序
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义key
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKeyOrderBySendTime", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndProcessDefinitionKeyOrderBySendTime(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> returnMap = new HashMap<>(16);
        List<Map<String, Object>> resList = new ArrayList<>();
        int totalCount;
        // 已办件，以办理时间排序，即发送出去的时间
        List<HistoricTaskInstance> htiList;
        String sql = "SELECT p.* from (" + " SELECT" + "	t.*" + " FROM" + "	ACT_HI_TASKINST t" + " LEFT JOIN ACT_HI_PROCINST p ON t.PROC_INST_ID_ = p.PROC_INST_ID_" + " WHERE" + "	t.PROC_DEF_ID_ LIKE '" + processDefinitionKey + "%'" + " AND p.END_TIME_ IS NULL" + " AND t.END_TIME_ IS NOT NULL"
            + " AND p.DELETE_REASON_ IS NULL" + " AND (" + "	t.ASSIGNEE_ = '" + userId + "'" + "	OR t.OWNER_ = '" + userId + "'" + " )" + " AND NOT EXISTS (" + "	SELECT" + "		ID_" + "	FROM" + "		ACT_HI_VARINST" + "	WHERE" + "		NAME_ = '" + userId + "'"
            + "	AND t.PROC_INST_ID_ = PROC_INST_ID_" + " )" + " ORDER BY t.END_TIME_ desc LIMIT 1000000" + " ) p" + " GROUP BY p.PROC_INST_ID_ ORDER BY p.END_TIME_ desc";
        htiList = historyService.createNativeHistoricTaskInstanceQuery().sql(sql).listPage((page - 1) * rows, rows);
        for (HistoricTaskInstance hpi : htiList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("processInstanceId", hpi.getProcessInstanceId());
            map.put("processDefinitionId", hpi.getProcessDefinitionId());
            map.put("endTime", hpi.getEndTime());
            resList.add(map);
        }
        String countSql = "select COUNT(RES.ID_) from ACT_HI_PROCINST RES WHERE RES.PROC_DEF_ID_ like #{processDefinitionKey} and RES.END_TIME_ IS NULL and RES.DELETE_REASON_ IS NULL "
            + "and (exists(select LINK.USER_ID_ from ACT_HI_IDENTITYLINK LINK where USER_ID_ = #{USER_ID_} and LINK.PROC_INST_ID_ = RES.ID_) ) and NOT EXISTS (select ID_ from ACT_HI_VARINST where NAME_ = #{USER_ID_} and RES.PROC_INST_ID_ = PROC_INST_ID_)";

        totalCount = (int)historyService.createNativeHistoricProcessInstanceQuery().sql(countSql).parameter("processDefinitionKey", processDefinitionKey + "%").parameter("USER_ID_", userId).count();
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("currpage", page);
        returnMap.put("rows", resList);
        return returnMap;
    }

    /**
     * 根据人员Id,系统标识获取用户的在办列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.getListByUserIdAndSystemName(userId, systemName, page, rows);
    }

    /**
     * 条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserId(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.searchListByUserId(userId, searchTerm, page, rows);
    }

    /**
     * 根据流程定义key条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.searchListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, searchTerm, page, rows);
    }

    /**
     * 根据系统名条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 英文系统名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName, @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoingService.searchListByUserIdAndSystemName(userId, systemName, searchTerm, page, rows);
    }
}
