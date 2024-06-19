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

import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.consts.UtilConsts;
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

    private final Draft4PositionApi draft4PositionApi;
    protected Logger log = LoggerFactory.getLogger(MobileDraftController.class);

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/delDraft")
    public void delDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draft4PositionApi.deleteDraft(tenantId, ids);
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
    public void getDeleteDraftCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draft4PositionApi.getDeleteDraftCount(tenantId, positionId, itemId);
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
     * 草稿箱计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getDraftCount")
    public void getDraftCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Integer count = draft4PositionApi.getDraftCount(tenantId, positionId, itemId);
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
    public void getManuscript(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId, @RequestParam(required = false) String title, @RequestParam(required = false) boolean delFlag, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draft4PositionApi.getDraftList(tenantId, positionId, page, rows, title, itemId, delFlag);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿列表失败");
            LOGGER.error("获取草稿列表失败", e);
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
    public void reduction(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String id, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draft4PositionApi.reduction(tenantId, id);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "还原失败");
            LOGGER.error("还原草稿失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids 草稿ids,“,”分隔
     */
    @RequestMapping(value = "/removeDraft")
    public void removeDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            map = draft4PositionApi.removeDraft(tenantId, ids);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            LOGGER.error("删除草稿失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }
}
