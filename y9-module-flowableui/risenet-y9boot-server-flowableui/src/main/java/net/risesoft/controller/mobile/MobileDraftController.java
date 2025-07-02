package net.risesoft.controller.mobile;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 草稿相关接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mobile/draft")
public class MobileDraftController {

    private final DraftApi draftApi;
    protected Logger log = LoggerFactory.getLogger(MobileDraftController.class);

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/delDraft")
    public void delDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9Result<Object> y9Result = draftApi.deleteDraft(tenantId, ids);
            if (y9Result.isSuccess()) {
                map.put(UtilConsts.SUCCESS, true);
                map.put("msg", "删除成功");
            }
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            LOGGER.error("删除草稿失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 回收站计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getDeleteDraftCount")
    public void getDeleteDraftCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draftApi.getDeleteDraftCount(tenantId, positionId, itemId).getData();
            map.put("count", count);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
            LOGGER.error("获取数据失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 草稿列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param delFlag 是否删除 true为回收站列表，false为草稿列表
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/getDraft")
    public void getDraft(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam(required = false) boolean delFlag,
        @RequestParam Integer page, @RequestParam Integer rows, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Page<Map<String, Object>> y9Page =
            draftApi.getDraftList(tenantId, positionId, page, rows, title, itemId, delFlag);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Page));
    }

    /**
     * 草稿箱计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getDraftCount")
    public void getDraftCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draftApi.getDraftCount(tenantId, positionId, itemId).getData();
            map.put("draftCount", count);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取草稿箱计数成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿箱计数失败");
            LOGGER.error("获取草稿箱计数失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param id 草稿id
     */
    @RequestMapping(value = "/reduction")
    public void reduction(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String id,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Result<Object> y9Result = draftApi.reduction(tenantId, id);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/removeDraft")
    public void removeDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Result<Object> y9Result = draftApi.removeDraft(tenantId, ids);
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
    }
}
