package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DoingService;
import net.risesoft.service.DoneService;
import net.risesoft.service.QueryListService;
import net.risesoft.service.TodoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 待办，在办，办结列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/workList")
public class WorkListRestController {

    private final TodoService todoService;

    private final DoingService doingService;

    private final DoneService doneService;

    private final ItemViewConfApi itemViewConfApi;

    private final QueryListService queryListService;

    /**
     * 获取已办件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/doingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doingList(@RequestParam String itemId, @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return doingService.listNew(itemId, searchTerm, page, rows);
    }

    /**
     * 获取在办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @RequestMapping(value = "/doingViewConf", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConfModel>> doingViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DOING.getValue());
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取办结件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/doneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doneList(@RequestParam String itemId, @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return doneService.listNew(itemId, searchTerm, page, rows);
    }

    /**
     * 获取办结列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @RequestMapping(value = "/doneViewConf", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConfModel>> doneViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DONE.getValue());
        return Y9Result.success(itemViewConfList, "获取成功");
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
    @RequestMapping(value = "/queryList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> queryList(@RequestParam String itemId, @RequestParam(required = false) String state, @RequestParam(required = false) String createDate, @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return queryListService.queryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取已办件列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/searchDoingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> searchDoingList(@RequestParam String itemId, @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return doingService.searchList(itemId, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取办结件列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/searchDoneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> searchDoneList(@RequestParam String itemId, @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return doneService.searchList(itemId, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @param searchMapStr 搜索列和值
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/searchTodoList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> searchTodoList(@RequestParam String itemId, @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return todoService.searchList(itemId, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/todoList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> todoList(@RequestParam String itemId, @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return todoService.listNew(itemId, searchTerm, page, rows);
    }

    /**
     * 获取待办列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @RequestMapping(value = "/todoViewConf", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConfModel>> todoViewConf(@RequestParam String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.TODO.getValue());
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 获取视图配置
     *
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @RequestMapping(value = "/viewConf", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConfModel>> viewConf(@RequestParam String itemId, @RequestParam(required = false) String viewType) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, viewType);
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}