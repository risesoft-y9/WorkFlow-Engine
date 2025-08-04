package net.risesoft.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.opinion.OpinionFrame;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.opinion.OpinionFrameService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/opinionFrame", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpinionFrameRestController {

    private final OpinionFrameService opinionFrameService;

    /**
     * 获取意见框
     *
     * @param id 意见框id
     * @return
     */
    @GetMapping(value = "/getOpinionFrame")
    public Y9Result<OpinionFrame> getOpinionFrame(@RequestParam String id) {
        OpinionFrame opinionFrame = opinionFrameService.getById(id);
        return Y9Result.success(opinionFrame, "获取成功");
    }

    /**
     * 获取意见框列表
     *
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @GetMapping(value = "/list")
    public Y9Page<OpinionFrame> list(@RequestParam Integer page, @RequestParam Integer rows) {
        Page<OpinionFrame> pageList = opinionFrameService.pageAll(page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 获取绑定的意见框列表
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @GetMapping(value = "/list4NotUsed")
    public Y9Page<OpinionFrame> list4NotUsed(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam int page, @RequestParam int rows) {
        Page<OpinionFrame> pageList =
            opinionFrameService.pageAllNotUsed(itemId, processDefinitionId, taskDefKey, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 移除意见框，同时将流程中绑定的意见框一并移除
     *
     * @param ids 意见框ids
     * @return
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        opinionFrameService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存意见框
     *
     * @param opinionFrame 意见框信息
     * @return
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(OpinionFrame opinionFrame) {
        opinionFrameService.saveOrUpdate(opinionFrame);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 根据关键字查找意见
     *
     * @param page 页码
     * @param rows 条数
     * @param keyword 意见框名称
     * @return
     */
    @GetMapping(value = "/search")
    public Y9Page<OpinionFrame> search(@RequestParam Integer page, @RequestParam Integer rows,
        @RequestParam(required = false) String keyword) {
        Page<OpinionFrame> pageList = opinionFrameService.search(page, rows, keyword);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 根据关键字查找意见
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @param page 页码
     * @param rows 条数
     * @param keyword 意见名称
     * @return
     */
    @GetMapping(value = "/search4NotUsed")
    public Y9Page<OpinionFrame> search4NotUsed(@RequestParam String itemId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam int page, @RequestParam int rows,
        @RequestParam(required = false) String keyword) {
        Page<OpinionFrame> pageList =
            opinionFrameService.search4NotUsed(itemId, processDefinitionId, taskDefKey, page, rows, keyword);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }
}
