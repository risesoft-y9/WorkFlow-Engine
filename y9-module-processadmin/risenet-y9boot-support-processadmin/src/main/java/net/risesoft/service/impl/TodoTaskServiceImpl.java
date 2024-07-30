package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.delegate.DelegateTask;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.todo.TodoTask;
import net.risesoft.service.TodoTaskService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Service(value = "todoTaskService")
public class TodoTaskServiceImpl implements TodoTaskService {

    private final OrgUnitApi orgUnitApi;

    private final TodoTaskApi todoTaskManager;

    private final ProcessParamApi processParamManager;

    private final ErrorLogApi errorLogManager;

    private final Y9Properties y9Conf;

    @Override
    public void deleteTodo(final DelegateTask task, final Map<String, Object> map) {
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        try {
            Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
            if (todoSwitch == null || Boolean.FALSE.equals(todoSwitch)) {
                LOGGER.info("######################保存超级待办按钮已关闭,如需保存超级待办请更改配置文件######################");
                return;
            }
            String tenantId = (String)map.get("tenantId");
            String assigneeId = task.getAssignee();
            boolean msg = todoTaskManager.deleteTodoTaskByTaskIdAndReceiverId(tenantId, task.getId(), assigneeId);
            if (msg) {
                LOGGER.info("##########################删除超级待办：成功-delete##########################");
            } else {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());
                ErrorLogModel errorLogModel = new ErrorLogModel();
                errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLogModel.setCreateTime(time);
                errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_DELETE_TODO);
                errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLogModel.setExtendField("删除统一待办返回false");
                errorLogModel.setProcessInstanceId(processInstanceId);
                errorLogModel.setTaskId(taskId);
                errorLogModel.setText("false");
                errorLogModel.setUpdateTime(time);
                errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
                LOGGER.info("##########################删除超级待办：失败-delete,taskId:{}##########################",
                    task.getId());
            }
        } catch (Exception e) {
            LOGGER.warn("##########################删除超级待办：失败{}##########################", e.getMessage());
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_DELETE_TODO);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
            errorLogModel.setExtendField("删除统一待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        }
    }

    @Override
    public void deleteTodoByProcessInstanceId(final FlowableEvent event, final Map<String, Object> variables) {
        try {
            Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
            if (todoSwitch == null || Boolean.FALSE.equals(todoSwitch)) {
                LOGGER.info("######################保存超级待办按钮已关闭,如需保存超级待办请更改配置文件######################");
                return;
            }
            FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl)event;
            ExecutionEntityImpl executionEntity = (ExecutionEntityImpl)entityEvent.getEntity();
            VariableInstance vie = executionEntity.getVariableInstance("tenantId");
            String tenantId = vie != null ? vie.getTextValue() : "";
            boolean msg = todoTaskManager.deleteByProcessInstanceId(tenantId, executionEntity.getProcessInstanceId());
            if (msg) {
                LOGGER.info("##########################删除超级待办：成功-delete##########################");
            } else {
                LOGGER.info("##########################删除超级待办：失败-delete,taskId:{}##########################",
                    executionEntity.getProcessInstanceId());
            }
        } catch (Exception e) {
            LOGGER.error("##########################删除超级待办：失败##########################" + e.getMessage());
        }
    }

    /**
     * Description:
     *
     * @param task DelegateTask
     * @param map Map<String, Object>
     */
    @Override
    public void saveTodoTask(final DelegateTask task, final Map<String, Object> map) {
        Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
        if (todoSwitch == null || Boolean.FALSE.equals(todoSwitch)) {
            LOGGER.info("######################保存超级待办按钮已关闭,如需保存超级待办请更改配置文件######################");
            return;
        }
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        try {
            String taskSenderId = (String)map.get(SysVariables.TASKSENDERID);
            String senderName = (String)map.get(SysVariables.TASKSENDER);
            String assignee = task.getAssignee();
            String tenantId = (String)map.get("tenantId");
            Y9LoginUserHolder.setTenantId(tenantId);
            String processSerialNumber = (String)map.get("processSerialNumber");
            ProcessParamModel processParamModel =
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            String itemId = processParamModel.getItemId();
            String itemName = processParamModel.getItemName();
            if (StringUtils.isNotBlank(tenantId)) {
                String senderDepartmentId, receiverDepartmentId, assigneeName;
                OrgUnit receiverPerson = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, assignee).getData();
                OrgUnit senderPerson = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, taskSenderId).getData();
                senderDepartmentId = senderPerson.getParentId();
                receiverDepartmentId = receiverPerson.getParentId();
                assigneeName = receiverPerson.getName();
                OrgUnit senderOrgUnit = orgUnitApi.getOrgUnit(tenantId, senderDepartmentId).getData();
                OrgUnit receiverOrgUnit = orgUnitApi.getOrgUnit(tenantId, receiverDepartmentId).getData();
                String systemName = processParamModel.getSystemName();
                String systemCnName = processParamModel.getSystemCnName();
                String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();
                String level = processParamModel.getCustomLevel() == null ? "" : processParamModel.getCustomLevel();
                String docNumber = processParamModel.getCustomNumber();
                String urgency = "0", commonly = "一般", important = "重要", urgent = "紧急";
                if (commonly.equals(level)) {
                    urgency = "0";
                } else if (important.equals(level)) {
                    urgency = "1";
                } else if (urgent.equals(level)) {
                    urgency = "2";
                }
                switch (level) {
                    case "普通":
                        urgency = "0";
                        break;
                    case "急":
                        urgency = "1";
                        break;
                    case "特急":
                        urgency = "2";
                        break;
                }

                TodoTask todo = new TodoTask();
                todo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                todo.setTenantId(tenantId);
                todo.setTaskId(task.getId());
                todo.setSystemName(systemName);
                todo.setSystemCnName(systemCnName);
                todo.setAppName(itemId);
                todo.setAppCnName(itemName);
                todo.setTitle(processParamModel.getTitle());
                todo.setReceiverId(assignee);
                todo.setReceiverName(assigneeName);
                todo.setReceiverDepartmentId(receiverDepartmentId);
                todo.setReceiverDepartmentName(receiverOrgUnit.getName());
                todo.setSenderId(taskSenderId);
                todo.setSenderName(senderName);
                todo.setSenderDepartmentId(senderDepartmentId);
                todo.setSenderDepartmentName(senderOrgUnit.getName());
                todo.setSendTime(new Date());
                todo.setIsNewTodo("1");
                todo.setUrgency(urgency);
                todo.setDocNumber(docNumber);
                todo.setEmailAble(false);
                if (StringUtils.isBlank(processParamModel.getSended())
                    || UtilConsts.FALSE.equals(processParamModel.getSended())) {
                    // 第一步新建产生的任务，不进行提醒
                    todo.setEmailAble(true);
                }
                todo.setProcessInstanceId(task.getProcessInstanceId());
                String url = todoTaskUrlPrefix + "?taskId=" + task.getId() + "&itemId=" + itemId
                    + "&processInstanceId=&type=fromTodo";
                todo.setUrl(url);
                boolean b = todoTaskManager.saveTodoTask(tenantId, todo);
                if (b) {
                    LOGGER.info("##########################保存超级待办成功-assignment##########################");
                } else {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String time = sdf.format(new Date());
                    ErrorLogModel errorLogModel = new ErrorLogModel();
                    errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    errorLogModel.setCreateTime(time);
                    errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_TODO_TASK);
                    errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
                    errorLogModel.setExtendField("保存统一待办返回false");
                    errorLogModel.setProcessInstanceId(processInstanceId);
                    errorLogModel.setTaskId(taskId);
                    errorLogModel.setText("false");
                    errorLogModel.setUpdateTime(time);
                    errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
                    LOGGER.info("##########################保存超级待办失败-生成超级待办信息时发生异常-taskId:{}##########################",
                        task.getId());
                }
            }
        } catch (Exception e) {
            LOGGER.error("##########################保存超级待办失败:{}##########################", e.getMessage());
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_TODO_TASK);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
            errorLogModel.setExtendField("保存统一待办失败");
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogManager.saveErrorLog(Y9LoginUserHolder.getTenantId(), errorLogModel);
        }
    }

}
