package net.risesoft.controller.worklist;

import java.util.ArrayList;
import java.util.List;
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

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.MonitorService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 监控办件
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/monitor", produces = MediaType.APPLICATION_JSON_VALUE)
public class MonitorRestController {

    private final MonitorService monitorService;

    private final HistoricProcessApi historicProcessApi;

    private final TransactionWordApi transactionWordApi;

    private final AttachmentApi attachmentApi;

    private final ProcessParamApi processParamApi;

    private final ItemApi itemApi;

    /**
     * 获取单位所有件列表
     *
     * @param itemId 事项id
     * @param searchName 搜索词
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/deptList")
    public Y9Page<Map<String, Object>> deptList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchName, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return monitorService.pageDeptList(itemId, searchName, userName, state, year, page, rows);
    }

    /**
     * 获取所有事项信息
     *
     * @return Y9Result<List < ItemModel>>
     */
    @GetMapping(value = "/getAllItemList")
    public Y9Result<List<ItemModel>> getAllItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return itemApi.getAllItemList(tenantId);
    }

    /**
     * 获取监控办件列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发起人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/monitorBanjianList")
    public Y9Page<Map<String, Object>> monitorBanjianList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String userName,
        @RequestParam(required = false) String state, @RequestParam(required = false) String year,
        @RequestParam Integer page, @RequestParam Integer rows) {
        return monitorService.pageMonitorBanjianList(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取监控阅件列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 收件人
     * @param state 办件状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping(value = "/monitorChaosongList")
    public Y9Page<ChaoSongModel> monitorChaosongList(@RequestParam(required = false) String searchName,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String senderName,
        @RequestParam(required = false) String userName, @RequestParam(required = false) String state,
        @RequestParam(required = false) String year, @RequestParam Integer page, @RequestParam Integer rows) {
        return monitorService.pageMonitorChaosongList(searchName, itemId, senderName, userName, state, year, page,
            rows);
    }

    /**
     * 获取监控在办列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/monitorDoingList")
    public Y9Page<Map<String, Object>> monitorDoingList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return monitorService.pageMonitorDoingList(itemId, searchTerm, page, rows);
    }

    /**
     * 获取监控办结列表
     *
     * @param itemId 事项id
     * @param searchTerm 搜索词
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/monitorDoneList")
    public Y9Page<Map<String, Object>> monitorDoneList(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String searchTerm, @RequestParam Integer page, @RequestParam Integer rows) {
        return monitorService.pageMonitorDoneList(itemId, searchTerm, page, rows);
    }

    /**
     * 批量彻底删除流程实例
     *
     * @param processInstanceIds 流程实例ids，逗号隔开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeProcess")
    public Y9Result<String> removeProcess(@RequestParam @NotBlank String processInstanceIds) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel;
        List<String> list = null;
        try {
            if (StringUtils.isNotBlank(processInstanceIds)) {
                list = new ArrayList<>();
                String[] ids = processInstanceIds.split(SysVariables.COMMA);
                for (String processInstanceId : ids) {
                    processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (processParamModel != null) {
                        list.add(processParamModel.getProcessSerialNumber());
                    }
                }
            }
            boolean b = historicProcessApi.removeProcess(tenantId, processInstanceIds).isSuccess();
            if (b) {
                // 批量删除附件表
                attachmentApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                transactionWordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
        }
        return Y9Result.failure("删除失败");
    }
}
