package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
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
@RequestMapping(value = "/vue/processTrack/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrack4GfgRestController {

    private final ProcessTrackApi processTrackApi;

    /**
     * 获取历史任务数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/historyList")
    public Y9Result<List<HistoryProcessModel>> historyList(@RequestParam @NotBlank String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoryProcessModel>
                items =
                processTrackApi.processTrackListWithActionName(tenantId, positionId, processInstanceId).getData();
        return Y9Result.success(items, "获取成功");
    }
}
