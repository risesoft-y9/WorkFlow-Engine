package net.risesoft.service;

import java.util.HashMap;
import java.util.List;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Document4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

    private final TodoTaskApi todoTaskApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

    private final TaskApi taskApi;

    private final Document4PositionApi document4PositionApi;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 循环发送
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param processInstanceId 流程实例id
     */
    @Async
    public void loopSending(final String tenantId, final String positionId, final String itemId,
        final String processSerialNumber, final String processDefinitionKey, final String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<TaskModel> taskModelList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
        if (taskModelList != null && !taskModelList.isEmpty()) {
            TaskModel taskModel = taskModelList.get(0);
            String taskDefinitionKey = taskModel.getTaskDefinitionKey();
            String processDefinitionId = taskModel.getProcessDefinitionId();
            String taskId = taskModel.getId();
            if (taskDefinitionKey.contains("skip_")) {// 当前任务自动跳过
                List<TargetModel> targetModelList =
                    processDefinitionApi.getTargetNodes(tenantId, processDefinitionId, taskDefinitionKey).getData();
                if (targetModelList != null && !targetModelList.isEmpty()) {
                    TargetModel targetModel = targetModelList.get(0);
                    String routeToTaskId = targetModel.getTaskDefKey();
                    List<String> userChoiceList =
                        document4PositionApi
                            .parserUser(tenantId, positionId, itemId, processDefinitionId, routeToTaskId,
                                targetModel.getTaskDefName(), processInstanceId, targetModel.getMultiInstance())
                            .getData();
                    if (userChoiceList != null && !userChoiceList.isEmpty()) {
                        String userChoice = "";
                        for (String userId : userChoiceList) {
                            userChoice = Y9Util.genCustomStr(userChoice, "6:" + userId, ";");
                        }
                        Y9Result<String> y9Result1 = document4PositionApi.saveAndForwarding(tenantId, positionId,
                            processInstanceId, taskId, "", itemId, processSerialNumber, processDefinitionKey,
                            userChoice, "", routeToTaskId, new HashMap<>());
                        if (y9Result1.isSuccess()) {
                            // 异步循环发送
                            this.loopSending(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey,
                                processInstanceId);
                        }
                    }
                }
            }
        }
    }

    /**
     * 更新统一待办，抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     */
    @Async
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSong4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollow4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

}
