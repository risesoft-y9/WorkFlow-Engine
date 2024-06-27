package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/processTrack", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessTrackRestController {

    @Autowired
    private ProcessTrackApi processTrackManager;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    /**
     * 获取历程数据
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @GetMapping(value = "/historyList")
    public Y9Result<Map<String, Object>> historyList(@RequestParam(required = false) String processInstanceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        List<HistoryProcessModel> items =
            processTrackManager.processTrackList(tenantId, userId, processInstanceId).getData();
        map.put("rows", items);
        int mychaosongNum = chaoSongInfoManager.countByUserIdAndProcessInstanceId(tenantId, userId, processInstanceId);
        int otherchaosongNum = chaoSongInfoManager.countByProcessInstanceId(tenantId, userId, processInstanceId);
        map.put("mychaosongNum", mychaosongNum);
        map.put("otherchaosongNum", otherchaosongNum);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取简易历程数据
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @GetMapping(value = "/processList")
    public Y9Result<List<HistoryProcessModel>> processList(@RequestParam(required = true) String processInstanceId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String userId = userInfo.getPersonId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        return processTrackManager.processTrackList4Simple(tenantId, userId, processInstanceId);
    }

}
