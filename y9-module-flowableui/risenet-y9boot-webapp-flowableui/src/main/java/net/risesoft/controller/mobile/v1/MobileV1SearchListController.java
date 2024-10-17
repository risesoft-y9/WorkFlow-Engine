package net.risesoft.controller.mobile.v1;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.extend.ItemTodoTaskApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SearchService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 办件列表相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/searchList")
public class MobileV1SearchListController {

    private final ItemTodoTaskApi todotaskApi;

    private final SearchService searchService;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    /**
     * 获取上次的表单数据
     *
     * @param itemId 事项id
     * @param tableName 表名
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getLastFormData")
    public Y9Result<Map<String, Object>> getLastFormData(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String tableName) {
        Map<String, Object> map = new HashMap<>();
        Y9Page<Map<String, Object>> y9Page = this.searchService.pageSearchList("", itemId, "", "", "", "", "", 1, 1);
        if ((y9Page.getRows() != null) && (!y9Page.getRows().isEmpty())) {
            Map<String, Object> m = y9Page.getRows().get(0);
            String processSerialNumber = (String)m.get("processSerialNumber");
            String sql = "select * from " + tableName + " where guid = '" + processSerialNumber + "'";
            List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
            if (!list.isEmpty()) {
                map = list.get(0);
            }
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取个人办件统计
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getMyCount")
    public Y9Result<Map<String, Object>> getMyCount() {
        Map<String, Object> map = new HashMap<>(16);
        int todoCount;
        long doingCount;
        int doneCount;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            // 统计统一待办
            todoCount = todotaskApi.countByReceiverId(tenantId, positionId).getData();
            // 统计流程在办件
            Y9Page<OfficeDoneInfoModel> y9Page =
                officeDoneInfoApi.searchAllByUserId(tenantId, positionId, "", "", "", "todo", "", "", "", 1, 1);
            doingCount = y9Page.getTotal();
            // 统计历史办结件
            doneCount = officeDoneInfoApi.countByUserId(tenantId, positionId, "").getData();
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取失败", e);
        }
        return Y9Result.failure(50000, "获取失败");
    }

    /**
     * 获取在办列表，办结列表
     *
     * @param searchName 文件编号，标题
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 状态，todo(未办结)，done(办结)
     * @param year 年度
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页面
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/getSearchList")
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return searchService.pageSearchList(searchName, itemId, userName, state, year, startDate, endDate, page, rows);
    }

}
