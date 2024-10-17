package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.extend.ItemMsgRemindApi;
import net.risesoft.api.itemadmin.extend.ItemTodoTaskApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.HistoricTaskApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ChaoSong;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.OpinionHistory;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.TaskVariable;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ItemMsgRemindModel;
import net.risesoft.model.itemadmin.TodoTaskModel;
import net.risesoft.model.platform.Department;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.OpinionHistoryRepository;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.repository.jpa.TaskVariableRepository;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.y9itemadmin.Y9ItemAdminProperties;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@EnableAsync
@Service(value = "asyncHandleService")
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
@Slf4j
@RequiredArgsConstructor
public class AsyncHandleService {

    private final ItemTodoTaskApi todotaskApi;

    private final TaskVariableRepository taskVariableRepository;

    private final ErrorLogService errorLogService;

    private final OfficeDoneInfoService officeDoneInfoService;

    private final Y9ItemAdminProperties y9ItemAdminProperties;

    private final PersonApi personApi;

    private final OrgUnitApi orgUnitApi;

    private final PositionApi positionApi;

    private final ProcessParamService processParamService;

    private final ItemMsgRemindApi itemMsgRemindApi;

    private final OpinionHistoryRepository opinionHistoryRepository;

    private final DraftEntityRepository draftEntityRepository;

    private final TransactionHistoryWordService transactionHistoryWordService;

    private final TransactionFileService transactionFileService;

    private final OpinionRepository opinionRepository;

    private final VariableApi variableApi;

    private final ProcessTrackRepository processTrackRepository;

    private final TaskApi taskApi;

    private final HistoricTaskApi historictaskApi;

    private final Process4SearchService process4SearchService;

    private final DepartmentApi departmentApi;

    private final ItemTaskConfService itemTaskConfService;

