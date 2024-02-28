package net.risesoft.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricActivityApi;
import net.risesoft.api.processadmin.HistoricVariableApi;
import net.risesoft.entity.Opinion;
import net.risesoft.model.platform.Position;
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
@RestController
@RequestMapping(value = "/vue/bpmnViewer")
public class BpmnViewerRestController {

    @Autowired
    private HistoricActivityApi historicActivityApi;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private HistoricVariableApi historicVariableApi;

    /**
     * 获取流程实例节点列表
     *
     * @param processInstanceId
     * @return
     */
    @RequestMapping(value = "/getTaskList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam(required = true) String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<HistoricActivityInstanceModel> list = new ArrayList<HistoricActivityInstanceModel>();
        try {
            list = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, "");
            String year = "";
            if (list == null || list.size() == 0) {
                OfficeDoneInfo info = officeDoneInfoService.findByProcessInstanceId(processInstanceId);
                year = info.getStartTime().substring(0, 4);
                list = historicActivityApi.getByProcessInstanceIdAndYear(tenantId, processInstanceId, year);
            }
            for (HistoricActivityInstanceModel task : list) {
                String assignee = task.getAssignee();
                task.setExecutionId("");
                if (assignee != null) {
                    // 意见
                    List<Opinion> opinion = opinionRepository.findByTaskIdAndPositionIdAndProcessTrackIdIsNull(task.getTaskId(), StringUtils.isBlank(assignee) ? "" : assignee);
                    task.setTenantId(opinion.size() > 0 ? opinion.get(0).getContent() : "");
                    Position employee = positionApi.getPosition(Y9LoginUserHolder.getTenantId(), assignee).getData();
                    if (employee != null) {
                        String employeeName = employee.getName();
                        HistoricVariableInstanceModel zhuBan = null;
                        try {
                            zhuBan = historicVariableApi.getByTaskIdAndVariableName(tenantId, task.getTaskId(), SysVariables.PARALLELSPONSOR, year);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (zhuBan != null) {// 办理人
                            task.setCalledProcessInstanceId(employeeName + "(主办)");
                        } else {
                            task.setCalledProcessInstanceId(employeeName);
                        }
                    }
                    if (task.getStartTime() != null && task.getEndTime() != null) {// 办理时长
                        task.setExecutionId(longTime(task.getStartTime(), task.getEndTime()));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.success(list, "获取成功");
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            Date d1 = endTime;
            Date d2 = startTime;
            long time = d1.getTime() - d2.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            String str = d + "天" + h + "小时" + m + "分" + s + "秒";
            return str;
        }
    }

}
