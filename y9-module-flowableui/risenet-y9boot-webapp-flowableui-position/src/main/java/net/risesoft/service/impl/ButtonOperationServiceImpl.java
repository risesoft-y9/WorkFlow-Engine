package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@RequiredArgsConstructor
@Service(value = "buttonOperationService")
public class ButtonOperationServiceImpl implements ButtonOperationService {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Document4PositionApi document4PositionApi;

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final ProcessTrack4PositionApi processTrack4PositionApi;

    private final RuntimeApi runtimeApi;

    private final HistoricTaskApi historictaskApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final ProcessParamApi processParamApi;

    @Override
    public void complete(String taskId, String taskDefName, String desc, String infoOvert) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId(),
            userName = Y9LoginUserHolder.getPosition().getName();
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(infoOvert)) {
            map.put("infoOvert", infoOvert);
        }
        variableApi.setVariables(tenantId, taskId, map);
        TaskModel taskModel = taskApi.findById(tenantId, taskId);
        String processInstanceId = taskModel.getProcessInstanceId();

        /*
          1办结
         */
        document4PositionApi.complete(tenantId, positionId, taskId);

        /*
          2更新自定义历程结束时间
         */
        List<ProcessTrackModel> ptModelList = processTrack4PositionApi.findByTaskId(tenantId, taskId);
        for (ProcessTrackModel ptModel : ptModelList) {
            if (StringUtils.isBlank(ptModel.getEndTime())) {
                ptModel.setEndTime(sdf.format(new Date()));
                processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
            }
        }
        /*
          3保存历程
         */
        ProcessTrackModel ptModel = new ProcessTrackModel();
        ptModel.setDescribed(desc);
        ptModel.setProcessInstanceId(processInstanceId);
        ptModel.setReceiverName(userName);
        ptModel.setSenderName(userName);
        ptModel.setStartTime(sdf.format(new Date()));
        ptModel.setEndTime(sdf.format(new Date()));
        ptModel.setTaskDefName(taskDefName);
        ptModel.setTaskId(taskId);
        ptModel.setId("");

        processTrack4PositionApi.saveOrUpdate(tenantId, ptModel);
    }

    @Override
    public void multipleResumeToDo(String processInstanceIds, String desc) throws Exception {
        if (StringUtils.isBlank(processInstanceIds)) {
            return;
        }
        String[] array = processInstanceIds.split(";");
        for (String processInstanceId : array) {
            if (StringUtils.isNotBlank(processInstanceId)) {
                resumeToDo(processInstanceId, desc);
            }
        }
    }

    @Override
    public void resumeToDo(String processInstanceId, String desc) throws Exception {
        String positionId = Y9LoginUserHolder.getPositionId(), userName = Y9LoginUserHolder.getPosition().getName(),
            tenantId = Y9LoginUserHolder.getTenantId();
        String newDate = sdf.format(new Date());
        try {
            /*
              1、恢复待办
             */

            String year;
            OfficeDoneInfoModel officeDoneInfoModel =
                officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
            if (officeDoneInfoModel != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
            } else {
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                year = processParamModel != null ? processParamModel.getCreateTime().substring(0, 4) : "";
            }

            HistoricTaskInstanceModel hisTaskModelTemp =
                historictaskApi.getByProcessInstanceIdOrderByEndTimeDesc(tenantId, processInstanceId, year).get(0);
            runtimeApi.recovery4Completed(tenantId, positionId, processInstanceId, year);
            /*
              2、添加流程的历程
             */
            ProcessTrackModel ptm = new ProcessTrackModel();
            ptm.setDescribed(desc);
            ptm.setProcessInstanceId(hisTaskModelTemp.getProcessInstanceId());
            ptm.setReceiverName(userName);
            ptm.setSenderName(userName);
            ptm.setStartTime(newDate);
            ptm.setEndTime(newDate);
            ptm.setTaskDefName("恢复待办");
            ptm.setTaskId(hisTaskModelTemp.getId());

            processTrack4PositionApi.saveOrUpdate(tenantId, ptm);

            /*
              2、添加流程的历程
             */
            ptm = new ProcessTrackModel();
            ptm.setDescribed(desc);
            ptm.setProcessInstanceId(hisTaskModelTemp.getProcessInstanceId());
            ptm.setReceiverName(userName);
            ptm.setSenderName(userName);
            ptm.setStartTime(newDate);
            ptm.setEndTime("");
            ptm.setTaskDefName(hisTaskModelTemp.getName());
            ptm.setTaskId(hisTaskModelTemp.getId());
            processTrack4PositionApi.saveOrUpdate(tenantId, ptm);
        } catch (Exception e) {
            LOGGER.error("runtimeApi resumeToDo error", e);
            throw new Exception("runtimeApi resumeToDo error");
        }
    }
}
