package net.risesoft.api.processadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.pojo.Y9Page;

/**
 * 在办件列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessDoingApi {

    /**
     * 获取已办件列表，按办理的时间排序
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义key
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getListByUserIdAndProcessDefinitionKeyOrderBySendTime")
    Y9Page<ProcessInstanceModel> getListByUserIdAndProcessDefinitionKeyOrderBySendTime(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 根据流程定义key条件搜索在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processDefinitionKey 流程定义Key
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 行数
     * @return {@code Y9Page<ProcessInstanceModel>} 通用请求返回对象 - data 在办列表
     * @since 9.6.6
     */
    @GetMapping(value = "/searchListByUserIdAndProcessDefinitionKey")
    Y9Page<ProcessInstanceModel> searchListByUserIdAndProcessDefinitionKey(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam(value = "searchTerm", required = false) String searchTerm, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);
}
