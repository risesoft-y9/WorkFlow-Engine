package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SearchService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 综合搜索
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/search")
public class SearchRestController {

    private final SearchService searchService;

    private final Item4PositionApi item4PositionApi;

    /**
     * 获取我的事项列表
     *
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getMyItemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getMyItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<Map<String, Object>> listMap = item4PositionApi.getMyItemList(tenantId, userId);
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 获取我的事项系统列表
     *
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getMyItemSystemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getMyItemSystemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemModel> listMap = item4PositionApi.getAllItemList(tenantId);
        List<Map<String, Object>> list = new ArrayList<>();
        for (ItemModel itemModel : listMap) {
            Map<String, Object> newmap = new HashMap<>(16);
            newmap.put("systemName", itemModel.getSystemName());
            newmap.put("systemCnName", itemModel.getSysLevel());
            if (!list.contains(newmap)) {
                list.add(newmap);
            }
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取个人所有件
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
    @RequestMapping(value = "/getSearchList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.getSearchList(searchName, itemId, userName, state, year, startDate, endDate, page, rows);
    }

    /**
     * 获取阅件列表
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
    @RequestMapping(value = "/getYuejianList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<ChaoSongModel> getYuejianList(@RequestParam(required = false) String searchName, @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.getYuejianList(searchName, itemId, userName, state, year, page, rows);
    }

}
