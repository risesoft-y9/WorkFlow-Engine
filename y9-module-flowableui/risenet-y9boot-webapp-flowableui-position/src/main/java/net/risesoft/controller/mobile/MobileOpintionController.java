package net.risesoft.controller.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 意见相关接口
 *
 * @author 10858
 *
 */
@RestController
@RequestMapping("/mobile/opintion")
public class MobileOpintionController {

    @Autowired
    private CommonSentencesApi commonSentencesApi;

    @Autowired
    private Opinion4PositionApi opinion4PositionApi;

    /**
     * 保存意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param formJsonData 意见json内容
     * @param response
     */
    @RequestMapping(value = "/comment/save")
    public void addComment(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String formJsonData, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            OpinionModel opinionModel = Y9JsonUtil.readValue(formJsonData, OpinionModel.class);
            opinionModel.setTenantId(tenantId + ":mobile");
            opinion4PositionApi.saveOrUpdate(tenantId, userId, positionId, opinionModel);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "添加成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "添加失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 是否已填写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @param response
     */
    @RequestMapping(value = "/comment/checkSignOpinion")
    public void checkSignOpinion(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String taskId, @RequestParam(required = false) String processSerialNumber,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            boolean b = opinion4PositionApi.checkSignOpinion(tenantId, userId, processSerialNumber, taskId);
            map.put("checkSignOpinion", b);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 意见id
     * @param response
     */
    @RequestMapping(value = "/comment/delete")
    public void deleteComment(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String id, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            opinion4PositionApi.delete(tenantId, id);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取个人常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param response
     */
    @RequestMapping(value = "/personalSetup")
    public void personalSetup(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            List<Map<String, Object>> listMap = commonSentencesApi.listSentencesService(tenantId, userId);
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(listMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * 获取意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，待办：todo,在办：doing,办结：done
     * @param opinionFrameMark 意见框标识
     * @param itemId 事项id
     * @param taskDefinitionKey 任务key
     * @param activitiUser 当前任务受让人
     * @param response
     */
    @RequestMapping(value = "/personCommentList")
    public void personCommentList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processSerialNumber, @RequestParam String taskId, @RequestParam String itembox,
        @RequestParam String opinionFrameMark, @RequestParam String itemId, @RequestParam String taskDefinitionKey, @RequestParam String activitiUser, @RequestParam(required = false) String orderByUser, HttpServletResponse response) {
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            listMap = opinion4PositionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, opinionFrameMark, itemId, taskDefinitionKey, activitiUser, orderByUser);
            map.put("opinionList", listMap);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 删除常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 常用语id
     * @param response
     */
    @RequestMapping(value = "/removeCommonSentences")
    public void removeCommonSentences(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String id, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            commonSentencesApi.delete(tenantId, id);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     *
     * 保存常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param content 内容
     * @param id 常用语id,新增id为空
     * @param response
     */
    @RequestMapping(value = "/saveCommonSentences")
    public void saveCommonSentences(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String content, @RequestParam String id, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            commonSentencesApi.save(tenantId, userId, id, content);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取个人常用语
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param response
     */
    @RequestMapping(value = "/systemSetup")
    public void systemSetup(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            List<Map<String, Object>> listMap = commonSentencesApi.listSentencesService(tenantId, userId);
            Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(listMap));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
