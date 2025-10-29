package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.TaskVariable;
import net.risesoft.entity.opinion.Opinion;
import net.risesoft.entity.opinion.OpinionHistory;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.enums.TodoTaskEventActionEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.TodoTaskEventModel;
import net.risesoft.model.platform.org.Department;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.TaskVariableRepository;
import net.risesoft.repository.opinion.OpinionHistoryRepository;
import net.risesoft.repository.opinion.OpinionRepository;
import net.risesoft.service.AsyncForwardingHandleService;
import net.risesoft.service.AsyncHandleService;
import net.risesoft.service.ErrorLogService;
import net.risesoft.service.Process4SearchService;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.service.core.ProcessParamService;
import net.risesoft.service.event.Y9TodoUpdateEvent;
import net.risesoft.service.word.Y9WordHistoryService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "asyncHandleService")
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class AsyncHandleServiceImpl implements AsyncHandleService {

    private final TaskVariableRepository taskVariableRepository;

    private final ErrorLogService errorLogService;

    private final PositionApi positionApi;

    private final ProcessParamService processParamService;

    private final OpinionHistoryRepository opinionHistoryRepository;

    private final DraftEntityRepository draftEntityRepository;

    private final Y9WordHistoryService y9WordHistoryService;

    private final OpinionRepository opinionRepository;

    private final VariableApi variableApi;

    private final TaskApi taskApi;

    private final HistoricTaskApi historictaskApi;

    private final Process4SearchService process4SearchService;

    private final DepartmentApi departmentApi;

    private final ItemTaskConfService itemTaskConfService;

    private final AsyncForwardingHandleService asyncForwardingHandleService;

    @Async
    @Override
    public void forwarding(final String tenantId, final OrgUnit orgUnit, final String processInstanceId,
        final ProcessParam processParam, final String sponsorHandle, final String sponsorGuid, final String taskId,
        final FlowElementModel flowElementModel, final Map<String, Object> variables,
        final List<String> userAndDeptIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9FlowableHolder.setOrgUnit(orgUnit);
        try {
            this.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, flowElementModel,
                variables, userAndDeptIdList);
        } catch (Exception e) {
            try {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                int num = userAndDeptIdList.size();
                String time = Y9DateTimeUtils.formatCurrentDateTime();
                // 发送失败,可能会出现统一待办已经保存成功,但任务没有在数据库产生,需要删除统一待办数据,只保存当前发送人的待办任务。
                Y9Context.publishEvent(new Y9TodoUpdateEvent<>(new TodoTaskEventModel(
                    TodoTaskEventActionEnum.DELETE_PROCESSINSTANCEID, tenantId, processInstanceId, taskId, "0")));
                // 保存任务发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("发送多人失败：" + num);
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);

                TaskVariable taskVariable = taskVariableRepository.findByTaskIdAndKeyName(taskId, "isForwarding");
                taskVariable.setUpdateTime(time);
                taskVariable.setText("false:" + num);
                taskVariableRepository.save(taskVariable);
            } catch (Exception e2) {
                LOGGER.error("保存错误日志失败", e2);
            }
        }
    }

    @Override
    public void forwarding4Gfg(String processInstanceId, ProcessParam processParam, String sponsorHandle,
        String sponsorGuid, String taskId, FlowElementModel flowElementModel, Map<String, Object> variables,
        List<String> userList) {
        OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = orgUnit.getId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(
            processParam.getItemId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (null != itemTaskConf && itemTaskConf.getSignTask()) {
            sponsorHandle = "true";
        }
        // 判断是否是主办办理，如果是，需要将协办未办理的的任务默认办理
        if (StringUtils.isNotBlank(sponsorHandle) && UtilConsts.TRUE.equals(sponsorHandle)) {
            List<TaskModel> taskNextList1 = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            for (TaskModel taskNext : taskNextList1) {
                if (!(taskId.equals(taskNext.getId()))) {
                    taskApi.complete(tenantId, taskNext.getId());
                    historictaskApi.setTenantId(tenantId, taskNext.getId());
                }
            }
        }
        // 主办设置类型，2为部门节点，3为人员节点
        String type;
        // 主办部门或人员的id
        String sponsor;
        if (StringUtils.isNotBlank(sponsorGuid)) {
            type = sponsorGuid.substring(0, 1);
            sponsor = sponsorGuid.substring(2);
            if (type.equals(String.valueOf(ItemPrincipalTypeEnum.DEPT.getValue()))) {
                // 设置主办部门下的第一个人员为主办人
                sponsorGuid = this.getSponsorPosition("", sponsor);
            } else {
                sponsorGuid = sponsor;
            }
        }
        processParam.setSended("true");
        processParam.setSponsorGuid(sponsorGuid);
        processParamService.saveOrUpdate(processParam);
        taskApi.completeWithVariables(tenantId, taskId, orgUnitId, variables);

        // 保存流程信息到ES
        process4SearchService.saveToDataCenter1(tenantId, taskId, processParam);
        String executionId = task.getExecutionId();
        asyncForwardingHandleService.forwardingHandle(tenantId, orgUnitId, task, executionId, processInstanceId,
            flowElementModel, sponsorGuid, processParam, userList);
    }

    @Override
    public void forwarding4Task(String processInstanceId, ProcessParam processParam, String sponsorHandle,
        String sponsorGuid, String taskId, FlowElementModel flowElementModel, Map<String, Object> variables,
        List<String> userList) {
        OrgUnit orgUnit = Y9FlowableHolder.getOrgUnit();
        String tenantId = Y9LoginUserHolder.getTenantId(), orgUnitId = orgUnit.getId();
        TaskModel task = taskApi.findById(tenantId, taskId).getData();
        ItemTaskConf itemTaskConf = itemTaskConfService.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(
            processParam.getItemId(), task.getProcessDefinitionId(), task.getTaskDefinitionKey());
        if (null != itemTaskConf && itemTaskConf.getSignTask()) {
            sponsorHandle = "true";
        }
        // 判断是否是主办办理，如果是，需要将协办未办理的的任务默认办理
        if (StringUtils.isNotBlank(sponsorHandle) && UtilConsts.TRUE.equals(sponsorHandle)) {
            List<TaskModel> taskNextList1 = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            for (TaskModel taskNext : taskNextList1) {
                if (!(taskId.equals(taskNext.getId()))) {
                    taskApi.complete(tenantId, taskNext.getId());
                    historictaskApi.setTenantId(tenantId, taskNext.getId());
                }
            }
        }
        // 主办设置类型，2为部门节点，3为人员节点
        String type;
        // 主办部门或人员的id
        String sponsor;
        if (StringUtils.isNotBlank(sponsorGuid)) {
            type = sponsorGuid.substring(0, 1);
            sponsor = sponsorGuid.substring(2);
            if (type.equals(String.valueOf(ItemPrincipalTypeEnum.DEPT.getValue()))) {
                // 设置主办部门下的第一个人员为主办人
                sponsorGuid = this.getSponsorPosition("", sponsor);
            } else {
                sponsorGuid = sponsor;
            }
        }
        Map<String, Object> vmap = new HashMap<>(16);
        // 解决协作状态串行办理历程的所有人员显示
        vmap.put(SysVariables.USERS, userList);
        variableApi.setVariables(tenantId, taskId, vmap);
        processParam.setSended("true");
        processParam.setSponsorGuid(sponsorGuid);
        processParamService.saveOrUpdate(processParam);
        taskApi.completeWithVariables(tenantId, taskId, orgUnitId, variables);

        // 保存流程信息到ES
        process4SearchService.saveToDataCenter1(tenantId, taskId, processParam);
        String executionId = task.getExecutionId();
        asyncForwardingHandleService.forwardingHandle(tenantId, orgUnitId, task, executionId, processInstanceId,
            flowElementModel, sponsorGuid, processParam, userList);
    }

    private String getSponsorPosition(String id, String deptId) {
        List<Department> deptList = departmentApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 = positionApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        if (!list0.isEmpty()) {
            id = list0.get(0).getId();
        } else {
            for (Department dept : deptList) {
                this.getSponsorPosition(id, dept.getId());
                if (!id.isEmpty()) {
                    break;
                }
            }
        }
        return id;
    }

    @Async
    @Override
    public void saveOpinionHistory(final String tenantId, final Opinion oldOpinion, final String opinionType) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            OpinionHistory history = new OpinionHistory();
            history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            history.setContent(oldOpinion.getContent());
            history.setCreateDate(oldOpinion.getCreateDate());
            history.setSaveDate(Y9DateTimeUtils.formatCurrentDateTime());
            history.setDeptId(oldOpinion.getDeptId());
            history.setDeptName(oldOpinion.getDeptName());
            history.setModifyDate(oldOpinion.getModifyDate());
            history.setOpinionFrameMark(oldOpinion.getOpinionFrameMark());
            history.setOpinionType(opinionType);
            history.setProcessInstanceId(oldOpinion.getProcessInstanceId());
            history.setProcessSerialNumber(oldOpinion.getProcessSerialNumber());
            history.setTaskId(oldOpinion.getTaskId());
            history.setTenantId(oldOpinion.getTenantId());
            history.setUserId(oldOpinion.getUserId());
            history.setUserName(oldOpinion.getUserName());
            opinionHistoryRepository.save(history);
        } catch (Exception e) {
            LOGGER.warn("*****saveOpinionHistory发生异常*****", e);
        }
    }

    @Async
    @Override
    public void sendMsgRemind(final String tenantId, final String userId, final String processSerialNumber,
        final String content) {
        /*try {
            if (!itemSettingService.getConfSetting().isMsgSwitch()) {
                return;
            }
            Y9LoginUserHolder.setTenantId(tenantId);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            String personIds = itemMsgRemindApi.getRemindConfig(tenantId, userId, "opinionRemind").getData();
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            if (StringUtils.isNotBlank(personIds) && StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                LOGGER.info("*****意见填写提醒*****");
                String title = processParam.getTitle();
                String itemId = processParam.getItemId();
                String todoTaskUrlPrefix = itemSettingApi.getConfSetting(tenantId).getData().getTodoTaskUrlPrefix();
                String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId="
                    + processParam.getProcessInstanceId() + "&type=fromCplane";
                Date date = new Date();
                String newPersonIds = "";
                String[] ids = personIds.split(",");
                OfficeDoneInfo officeDoneInfo =
                    officeDoneInfoService.findByProcessInstanceId(processParam.getProcessInstanceId());
                for (String id : ids) {
                    //参与该件的人才提醒
                              if (officeDoneInfo != null && officeDoneInfo.getAllUserId().contains(id)) {
                               if (!newPersonIds.contains(id)) {
                                   newPersonIds = Y9Util.genCustomStr(newPersonIds, id);
                               }
                              }
                              }
                              if (StringUtils.isNotBlank(newPersonIds)) {
                              ItemMsgRemindModel info = new ItemMsgRemindModel();
                              info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                              info.setItemId(processParam.getItemId());
                              info.setMsgType(ItemMsgRemindModel.MSG_TYPE_OPINION);
                              info.setProcessInstanceId(processParam.getProcessInstanceId());
                              info.setStartTime(sdf.format(date));
                              info.setSystemName(processParam.getSystemName());
                              info.setTitle(title);
                              info.setTenantId(tenantId);
                              info.setUrl(url);
                              info.setUserName(orgUnit.getName());
                              info.setTime(date.getTime());
                              info.setReadUserId("");
                              info.setAllUserId(newPersonIds);
                              info.setContent(content);
                              itemMsgRemindApi.saveMsgRemindInfo(tenantId, info);
                              }
                              }
                              } catch (Exception e) {
                              LOGGER.warn("*****sendMsgRemind发生异常*****", e);
                              }*/
    }

    @Async
    @Override
    public void startProcessHandle(final String tenantId, final String processSerialNumber, final String taskId,
        final String processInstanceId, final String searchTerm) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            try {
                Map<String, Object> val = new HashMap<>();
                val.put("val", searchTerm);
                variableApi.setVariableByProcessInstanceId(tenantId, processInstanceId, "searchTerm", val);
            } catch (Exception e) {
                LOGGER.warn("*****startProcessHandle发生异常*****", e);
            }
            opinionRepository.update(processInstanceId, taskId, processSerialNumber);
            y9WordHistoryService.update(taskId, processSerialNumber);
            draftEntityRepository.deleteByProcessSerialNumber(processSerialNumber);
        } catch (Exception e) {
            LOGGER.warn("*****startProcessSave发生异常*****", e);
        }
    }
}
