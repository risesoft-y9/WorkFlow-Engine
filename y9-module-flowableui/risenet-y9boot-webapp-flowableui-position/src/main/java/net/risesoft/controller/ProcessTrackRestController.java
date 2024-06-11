package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import javax.validation.constraints.NotBlank;

import java.util.List;
import java.util.Map;

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
@RequestMapping(value = "/vue/processTrack")
public class ProcessTrackRestController {

    private final ProcessTrack4PositionApi processTrack4PositionApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final RepositoryApi repositoryApi;

    /**
     * 获取流程图
     *
     * @param resourceType        类型
     * @param processInstanceId   流程实例id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/getFlowChart", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<String> getFlowChart(@RequestParam String resourceType, @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String processDefinitionId) {
        try {
            return repositoryApi.getXmlByProcessInstance(Y9LoginUserHolder.getTenantId(), resourceType, processInstanceId, processDefinitionId);
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
    @RequestMapping(value = "/getTaskList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam @NotBlank String processInstanceId) {
        try {
            return processTrack4PositionApi.getTaskList(Y9LoginUserHolder.getTenantId(), processInstanceId);
        } catch (Exception e) {
            LOGGER.error("获取流程图任务节点信息失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取历程数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/historyList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> historyList(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map;
        map = processTrack4PositionApi.processTrackList(tenantId, positionId, processInstanceId);
        int mychaosongNum = chaoSong4PositionApi.countByUserIdAndProcessInstanceId(tenantId, positionId, processInstanceId);
        int otherchaosongNum = chaoSong4PositionApi.countByProcessInstanceId(tenantId, positionId, processInstanceId);
        map.put("mychaosongNum", mychaosongNum);
        map.put("otherchaosongNum", otherchaosongNum);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取简易历程数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/processList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> processList(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list;
        try {
            Map<String, Object> map = processTrack4PositionApi.processTrackList4Simple(tenantId, positionId, processInstanceId);
            if ((boolean) map.get(UtilConsts.SUCCESS)) {
                list = (List<Map<String, Object>>) map.get("rows");
                return Y9Result.success(list, "获取成功");
            }
        } catch (Exception e) {
            LOGGER.error("获取简易历程数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

}
