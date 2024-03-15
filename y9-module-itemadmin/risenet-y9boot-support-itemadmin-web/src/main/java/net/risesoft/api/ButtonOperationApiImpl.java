package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ButtonOperationApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.platform.Person;
import net.risesoft.model.processadmin.HistoricTaskInstanceModel;
import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.service.DocumentService;
import net.risesoft.service.MultiInstanceService;
import net.risesoft.util.CommonOpt;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.HistoricTaskApiClient;
import y9.client.rest.processadmin.ProcessDefinitionApiClient;
import y9.client.rest.processadmin.RuntimeApiClient;
import y9.client.rest.processadmin.SpecialOperationApiClient;
import y9.client.rest.processadmin.TaskApiClient;
import y9.client.rest.processadmin.VariableApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/buttonOperation")
public class ButtonOperationApiImpl implements ButtonOperationApi {

    @Autowired
    private DocumentService documentService;

    @Autowired
    private MultiInstanceService multiInstanceService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private TaskApiClient taskManager;

    @Autowired
    private VariableApiClient variableManager;

    @Autowired
    private ProcessDefinitionApiClient processDefinitionManager;

    @Autowired
    private HistoricTaskApiClient historicTaskManager;

    @Autowired
    private RuntimeApiClient runtimeManager;

    @Autowired
    private SpecialOperationApiClient specialOperationManager;

    @Override
    @PostMapping(value = "/addMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
    public void addMultiInstanceExecution(String tenantId, String userId, String activityId, String parentExecutionId,
        String taskId, String elementUser) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        multiInstanceService.addMultiInstanceExecution(activityId, parentExecutionId, taskId, elementUser);
    }

    @Override
    @PostMapping(value = "/deleteMultiInstanceExecution", produces = MediaType.APPLICATION_JSON_VALUE)
    public void deleteMultiInstanceExecution(String tenantId, String userId, String executionId, String taskId,
        String elementUser) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        multiInstanceService.deleteMultiInstanceExecution(executionId, taskId, elementUser);
    }

    @Override
    @PostMapping(value = "/directSend", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean directSend(String tenantId, String userId, String taskId, String routeToTask,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        boolean b = false;
        try {
            ProcessInstanceModel processInstance = runtimeManager.getProcessInstance(tenantId, processInstanceId);
            String startUserId = "3" + SysVariables.COLON + processInstance.getStartUserId();
            documentService.forwarding(taskId, UtilConsts.TRUE, startUserId, routeToTask, "");
            b = true;
        } catch (Exception e) {
            b = false;
            e.printStackTrace();
        }
        return b;
    }

    @Override
    @PostMapping(value = "/refuseClaimRollback", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> refuseClaimRollback(String tenantId, String userId, String taskId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        String deptId = person.getParentId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "退回成功");
            taskManager.claim(tenantId, userId, taskId);
            TaskModel currentTask = taskManager.findById(tenantId, taskId);
            List<String> userAndDeptIdList = new ArrayList<String>();
            // 获取当前任务的前一个任务
            HistoricTaskInstanceModel hti = historicTaskManager.getThePreviousTask(tenantId, taskId);
            // 前一任务的受让人，标题
            String assignee = hti.getAssignee();
            userAndDeptIdList.add(assignee);
            String htiMultiInstance = processDefinitionManager.getNodeType(tenantId, hti.getProcessDefinitionId(),
                hti.getTaskDefinitionKey());
            Map<String, Object> variables = CommonOpt.setVariables(userId + SysVariables.COLON + deptId,
                person.getName(), hti.getTaskDefinitionKey(), userAndDeptIdList, "");
            Map<String, Object> val = new HashMap<String, Object>();
            val.put("val", SysVariables.REFUSECLAIMROLLBACK);
            variableManager.setVariableLocal(tenantId, taskId, SysVariables.REFUSECLAIMROLLBACK, val);
            taskManager.completeWithVariables(tenantId, taskId, variables);
            // 如果上一任务是并行，则回退时设置主办人
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

    @Override
    @PostMapping(value = "/reposition", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void reposition(String tenantId, String userId, String taskId, String repositionToTaskId,
        @RequestBody List<String> userChoice, String reason, String sponsorGuid) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        specialOperationManager.reposition(tenantId, userId, taskId, repositionToTaskId, userChoice, reason,
            sponsorGuid);
    }

    @Override
    @PostMapping(value = "/rollBack", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollBack(String tenantId, String userId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        specialOperationManager.rollBack(tenantId, userId, taskId, reason);
    }

    @Override
    @PostMapping(value = "/rollbackToSender", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToSender(String tenantId, String userId, String taskId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        specialOperationManager.rollbackToSender(tenantId, userId, taskId);
    }

    @Override
    @PostMapping(value = "/rollbackToStartor", produces = MediaType.APPLICATION_JSON_VALUE)
    public void rollbackToStartor(String tenantId, String userId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        specialOperationManager.rollbackToStartor(tenantId, userId, taskId, reason);
    }

    @Override
    @PostMapping(value = "/specialComplete", produces = MediaType.APPLICATION_JSON_VALUE)
    public void specialComplete(String tenantId, String userId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        specialOperationManager.specialComplete(tenantId, userId, taskId, reason);
    }

    @Override
    @PostMapping(value = "/takeback", produces = MediaType.APPLICATION_JSON_VALUE)
    public void takeback(String tenantId, String userId, String taskId, String reason) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        specialOperationManager.takeBack(tenantId, userId, taskId, reason);
    }
}
