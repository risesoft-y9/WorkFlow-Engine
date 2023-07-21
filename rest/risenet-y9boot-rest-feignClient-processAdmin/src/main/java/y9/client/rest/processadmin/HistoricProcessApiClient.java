package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "HistoricProcessApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}", path = "/services/rest/historicProcess")
public interface HistoricProcessApiClient extends HistoricProcessApi {

    /**
     * 删除流程实例，在办件设为暂停，办结件加删除标识
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping("/deleteProcessInstance")
    boolean deleteProcessInstance(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id获取实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping("/getById")
    HistoricProcessInstanceModel getById(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id和年度获取实例
     *
     * @param tenantId 租户id
     * @param id 流程实例id
     * @param year 年份
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping("/getByIdAndYear")
    HistoricProcessInstanceModel getByIdAndYear(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id, @RequestParam("year") String year);

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param tenantId 租户id
     * @param superProcessInstanceId 父流程实例id
     * @return List&lt;HistoricProcessInstanceModel&gt;
     */
    @Override
    @GetMapping("/getBySuperProcessInstanceId")
    List<HistoricProcessInstanceModel> getBySuperProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("superProcessInstanceId") String superProcessInstanceId);

    /**
     * 根据流程实例获取父流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 父流程实例id
     * @return HistoricProcessInstanceModel
     */
    @Override
    @GetMapping("/getSuperProcessInstanceById")
    HistoricProcessInstanceModel getSuperProcessInstanceById(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 恢复流程实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping("/recoveryProcess")
    boolean recoveryProcess(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping("/removeProcess")
    boolean removeProcess(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 彻底删除流程实例，岗位
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @Override
    @PostMapping("/removeProcess4Position")
    boolean removeProcess4Position(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 设置优先级
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param priority priority
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/setPriority")
    void setPriority(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("priority") String priority) throws Exception;
}
