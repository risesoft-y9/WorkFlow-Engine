package net.risesoft.controller.worklist;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.SearchService;

/**
 * 综合搜索
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/search", produces = MediaType.APPLICATION_JSON_VALUE)
public class SearchRestController {

    private final SearchService searchService;

    /**
     * 获取个人所有件综合搜索列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/list")
    public Y9Page<Map<String, Object>> list(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.pageSearchList(searchName, itemId, userName, state, year, startDate, endDate, page, rows);
    }

    /**
     * 获取个人阅件综合搜索列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping(value = "/chaoSongList")
    public Y9Page<ChaoSongModel> chaoSongList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.pageYuejianList(searchName, itemId, userName, state, year, page, rows);
    }
}