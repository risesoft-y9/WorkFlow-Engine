package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

@Slf4j
@RequiredArgsConstructor
@Service(value = "buttonOperationService")
public class ButtonOperationServiceImpl implements ButtonOperationService {
    private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final DocumentApi documentApi;

    private final TaskApi taskApi;

    private final VariableApi variableApi;

    private final ProcessTrackApi processTrackApi;

    private final RuntimeApi runtimeApi;

    private final HistoricTaskApi historictaskApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final ProcessParamApi processParamApi;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ActRuDetailApi actRuDetailApi;

    private final HistoricProcessApi historicProcessApi;

    private final ButtonOperationApi buttonOperationApi;

    @Override
    public void complete(String taskId, String taskDefName, String desc, String infoOvert) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        String userName = Y9LoginUserHolder.getPosition().getName();
        Map<String, Object> map = new HashMap<>(16);
        if (StringUtils.isNotBlank(infoOvert)) {
            map.put("infoOvert", infoOvert);
        }
        variableApi.setVariables(tenantId, taskId, map);
        TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
        String processInstanceId = taskModel.getProcessInstanceId();

        /*
          1办结
         */
        documentApi.complete(tenantId, positionId, taskId);

        boolean isSubProcessChildNode = processDefinitionApi
                .isSubProcessChildNode(tenantId, taskModel.getProcessDefinitionId(), taskModel.getTaskDefinitionKey())
                .getData();
        if (isSubProcessChildNode) {// 子流程办结，不更新自定义历程信息
            return;
        }
        /*
          2更新自定义历程结束时间
         */
        List<ProcessTrackModel> ptModelList = processTrackApi.findByTaskId(tenantId, taskId).getData();
        for (ProcessTrackModel ptModel : ptModelList) {
            if (StringUtils.isBlank(ptModel.getEndTime())) {
                ptModel.setEndTime(sdf.format(new Date()));
                processTrackApi.saveOrUpdate(tenantId, ptModel);
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

        processTrackApi.saveOrUpdate(tenantId, ptModel);
    }

    @Override
    public Y9Result<String> complete4Sub(String taskId, String taskDefName, String desc) throws Exception {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        String mainSenderId = variableApi.getVariable(Y9LoginUserHolder.getTenantId(), taskId, SysVariables.MAINSENDERID).getData();
        if (StringUtils.isBlank(mainSenderId)) {
            return Y9Result.failure("办结失败：缺少主流程的发送人！");
        }
        /*
          1办结
         */
        List<String> userList = new ArrayList<>();
        userList.add(Y9JsonUtil.readValue(mainSenderId, String.class));
        documentApi.completeSub(tenantId, positionId, taskId, userList);
        return Y9Result.successMsg("办结成功！");
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
    public void multipleResumeTodo(String processInstanceIds, String desc) throws Exception {
        if (StringUtils.isBlank(processInstanceIds)) {
            return;
        }
        String[] array = processInstanceIds.split(";");
        for (String processInstanceId : array) {
            if (StringUtils.isNotBlank(processInstanceId)) {
                resumeTodo(processInstanceId, desc);
            }
        }
    }

    @Override
    public void resumeToDo(String processInstanceId, String desc) throws Exception {
        String positionId = Y9LoginUserHolder.getPositionId();
        String userName = Y9LoginUserHolder.getPosition().getName();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            /*
              1、恢复待办
             */

            String year;
            OfficeDoneInfoModel officeDoneInfoModel =
                    officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfoModel != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
            } else {
                ProcessParamModel processParamModel =
                        processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                year = processParamModel != null ? processParamModel.getCreateTime().substring(0, 4) : "";
            }

            HistoricTaskInstanceModel hisTaskModelTemp = historictaskApi
                    .getByProcessInstanceIdOrderByEndTimeDesc(tenantId, processInstanceId, year).getData().get(0);
            runtimeApi.recovery4Completed(tenantId, positionId, processInstanceId, year);

            /*
              2、添加流程的历程
             */
            ProcessTrackModel ptm = new ProcessTrackModel();
            ptm.setDescribed(desc);
            ptm.setProcessInstanceId(hisTaskModelTemp.getProcessInstanceId());
            ptm.setReceiverName(userName);
            ptm.setSenderName(userName);
            ptm.setStartTime(sdf.format(new Date()));
            ptm.setEndTime(sdf.format(new Date()));
            ptm.setTaskDefName("恢复待办");
            ptm.setTaskId(hisTaskModelTemp.getId());

            processTrackApi.saveOrUpdate(tenantId, ptm);

            /*
              2、添加流程的历程
             */
            ptm = new ProcessTrackModel();
            ptm.setDescribed(desc);
            ptm.setProcessInstanceId(hisTaskModelTemp.getProcessInstanceId());
            ptm.setReceiverName(userName);
            ptm.setSenderName(userName);
            ptm.setStartTime(sdf.format(new Date()));
            ptm.setEndTime("");
            ptm.setTaskDefName(hisTaskModelTemp.getName());
            ptm.setTaskId(hisTaskModelTemp.getId());
            processTrackApi.saveOrUpdate(tenantId, ptm);
        } catch (Exception e) {
            LOGGER.error("runtimeApi resumeToDo error", e);
            throw new Exception("runtimeApi resumeToDo error");
        }
    }