    /**
     * 异步发送
     *
     * @param tenantId
     * @param orgUnit
     * @param processInstanceId
     * @param processParam
     * @param sponsorHandle
     * @param sponsorGuid
     * @param taskId
     * @param multiInstance
     * @param variables
     * @param userAndDeptIdList
     * @return
     */
    @Async
    public void forwarding(final String tenantId, final OrgUnit orgUnit, final String processInstanceId,
        final ProcessParam processParam, final String sponsorHandle, final String sponsorGuid, final String taskId,
        final String multiInstance, final Map<String, Object> variables, final List<String> userAndDeptIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        try {
            this.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance,
                variables, userAndDeptIdList);
        } catch (Exception e) {
            try {
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                int num = userAndDeptIdList.size();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String time = sdf.format(new Date());

                // 发送失败,可能会出现统一待办已经保存成功,但任务没有在数据库产生,需要删除统一待办数据,只保存当前发送人的待办任务。
                todotaskApi.deleteByProcessInstanceId4New(tenantId, taskId, processInstanceId);

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
                e2.printStackTrace();
            }
        }
    }

    public void forwarding4Task(String processInstanceId, ProcessParam processParam, String sponsorHandle,
        String sponsorGuid, String taskId, String multiInstance, Map<String, Object> variables, List<String> userList)
        throws Exception {
        OrgUnit orgUnit = Y9LoginUserHolder.getOrgUnit();
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
        /**
         * 主办设置类型，2为部门节点，3为人员节点
         */
        String type = "";
        /**
         * 主办部门或人员的id
         */
        String sponsor = "";
        if (StringUtils.isNotBlank(sponsorGuid)) {
            type = sponsorGuid.substring(0, 1);
            sponsor = sponsorGuid.substring(2);
            if (type.equals(String.valueOf(ItemPrincipalTypeEnum.DEPT.getValue()))) {
                /**
                 * 设置主办部门下的第一个人员为主办人
                 */
                sponsorGuid = this.getSponsorPosition("", sponsor);
            } else {
                sponsorGuid = sponsor;
            }
        }
        Map<String, Object> vmap = new HashMap<>(16);
        /**
         * 解决协作状态串行办理历程的所有人员显示
         */
        vmap.put(SysVariables.USERS, userList);
        variableApi.setVariables(tenantId, taskId, vmap);
        processParam.setSended("true");
        processParam.setSponsorGuid(sponsorGuid);
        processParamService.saveOrUpdate(processParam);
        taskApi.completeWithVariables(tenantId, taskId, orgUnitId, variables);

        // 保存流程信息到ES
        process4SearchService.saveToDataCenter1(tenantId, taskId, processParam);

        this.forwardingHandle(tenantId, orgUnitId, taskId, processInstanceId, multiInstance, sponsorGuid, processParam);
    }

    /**
     * 发送后异步处理
     *
     * @param tenantId
     * @param taskId
     * @param processInstanceId
     * @param multiInstance
     * @param sponsorGuid
     * @param processParam
     */
    @Async
    public void forwardingHandle(final String tenantId, final String orgUnitId, final String taskId,
        final String processInstanceId, final String multiInstance, final String sponsorGuid,
        final ProcessParam processParam) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
            Y9LoginUserHolder.setOrgUnit(orgUnit);
            // 更新自定义历程结束时间
            List<ProcessTrack> ptModelList = processTrackRepository.findByTaskId(taskId);
            for (ProcessTrack ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    ptModel.setEndTime(sdf.format(new Date()));
                    processTrackRepository.save(ptModel);
                }
            }
            // 保存下个任务节点的时限
            List<TaskModel> nextTaskList = taskApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            for (TaskModel taskNext : nextTaskList) {
                Map<String, Object> vars = new HashMap<>(16);
                vars.put(SysVariables.TASKSENDER, orgUnit.getName());
                vars.put(SysVariables.TASKSENDERID, orgUnitId);
                /**
                 * 并行状态且区分主协办情况下，如果受让人是主办人，则将主办人guid设为任务变量
                 */
                if (SysVariables.PARALLEL.equals(multiInstance)) {
                    // boolean isEntrusr = entrustDetailService.haveEntrustDetailByTaskId(taskNext.getId());
                    // if (!isEntrusr) {
                    if (taskNext.getAssignee().equals(sponsorGuid)) {
                        vars.put(SysVariables.PARALLELSPONSOR, sponsorGuid);
                    }
                    // } else {
                    // EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskNext.getId());
                    // String owner = (entrustDetail == null ? "" : entrustDetail.getOwnerId());
                    //// 出差委托更换主办人
                    // if (owner.contains(sponsorGuid)) {
                    // vars.put(SysVariables.PARALLELSPONSOR, taskNext.getAssignee().split(SysVariables.COLON)[0]);
                    // processParam.setSponsorGuid(taskNext.getAssignee().split(SysVariables.COLON)[0]);
                    // processParamService.saveOrUpdate(processParam);
                    // }
                    // }
                }
                variableApi.setVariablesLocal(tenantId, taskNext.getId(), vars);
            }
        } catch (

        Exception e) {
            LOGGER.warn("*****forwardingHandle发送发生异常*****", e);
        }
    }

    private String getSponsorPosition(String id, String deptId) {
        List<Department> deptList = departmentApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        List<Position> list0 = positionApi.listByParentId(Y9LoginUserHolder.getTenantId(), deptId).getData();
        if (!list0.isEmpty()) {
            id = list0.get(0).getId();
        } else {
            for (Department dept : deptList) {
                this.getSponsorPosition(id, dept.getId());
                if (!id.equals("")) {
                    break;
                }
            }
        }
        return id;
    }

    /**
     * 保存抄送件到统一待办
     *
     * @param tenantId
     * @param csList
     */
    @Async
    public void saveChaoSong4Todo(final String tenantId, final List<ChaoSongInfo> csList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = "";
        String taskId = "";
        String id = "";
        Y9LoginUserHolder.setTenantId(tenantId);
        for (ChaoSongInfo info : csList) {
            try {
                id = info.getId();
                taskId = info.getTaskId();
                processInstanceId = info.getProcessInstanceId();
                TodoTaskModel todo = new TodoTaskModel();
                todo.setTenantId(tenantId);
                todo.setSystemName("阅件");
                todo.setSystemCnName("阅件");
                todo.setAppName("阅件");
                todo.setAppCnName("阅件");
                todo.setTitle(info.getTitle());
                todo.setSenderId(info.getSenderId());
                todo.setSenderName(info.getSenderName());
                todo.setSenderDepartmentId(info.getSendDeptId());
                todo.setSenderDepartmentName(info.getSendDeptName());
                todo.setSendTime(sdf.parse(info.getCreateTime()));
                todo.setIsNewTodo("1");
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                String level = processParam.getCustomLevel() == null ? "" : processParam.getCustomLevel();
                String todoTaskUrlPrefix = processParam.getTodoTaskUrlPrefix();
                String urgency = "0";
                if (level.equals("一般")) {
                    urgency = "0";
                } else if (level.equals("重要")) {
                    urgency = "1";
                } else if (level.equals("紧急")) {
                    urgency = "2";
                }
                if ("普通".equals(level)) {
                    urgency = "0";
                } else if ("急".equals(level)) {
                    urgency = "1";
                } else if ("特急".equals(level)) {
                    urgency = "2";
                }
                todo.setUrgency(urgency);
                todo.setDocNumber(processParam.getCustomNumber());
                todo.setProcessInstanceId(processInstanceId);
                String url = todoTaskUrlPrefix.replace("index", "readIndex") + "?id=" + info.getId() + "&itemId="
                    + info.getItemId() + "&processInstanceId=" + info.getProcessInstanceId()
                    + "&type=fromTodo&appName=chaoSong";
                todo.setUrl(url);
                todo.setTaskId(id);
                todo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                todo.setReceiverId(info.getUserId());
                todo.setReceiverName(info.getUserName());
                todo.setReceiverDepartmentId(info.getUserDeptId());
                todo.setReceiverDepartmentName(info.getUserDeptName());
                todotaskApi.saveTodoTask(tenantId, todo);
            } catch (Exception e) {
                String time = sdf.format(new Date());
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                // 保存发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_CHAOSONG);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("阅件保存至统一待办失败，抄送id:" + id);
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);
            }
        }
    }

    /**
     * 保存抄送件到统一待办
     *
     * @param tenantId
     * @param csList
     */
    @Async
    public void saveChaoSongTodo(final String tenantId, final List<ChaoSong> csList) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String processInstanceId = "";
        String taskId = "";
        String id = "";
        Y9LoginUserHolder.setTenantId(tenantId);
        for (ChaoSong info : csList) {
            try {
                id = info.getId();
                taskId = info.getTaskId();
                processInstanceId = info.getProcessInstanceId();
                TodoTaskModel todo = new TodoTaskModel();
                todo.setTenantId(tenantId);
                todo.setSystemName("阅件");
                todo.setSystemCnName("阅件");
                todo.setAppName("阅件");
                todo.setAppCnName("阅件");
                todo.setTitle(info.getTitle());
                todo.setSenderId(info.getSenderId());
                todo.setSenderName(info.getSenderName());
                todo.setSenderDepartmentId(info.getSendDeptId());
                todo.setSenderDepartmentName(info.getSendDeptName());
                todo.setSendTime(sdf.parse(info.getCreateTime()));
                todo.setIsNewTodo("1");
                ProcessParam processParam = processParamService.findByProcessInstanceId(processInstanceId);
                String level = processParam.getCustomLevel() == null ? "" : processParam.getCustomLevel();
                String todoTaskUrlPrefix = processParam.getTodoTaskUrlPrefix();
                String urgency = "0";
                if (level.equals("一般")) {
                    urgency = "0";
                } else if (level.equals("重要")) {
                    urgency = "1";
                } else if (level.equals("紧急")) {
                    urgency = "2";
                }

                if ("普通".equals(level)) {
                    urgency = "0";
                } else if ("急".equals(level)) {
                    urgency = "1";
                } else if ("特急".equals(level)) {
                    urgency = "2";
                }

                todo.setUrgency(urgency);
                todo.setDocNumber(processParam.getCustomNumber());
                todo.setProcessInstanceId(processInstanceId);
                String url = todoTaskUrlPrefix.replace("index", "readIndex") + "?id=" + info.getId() + "&itemId="
                    + info.getItemId() + "&processInstanceId=" + info.getProcessInstanceId()
                    + "&type=fromTodo&appName=chaoSong";
                todo.setUrl(url);
                todo.setTaskId(id);
                todo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                todo.setReceiverId(info.getUserId());
                todo.setReceiverName(info.getUserName());
                todo.setReceiverDepartmentId(info.getUserDeptId());
                todo.setReceiverDepartmentName(info.getUserDeptName());
                todotaskApi.saveTodoTask(tenantId, todo);
            } catch (Exception e) {
                String time = sdf.format(new Date());
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                // 保存发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_SAVE_CHAOSONG);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("阅件保存至统一待办失败，抄送id:" + id);
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);
            }
        }
    }

    /**
     * 保存意见历史记录
     *
     * @param oldOpinion
     * @param opinionType
     */
    @Async
    public void saveOpinionHistory(final String tenantId, final Opinion oldOpinion, final String opinionType) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            OpinionHistory history = new OpinionHistory();
            history.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            history.setContent(oldOpinion.getContent());
            history.setCreateDate(oldOpinion.getCreateDate());
            history.setSaveDate(sdf.format(new Date()));
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

    /**
     * 发送意见填写消息提醒
     *
     * @param processSerialNumber
     */
    @Async
    public void sendMsgRemind(final String tenantId, final String userId, final String processSerialNumber,
        final String content) {
        try {
            Boolean msgSwitch = y9ItemAdminProperties.getMsgSwitch();
            if (msgSwitch == null || !msgSwitch) {
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
                String todoTaskUrlPrefix = processParam.getTodoTaskUrlPrefix();
                String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId="
                    + processParam.getProcessInstanceId() + "&type=fromCplane";
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String newPersonIds = "";
                String[] ids = personIds.split(",");
                OfficeDoneInfo officeDoneInfo =
                    officeDoneInfoService.findByProcessInstanceId(processParam.getProcessInstanceId());
                for (String id : ids) {
                    /**
                     * 参与该件的人才提醒
                     */
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
        }
    }

    /**
     * 启动流程后数据处理
     *
     * @param processSerialNumber
     * @param taskId
     * @param processInstanceId
     * @param searchTerm
     */
    @Async
    public void startProcessHandle(final String tenantId, final String processSerialNumber, final String taskId,
        final String processInstanceId, final String searchTerm) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            try {
                Map<String, Object> val = new HashMap<>();
                val.put("val", searchTerm);
                variableApi.setVariableByProcessInstanceId(tenantId, processInstanceId, "searchTerm", val);
            } catch (Exception e) {
                e.printStackTrace();
            }
            opinionRepository.update(processInstanceId, taskId, processSerialNumber);
            transactionFileService.update(processSerialNumber, processInstanceId, taskId);
            transactionHistoryWordService.update(taskId, processSerialNumber);
            draftEntityRepository.deleteByProcessSerialNumber(processSerialNumber);
        } catch (Exception e) {
            LOGGER.warn("*****startProcessSave发生异常*****", e);
        }
    }

    /**
     * 异步抄送微信提醒
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param list
     * @return
     */
    @Async
    public void weiXinRemind(final String tenantId, final String userId, final String processSerialNumber,
        final List<ChaoSong> list) {
        Boolean weiXinSwitch = y9ItemAdminProperties.getWeiXinSwitch();
        if (weiXinSwitch == null || Boolean.FALSE.equals(weiXinSwitch)) {
            LOGGER.info("######################微信提醒开关已关闭,如需微信提醒请更改配置文件######################");
            return;
        }
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String documentTitle = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            Person person = personApi.get(tenantId, userId).getData();
            for (ChaoSong cs : list) {
                String assignee = cs.getUserId();
                HttpClient client = new HttpClient();
                client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                PostMethod method = new PostMethod();
                method.addParameter("userId", assignee);
                method.addParameter("title", documentTitle);
                method.addParameter("taskSender", person.getName());
                method.addParameter("taskName", itemName + "-阅件");
                method.addParameter("processSerialNumber", processSerialNumber);
                method.addParameter("processDefinitionKey", "processDefinitionKey");
                method.addParameter("processInstanceId", cs.getProcessInstanceId());
                method.addParameter("taskId", "");
                method.addParameter("itemId", itemId);
                String url = y9ItemAdminProperties.getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：" + code + "##########################");
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8);
                    LOGGER.info("##########################返回状态：" + response + "##########################");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("抄送微信提醒发生异常", e);
        }
    }

    /**
     * 异步抄送微信提醒
     *
     * @param tenantId
     * @param userId
     * @param processSerialNumber
     * @param list
     * @return
     */
    @Async
    public void weiXinRemind4ChaoSongInfo(final String tenantId, final String userId, final String processSerialNumber,
        final List<ChaoSongInfo> list) {
        Boolean weiXinSwitch = y9ItemAdminProperties.getWeiXinSwitch();
        if (weiXinSwitch == null || Boolean.FALSE.equals(weiXinSwitch)) {
            LOGGER.info("######################微信提醒开关已关闭,如需微信提醒请更改配置文件######################");
            return;
        }
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String documentTitle = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            Person person = personApi.get(tenantId, userId).getData();
            OfficeDoneInfo officeDoneInfo =
                officeDoneInfoService.findByProcessInstanceId(list.get(0).getProcessInstanceId());
            for (ChaoSongInfo cs : list) {
                String assignee = cs.getUserId();
                HttpClient client = new HttpClient();
                client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
                client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
                PostMethod method = new PostMethod();
                method.addParameter("userId", assignee);
                method.addParameter("title", documentTitle);
                method.addParameter("taskSender", person.getName());
                method.addParameter("taskName", itemName + "-阅件");
                method.addParameter("processSerialNumber", processSerialNumber);
                method.addParameter("processDefinitionKey", officeDoneInfo.getProcessDefinitionKey());
                method.addParameter("processInstanceId", cs.getProcessInstanceId());
                method.addParameter("taskId", "");
                method.addParameter("itemId", itemId);
                String url = y9ItemAdminProperties.getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：" + code + "##########################");
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                        StandardCharsets.UTF_8);
                    LOGGER.info("##########################返回状态：" + response + "##########################");
                }
            }
        } catch (Exception e) {
            LOGGER.warn("抄送微信提醒发生异常", e);
        }
    }
}
