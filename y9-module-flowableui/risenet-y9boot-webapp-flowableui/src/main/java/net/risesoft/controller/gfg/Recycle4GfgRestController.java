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
@RequestMapping(value = "/vue/recycle/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class Recycle4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    private final ItemViewConfApi itemViewConfApi;

    /**
     * 获取在办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/recycleViewConf")
    public Y9Result<List<ItemViewConfModel>> recycleViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = this.itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DRAFT.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取回收站列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/recycleList")
    public Y9Page<Map<String, Object>> recycleList(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.recycleList(itemId, searchMapStr, page, rows);
    }

    /**
     * 获取回收站列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/recycleList4Dept")
    public Y9Page<Map<String, Object>> recycleList4Dept(@RequestParam String itemId, @RequestParam boolean isBureau,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.recycleList4Dept(itemId, isBureau, page, rows);
    }

    /**
     * 获取回收站列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/recycleList4All")
    public Y9Page<Map<String, Object>> recycleList4All(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.recycleList4All(itemId, page, rows);
    }
}