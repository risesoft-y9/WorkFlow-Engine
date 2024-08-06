package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessTrackService;

/**
 * 获取流程历程
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/processTrack", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessHistoryRestController {

    private final ProcessTrackService processTrackService;

    /**
     * 获取历程信息
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @GetMapping(value = "/historyList")
    public Y9Result<List<HistoryProcessModel>> historyList(@RequestParam String processInstanceId) {
        List<HistoryProcessModel> items = processTrackService.listByProcessInstanceId(processInstanceId);
        return Y9Result.success(items, "获取成功");
    }

}
