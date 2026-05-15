package net.risesoft.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.FlowableUiAuditLogEnum;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.AuditLogEvent;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DocumentHandleService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9StringUtil;

@RequiredArgsConstructor
@Slf4j
@Service
public class DocumentHandleServiceImpl implements DocumentHandleService {

    private final ProcessParamApi processParamApi;
    private final TaskApi taskApi;

    @Override
    @Transactional
    public Y9Result<String> sign4Batch(String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
        try {
            // 收集任务信息
            BatchSignResult batchSignResult = collectBatchSignInfo(tenantId, taskIdAndProcessSerialNumbers);
            // 验证任务状态
            Y9Result<String> validationResult = validateBatchSignTasks(batchSignResult);
            if (validationResult != null) {
                return validationResult;
            }
            // 执行批量签收
            BatchOperationResult operationResult =
                executeBatchSign(tenantId, positionId, taskIdAndProcessSerialNumbers);
            return Y9Result
                .successMsg("签收成功" + operationResult.successCount + "条，签收失败" + operationResult.failCount + "条");
        } catch (Exception e) {
            LOGGER.error("批量签收失败", e);
            return Y9Result.failure("校验是否批量签收失败，发生异常");
        }
    }

    /**
     * 收集批量签收信息
     */
    private BatchSignResult collectBatchSignInfo(String tenantId, String[] taskIdAndProcessSerialNumbers) {
        BatchSignResult result = new BatchSignResult();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            String[] tpArr = taskIdAndProcessSerialNumber.split(":");
            processTaskInfo(tenantId, tpArr, result);
        }
        return result;
    }

    /**
     * 处理单个任务信息
     */
    private void processTaskInfo(String tenantId, String[] tpArr, BatchSignResult result) {
        try {
            TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
            ProcessParamModel ppModel = processParamApi.findByProcessSerialNumber(tenantId, tpArr[1]).getData();
            if (task == null) {
                appendTaskTitle(result.processedTaskMsg, ppModel.getTitle());
            } else if (StringUtils.isNotBlank(task.getAssignee())) {
                appendTaskTitle(result.signedTaskMsg, ppModel.getTitle());
            }
        } catch (Exception e) {
            LOGGER.warn("处理任务信息失败: taskId={}, processSerialNumber={}", tpArr[0], tpArr[1], e);
        }
    }

    /**
     * 添加任务标题到消息中
     */
    private void appendTaskTitle(StringBuilder msg, String title) {
        if (StringUtils.isBlank(msg.toString())) {
            msg.append(title);
        } else {
            msg.append(",").append(title);
        }
    }

    /**
     * 验证批量签收任务
     */
    private Y9Result<String> validateBatchSignTasks(BatchSignResult batchSignResult) {
        if (StringUtils.isNotBlank(batchSignResult.signedTaskMsg.toString())) {
            return Y9Result.failure("不能批量签收，以下待办已签收：" + batchSignResult.signedTaskMsg);
        }
        if (StringUtils.isNotBlank(batchSignResult.processedTaskMsg.toString())) {
            return Y9Result.failure("不能批量签收，以下待办已处理：" + batchSignResult.processedTaskMsg);
        }
        return null; // 验证通过
    }

    /**
     * 执行批量签收操作
     */
    private BatchOperationResult executeBatchSign(String tenantId, String positionId,
        String[] taskIdAndProcessSerialNumbers) {
        BatchOperationResult result = new BatchOperationResult();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            String[] tpArr = taskIdAndProcessSerialNumber.split(":");
            executeSingleSign(tenantId, positionId, tpArr[0], tpArr[1], result);
        }
        return result;
    }

    /**
     * 执行单个任务签收
     */
    private void executeSingleSign(String tenantId, String positionId, String taskId, String processSerialNumber,
        BatchOperationResult result) {
        try {
            Y9Result<Object> y9Result = taskApi.claim(tenantId, positionId, taskId);
            if (y9Result.isSuccess()) {
                result.successCount++;
                ProcessParamModel ppModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                AuditLogEvent auditLogEvent = AuditLogEvent.builder()
                    .action(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getAction())
                    .description(
                        Y9StringUtil.format(FlowableUiAuditLogEnum.DOCUMENT_CLAIM.getDescription(), ppModel.getTitle()))
                    .objectId(taskId)
                    .oldObject(processSerialNumber)
                    .currentObject(null)
                    .build();
                Y9Context.publishEvent(auditLogEvent);
                LOGGER.info("任务签收成功: taskId={}", taskId);
            } else {
                result.failCount++;
                LOGGER.warn("任务签收失败: taskId={}", taskId);
            }
        } catch (Exception e) {
            result.failCount++;
            LOGGER.error("任务签收异常: taskId={}", taskId, e);
        }
    }

    /**
     * 批量签收结果封装类
     */
    private static class BatchSignResult {
        final StringBuilder processedTaskMsg = new StringBuilder();
        final StringBuilder signedTaskMsg = new StringBuilder();
    }

    /**
     * 批量操作结果封装类
     */
    private static class BatchOperationResult {
        int successCount = 0;
        int failCount = 0;
    }
}
