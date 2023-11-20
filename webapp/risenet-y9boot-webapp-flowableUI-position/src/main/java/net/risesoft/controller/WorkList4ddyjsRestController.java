package net.risesoft.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.pojo.Y9Page;
import net.risesoft.service.QueryListService;
import net.risesoft.service.WorkList4ddyjsService;

/**
 * 当代中国研究所使用
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
    public Y9Page<Map<String, Object>> doingList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.doingList(itemId, searchTerm, page, rows);
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
    public Y9Page<Map<String, Object>> doneList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.doneList(itemId, searchTerm, page, rows);
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
    public Y9Page<Map<String, Object>> todoList(@RequestParam(required = true) String itemId, @RequestParam(required = false) String searchTerm, @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return workList4ddyjsService.todoList(itemId, searchTerm, page, rows);
    }

}