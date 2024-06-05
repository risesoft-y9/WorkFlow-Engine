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

import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.todo.TodoTaskApi;
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
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/searchList")
public class MobileV1SearchListController {

    private final TodoTaskApi todotaskApi;

    private final SearchService searchService;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @RequestMapping(value = "/getLastFormData")
    public Y9Result<Map<String, Object>> getLastFormData(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String tableName) {
        Map<String, Object> map = new HashMap<String, Object>();
        Y9Page<Map<String, Object>> y9Page = this.searchService.getSearchList("", itemId, "", "", "", "", "", Integer.valueOf(1), Integer.valueOf(1));
        if ((y9Page.getRows() != null) && (y9Page.getRows().size() > 0)) {
            Map<String, Object> m = y9Page.getRows().get(0);
            String processSerialNumber = (String)m.get("processSerialNumber");
            String sql = "select * from " + tableName + " where guid = '" + processSerialNumber + "'";
            List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
            if (list.size() > 0) {
                map = list.get(0);
            }
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取个人办件统计
     *
     * @return
     */
    @RequestMapping(value = "/getMyCount")
    public Y9Result<Map<String, Object>> getMyCount() {
        Map<String, Object> map = new HashMap<String, Object>(16);
        int todoCount = 0;
        long doingCount = 0;
        int doneCount = 0;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            // 统计统一待办
            todoCount = todotaskApi.countByReceiverId(tenantId, positionId);
            // 统计流程在办件
            Map<String, Object> m = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId, "", "", "", "todo", "", "", "", 1, 1);
            doingCount = Long.parseLong(m.get("total").toString());
            // 统计历史办结件
            doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, "");
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure(50000, "获取失败");
    }

    /**
     * 获取在办列表，办结列表
     *
     * @param searchName 文件编号，标题
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 状态，todo在办，done办结
     * @param year 年度
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @param page 页面
     * @param rows 条数
     * @return
     */
    @RequestMapping(value = "/getSearchList")
    public Y9Page<Map<String, Object>> getSearchList(@RequestParam String searchName, @RequestParam String itemId, @RequestParam String userName, @RequestParam String state, @RequestParam String year, @RequestParam String startDate, @RequestParam String endDate, @RequestParam Integer page,
        @RequestParam Integer rows) {
        return searchService.getSearchList(searchName, itemId, userName, state, year, startDate, endDate, page, rows);
    }

}
