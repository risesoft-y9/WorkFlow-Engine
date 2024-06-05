package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

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
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/draft")
public class MobileV1DraftController {

    private final Draft4PositionApi draft4PositionApi;

    /**
     * 彻底删除草稿
     *
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/delDraft")
    public Y9Result<String> delDraft(@RequestParam @NotBlank String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            map = draft4PositionApi.deleteDraft(tenantId, ids);
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
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getDeleteDraftCount")
    public Y9Result<Integer> getDeleteDraftCount(@RequestParam @NotBlank String itemId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Integer count = draft4PositionApi.getDeleteDraftCount(tenantId, positionId, itemId);
            return Y9Result.success(count, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 草稿列表
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param delFlag 是否删除 true为回收站列表，false为草稿列表
     * @param page 页码
     * @param rows 行数
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getDraft")
    public Y9Page<Map<String, Object>> getDraft(@RequestParam @NotBlank String itemId, @RequestParam String title, boolean delFlag, @RequestParam @NotBlank Integer page, @RequestParam @NotBlank Integer rows) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            map = draft4PositionApi.getDraftList(tenantId, positionId, page, rows, title, itemId, delFlag);
            if ((boolean)map.get("success")) {
                List<Map<String, Object>> list = (List<Map<String, Object>>)map.get("rows");
                return Y9Page.success(page, Integer.valueOf(map.get("totalpage").toString()), Long.valueOf(map.get("total").toString()), list, "获取成功");
            }
        } catch (Exception e) {
            LOGGER.error("草稿箱列表异常：");
            e.printStackTrace();
        }
        return Y9Page.failure(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取失败", GlobalErrorCodeEnum.FAILURE.getCode());
    }

    /**
     * 草稿箱计数
     *
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getDraftCount")
    public Y9Result<Integer> getDraftCount(@RequestParam @NotBlank String itemId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Integer count = draft4PositionApi.getDraftCount(tenantId, positionId, itemId);
            return Y9Result.success(count, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 还原草稿
     *
     * @param id 草稿id
     */
    @RequestMapping(value = "/reduction")
    public Y9Result<String> reduction(@RequestParam @NotBlank String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            map = draft4PositionApi.reduction(tenantId, id);
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
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/removeDraft")
    public Y9Result<String> removeDraft(@RequestParam @NotBlank String ids) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            map = draft4PositionApi.removeDraft(tenantId, ids);
            if ((boolean)map.get("success")) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }
}
