package net.risesoft.controller.mobile.v1;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/mobile/v1/searchList")
public class MobileV1SearchListController {

    protected Logger log = LoggerFactory.getLogger(MobileV1SearchListController.class);

    @Autowired
    private TodoTaskApi todotaskApi;

    @Autowired
    private SearchService searchService;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    /**
     * 获取个人办件统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @return
     */
    @RequestMapping(value = "/getMyCount")
    public Y9Result<Map<String, Object>> getMyCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        int todoCount = 0;
        long doingCount = 0;
        int doneCount = 0;
        try {
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
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
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
    @ResponseBody
    @RequestMapping(value = "/getSearchList")
    public Y9Page<Map<String, Object>> getSearchList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String searchName, @RequestParam(required = false) String itemId,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String state, @RequestParam(required = false) String year, @RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate, @RequestParam(required = true) Integer page,
        @RequestParam(required = true) Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return searchService.getSearchList(searchName, itemId, userName, state, year, startDate, endDate, page, rows);
    }

}
