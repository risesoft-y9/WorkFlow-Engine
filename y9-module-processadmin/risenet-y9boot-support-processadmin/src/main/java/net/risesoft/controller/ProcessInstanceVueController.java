package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.Y9WordApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomIdentityService;
import net.risesoft.service.CustomTaskService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/processInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessInstanceVueController {

    protected final HistoryService historyService;
    private final RuntimeService runtimeService;
    private final OrgUnitApi orgUnitApi;
    private final Y9WordApi y9WordApi;
    private final AttachmentApi attachmentApi;
    private final ProcessParamApi processParamApi;
    private final CustomHistoricProcessService customHistoricProcessService;
    private final OfficeDoneInfoApi officeDoneInfoApi;
    private final CustomIdentityService customIdentityService;
    private final CustomTaskService customTaskService;

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delete")
    public Y9Result<String> delete(@RequestParam String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel;
        List<String> list = new ArrayList<>();
        try {
            processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (processParamModel != null) {
                list.add(processParamModel.getProcessSerialNumber());
            }
            boolean b = customHistoricProcessService.removeProcess(processInstanceId);
            if (b) {
                // 批量删除附件表
                attachmentApi.delBatchByProcessSerialNumbers(tenantId, list);
                // 批量删除正文表
                y9WordApi.delBatchByProcessSerialNumbers(tenantId, list);
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除流程实例失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取流程实例列表
     *
     * @param processInstanceId 流程实例id
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/runningList")
    public Y9Page<Map<String, Object>> runningList(@RequestParam(required = false) String processInstanceId,
        @RequestParam int page, @RequestParam int rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> items = new ArrayList<>();
        long totalCount;
        List<ProcessInstance> processInstanceList;
        if (StringUtils.isBlank(processInstanceId)) {
            totalCount = runtimeService.createProcessInstanceQuery().count();
            processInstanceList =
                runtimeService.createProcessInstanceQuery().orderByStartTime().desc().listPage((page - 1) * rows, rows);
        } else {
            totalCount = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).count();
            processInstanceList = runtimeService.createProcessInstanceQuery()
                .processInstanceId(processInstanceId)
                .orderByStartTime()
                .desc()
                .listPage((page - 1) * rows, rows);
        }

        List<String> startUserIdList = processInstanceList.stream()
            .map(ProcessInstance::getStartUserId)
            .filter(StringUtils::isNotBlank)
            .collect(Collectors.toList());
        Map<String,
            OrgUnit> idOrgUnitMap = orgUnitApi.listPersonOrPositionByIds(tenantId, startUserIdList)
                .getData()
                .stream()
                .collect(Collectors.toMap(OrgUnit::getId, orgUnit -> orgUnit));

        for (ProcessInstance processInstance : processInstanceList) {
            processInstanceId = processInstance.getId();
            Map<String, Object> map = new HashMap<>(16);
            map.put("processInstanceId", processInstanceId);
            map.put("processDefinitionId", processInstance.getProcessDefinitionId());
            map.put("processDefinitionName", processInstance.getProcessDefinitionName());
            map.put("startTime", processInstance.getStartTime() == null ? ""
                : Y9DateTimeUtils.formatDateTime(processInstance.getStartTime()));
            try {
                map.put("activityName",
                    runtimeService.createActivityInstanceQuery()
                        .processInstanceId(processInstanceId)
                        .orderByActivityInstanceStartTime()
                        .desc()
                        .list()
                        .get(0)
                        .getActivityName());
                map.put(SysVariables.SUSPENDED_KEY, processInstance.isSuspended());
                map.put("startUserName", "无");
                if (StringUtils.isNotBlank(processInstance.getStartUserId())) {
                    OrgUnit orgUnit = idOrgUnitMap.get(processInstance.getStartUserId());
                    OrgUnit parent = orgUnitApi.getOrgUnitAsParent(tenantId, orgUnit.getParentId()).getData();
                    map.put("startUserName", orgUnit.getName() + "(" + parent.getName() + ")");
                }
            } catch (Exception e) {
                LOGGER.error("获取流程实例列表失败processInstanceId:{}, e:{}", processInstanceId, e);
            }
            items.add(map);
        }
        int totalPages = (int)totalCount / rows + 1;
        return Y9Page.success(page, totalPages, totalCount, items, "获取列表成功");
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processInstanceId 流程实例
     * @return Y9Result<String>
     */
    @PostMapping(value = "/switchSuspendOrActive")
    public Y9Result<String> switchSuspendOrActive(@RequestParam String state, @RequestParam String processInstanceId) {
        try {
            if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
                runtimeService.activateProcessInstanceById(processInstanceId);
                return Y9Result.successMsg("已激活ID为[" + processInstanceId + "]的流程实例。");
            } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
                runtimeService.suspendProcessInstanceById(processInstanceId);
                return Y9Result.successMsg("已挂起ID为[" + processInstanceId + "]的流程实例。");
            }
        } catch (Exception e) {
            LOGGER.error("挂起、激活流程实例失败", e);
        }
        return Y9Result.failure("操作失败");
    }
}
