package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * 历史活动实例模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface HistoricActivityApi {

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoricActivityInstanceModel>>}
     */
    @GetMapping("/getByProcessInstanceId")
    Y9Result<List<HistoricActivityInstanceModel>> getByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年度
     * @return {@code Y9Result<List<HistoricActivityInstanceModel>>}
     */
    @GetMapping("/getByProcessInstanceIdAndYear")
    Y9Result<List<HistoricActivityInstanceModel>> getByProcessInstanceIdAndYear(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "year", required = false) String year);

}
