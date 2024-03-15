package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.ButtonOperation4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RuntimeApi;
import net.risesoft.api.processadmin.SpecialOperationApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.DocumentService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 按钮操作接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/buttonOperation4Position")
public class ButtonOperationApiImpl implements ButtonOperation4PositionApi {

    @Resource(name = "documentService")
    private DocumentService documentService;

    @Autowired
    private MultiInstanceService multiInstanceService;

    @Autowired
    private PositionApi positionManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private HistoricTaskApi historicTaskManager;

    @Autowired
    private RuntimeApi runtimeManager;

    @Autowired
    private SpecialOperationApi specialOperationManager;

    /**
     * 加签
     *
     * @param tenantId 租户id
     * @param activityId 活动Id
     * @param parentExecutionId 父执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/addMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addMultiInstanceExecution(String tenantId, String activityId, String parentExecutionId, String taskId,
        String elementUser) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, taskId, elementUser);
    }

    /**
     * 减签
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @param taskId 任务id
     * @param elementUser 选择人id
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/deleteMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMultiInstanceExecution(String tenantId, String executionId, String taskId, String elementUser)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        multiInstanceService.deleteMultiInstanceExecution(executionId, taskId, elementUser);
    }

    /**
     * 直接发送至流程启动人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param routeToTask 任务key
     * @param processInstanceId 流程实例ID
     * @return boolean
     */
    @Override
    @PostMapping(value = "/directSend", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean directSend(String tenantId, String positionId, String taskId, String routeToTask,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        boolean b = false;
        try {
            ProcessInstanceModel processInstance = runtimeManager.getProcessInstance(tenantId, processInstanceId);
            String startUserId = "6" + SysVariables.COLON + processInstance.getStartUserId();
            documentService.forwarding(taskId, "true", startUserId, routeToTask, "");
            b = true;
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    /**
     * 最后一人拒签退回
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @return Map
     */
    @Override
    @PostMapping(value = "/refuseClaimRollback", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> refuseClaimRollback(String tenantId, String positionId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "退回成功");
            taskManager.claim(tenantId, positionId, taskId);
            TaskModel currentTask = taskManager.findById(tenantId, taskId);
            List<String> userAndDeptIdList = new ArrayList<String>();
            // 获取当前任务的前一个任务
            HistoricTaskInstanceModel hti = historicTaskManager.getThePreviousTask(tenantId, taskId);
            // 前一任务的受让人，标题
            String assignee = hti.getAssignee();
            userAndDeptIdList.add(assignee);
            Position position = positionManager.get(tenantId, positionId).getData();
            String htiMultiInstance = processDefinitionManager.getNodeType(tenantId, hti.getProcessDefinitionId(),
                hti.getTaskDefinitionKey());
            Map<String, Object> variables = CommonOpt.setVariables(positionId, position.getName(),
                hti.getTaskDefinitionKey(), userAndDeptIdList, "");
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("val", SysVariables.REFUSECLAIMROLLBACK);
            variableManager.setVariableLocal(tenantId, taskId, SysVariables.REFUSECLAIMROLLBACK, val);
            taskManager.completeWithVariables(tenantId, taskId, variables);
            /**
             * 如果上一任务是并行，则回退时设置主办人
             */
            if (SysVariables.PARALLEL.equals(htiMultiInstance)) {
                List<TaskModel> taskNextList1 =
                    taskManager.findByProcessInstanceId(tenantId, currentTask.getProcessInstanceId());
                for (TaskModel taskModelNext : taskNextList1) {
                    Map<String, Object> val1 = new HashMap<String, Object>();
                    val1.put("val", assignee.split(SysVariables.COLON)[0]);
                    variableManager.setVariableLocal(tenantId, taskModelNext.getId(), SysVariables.PARALLELSPONSOR,
                        val1);
                }
            }
        } catch (Exception e) {
            taskManager.unclaim(tenantId, taskId);
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "退回失败");
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 重定位
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param repositionToTaskId 重定位任务key
     * @param userChoice 选择人id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/reposition", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition(String tenantId, String positionId, String taskId, String repositionToTaskId,
        @RequestParam("userChoice") List<String> userChoice, String reason, String sponsorGuid) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.reposition4Position(tenantId, positionId, taskId, repositionToTaskId, userChoice,
            reason, sponsorGuid);
    }

    /**
     * 退回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/rollBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack(String tenantId, String positionId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.rollBack4Position(tenantId, positionId, taskId, reason);
    }

    /**
     * 发回给上一步的发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/rollbackToSender", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender(String tenantId, String positionId, String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.rollbackToSender4Position(tenantId, positionId, taskId);
    }

    /**
     * 退回操作，直接退回到办件登记人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param resson 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/rollbackToStartor", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor(String tenantId, String positionId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.rollbackToStartor4Position(tenantId, positionId, taskId, reason);
    }

    /**
     * 特殊办结
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/specialComplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete(String tenantId, String positionId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.specialComplete4Position(tenantId, positionId, taskId, reason);
    }

    /**
     * 收回操作
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param taskId 任务id
     * @param reason 原因
     * @throws Exception exception
     */
    @Override
    @PostMapping(value = "/takeback", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeback(String tenantId, String positionId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.takeBack4Position(tenantId, positionId, taskId, reason);
    }
}
