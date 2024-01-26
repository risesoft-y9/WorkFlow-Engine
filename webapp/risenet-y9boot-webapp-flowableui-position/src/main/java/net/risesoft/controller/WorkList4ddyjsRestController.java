package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.QueryListService;
import net.risesoft.service.WorkList4ddyjsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 *
 * @author zhangchongjie
 * @date 2023/11/20
 */
@RestController
@RequestMapping(value = "/vue/ddyjs/workList")
public class WorkList4ddyjsRestController {

    @Autowired
    private WorkList4ddyjsService workList4ddyjsService;

    @Autowired
    private QueryListService queryListService;

    @Autowired
    private Draft4PositionApi draft4PositionApi;

    @Autowired
    private Item4PositionApi item4PositionApi;

    /**
     * 我的传阅列表
     *
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/chuanyueList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> chuanyueList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.myChaoSongList(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取已办件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/doingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doingList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.doingList(itemId, searchItemId, searchTerm, page, rows);
    }

    /**
     * 获取办结件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/doneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> doneList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.doneList(itemId, searchItemId, searchTerm, page, rows);
    }

    /**
     * 草稿列表
     *
     * @param page
     * @param rows
     * @param itemId
     * @param title
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/draftList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> draftList(@RequestParam(required = true) int page, @RequestParam(required = true) int rows, @RequestParam(required = true) String itemId, @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
        Map<String, Object> map = draft4PositionApi.getDraftListBySystemName(tenantId, positionId, page, rows, title, item.getSystemName(), false);
        List<Map<String, Object>> draftList = (List<Map<String, Object>>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()), Integer.parseInt(map.get("total").toString()), draftList, "获取列表成功");
    }

    /**
     * 关注列表
     *
     * @param itemId
     * @param searchTerm
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/followList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> followList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.followList(itemId, searchTerm, page, rows);
    }

    /**
     * 首页已传阅列表
     *
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/homeChaosongList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> homeChaosongList(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.myChaoSongList("", "", "", "", "", page, rows);
    }

    /**
     * 首页我的在办事项
     *
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/homeDoingList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> homeDoingList(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.homeDoingList(page, rows);
    }

    /**
     * 首页办结事项
     *
     * @param page
     * @param rows
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/homeDoneList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> homeDoneList(@RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
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
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/queryList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> queryList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String state, @RequestParam(required = false) String createDate, @RequestParam(required = false) String tableName, @RequestParam(required = false) String searchMapStr,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return queryListService.queryList(itemId, state, createDate, tableName, searchMapStr, page, rows);
    }

    /**
     * 获取待办件列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/todoList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> todoList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchItemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.todoList(itemId, searchItemId, searchTerm, page, rows);
    }

}