    @Override
    public void resumeTodo(String processInstanceId, String desc) throws Exception {
        String positionId = Y9LoginUserHolder.getPositionId();
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            // 1、恢复待办
            String year;
            OfficeDoneInfoModel officeDoneInfoModel =
                officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (officeDoneInfoModel != null) {
                year = officeDoneInfoModel.getStartTime().substring(0, 4);
            } else {
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                year = processParamModel != null ? processParamModel.getCreateTime().substring(0, 4) : "";
            }
            HistoricTaskInstanceModel hisTaskModel = historictaskApi
                .getByProcessInstanceIdOrderByEndTimeDesc(tenantId, processInstanceId, year).getData().get(0);
            runtimeApi.recovery4Completed(tenantId, positionId, processInstanceId, year);
            // 2、添加动作名称
            Map<String, Object> vars = new HashMap<>();
            vars.put("val", "重新激活");
            variableApi.setVariableByProcessInstanceId(tenantId, processInstanceId,
                SysVariables.ACTIONNAME + ":" + positionId, vars);
            // 3、重定位
            buttonOperationApi.reposition(tenantId, positionId, hisTaskModel.getId(),
                hisTaskModel.getTaskDefinitionKey(), List.of(hisTaskModel.getAssignee()), "重新激活", "");
        } catch (Exception e) {
            LOGGER.error("runtimeApi resumeToDo error", e);
            throw new Exception("runtimeApi resumeToDo error");
        }
    }

    @Override
    public Y9Result<String> deleteTodos(String[] taskIdAndProcessSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int total = taskIdAndProcessSerialNumbers.length;
        AtomicInteger success = new AtomicInteger();
        AtomicInteger error = new AtomicInteger();
        List<TargetModel> nodeList = new ArrayList<>();
        for (String taskIdAndProcessSerialNumber : taskIdAndProcessSerialNumbers) {
            try {
                String[] tpArr = taskIdAndProcessSerialNumber.split(":");
                TaskModel task = taskApi.findById(tenantId, tpArr[0]).getData();
                if (null == task) {
                    error.getAndIncrement();
                    continue;
                }
                if (nodeList.isEmpty()) {
                    String startNode = processDefinitionApi.getStartNodeKeyByProcessDefinitionId(tenantId, task.getProcessDefinitionId()).getData();
                    nodeList = processDefinitionApi.getTargetNodes(tenantId, task.getProcessDefinitionId(), startNode).getData();
                }
                boolean canDelete = nodeList.stream().anyMatch(node -> node.getTaskDefKey().equals(task.getTaskDefinitionKey()));
                if (canDelete) {
                    actRuDetailApi.deleteByProcessSerialNumber(tenantId, tpArr[1]);
                    success.getAndIncrement();
                } else {
                    error.getAndIncrement();
                }
            } catch (Exception e) {
                error.getAndIncrement();
                e.printStackTrace();
            }
        }
        return Y9Result.successMsg("成功删除" + success.get() + "条，失败" + error.get() + "条，共" + total + "条");
    }

    @Override
    public Y9Result<String> deleteByProcessSerialNumbers(String[] processSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int total = processSerialNumbers.length;
        AtomicInteger success = new AtomicInteger();
        AtomicInteger error = new AtomicInteger();
        List<TargetModel> nodeList = new ArrayList<>();
        for (String processSerialNumber : processSerialNumbers) {
            try {
                if (actRuDetailApi.deleteByProcessSerialNumber(tenantId, processSerialNumber).isSuccess()) {
                    success.getAndIncrement();
                } else {
                    error.getAndIncrement();
                }
            } catch (Exception e) {
                error.getAndIncrement();
                e.printStackTrace();
            }
        }
        return Y9Result.successMsg("成功删除" + success.get() + "条，失败" + error.get() + "条，共" + total + "条");
    }

    @Override
    public Y9Result<String> recovers(String[] processSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        for (String processSerialNumber : processSerialNumbers) {
            actRuDetailApi.recoveryByProcessSerialNumber(tenantId, processSerialNumber);
        }
        return Y9Result.successMsg("成功恢复" + processSerialNumbers.length + "条待办");
    }

    @Override
    public Y9Result<String> removes(String[] processSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        ProcessParamModel processParamModel;
        for (String processSerialNumber : processSerialNumbers) {
            actRuDetailApi.removeByProcessSerialNumber(tenantId, processSerialNumber);
            processParamModel = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            // 删除流程实例
            historicProcessApi.deleteProcessInstance(tenantId, processParamModel.getProcessInstanceId());
        }
        return Y9Result.successMsg("成功删除" + processSerialNumbers.length + "条待办");
    }
}
