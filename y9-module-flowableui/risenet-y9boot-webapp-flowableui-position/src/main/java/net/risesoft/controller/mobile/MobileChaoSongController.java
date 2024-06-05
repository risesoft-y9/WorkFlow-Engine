package net.risesoft.controller.mobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.ItemRole4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Person;
import net.risesoft.util.DocumentUtil;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 抄送阅件相关接口
 *
 * @author 10858
 *
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/chaosong")
public class MobileChaoSongController {
    protected final Logger log = LoggerFactory.getLogger(MobileChaoSongController.class);

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final ItemRole4PositionApi itemRole4PositionApi;

    private final PersonApi personApi;

    /**
     * 抄送件收回
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param ids 抄送件ids
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/deleteList")
    public void deleteList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @NotBlank String ids, @NotBlank String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String id[] = ids.split(",");
            chaoSong4PositionApi.deleteByIds(tenantId, id);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端跟踪抄送件收回");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 抄送件详情
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @param status 状态0为未阅件打开，1为已阅件打开
     * @param response
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public void detail(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String id, @RequestParam @NotBlank String processInstanceId, Integer status,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setPositionId(positionId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            map = chaoSong4PositionApi.detail(tenantId, positionId, id, processInstanceId, status, true);
            String processSerialNumber = (String)map.get("processSerialNumber");
            String activitiUser = (String)map.get(SysVariables.ACTIVITIUSER);
            String processDefinitionId = (String)map.get("processDefinitionId");
            String taskDefKey = (String)map.get("taskDefKey");
            String formIds = (String)map.get("formId");
            String formNames = (String)map.get("formName");
            String taskId = (String)map.get("taskId");
            String itemId = (String)map.get("itemId");
            String itembox = (String)map.get("itembox");
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber, processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端跟踪查看抄送件详情");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取抄送选人
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param id 父节点id
     * @param principalType 选择类型 2是部门，3是群组
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/findCsUser")
    @ResponseBody
    public void findCsUser(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String id, @RequestParam Integer principalType, @RequestParam String processInstanceId,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        try {
            item = itemRole4PositionApi.findCsUser(Y9LoginUserHolder.getTenantId(), userId, positionId, id, principalType, processInstanceId);
        } catch (Exception e) {
            log.error("手机端跟踪获取抄送选人");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
        return;
    }

    /**
     * 选人搜索
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param principalType 选择类型 2是部门，3是群组
     * @param processInstanceId 流程实例id
     * @param name 搜索姓名
     * @param response
     */
    @RequestMapping(value = "/findCsUserSearch")
    @ResponseBody
    public void findCsUserSearch(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String name, @RequestParam Integer principalType, @RequestParam String processInstanceId,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> item = new ArrayList<Map<String, Object>>();
        try {
            item = itemRole4PositionApi.findCsUserSearch(tenantId, userId, positionId, name, principalType, processInstanceId);
        } catch (Exception e) {
            log.error("手机端跟踪选人搜索");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
        return;
    }

    /**
     * 办件抄送列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param type，“my”为我的抄送，其余为所有抄送
     * @param processInstanceId 流程实例id
     * @param rows 行数
     * @param page 页码
     * @param response
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    public void list(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, String type, @NotBlank String processInstanceId, int rows, int page, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (type.equals("my")) {
                map = chaoSong4PositionApi.getListBySenderIdAndProcessInstanceId(tenantId, positionId, processInstanceId, "", rows, page);
            } else {
                map = chaoSong4PositionApi.getListByProcessInstanceId(tenantId, positionId, processInstanceId, "", rows, page);
            }
        } catch (Exception e) {
            log.error("手机端跟踪办件抄送列表");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 抄送件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param documentTitle 搜索标题
     * @param status 状态，0为未阅件，1为已阅件
     * @param rows 行数
     * @param page 页码
     * @param response
     */
    @RequestMapping(value = "/search")
    @ResponseBody
    public void search(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String year, @RequestParam String documentTitle, Integer status, int rows, int page, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (status == 0) {
                map = chaoSong4PositionApi.getTodoList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 1) {
                map = chaoSong4PositionApi.getDoneList(tenantId, positionId, documentTitle, rows, page);
            }
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端跟踪查看抄送件列表");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 抄送发送
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param users 发送人员
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param response
     */
    @RequestMapping(value = "/send")
    @ResponseBody
    public void send(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processInstanceId, @RequestParam @NotBlank String users, @RequestParam String isSendSms,
        @RequestParam String isShuMing, @RequestParam String smsContent, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(1);
        try {
            map = chaoSong4PositionApi.save(tenantId, userId, positionId, processInstanceId, users, isSendSms, isShuMing, smsContent, "");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端跟踪查看抄送件发送");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
