package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.exception.GlobalErrorCodeEnum;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 草稿相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequestMapping("/mobile/v1/draft")
public class MobileDraftController {

    protected Logger log = LoggerFactory.getLogger(MobileDraftController.class);

    @Autowired
    private Draft4PositionApi draftManager;

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/delDraft")
    public Y9Result<String> delDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draftManager.deleteDraft(tenantId, ids);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 回收站计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getDeleteDraftCount")
    public Y9Result<Integer> getDeleteDraftCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draftManager.getDeleteDraftCount(tenantId, positionId, itemId);
            return Y9Result.success(count, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 草稿列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param delFlag 是否删除 true为回收站列表，false为草稿列表
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getDraft")
    public Y9Page<Map<String, Object>> getDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, boolean delFlag,
        @RequestParam Integer page, @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draftManager.getDraftList(tenantId, positionId, page, rows, title, itemId, delFlag);
            if ((boolean)map.get("success")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
                return Y9Page.success(page, Integer.valueOf(map.get("totalpage").toString()), Long.valueOf(map.get("total").toString()), list, "获取成功");
            }
        } catch (Exception e) {
            log.error("草稿箱列表异常：");
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 草稿箱计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getDraftCount")
    public Y9Result<Integer> getDraftCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draftManager.getDraftCount(tenantId, positionId, itemId);
            return Y9Result.success(count, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/reduction")
    public Y9Result<String> reduction(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String id, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draftManager.reduction(tenantId, id);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("还原成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("还原失败");
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/removeDraft")
    public Y9Result<String> removeDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draftManager.removeDraft(tenantId, ids);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }
}
