package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessTrackService;

/**
 * 获取流程历程
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@RestController
@RequestMapping(value = "/vue/processTrack")
public class ProcessHistoryRestController {

    @Autowired
    private ProcessTrackService processTrackService;

    /**
     * 获取历程信息
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/historyList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> historyList(@RequestParam(required = true) String processInstanceId) {
        List<Map<String, Object>> items = processTrackService.getListMap(processInstanceId);
        return Y9Result.success(items, "获取成功");
    }

}
