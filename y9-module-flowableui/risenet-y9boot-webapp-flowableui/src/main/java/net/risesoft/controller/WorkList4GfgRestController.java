package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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
import net.risesoft.model.itemadmin.QueryParamModel;
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
@RequestMapping(value = "/vue/workList/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkList4GfgRestController {

    private final WorkList4GfgService workList4GfgService;

    private final DoingService doingService;

    private final DoneService doneService;

    private final ItemViewConfApi itemViewConfApi;

    private final QueryListService queryListService;

    /**
     * 获取在办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doingList")
    public Y9Page<Map<String, Object>> doingList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.doingList(itemId, page, rows);
    }

    /**
     * 获取在办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/doingViewConf")
    public Y9Result<List<ItemViewConfModel>> doingViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DOING.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

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
     * 获取已办（包括在办、办结）列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/haveDoneList")
    public Y9Page<Map<String, Object>> haveDoneList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.haveDoneList(itemId, page, rows);
    }

    /**
     * 获取所有本人经手件的列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/allList")
    public Y9Page<Map<String, Object>> allList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.allList(itemId, page, rows);
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

    /**
     * 获取在办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/recycleViewConf")
    public Y9Result<List<ItemViewConfModel>> recycleViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
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
    @GetMapping(value = "/recycleList")
    public Y9Page<Map<String, Object>> recycleList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.recycleList(itemId, page, rows);
    }

    /**
     * 综合查询
     *
     * @param itemId 事项id
     * @param state 状态
     * @param createDate 日期
     * @param tableName 表名
     * @param searchMapStr 查询字段信息
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/queryList")
    public Y9Page<Map<String, Object>> queryList(@RequestParam String itemId,
        @RequestParam(required = false) String state, @RequestParam(required = false) String createDate,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return queryListService.pageQueryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取已办件多条件查询列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/searchDoingList")
    public Y9Page<Map<String, Object>> searchDoingList(@RequestParam String itemId,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return doingService.pageSearchList(itemId, tableName, searchMapStr, page, rows);
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
        return doneService.pageSearchList(itemId, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取待办件多条件查询列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/searchTodoList")
    public Y9Page<Map<String, Object>> searchTodoList(@RequestParam String itemId,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.pageSearchList(itemId, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return workList4GfgService.todoList(itemId, page, rows);
    }

    /**
     * 获取待办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/todoViewConf")
    public Y9Result<List<ItemViewConfModel>> todoViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.TODO.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/allTodoList")
    public Y9Page<Map<String, Object>> allTodoList(@Valid QueryParamModel queryParamModel) {
        return workList4GfgService.allTodoList(queryParamModel);
    }

    /**
     * 根据事项id和视图类型获取视图配置
     *
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/viewConf")
    public Y9Result<List<ItemViewConfModel>> viewConf(@RequestParam String itemId,
        @RequestParam(required = false) String viewType) {
        List<ItemViewConfModel> itemViewConfList =
            itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, viewType).getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}