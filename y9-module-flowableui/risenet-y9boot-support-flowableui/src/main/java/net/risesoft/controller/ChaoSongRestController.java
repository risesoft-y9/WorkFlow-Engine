package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 抄送
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/chaoSong", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChaoSongRestController {

    private final ChaoSongApi chaoSongApi;

    private final AttachmentApi attachmentApi;

    private final TransactionWordApi transactionWordApi;

    private final SpeakInfoApi speakInfoApi;

    private final AssociatedFileApi associatedFileApi;

    private final OfficeFollowApi officeFollowApi;

    private final DocumentApi documentApi;

    private final Y9Properties y9Config;

    /**
     * 改变抄送件意见状态
     *
     * @param id 抄送id
     * @param type 意见状态
     * @return Y9Result<String>
     */
    @PostMapping(value = "/changeChaoSongState")
    public Y9Result<String> changeChaoSongState(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String type) {
        try {
            chaoSongApi.changeChaoSongState(Y9LoginUserHolder.getTenantId(), id, type);
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            LOGGER.error("changeChaoSongState error", e);
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 批量设置抄送状态为已阅
     *
     * @param ids 抄送id,逗号隔开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/changeStatus")
    public Y9Result<String> changeStatus(@RequestParam String[] ids) {
        try {
            chaoSongApi.changeStatus(Y9LoginUserHolder.getTenantId(), ids);
            return Y9Result.successMsg("操作成功");
        } catch (Exception e) {
            LOGGER.error("changeStatus error", e);
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 批量删除抄送件
     *
     * @param ids 抄送ids，逗号隔开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteList")
    public Y9Result<String> deleteList(@RequestParam String[] ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            chaoSongApi.deleteByIds(tenantId, ids);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("deleteList error", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取抄送件详情数据
     *
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @param openNotRead 是否打开不已阅
     * @param status 抄送状态
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/detail")
    public Y9Result<Map<String, Object>> detail(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String processInstanceId, @RequestParam(required = false) Boolean openNotRead,
        @RequestParam Integer status) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map;
        try {
            OpenDataModel model = chaoSongApi
                .detail(person.getTenantId(), positionId, id, processInstanceId, status, openNotRead, false).getData();
            String str = Y9JsonUtil.writeValueAsString(model);
            map = Y9JsonUtil.readHashMap(str);
            map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
            map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
            map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
            map.put("jsVersion", new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
            String processSerialNumber = model.getProcessSerialNumber();
            Integer fileNum = attachmentApi.fileCounts(tenantId, processSerialNumber).getData();
            int docNum = 0;
            // 是否正文正常
            TransactionWordModel wordMap =
                transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
            if (wordMap != null && wordMap.getId() != null) {
                docNum = 1;
            }
            int speakInfoNum =
                speakInfoApi.getNotReadCount(tenantId, person.getPersonId(), processInstanceId).getData();
            int associatedFileNum = associatedFileApi.countAssociatedFile(tenantId, processSerialNumber).getData();
            map.put("userName", Y9LoginUserHolder.getUserInfo().getName());
            map.put("docNum", docNum);
            map.put("speakInfoNum", speakInfoNum);
            map.put("associatedFileNum", associatedFileNum);
            map.put("fileNum", fileNum);
            map.put("tenantId", tenantId);
            map.put("userId", person.getPersonId());
            int follow = officeFollowApi.countByProcessInstanceId(tenantId, positionId, processInstanceId).getData();
            map.put("follow", follow > 0);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("detail error", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取抄送信息列表
     *
     * @param type 类型，my为我的抄送
     * @param userName 收件人
     * @param processInstanceId 流程实例id
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping(value = "/list")
    public Y9Page<ChaoSongModel> list(@RequestParam @NotBlank String type,
        @RequestParam(required = false) String userName, @RequestParam @NotBlank String processInstanceId,
        @RequestParam int rows, @RequestParam int page) {
        String tenantId = Y9LoginUserHolder.getTenantId(), senderId = Y9LoginUserHolder.getPositionId();
        try {
            if (type.equals("my")) {
                return chaoSongApi.getListBySenderIdAndProcessInstanceId(tenantId, senderId, processInstanceId,
                    userName, rows, page);
            } else {
                return chaoSongApi.getListByProcessInstanceId(tenantId, senderId, processInstanceId, userName, rows,
                    page);
            }
        } catch (Exception e) {
            LOGGER.error("获取抄送信息", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 保存抄送信息
     *
     * @param processInstanceId 流程实例id
     * @param users 收件人
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param smsPersonId 短信部分提醒人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @return Y9Result<Map < String, Object>>
     */
    @PostMapping(value = "/save")
    public Y9Result<Map<String, Object>> save(@RequestParam @NotBlank String processInstanceId,
        @RequestParam @NotBlank String users, @RequestParam(required = false) String isSendSms,
        @RequestParam(required = false) String isShuMing, @RequestParam(required = false) String smsContent,
        @RequestParam(required = false) String smsPersonId, @RequestParam(required = false) String itemId,
        @RequestParam(required = false) String processSerialNumber,
        @RequestParam(required = false) String processDefinitionKey) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            Map<String, Object> resMap = new HashMap<>(16);
            if (StringUtils.isBlank(processInstanceId)) {
                Y9Result<StartProcessResultModel> y9Result = documentApi.startProcess(Y9LoginUserHolder.getTenantId(),
                    Y9LoginUserHolder.getPositionId(), itemId, processSerialNumber, processDefinitionKey);
                if (y9Result.isSuccess()) {
                    processInstanceId = y9Result.getData().getProcessInstanceId();
                    String taskId = y9Result.getData().getTaskId();
                    resMap.put("processInstanceId", processInstanceId);
                    resMap.put("taskId", taskId);
                } else {
                    return Y9Result.failure("抄送失败，流程启动失败");
                }
            }
            Y9Result<Object> y9Result =
                chaoSongApi.save(person.getTenantId(), userId, Y9LoginUserHolder.getPositionId(), processInstanceId,
                    users, isSendSms, isShuMing, smsContent, smsPersonId);
            if (y9Result.isSuccess()) {
                return Y9Result.success(resMap, "抄送成功");
            }
        } catch (Exception e) {
            LOGGER.error("抄送", e);
        }
        return Y9Result.failure("抄送失败");
    }

    /**
     * 搜索抄送列表（标题、列表类型）
     *
     * @param documentTitle 搜索词
     * @param status 列表类型：0为未阅件，1为已阅件，2为批阅件
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping(value = "/search")
    public Y9Page<ChaoSongModel> search(@RequestParam(required = false) String documentTitle,
        @RequestParam Integer status, @RequestParam int rows, @RequestParam int page) {
        String positionId = Y9LoginUserHolder.getPositionId(), tenantId = Y9LoginUserHolder.getTenantId();
        try {
            if (status == 0) {
                return chaoSongApi.getTodoList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 1) {
                return chaoSongApi.getDoneList(tenantId, positionId, documentTitle, rows, page);
            } else if (status == 2) {
                return chaoSongApi.getOpinionChaosongByUserId(tenantId, positionId, documentTitle, rows, page);
            }
        } catch (Exception e) {
            LOGGER.error("获取抄送信息", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
