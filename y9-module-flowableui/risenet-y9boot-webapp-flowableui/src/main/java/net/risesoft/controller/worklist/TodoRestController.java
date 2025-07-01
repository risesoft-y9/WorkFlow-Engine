package net.risesoft.controller.worklist;

import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.model.itemadmin.QueryParamModel;
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
@RequestMapping(value = "/vue/todo/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class TodoRestController {

    private final WorkList4GfgService workList4GfgService;

    private final ItemViewConfApi itemViewConfApi;

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param searchMapStr 查询参数
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "待办列表")
    @PostMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestParam String itemId,
        @RequestParam(required = false) String searchMapStr, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList(itemId, searchMapStr, page, rows);
    }

    @FlowableLog(operationName = "待办列表-其他")
    @PostMapping(value = "/todoList4Other")
    public Y9Page<Map<String, Object>> todoList4Other(@RequestParam String itemId, @RequestParam String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList4Other(itemId, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @FlowableLog(operationName = "待办列表-指定节点")
    @RequestMapping(value = "/todoList4TaskDefKey")
    public Y9Page<Map<String, Object>> todoList4TaskDefKey(@RequestParam String itemId,
        @RequestParam(required = false) String taskDefKey, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4GfgService.todoList4TaskDefKey(itemId, taskDefKey, searchMapStr, page, rows);
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
    @FlowableLog(operationName = "我的待办")
    @GetMapping(value = "/allTodoList")
    public Y9Page<Map<String, Object>> allTodoList(@Valid QueryParamModel queryParamModel) {
        return workList4GfgService.allTodoList(queryParamModel);
    }
}