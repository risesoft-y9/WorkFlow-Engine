package net.risesoft.controller.mobile.v1;

import lombok.RequiredArgsConstructor;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 监控列表相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
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
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param itemId     事项id
     */
    @RequestMapping(value = "/monitorDoingCount")
    public Y9Result<Long> monitorDoingCount(@RequestHeader("auth-tenantId") String tenantId,
                                            @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
                                            @RequestParam String itemId) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoingCount = monitorApi.getDoingCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoingCount, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控在办件
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param itemId     事项id
     * @param title      标题
     * @param page       页码
     * @param rows       条数
     * @return
     */
    @RequestMapping(value = "/monitorDoingList")
    public Y9Page<Map<String, Object>> monitorDoingList(@RequestHeader("auth-tenantId") String tenantId,
                                                        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
                                                        @RequestParam String itemId, @RequestParam(required = false) String title, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return monitorService.monitorDoingList(itemId, title, page, rows);
    }

    /**
     * 监控办结件统计
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param itemId     事项id
     */
    @RequestMapping(value = "/monitorDoneCount")
    public Y9Result<Long> monitorDoneCount(@RequestHeader("auth-tenantId") String tenantId,
                                           @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
                                           @RequestParam String itemId) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoneCount = monitorApi.getDoneCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoneCount, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控办结件
     *
     * @param tenantId   租户id
     * @param userId     人员id
     * @param positionId 岗位id
     * @param itemId     事项id
     * @param title      标题
     * @param page       页码
     * @param rows       条数
     */
    @RequestMapping(value = "/monitorDoneList")
    public Y9Page<Map<String, Object>> monitorDoneList(@RequestHeader("auth-tenantId") String tenantId,
                                                       @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
                                                       @RequestParam String itemId, @RequestParam(required = false) String title, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return monitorService.monitorDoneList(itemId, title, page, rows);
    }

    /**
     * 彻底删除流程实例
     *
     * @param tenantId          租户id
     * @param positionId        岗位id
     * @param userId            人员id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/removeProcess")
    public Y9Result<String> removeProcess(@RequestHeader("auth-tenantId") String tenantId,
                                          @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
                                          @RequestParam String processInstanceId) {
        try {
            ProcessParamModel processParamModel = null;
            List<String> list = new ArrayList<String>();
            processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = historicProcessApi.removeProcess4Position(tenantId, processInstanceId);
            if (b) {
                // 批量删除附件表
                attachment4PositionApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

}
