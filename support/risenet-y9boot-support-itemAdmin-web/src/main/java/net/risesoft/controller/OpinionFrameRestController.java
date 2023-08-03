package net.risesoft.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.entity.OpinionFrame;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OpinionFrameService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/opinionFrame")
public class OpinionFrameRestController {

    @Autowired
    private OpinionFrameService opinionFrameService;

    /**
     * 获取意见框
     *
     * @param id 意见框id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getOpinionFrame", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<OpinionFrame> getOpinionFrame(@RequestParam(required = true) String id) {
        OpinionFrame opinionFrame = opinionFrameService.findOne(id);
        return Y9Result.success(opinionFrame, "获取成功");
    }

    /**
     * 获取意见框列表
     *
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<OpinionFrame> list(@RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        Page<OpinionFrame> pageList = opinionFrameService.findAll(page, rows);
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
    @ResponseBody
    @RequestMapping(value = "/list4NotUsed", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<OpinionFrame> list4NotUsed(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = true) int page, @RequestParam(required = true) int rows) {
        Page<OpinionFrame> pageList =
            opinionFrameService.findAllNotUsed(itemId, processDefinitionId, taskDefKey, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 移除意见框，同时将流程中绑定的意见框一并移除
     *
     * @param ids 意见框ids
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> remove(@RequestParam(required = true) String[] ids) {
        opinionFrameService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存意见框
     *
     * @param opinionFrame 意见框信息
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
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
    @RequestMapping(value = "/search", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<OpinionFrame> search(@RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows, @RequestParam(required = false) String keyword) {
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
    @RequestMapping(value = "/search4NotUsed", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Page<OpinionFrame> search4NotUsed(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey,
        @RequestParam(required = true) int page, @RequestParam(required = true) int rows,
        @RequestParam(required = false) String keyword) {
        Page<OpinionFrame> pageList =
            opinionFrameService.search4NotUsed(itemId, processDefinitionId, taskDefKey, page, rows, keyword);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }
}
