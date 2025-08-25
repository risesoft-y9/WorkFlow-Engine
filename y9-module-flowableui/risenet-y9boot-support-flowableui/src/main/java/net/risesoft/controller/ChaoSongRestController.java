package net.risesoft.controller;

import java.util.ArrayList;
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

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

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

    private final DocumentApi documentApi;

    /**
     * 改变抄送件意见状态
     *
     * @param id 抄送id
     * @param type 意见状态
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "改变抄送件意见状态")
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
    @FlowableLog(operationName = "批量设置抄送状态为已阅")
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
    @FlowableLog(operationName = "批量删除抄送件", operationType = FlowableOperationTypeEnum.DELETE)
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
    @FlowableLog(operationName = "保存抄送信息", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/save")
    public Y9Result<Map<String, Object>> save(@RequestParam(required = false) String processInstanceId,
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
    @FlowableLog(operationName = "搜索抄送列表")
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
