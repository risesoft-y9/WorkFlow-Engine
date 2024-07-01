package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MonitorService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 监控列表相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/monitor")
public class MobileV1MonitorController {

    private final HistoricProcessApi historicProcessApi;

    private final MonitorApi monitorApi;

    private final Item4PositionApi item4PositionApi;

    private final MonitorService monitorService;

    private final ProcessParamApi processParamApi;

    private final TransactionWordApi transactionWordApi;

    private final Attachment4PositionApi attachment4PositionApi;

    /**
     * 监控在办件统计
     *
     * @param itemId 事项id
     * @return Y9Result<Long>
     */
    @RequestMapping(value = "/monitorDoingCount")
    public Y9Result<Long> monitorDoingCount(@RequestParam @NotBlank String itemId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoingCount = monitorApi.getDoingCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoingCount, "获取数据成功");
        } catch (Exception e) {
            LOGGER.error("监控在办件统计失败", e);
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控在办件
     *
     * @param itemId 事项id
     * @param title 标题
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/monitorDoingList")
    public Y9Page<Map<String, Object>> monitorDoingList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam int page, @RequestParam int rows) {
        return monitorService.monitorDoingList(itemId, title, page, rows);
    }

    /**
     * 监控办结件统计
     *
     * @param itemId 事项id
     * @return Y9Result<Long>
     */
    @RequestMapping(value = "/monitorDoneCount")
    public Y9Result<Long> monitorDoneCount(@RequestParam @NotBlank String itemId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoneCount = monitorApi.getDoneCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoneCount, "获取数据成功");
        } catch (Exception e) {
            LOGGER.error("监控办结件统计失败", e);
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控办结件
     *
     * @param itemId 事项id
     * @param title 标题
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/monitorDoneList")
    public Y9Page<Map<String, Object>> monitorDoneList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam int page, @RequestParam int rows) {
        return monitorService.monitorDoneList(itemId, title, page, rows);
    }

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/removeProcess")
    public Y9Result<String> removeProcess(@RequestParam @NotBlank String processInstanceId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            ProcessParamModel processParamModel;
            List<String> list = new ArrayList<>();
            processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = historicProcessApi.removeProcess4Position(tenantId, processInstanceId).isSuccess();
            if (b) {
                // 批量删除附件表
                attachment4PositionApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("彻底删除流程实例失败", e);
        }
        return Y9Result.failure("发生异常");
    }

}
