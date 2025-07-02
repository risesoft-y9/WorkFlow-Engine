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
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ItemRoleApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ItemRoleOrgUnitModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.DocumentUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 抄送阅件相关接口
 *
 * @author 10858
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/mobile/chaosong")
public class MobileChaoSongController {
    protected final Logger log = LoggerFactory.getLogger(MobileChaoSongController.class);

    private final ChaoSongApi chaoSongApi;

    private final ItemRoleApi itemRoleApi;

    private final PersonApi personApi;

    /**
     * 抄送件收回
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     */
    @ResponseBody
    @RequestMapping(value = "/deleteList")
    public void deleteList(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String ids,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            String[] id = ids.split(",");
            chaoSongApi.deleteByIds(tenantId, id);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("抄送件收回失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @RequestMapping(value = "/detail")
    @ResponseBody
    public void detail(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String id,
        @RequestParam @NotBlank String processInstanceId, @RequestParam(required = false) Integer status,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setPositionId(positionId);
            Person person = personApi.get(tenantId, userId).getData();
            Y9LoginUserHolder.setPerson(person);
            OpenDataModel model =
                chaoSongApi.detail(tenantId, positionId, id, processInstanceId, status, false, true).getData();
            String processSerialNumber = model.getProcessSerialNumber();
            String activitiUser = model.getActivitiUser();
            String processDefinitionId = model.getProcessDefinitionId();
            String taskDefKey = model.getTaskDefKey();
            String formIds = model.getFormId();
            String formNames = model.getFormName();
            String taskId = model.getTaskId();
            String itemId = model.getItemId();
            String itembox = model.getItembox();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);
            DocumentUtil documentUtil = new DocumentUtil();
            Map<String, Object> dataMap = documentUtil.documentDetail(itemId, processDefinitionId, processSerialNumber,
                processInstanceId, taskDefKey, taskId, itembox, activitiUser, formIds, formNames);
            map.putAll(dataMap);
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取抄送选人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @RequestMapping(value = "/findCsUser")
    @ResponseBody
    public void findCsUser(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String id,
        @RequestParam(required = false) Integer principalType, @RequestParam(required = false) String processInstanceId,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        try {
            item = itemRoleApi
                .findCsUser(Y9LoginUserHolder.getTenantId(), userId, positionId, id, principalType, processInstanceId)
                .getData();
        } catch (Exception e) {
            LOGGER.error("获取抄送选人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
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
     */
    @RequestMapping(value = "/findCsUserSearch")
    @ResponseBody
    public void findCsUserSearch(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam(required = false) String name, @RequestParam(required = false) Integer principalType,
        @RequestParam(required = false) String processInstanceId, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemRoleOrgUnitModel> item = new ArrayList<>();
        try {
            item = itemRoleApi.findCsUserSearch(tenantId, userId, positionId, name, principalType, processInstanceId)
                .getData();
        } catch (Exception e) {
            LOGGER.error("获取抄送选人失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(item));
    }

    /**
     * 办件抄送列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param type，“my”为我的抄送，其余为所有抄送
     * @param processInstanceId 流程实例id
     * @param rows 行数
     * @param page 页码
     */
    @ResponseBody
    @RequestMapping(value = "/list")
    public void list(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String type,
        @RequestParam String processInstanceId, @RequestParam int rows, @RequestParam int page,
        HttpServletResponse response) {
        Y9Page<ChaoSongModel> y9Page = null;
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            if (type.equals("my")) {
                y9Page = chaoSongApi.getListBySenderIdAndProcessInstanceId(tenantId, positionId, processInstanceId, "",
                    rows, page);
            } else {
                y9Page =
                    chaoSongApi.getListByProcessInstanceId(tenantId, positionId, processInstanceId, "", rows, page);
            }
        } catch (Exception e) {
            LOGGER.error("获取抄送列表失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Page));
    }

    /**
     * 抄送件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 搜索标题
     * @param status 状态，0为未阅件，1为已阅件
     * @param rows 行数
     * @param page 页码
     */
    @RequestMapping(value = "/search")
    @ResponseBody
    public void search(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String documentTitle,
        @RequestParam Integer status, @RequestParam int rows, @RequestParam int page, HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Page<ChaoSongModel> y9Page = null;
        try {
            if (status == 0) {
                y9Page = chaoSongApi.getTodoList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 1) {
                y9Page = chaoSongApi.getDoneList(tenantId, positionId, documentTitle, rows, page);
            }
        } catch (Exception e) {
            LOGGER.error("获取抄送列表失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Page));
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
     */
    @RequestMapping(value = "/send")
    @ResponseBody
    public void send(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam(required = false) String processInstanceId,
        @RequestParam @NotBlank String users, @RequestParam(required = false) String isSendSms,
        @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9Result<Object> y9Result = null;
        try {
            y9Result = chaoSongApi.save(tenantId, userId, positionId, processInstanceId, users, isSendSms, isShuMing,
                smsContent, "");
        } catch (Exception e) {
            LOGGER.error("发送抄送失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(y9Result));
    }
}
