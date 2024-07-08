package net.risesoft.controller;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.ChaoSongModel;
import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.QueryListService;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author zhangchongjie
 * @date 2023/11/20
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/ddyjs/workList", produces = MediaType.APPLICATION_JSON_VALUE)
public class WorkList4ddyjsRestController {

    private final WorkList4ddyjsService workList4ddyjsService;

    private final QueryListService queryListService;

    private final Draft4PositionApi draft4PositionApi;

    private final Item4PositionApi item4PositionApi;

    /**
     * 我的传阅列表
     *
     * @param searchName 标题
     * @param itemId 事项id
     * @param userName 发送人
     * @param state 状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/chuanyueList")
    public Y9Page<ChaoSongModel> chuanyueList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.myChaoSongList(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取已办件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/doingList")
    public Y9Page<Map<String, Object>> doingList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.doingList(itemId, searchItemId, searchTerm, page, rows);
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
    @GetMapping(value = "/doneList")
    public Y9Page<Map<String, Object>> doneList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.doneList(itemId, searchItemId, searchTerm, page, rows);
    }

    /**
     * 草稿列表
     *
     * @param page 页码
     * @param rows 条数
     * @param itemId 事项id
     * @param title 搜索词
     * @return Y9Page<DraftModel>
     */
    @GetMapping(value = "/draftList")
    public Y9Page<DraftModel> draftList(@RequestParam int page, @RequestParam int rows,
        @RequestParam @NotBlank String itemId, @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
        return draft4PositionApi.getDraftListBySystemName(tenantId, positionId, page, rows, title, item.getSystemName(),
            false);
    }

    /**
     * 关注列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<OfficeFollowModel>
     */
    @GetMapping(value = "/followList")
    public Y9Page<OfficeFollowModel> followList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.followList(itemId, searchTerm, page, rows);
    }

    /**
     * 首页已传阅列表
     *
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/homeChaosongList")
    public Y9Page<ChaoSongModel> homeChaosongList(@RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.myChaoSongList("", "", "", "", "", page, rows);
    }

    /**
     * 首页我的在办事项
     *
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/homeDoingList")
    public Y9Page<Map<String, Object>> homeDoingList(@RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.homeDoingList(page, rows);
    }

    /**
     * 首页办结事项
     *
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/homeDoneList")
    public Y9Page<Map<String, Object>> homeDoneList(@RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.homeDoneList(page, rows);
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
    public Y9Page<Map<String, Object>> queryList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String state, @RequestParam(required = false) String createDate,
        @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return queryListService.queryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
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
    @GetMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return workList4ddyjsService.todoList(itemId, searchItemId, searchTerm, page, rows);
    }

}