package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

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
        return this.workList4GfgService.doingList(itemId, page, rows);
    }

    /**
     * 获取在办件列表
     *
     * @param itemId 事项id
     * @param days 过期天数
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doingList4DuBan")
    public Y9Page<Map<String, Object>> doingList4DuBan(@RequestParam String itemId, @RequestParam Integer days,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.doingList4DuBan(itemId, days, page, rows);
    }

    /**
     * 获取科室在办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doingList4Dept")
    public Y9Page<Map<String, Object>> doingList4Dept(@RequestParam String itemId, @RequestParam boolean isBureau,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.doingList4Dept(itemId, isBureau, page, rows);
    }

    /**
     * 获取所有在办件
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doingList4All")
    public Y9Page<Map<String, Object>> doingList4All(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.doingList4All(itemId, page, rows);
    }

    /**
     * 获取在办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/doingViewConf")
    public Y9Result<List<ItemViewConfModel>> doingViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = this.itemViewConfApi
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
    public Y9Page<Map<String, Object>> doneList4All(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.doneList4All(itemId, page, rows);
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
        return this.workList4GfgService.haveDoneList(itemId, page, rows);
    }

    /**
     * 获取流程序列号的所有会签流程信息
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getSignDeptDetailList")
    public Y9Result<List<Map<String, Object>>> getSignDeptDetailList(@RequestParam String processSerialNumber) {
        return this.workList4GfgService.getSignDeptDetailList(processSerialNumber);
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
        return this.workList4GfgService.allList(itemId, page, rows);
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
    @GetMapping(value = "/recycleList")
    public Y9Page<Map<String, Object>> recycleList(@RequestParam String itemId, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return this.workList4GfgService.recycleList(itemId, page, rows);
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
        return this.queryListService.pageQueryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
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
        return this.doingService.pageSearchList(itemId, tableName, searchMapStr, page, rows);
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

    /**
     * 获取待办件多条件查询列表
     *
     * @param itemId 事项id
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @PostMapping(value = "/searchTodoList")
    public Y9Page<Map<String, Object>> searchTodoList(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.todoList(itemId, searchMapStr, page, rows);
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
        return this.workList4GfgService.todoList(itemId, "", page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/todoList4TaskDefKey")
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(@RequestParam String itemId,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return this.workList4GfgService.todoList4TaskDefKey(itemId, taskDefKey, searchMapStr, page, rows);
    }

    /**
     * 获取待办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/todoViewConf")
    public Y9Result<List<ItemViewConfModel>> todoViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = this.itemViewConfApi
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
        return this.workList4GfgService.allTodoList(queryParamModel);
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
            this.itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, viewType).getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}