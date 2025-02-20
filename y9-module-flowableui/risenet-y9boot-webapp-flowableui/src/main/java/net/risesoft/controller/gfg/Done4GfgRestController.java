package net.risesoft.controller.gfg;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DoingService;
import net.risesoft.service.DoneService;
import net.risesoft.service.QueryListService;
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
@RequestMapping(value = "/vue/done/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class Done4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    private final DoingService doingService;

    private final DoneService doneService;

    private final ItemViewConfApi itemViewConfApi;

    private final QueryListService queryListService;

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
        return this.workList4GfgService.doneList(itemId, page, rows);
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
    @GetMapping(value = "/doneList4Dept")
    public Y9Page<Map<String, Object>> doneList4Dept(@RequestParam String itemId, @RequestParam boolean isBureau,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.doneList4Dept(itemId, isBureau, page, rows);
    }

    /**
     * 获取科室办结件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doneList4All")
    public Y9Page<Map<String, Object>> doneList4All(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.doneList4All(itemId, searchMapStr, page, rows);
    }

    /**
     * 获取办结列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/doneViewConf")
    public Y9Result<List<ItemViewConfModel>> doneViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = this.itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DONE.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取办结件多条件查询列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/searchDoneList")
    public Y9Page<Map<String, Object>> searchDoneList(@RequestParam String itemId,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.doneService.pageSearchList(itemId, tableName, searchMapStr, page, rows);
    }
}