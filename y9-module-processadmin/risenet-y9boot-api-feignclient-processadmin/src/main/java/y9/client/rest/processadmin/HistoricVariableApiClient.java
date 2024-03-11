package y9.client.rest.processadmin;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "HistoricVariableApiClient", name = "${y9.service.processAdmin.name:processAdmin}",
    url = "${y9.service.processAdmin.directUrl:}",
    path = "/${y9.service.processAdmin.name:processAdmin}/services/rest/historicVariable")
public interface HistoricVariableApiClient extends HistoricVariableApi {

    /**
     * 根据流程实例Id,获取历史流程变量集合
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return List&lt;HistoricVariableInstanceModel&gt;
     */
    @Override
    @GetMapping("/getByProcessInstanceId")
    List<HistoricVariableInstanceModel> getByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 根据流程实例Id和流程变量的Key,获取历史流程变量的值
     * 
     * @param tenantId
     * @param processInstanceId
     * @param variableName
     * @param year
     * @return
     */
    @Override
    @GetMapping("/getByProcessInstanceIdAndVariableName")
    HistoricVariableInstanceModel getByProcessInstanceIdAndVariableName(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("variableName") String variableName,
        @RequestParam("year") String year);

    /**
     * 根据流程实例Id,获取历史任务变量的值集合
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;HistoricVariableInstanceModel&gt;
     */
    @Override
    @GetMapping("/getByTaskId")
    List<HistoricVariableInstanceModel> getByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 
     * Description: 根据流程实例Id和流程变量的Key,获取历史任务变量的值
     * 
     * @param tenantId
     * @param taskId
     * @param variableName
     * @param year
     * @return
     */
    @Override
    @GetMapping("/getByTaskIdAndVariableName")
    HistoricVariableInstanceModel getByTaskIdAndVariableName(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("variableName") String variableName,
        @RequestParam("year") String year);

    /**
     * 根据流程实例Id,获取指定的流程变量
     *
     * @param tenantId
     * @param processInstanceId
     * @param keys
     * @return
     */
    @Override
    @GetMapping(value = "/getVariables", consumes = MediaType.APPLICATION_JSON_VALUE)
    Map<String, Object> getVariables(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestBody Collection<String> keys);
}
