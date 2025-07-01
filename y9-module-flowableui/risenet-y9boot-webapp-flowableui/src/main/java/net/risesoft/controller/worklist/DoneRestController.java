package net.risesoft.controller.worklist;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 待办，在办，办结列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/done", produces = MediaType.APPLICATION_JSON_VALUE)
public class DoneRestController {

    private final WorkList4GfgService workList4GfgService;

    private final ItemViewConfApi itemViewConfApi;

    /**
     * 获取办结件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doneList")
    public Y9Page<Map<String, Object>> doneList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.doneList(itemId, page, rows);
    }

    /**
     * 获取科室办结件列表
     *
     * @param itemId 事项id
     * @param isBureau 是否是委办局
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控本处室/司局办结列表", logLevel = FlowableLogLevelEnum.ADMIN)
    @PostMapping(value = "/doneList4Dept")
    public Y9Page<Map<String, Object>> doneList4Dept(@RequestParam String itemId, @RequestParam boolean isBureau,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.doneList4Dept(itemId, isBureau, searchMapStr, page, rows);
    }

    /**
     * 获取所有办结件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控所有办结件列表", logLevel = FlowableLogLevelEnum.ADMIN)
    @PostMapping(value = "/doneList4All")
    public Y9Page<Map<String, Object>> doneList4All(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.doneList4All(itemId, searchMapStr, page, rows);
    }

    /**
     * 获取办结列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/doneViewConf")
    public Y9Result<List<ItemViewConfModel>> doneViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DONE.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}