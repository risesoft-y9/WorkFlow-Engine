package net.risesoft.controller.mobile;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 监控列表相关接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mobile/monitor")
public class MobileMonitorController {

    private final HistoricProcessApi historicProcessApi;

    private final MonitorApi monitorApi;

    private final ItemApi itemApi;

    /**
     * 删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/deleteProcessInstance")
    public void deleteProcessInstance(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam @NotBlank String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            boolean b = historicProcessApi.deleteProcessInstance(tenantId, processInstanceId).isSuccess();
            map.put(UtilConsts.SUCCESS, b);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("删除流程实例异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 监控在办件统计
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/monitorDoingCount")
    public void monitorDoingCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam @NotBlank String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoingCount =
                monitorApi.getDoingCountByProcessDefinitionKey(tenantId, processDefinitionKey).getData();
            map.put("monitorDoingCount", monitorDoingCount);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 监控在办件
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param title 标题
     * @param page 页码
     * @param rows 条数
     */
    @RequestMapping(value = "/monitorDoingList")
    public void monitorDoingList(@RequestHeader("auth-tenantId") String tenantId, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam int page, @RequestParam int rows,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            // map = monitorService.monitorDoingList(itemId, title, page, rows);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 监控办结件统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/monitorDoneCount")
    public void monitorDoneCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam @NotBlank String itemId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            long monitorDoneCount =
                monitorApi.getDoneCountByProcessDefinitionKey(tenantId, processDefinitionKey).getData();
            map.put("monitorDoneCount", monitorDoneCount);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "获取数据成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "获取数据失败");
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
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
     */
    @RequestMapping(value = "/monitorDoneList")
    public void monitorDoneList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        @RequestParam @NotBlank String itemId, @RequestParam(required = false) String title, @RequestParam int page,
        @RequestParam int rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            // map = monitorService.monitorDoneList(itemId, title, page, rows);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 彻底删除流程实例
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/removeProcess")
    public void removeProcess(@RequestHeader("auth-tenantId") String tenantId,
        @RequestParam @NotBlank String processInstanceId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            boolean b = historicProcessApi.removeProcess(tenantId, processInstanceId).isSuccess();
            map.put(UtilConsts.SUCCESS, b);
        } catch (Exception e) {
            map.put("msg", "发生异常");
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

}
