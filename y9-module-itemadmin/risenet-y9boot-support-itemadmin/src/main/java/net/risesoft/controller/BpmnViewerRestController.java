package net.risesoft.controller;

import java.util.ArrayList;
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
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.model.processadmin.HistoricVariableInstanceModel;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.service.OfficeDoneInfoService;
import net.risesoft.util.SysVariables;
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
        List<HistoricActivityInstanceModel> list = new ArrayList<>();
        try {
            list = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, "").getData();
            String year = "";
            if (list == null || list.isEmpty()) {
                OfficeDoneInfo info = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                year = info.getStartTime().substring(0, 4);
                list = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, year).getData();
            }
            for (HistoricActivityInstanceModel task : list) {
                String assignee = task.getAssignee();
                task.setExecutionId("");
                if (assignee != null) {
                    // 意见
                    List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(
                        task.getTaskId(), StringUtils.isBlank(assignee) ? "" : assignee);
                    OrgUnit employee = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                    HistoricVariableInstanceModel zhuBan = null;
                    try {
                        zhuBan = historicVariableApi
                            .getByTaskIdAndVariableName(tenantId, task.getTaskId(), SysVariables.PARALLELSPONSOR, year)
                            .getData();
                    } catch (Exception e) {
                        LOGGER.error("获取主办人失败", e);
                    }
                    String employeeName = "";
                    if (employee != null) {
                        employeeName = employee.getName();
                    }
                    if (StringUtils.isNotBlank(task.getTenantId())) {// tenantId存的是岗位/人员名称，优先显示这个名称
                        employeeName = task.getTenantId();
                    }
                    // 将TenantId字段存意见
                    task.setTenantId(!opinion.isEmpty() ? opinion.get(0).getContent() : "");
                    if (zhuBan != null) {// 办理人
                        task.setCalledProcessInstanceId(employeeName + "(主办)");
                    } else {
                        task.setCalledProcessInstanceId(employeeName);
                    }
                    if (task.getStartTime() != null && task.getEndTime() != null) {// 办理时长
                        task.setExecutionId(longTime(task.getStartTime(), task.getEndTime()));
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取流程实例节点列表失败", e);
        }
        return Y9Result.success(list, "获取成功");
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
