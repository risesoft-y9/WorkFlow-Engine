package net.risesoft.controller;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.opinion.OpinionRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 流程图展示
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/bpmnViewer", produces = MediaType.APPLICATION_JSON_VALUE)
public class BpmnViewerRestController {

    private final HistoricActivityApi historicActivityApi;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final OpinionRepository opinionRepository;

    private final OrgUnitApi orgUnitApi;

    private final HistoricVariableApi historicVariableApi;

    /**
     * 获取流程实例节点列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<HistoricActivityInstanceModel>>
     */
    @GetMapping(value = "/getTaskList")
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricActivityInstanceModel> list;
        try {
            String year = "";
            list = historicActivityApi.getByProcessInstanceId(tenantId, processInstanceId).getData();
            if (list == null || list.isEmpty()) {
                OfficeDoneInfo info = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                year = info.getStartTime().substring(0, 4);
                list = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, year).getData();
            }
            for (HistoricActivityInstanceModel task : list) {
                processTaskDetails(tenantId, task, year);
            }
        } catch (Exception e) {
            LOGGER.error("获取流程实例节点列表失败，processInstanceId: {}", processInstanceId, e);
            return Y9Result.failure("获取流程实例节点列表失败");
        }

        return Y9Result.success(list, "获取成功");
    }

    /**
     * 处理任务详情信息
     */
    private void processTaskDetails(String tenantId, HistoricActivityInstanceModel task, String year) {
        String assignee = task.getAssignee();
        task.setExecutionId(""); // 重用字段存储办理时长
        if (assignee != null) {
            try {
                // 获取意见信息
                List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(
                    task.getTaskId(), StringUtils.defaultString(assignee));
                // 获取办理人信息
                OrgUnit employee = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                // 获取主办人信息
                HistoricVariableInstanceModel zhuBan = getZhuBanInfo(tenantId, task, year);
                // 设置办理人名称
                String employeeName = getEmployeeName(task, employee);
                // 将TenantId字段存储意见内容
                task.setTenantId(!opinion.isEmpty() ? opinion.get(0).getContent() : "");
                // 设置办理人显示名称（含主办标识）
                task.setCalledProcessInstanceId(zhuBan != null ? employeeName + "(主办)" : employeeName);
                // 计算并设置办理时长
                if (task.getStartTime() != null && task.getEndTime() != null) {
                    task.setExecutionId(longTime(task.getStartTime(), task.getEndTime()));
                }
            } catch (Exception e) {
                LOGGER.error("处理任务详情信息失败，taskId: {}", task.getTaskId(), e);
            }
        }
    }

    /**
     * 获取主办人信息
     */
    private HistoricVariableInstanceModel getZhuBanInfo(String tenantId, HistoricActivityInstanceModel task,
        String year) {
        try {
            return historicVariableApi
                .getByTaskIdAndVariableName(tenantId, task.getTaskId(), SysVariables.PARALLEL_SPONSOR, year)
                .getData();
        } catch (Exception e) {
            LOGGER.error("获取主办人失败，taskId: {}", task.getTaskId(), e);
            return null;
        }
    }

    /**
     * 获取办理人名称
     */
    private String getEmployeeName(HistoricActivityInstanceModel task, OrgUnit employee) {
        // 如果tenantId字段已包含名称，则优先使用
        if (StringUtils.isNotBlank(task.getTenantId())) {
            return task.getTenantId();
        }

        return employee != null ? employee.getName() : "";
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            long time = endTime.getTime() - startTime.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            return d + "天" + h + "小时" + m + "分" + s + "秒";
        }
    }

}
