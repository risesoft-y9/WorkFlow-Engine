package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RestController
@RequestMapping("/mobile/v1/monitor")
public class MobileMonitorController {

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private MonitorApi monitorManager;

    @Autowired
    private Item4PositionApi itemManager;

    @Autowired
    private MonitorService monitorService;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private Attachment4PositionApi attachmentManager;

    /**
     * 删除流程实例
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param response
     */
    /* @RequestMapping(value = "/deleteProcessInstance")
    public Y9Result<String> deleteProcessInstance(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processInstanceId, HttpServletResponse response) {
        try {
            boolean b = historicProcessManager.deleteProcessInstance(tenantId, processInstanceId);
            if (b) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }*/

    /**
     * 监控在办件统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/monitorDoingCount")
    public Y9Result<Long> monitorDoingCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoingCount = monitorManager.getDoingCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoingCount, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控在办件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 标题
     * @param page 页码
     * @param rows 条数
     * @param response
     * @return
     */
    @RequestMapping(value = "/monitorDoingList")
    public Y9Page<Map<String, Object>> monitorDoingList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, int page, int rows,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return monitorService.monitorDoingList(itemId, title, page, rows);
    }

    /**
     * 监控办结件统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/monitorDoneCount")
    public Y9Result<Long> monitorDoneCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoneCount = monitorManager.getDoneCountByProcessDefinitionKey(tenantId, processDefinitionKey);
            return Y9Result.success(monitorDoneCount, "获取数据成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取数据失败");
    }

    /**
     * 监控办结件
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 标题
     * @param page 页码
     * @param rows 条数
     * @param response
     */
    @RequestMapping(value = "/monitorDoneList")
    public Y9Page<Map<String, Object>> monitorDoneList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, int page, int rows,
        HttpServletResponse response) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return monitorService.monitorDoneList(itemId, title, page, rows);
    }

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/removeProcess")
    public Y9Result<String> removeProcess(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processInstanceId, HttpServletResponse response) {
        try {
            ProcessParamModel processParamModel = null;
            List<String> list = new ArrayList<String>();
            processParamModel = processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = historicProcessManager.removeProcess4Position(tenantId, processInstanceId);
            if (b) {
                // 批量删除附件表
                attachmentManager.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordManager.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("发生异常");
    }

}
