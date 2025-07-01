package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 历程，流程图数据
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/processTrack", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrackRestController {

    private final ProcessTrackApi processTrackApi;

    private final ChaoSongApi chaoSongApi;

    private final RepositoryApi repositoryApi;

    /**
     * 获取流程图数据
     *
     * @param resourceType 类型
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<String>
     */
    @GetMapping(value = "/getFlowChart")
    public Y9Result<String> getFlowChart(@RequestParam(required = false) String resourceType,
        @RequestParam(required = false) String processInstanceId, @RequestParam @NotBlank String processDefinitionId) {
        try {
            return repositoryApi.getXmlByProcessInstance(Y9LoginUserHolder.getTenantId(), resourceType,
                processInstanceId, processDefinitionId);
        } catch (Exception e) {
            LOGGER.error("获取流程图失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取流程图任务节点信息
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < HistoricActivityInstanceModel>>
     */
    @GetMapping(value = "/getTaskList")
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam @NotBlank String processInstanceId) {
        try {
            return processTrackApi.getTaskList(Y9LoginUserHolder.getTenantId(), processInstanceId);
        } catch (Exception e) {
            LOGGER.error("获取流程图任务节点信息失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取历史任务数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/historyList")
    public Y9Result<Map<String, Object>> historyList(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>();
        List<HistoryProcessModel> items =
            processTrackApi.processTrackList(tenantId, positionId, processInstanceId).getData();
        int mychaosongNum =
            chaoSongApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId).getData();
        int otherchaosongNum = chaoSongApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
        map.put("rows", items);
        map.put("mychaosongNum", mychaosongNum);
        map.put("otherchaosongNum", otherchaosongNum);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取历史任务数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationType = FlowableOperationTypeEnum.BROWSE, operationName = "查看电子历程")
    @GetMapping(value = "/list")
    public Y9Result<List<HistoryProcessModel>> list(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoryProcessModel> items =
            processTrackApi.processTrackListWithActionName(tenantId, positionId, processInstanceId).getData();
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取简易历史任务数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/processList")
    public Y9Result<List<HistoryProcessModel>> processList(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return processTrackApi.processTrackList4Simple(tenantId, positionId, processInstanceId);
        } catch (Exception e) {
            LOGGER.error("获取简易历程数据失败", e);

        }
        return Y9Result.failure("获取失败");
    }

}
