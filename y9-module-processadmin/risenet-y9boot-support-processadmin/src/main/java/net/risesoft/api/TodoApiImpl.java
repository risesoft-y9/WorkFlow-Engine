package net.risesoft.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.service.CustomDoingService;
import net.risesoft.service.CustomDoneService;
import net.risesoft.service.CustomTodoService;
import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * 待办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/processTodo")
public class TodoApiImpl implements ProcessTodoApi {

    @Autowired
    private CustomTodoService customTodoService;

    @Autowired
    private CustomDoingService customDoingService;

    @Autowired
    private CustomDoneService customDoneService;

    /**
     * 根据人员idd获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCountByUserId(@RequestParam String tenantId, @RequestParam String userId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        long todoCount = customTodoService.getCountByUserId(userId);
        long doingCount = customDoingService.getCountByUserId(userId);
        long doneCount = customDoneService.getCountByUserId(userId);
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        map.put("doneCount", doneCount);
        return map;
    }

    /**
     * 根据人员id，流程定义Key获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getCountByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCountByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        long todoCount = customTodoService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        long doingCount = customDoingService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        return map;
    }

    /**
     * 根据人员id，系统标识获取对应事项的办件统计（包括待办件，在办件，办结件）
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getCountByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        long todoCount = customTodoService.getCountByUserIdAndSystemName(userId, systemName);
        long doingCount = customDoingService.getCountByUserIdAndSystemName(userId, systemName);
        long doneCount = customDoneService.getCountByUserIdAndSystemName(userId, systemName);
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        map.put("doneCount", doneCount);
        return map;
    }

    /**
     * 根据人员Id获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserId(@RequestParam String tenantId, @RequestParam String userId, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getListByUserId(userId, page, rows);
    }

    /**
     * 根据人员Id，流程定义Key获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(processDefinitionKey) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or processDefinitionKey or userId is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, page, rows);
    }

    /**
     * 根据人员Id,系统标识获取用户的待办任务(分页)
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(systemName) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or systemName or userId is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getListByUserIdAndSystemName(userId, systemName, page, rows);
    }

    /**
     * 根据岗位id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processDefinitionKey 流程定义key
     * @return long
     */
    @Override
    @GetMapping(value = "/getTodoCountByPositionIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getTodoCountByPositionIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String positionId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getCountByUserIdAndProcessDefinitionKey(positionId, processDefinitionKey);
    }

    /**
     * 根据人员id获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @return long
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getTodoCountByUserId(@RequestParam String tenantId, @RequestParam String userId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getCountByUserId(userId);
    }

    /**
     * 根据人员id,流程定义key获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义key
     * @return long
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getTodoCountByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
    }

    /**
     * 根据人员id，系统标识获取对应事项的待办数量
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统名称
     * @return long
     */
    @Override
    @GetMapping(value = "/getTodoCountByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public long getTodoCountByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customTodoService.getCountByUserIdAndSystemName(userId, systemName);
    }

    /**
     * 条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserId(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> m = customTodoService.searchListByUserId(userId, searchTerm, page, rows);
        return m;
    }

    /**
     * 根据流程定义Key条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> m = customTodoService.searchListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, searchTerm, page, rows);
        return m;
    }

    /**
     * 根据系统英文名称条件搜索待办件
     *
     * @param tenantId 租户Id
     * @param userId 人员Id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName, @RequestParam String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Map<String, Object> m = customTodoService.searchListByUserIdAndSystemName(userId, systemName, searchTerm, page, rows);
        return m;
    }
}
