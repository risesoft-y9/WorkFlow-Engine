package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping(value = "/vue/processTrack")
public class ProcessTrackRestController {

    @Autowired
    private ProcessTrack4PositionApi processTrack4PositionApi;

    @Autowired
    private ChaoSong4PositionApi chaoSong4PositionApi;

    /**
     * 获取历程数据
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/historyList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> historyList(@RequestParam(required = false) String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
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
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/processList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> processList(@RequestParam(required = true) String processInstanceId) {
        Position position = Y9LoginUserHolder.getPosition();
        String positionId = position.getId(), tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            Map<String, Object> map = processTrack4PositionApi.processTrackList4Simple(tenantId, positionId, processInstanceId);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                list = (List<Map<String, Object>>)map.get("rows");
                return Y9Result.success(list, "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

}
