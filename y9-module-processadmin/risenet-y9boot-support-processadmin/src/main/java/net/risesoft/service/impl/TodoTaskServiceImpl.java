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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
@Service(value = "todoTaskService")
@Slf4j
public class TodoTaskServiceImpl implements TodoTaskService {

    @Autowired
    private OrgUnitApi orgUnitManager;

    @Autowired
    private TodoTaskApi todoTaskManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ErrorLogApi errorLogManager;

    @Autowired
    private Y9Properties y9Conf;

    @Override
    public void deleteTodo(final DelegateTask task, final Map<String, Object> map) {
        String taskId = task.getId();
        String processInstanceId = task.getProcessInstanceId();
        try {
            Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
            if (!todoSwitch) {
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
            LOGGER.warn("##########################删除超级待办：失败{}##########################", task.getId());
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
            e.printStackTrace();
        }
    }

    @Override
    public void deleteTodoByProcessInstanceId(final FlowableEvent event, final Map<String, Object> variables) {
        try {
            Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
            if (!todoSwitch) {
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
            LOGGER.warn("##########################删除超级待办：失败##########################");

            e.printStackTrace();
        }
    }

    /**
     * Description:
     *
     * @param task
     * @param map
     */
    @Override
    public void saveTodoTask(final DelegateTask task, final Map<String, Object> map) {
        Boolean todoSwitch = y9Conf.getApp().getProcessAdmin().getTodoSwitch();
        if (!todoSwitch) {
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
                processParamManager.findByProcessSerialNumber(tenantId, processSerialNumber);
            String itemId = processParamModel.getItemId();
            String itemName = processParamModel.getItemName();
            if (StringUtils.isNotBlank(tenantId)) {
                String senderDepartmentId = "";
                String receiverDepartmentId = "";
                String assigneeName = "";
                OrgUnit receiverPerson = orgUnitManager.getOrgUnit(tenantId, assignee).getData();
                OrgUnit senderPerson = orgUnitManager.getOrgUnit(tenantId, taskSenderId).getData();
                senderDepartmentId = senderPerson.getParentId();
                receiverDepartmentId = receiverPerson.getParentId();
                assigneeName = receiverPerson.getName();
                OrgUnit senderOrgUnit = orgUnitManager.getOrgUnit(tenantId, senderDepartmentId).getData();
                OrgUnit receiverOrgUnit = orgUnitManager.getOrgUnit(tenantId, receiverDepartmentId).getData();
                String systemName = processParamModel.getSystemName();
                String systemCnName = processParamModel.getSystemCnName();
                String todoTaskUrlPrefix = processParamModel.getTodoTaskUrlPrefix();
                String level = processParamModel.getCustomLevel() == null ? "" : processParamModel.getCustomLevel();
                String docNumber = processParamModel.getCustomNumber();
                String urgency = "0", yiban = "一般", zhongyao = "重要", jinji = "紧急";
                if (yiban.equals(level)) {
                    urgency = "0";
                } else if (zhongyao.equals(level)) {
                    urgency = "1";
                } else if (jinji.equals(level)) {
                    urgency = "2";
                }

                if ("普通".equals(level)) {
                    urgency = "0";
                } else if ("急".equals(level)) {
                    urgency = "1";
                } else if ("特急".equals(level)) {
                    urgency = "2";
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
            LOGGER.warn("##########################保存超级待办失败-taskId:{}##########################", task.getId());
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
            e.printStackTrace();
        }
    }

}
