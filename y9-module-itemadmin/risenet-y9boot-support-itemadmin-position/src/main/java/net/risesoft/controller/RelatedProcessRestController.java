package net.risesoft.controller;

import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.RelatedProcess;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.RelatedProcessService;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/vue/relatedProcess")
public class RelatedProcessRestController {

    private final RelatedProcessService relatedProcessService;

    /**
     * 复制权限
     *
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copyPerm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyPerm(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId) {
        // relatedProcessService.copyPerm(itemId,processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 删除权限绑定
     *
     * @param id 权限id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam(required = true) String id) {
        relatedProcessService.delete(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 事项列表
     *
     * @return
     */
    @RequestMapping(value = "/getBindItemlist", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<RelatedProcess> list(@RequestParam String parentItemId, Integer page, Integer rows) {
        Page<RelatedProcess> pageList = relatedProcessService.findAll(parentItemId, page, rows);
        return Y9Page.success(page, rows, pageList.getTotalElements(), pageList.getContent(), "获取关联流程列表成功！");
    }

    /**
     * 保存权限
     *
     * @param parentItemId 父事项id
     * @param idList
     * @return
     */
    @RequestMapping(value = "/saveBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> save(@RequestParam(required = true) String parentItemId,
        @RequestParam(name = "ids") String[] ids) {
        relatedProcessService.save(parentItemId, ids);
        return Y9Result.successMsg("保存成功");
    }

}