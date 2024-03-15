package net.risesoft.controller.mobile;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Person;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/draft")
public class MobileDraftController {

    protected Logger log = LoggerFactory.getLogger(MobileWorkListController.class);

    @Autowired
    private PersonApi personApi;

    @Autowired
    private DraftApi draftManager;

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/delDraft")
    public void delDraft(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = draftManager.deleteDraft(tenantId, userId, ids);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 回收站计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getDeleteDraftCount")
    public void getDeleteDraftCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.get(tenantId, userId).getData());
            Integer count = draftManager.getDeleteDraftCount(tenantId, userId, itemId);
            map.put("count", count);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 草稿箱计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getDraftCount")
    public void getDraftCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.get(tenantId, userId).getData());
            Integer count = draftManager.getDraftCount(tenantId, userId, itemId);
            map.put("draftCount", count);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取草稿箱计数成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿箱计数失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 草稿列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param delFlag 是否删除 true为回收站列表，false为草稿列表
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/getDraft")
    public void getManuscript(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String itemId, @RequestParam String title,
        boolean delFlag, @RequestParam Integer page, @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = draftManager.getDraftList(tenantId, userId, page, rows, title, itemId, delFlag);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取草稿列表失败");
            log.error("草稿箱列表异常：");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/reduction")
    public void reduction(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String id, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = draftManager.reduction(tenantId, userId, id);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "还原失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids 草稿ids,“,”分隔
     * @param response
     */
    @RequestMapping(value = "/removeDraft")
    public void removeDraft(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String ids, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = draftManager.removeDraft(tenantId, userId, ids);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
