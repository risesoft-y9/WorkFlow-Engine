package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.DoneApi;
import net.risesoft.service.CustomDoneService;
import net.risesoft.service.FlowableTenantInfoHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 办结件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/done")
public class DoneApiImpl implements DoneApi {

    private final CustomDoneService customDoneService;

    /**
     * 根据人员Id获取用户的办结流程列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
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
        return customDoneService.getListByUserId(userId, page, rows);
    }

    /**
     * 根据人员Id,事项ID获取用户的办结流程列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndProcessDefinitionKey(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String processDefinitionKey, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId or processDefinitionKey is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoneService.getListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, page, rows);
    }

    /**
     * 根据人员Id,系统标识获取用户的办结流程列表(分页,包含流程变量)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统名称
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/getListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getListByUserIdAndSystemName(@RequestParam String tenantId, @RequestParam String userId, @RequestParam String systemName, @RequestParam Integer page, @RequestParam Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId or systemName is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoneService.getListByUserIdAndSystemName(userId, systemName, page, rows);
    }

    /**
     * 条件搜索办结件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/searchListByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserId(String tenantId, String userId, String searchTerm, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoneService.searchListByUserId(userId, searchTerm, page, rows);
    }

    /**
     * 根据流程定义key条件搜索办结件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndProcessDefinitionKey", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String tenantId, String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId or processDefinitionKey is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoneService.searchListByUserIdAndProcessDefinitionKey(userId, processDefinitionKey, searchTerm, page, rows);
    }

    /**
     * 根据系统标识条件搜索办结件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统英文名称
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return Map<String, Object>
     * @throws Exception Exception
     */
    @Override
    @GetMapping(value = "/searchListByUserIdAndSystemName", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> searchListByUserIdAndSystemName(String tenantId, String userId, String systemName, String searchTerm, Integer page, Integer rows) throws Exception {
        if (StringUtils.isEmpty(tenantId) || StringUtils.isEmpty(userId)) {
            throw new Exception("tenantId or userId or processDefinitionKey is null !");
        }
        FlowableTenantInfoHolder.setTenantId(tenantId);
        return customDoneService.searchListByUserIdAndSystemName(userId, systemName, searchTerm, page, rows);
    }
}
