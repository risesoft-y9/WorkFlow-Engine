package y9.client.rest.processadmin;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "HistoricTaskApiClient", name = "processAdmin", url = "${y9.common.processAdminBaseUrl}",
    path = "/services/rest/historicTask")
public interface HistoricTaskApiClient extends HistoricTaskApi {

    /**
     *
     * Description: 根据流程实例id,获取任务
     *
     * @param tenantId
     * @param processInstanceId
     * @param year
     * @return
     */
    @Override
    @GetMapping("/findTaskByProcessInstanceIdOrByEndTimeAsc")
    List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrByEndTimeAsc(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("year") String year);

    /**
     *
     * Description: 根据流程实例id,开始时间升序获取
     *
     * @param tenantId
     * @param processInstanceId
     * @param year
     * @return
     */
    @Override
    @GetMapping("/findTaskByProcessInstanceIdOrderByStartTimeAsc")
    List<HistoricTaskInstanceModel> findTaskByProcessInstanceIdOrderByStartTimeAsc(
        @RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("year") String year);

    /**
     * 根据任务Id获取任务实例
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    @Override
    @GetMapping("/getById")
    HistoricTaskInstanceModel getById(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);

    /**
     *
     * Description: 根据流程实例获取所有历史任务实例
     *
     * @param tenantId
     * @param processInstanceId
     * @param year
     * @return
     */
    @Override
    @GetMapping("/getByProcessInstanceId")
    List<HistoricTaskInstanceModel> getByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("year") String year);

    /**
     *
     * Description: 根据流程实例获取所有历史任务实例-按照办结时间倒序
     *
     * @param tenantId
     * @param processInstanceId
     * @param year
     * @return
     */
    @Override
    @GetMapping("/getByProcessInstanceIdOrderByEndTimeDesc")
    List<HistoricTaskInstanceModel> getByProcessInstanceIdOrderByEndTimeDesc(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("year") String year);

    /**
     *
     * Description: 根据执行实例获取已经办理完成的任务数量
     *
     * @param tenantId
     * @param executionId
     * @return
     */
    @Override
    @GetMapping("/getFinishedCountByExecutionId")
    long getFinishedCountByExecutionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("executionId") String executionId);

    /**
     * 获取当前任务的上一个任务节点，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return HistoricTaskInstanceModel
     */
    @Override
    @GetMapping("/getThePreviousTask")
    HistoricTaskInstanceModel getThePreviousTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 获取当前任务的上一个任务节点产生的所有任务，当前任务只可以是正在运行的任务实例
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/getThePreviousTasks")
    List<HistoricTaskInstanceModel> getThePreviousTasks(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 设置历史任务TANENT_ID_字段，存放协办任务是否被强制办结标识
     *
     * @param tenantId
     * @param taskId
     */
    @Override
    @PostMapping("/setTenantId")
    void setTenantId(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId);
}
