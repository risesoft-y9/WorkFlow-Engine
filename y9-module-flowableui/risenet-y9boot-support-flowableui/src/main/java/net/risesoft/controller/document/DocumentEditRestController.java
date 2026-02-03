package net.risesoft.controller.document;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.log.FlowableLogLevelEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 打开公文
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/document/edit", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentEditRestController {

    private final DocumentApi documentApi;

    private final TaskApi taskApi;

    private final HistoricVariableApi historicVariableApi;

    /**
     * 获取草稿详细信息（打开草稿时调用）
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @return Y9Result<DocumentDetailModel>
     */
    @GetMapping("/draft")
    public Y9Result<DocumentDetailModel> draft(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9FlowableHolder.getPositionId();
        DocumentDetailModel model =
            documentApi.editDraft(tenantId, positionId, itemId, processSerialNumber, false).getData();
        return Y9Result.success(model, "获取成功");
    }

    /**
     * 获取编辑在办件数据
     * 
     * @param documentId 打开的办件的id，主件的这个id为processSerialNumber，子件的这个id为signDeptDetailId
     * @param processInstanceId 流程实例id
     * @return Y9Result<DocumentDetailModel>
     */
    @FlowableLog(operationName = "在办详情")
    @GetMapping(value = "/doing")
    public Y9Result<DocumentDetailModel> doing(@RequestParam @NotBlank String documentId,
        @RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDoing(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), processInstanceId,
                    documentId, false, ItemBoxTypeEnum.DOING, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取在办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控在办件详情", logLevel = FlowableLogLevelEnum.ADMIN)
    @GetMapping(value = "/doingAdmin")
    public Y9Result<DocumentDetailModel> doingAdmin(@RequestParam @NotBlank String documentId,
        @RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDoing(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), processInstanceId,
                    documentId, true, ItemBoxTypeEnum.MONITOR_DOING, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取监控在办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "办结详情")
    @GetMapping(value = "/done")
    public Y9Result<DocumentDetailModel> done(@RequestParam @NotBlank String documentId,
        @RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDone(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), processInstanceId,
                    documentId, false, ItemBoxTypeEnum.DONE, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取办结办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "监控办结详情", logLevel = FlowableLogLevelEnum.ADMIN)
    @GetMapping(value = "/doneAdmin")
    public Y9Result<DocumentDetailModel> doneAdmin(@RequestParam @NotBlank String documentId,
        @RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model = documentApi
                .editDone(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), processInstanceId,
                    documentId, true, ItemBoxTypeEnum.MONITOR_DONE, false)
                .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取监控办结办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取回收站办件数据
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "回收件详情")
    @GetMapping(value = "/recycle")
    public Y9Result<DocumentDetailModel> recycle(@RequestParam @NotBlank String processInstanceId) {
        try {
            DocumentDetailModel model =
                documentApi
                    .editRecycle(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(), processInstanceId,
                        false)
                    .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取回收办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取编辑办件数据
     *
     * @param taskId 任务id
     * @return Y9Result<Map < String, Object>>
     */
    @FlowableLog(operationName = "待办详情")
    @GetMapping(value = "/todo")
    public Y9Result<DocumentDetailModel> todo(@RequestParam @NotBlank String taskId) {
        try {
            TaskModel task = taskApi.findById(Y9LoginUserHolder.getTenantId(), taskId).getData();
            if (null == task) {
                return Y9Result.failure("当前待办已处理！");
            }
            DocumentDetailModel model =
                documentApi
                    .editTodo(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
                        Y9FlowableHolder.getPositionId(), taskId, false)
                    .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取待办件数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取抄送件详情数据
     *
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/chaoSong")
    public Y9Result<DocumentDetailModel> chaoSong(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String processInstanceId, @RequestParam @NotBlank String itembox) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String positionId = Y9FlowableHolder.getPositionId();
        try {
            DocumentDetailModel model =
                documentApi.editChaoSong(person.getTenantId(), positionId, id, processInstanceId, false, itembox)
                    .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("detail error", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取抄送件详情数据
     *
     * @param id 抄送id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/chaoSongAdmin")
    public Y9Result<DocumentDetailModel> chaoSongAdmin(@RequestParam @NotBlank String id,
        @RequestParam @NotBlank String processInstanceId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String positionId = Y9FlowableHolder.getPositionId();
        try {
            DocumentDetailModel model =
                documentApi
                    .editChaoSong(person.getTenantId(), positionId, id, processInstanceId, false,
                        ItemBoxTypeEnum.MONITOR_CHAOSONG.getValue())
                    .getData();
            return Y9Result.success(model, "获取成功");
        } catch (Exception e) {
            LOGGER.error("detail error", e);
        }
        return Y9Result.failure("获取失败");
    }
}