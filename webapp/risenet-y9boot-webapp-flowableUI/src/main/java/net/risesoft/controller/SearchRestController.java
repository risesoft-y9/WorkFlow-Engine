package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SearchService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/search")
public class SearchRestController {

    @Autowired
    private SearchService searchService;

    @Autowired
    private ItemApi itemManager;

    /**
     * 获取公务邮件列表
     *
     * @param title 搜索词
     * @param userName 姓名
     * @param fileType 文件夹类型
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/getEmailList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getEmailList(@RequestParam(required = false) String title,
        @RequestParam(required = false) String userName, @RequestParam(required = false) Integer fileType,
        @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return searchService.getEmailList(page, rows, startDate, endDate, fileType, userName, title);
    }

    /**
     * 获取我的事项列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMyItemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getMyItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<Map<String, Object>> listMap = itemManager.getMyItemList(tenantId, userId);
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 获取个人所有件
     *
     * @param searchName 搜索词
     * @param itemName 事项名称
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getSearchList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemName, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return searchService.getSearchList(searchName, itemName, userName, state, year, page, rows);
    }

    /**
     * 获取阅件列表
     *
     * @param searchName 搜索词
     * @param itemName 事项名称
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getYuejianList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getYuejianList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemName, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = true) Integer page, @RequestParam(required = true) Integer rows) {
        return searchService.getYuejianList(searchName, itemName, userName, state, year, page, rows);
    }

}
