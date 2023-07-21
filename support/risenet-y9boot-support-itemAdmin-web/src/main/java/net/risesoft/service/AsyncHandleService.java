package net.risesoft.service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.org.DepartmentApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ChaoSong;
import net.risesoft.entity.EntrustDetail;
import net.risesoft.entity.ErrorLog;
import net.risesoft.entity.Opinion;
import net.risesoft.entity.OpinionHistory;
import net.risesoft.entity.ProcessParam;
import net.risesoft.entity.ProcessTrack;
import net.risesoft.entity.TaskVariable;
import net.risesoft.enums.ItemPrincipalTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.msgremind.MsgRemindInfoModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.todo.TodoTask;
import net.risesoft.model.user.UserInfo;
import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;
import net.risesoft.repository.jpa.DraftEntityRepository;
import net.risesoft.repository.jpa.OpinionHistoryRepository;
import net.risesoft.repository.jpa.OpinionRepository;
import net.risesoft.repository.jpa.ProcessTrackRepository;
import net.risesoft.repository.jpa.TaskVariableRepository;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.util.Y9Util;

import jodd.util.StringUtil;
import y9.client.rest.open.msgremind.MsgRemindInfoApiClient;
import y9.client.rest.open.todo.TodoTaskApiClient;
import y9.client.rest.processadmin.HistoricTaskApiClient;
import y9.client.rest.processadmin.TaskApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@EnableAsync
@Service(value = "asyncHandleService")
@Transactional(value = "rsTenantTransactionManager", rollbackFor = Exception.class)
@Slf4j
public class AsyncHandleService {

    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private TodoTaskApiClient todoTaskManager;

    @Autowired
    private TaskVariableRepository taskVariableRepository;

    @Autowired
    private ErrorLogService errorLogService;

    @Autowired
    private OfficeDoneInfoService officeDoneInfoService;

    @Autowired
    private Y9Properties y9Conf;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private ProcessParamService processParamService;

    @Autowired
    private MsgRemindInfoApiClient msgRemindInfoManager;

    @Autowired
    private OpinionHistoryRepository opinionHistoryRepository;

    @Autowired
    private DraftEntityRepository draftEntityRepository;

    @Autowired
    private TransactionHistoryWordService transactionHistoryWordService;

    @Autowired
    private TransactionFileService transactionFileService;

    @Autowired
    private OpinionRepository opinionRepository;

    @Autowired
    private VariableApi variableManager;

    @Autowired
    private ProcessTrackRepository processTrackRepository;

    @Autowired
    private TaskApiClient taskManager;

    @Autowired
    private EntrustDetailService entrustDetailService;

    @Autowired
    private Process4SearchService process4SearchService;

    @Autowired
    private HistoricTaskApiClient historicTaskManager;

    @Autowired
    private DepartmentApi departmentManager;

    /**
     * 异步发送
     *
     * @param tenantId
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
    public void forwarding(final String tenantId, final String processInstanceId, final ProcessParam processParam, final String sponsorHandle, final String sponsorGuid, final String taskId, final String multiInstance, final Map<String, Object> variables, final List<String> userAndDeptIdList) {
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            this.forwarding4Task(processInstanceId, processParam, sponsorHandle, sponsorGuid, taskId, multiInstance, variables, userAndDeptIdList);
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
                todoTaskManager.deleteByProcessInstanceId4New(tenantId, taskId, processInstanceId);

                // 保存任务发送错误日志
                ErrorLog errorLog = new ErrorLog();
                errorLog.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLog.setCreateTime(time);
                errorLog.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING);
                errorLog.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLog.setExtendField("发送多人失败：" + String.valueOf(num));
                errorLog.setProcessInstanceId(processInstanceId);
                errorLog.setTaskId(taskId);
                errorLog.setText(msg);
                errorLog.setUpdateTime(time);
                errorLogService.saveErrorLog(errorLog);

                TaskVariable taskVariable = taskVariableRepository.findByTaskIdAndKeyName(taskId, "isForwarding");
                taskVariable.setUpdateTime(time);
                taskVariable.setText("false:" + String.valueOf(num));
                taskVariableRepository.save(taskVariable);
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    public void forwarding4Task(String processInstanceId, ProcessParam processParam, String sponsorHandle, String sponsorGuid, String taskId, String multiInstance, Map<String, Object> variables, List<String> userList) throws Exception {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), personId = userInfo.getPersonId();
        // 判断是否是主办办理，如果是，需要将协办未办理的的任务默认办理
        if (StringUtils.isNotBlank(sponsorHandle) && (UtilConsts.TRUE).equals(sponsorHandle)) {
            List<TaskModel> taskNextList1 = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
            for (TaskModel taskNext : taskNextList1) {
                if (!(taskId.equals(taskNext.getId()))) {
                    // 办结协办人任务
                    taskManager.complete(tenantId, taskNext.getId());
                    // 设置强制办理任务标识
                    historicTaskManager.setTenantId(tenantId, taskNext.getId());
                }
            }
        }
        String type = "";
        String sponsor = "";
        if (StringUtil.isNotBlank(sponsorGuid)) {
            type = sponsorGuid.substring(0, 1);
            sponsor = sponsorGuid.substring(2);
            if (ItemPrincipalTypeEnum.DEPT.getValue().equals(type)) {
                List<Person> personList = departmentManager.listAllPersonsByDisabled(tenantId, sponsor, false);
                if (!personList.isEmpty()) {
                    // 设置主办部门下的第一个人员为主办人
                    sponsorGuid = personList.get(0).getId();
                }
            } else {
                sponsorGuid = sponsor;
            }
        }
        Map<String, Object> vmap = new HashMap<String, Object>(16);
        // 解决协作状态串行办理历程的所有人员显示
        vmap.put(SysVariables.USERS, userList);
        variableManager.setVariables(tenantId, taskId, vmap);
        processParam.setSended(UtilConsts.TRUE);
        processParam.setSponsorGuid(sponsorGuid);
        processParamService.saveOrUpdate(processParam);
        // 办结本次任务
        taskManager.completeWithVariables(tenantId, taskId, variables);

        // 保存流程信息到ES
        process4SearchService.saveToDataCenter1(tenantId, taskId, processParam);

        this.forwardingHandle(tenantId, personId, taskId, processInstanceId, multiInstance, sponsorGuid, processParam);
    }

    /**
     * 发送后异步处理
     *
     * @param tenantId
     * @param userId
     * @param taskId
     * @param processInstanceId
     * @param multiInstance
     * @param sponsorGuid
     * @param processParam
     */
    @Async
    public void forwardingHandle(final String tenantId, final String userId, final String taskId, final String processInstanceId, final String multiInstance, final String sponsorGuid, final ProcessParam processParam) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            // 更新自定义历程结束时间
            List<ProcessTrack> ptModelList = processTrackRepository.findByTaskId(taskId);
            for (ProcessTrack ptModel : ptModelList) {
                if (StringUtils.isBlank(ptModel.getEndTime())) {
                    ptModel.setEndTime(sdf.format(new Date()));
                    processTrackRepository.save(ptModel);
                }
            }
            // 保存下个任务节点的时限
            List<TaskModel> nextTaskList = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
            for (TaskModel taskNext : nextTaskList) {
                Map<String, Object> vars = new HashMap<String, Object>(16);
                vars.put(SysVariables.TASKSENDER, person.getName());
                vars.put(SysVariables.TASKSENDERID, userId);
                /**
                 * 并行状态且区分主协办情况下，如果受让人是主办人，则将主办人guid设为任务变量
                 */
                if (SysVariables.PARALLEL.equals(multiInstance)) {
                    boolean isEntrusr = entrustDetailService.haveEntrustDetailByTaskId(taskNext.getId());
                    if (!isEntrusr) {
                        if (taskNext.getAssignee().equals(sponsorGuid)) {
                            vars.put(SysVariables.PARALLELSPONSOR, sponsorGuid);
                        }
                    } else {
                        EntrustDetail entrustDetail = entrustDetailService.findByTaskId(taskNext.getId());
                        String owner = (entrustDetail == null ? "" : entrustDetail.getOwnerId());
                        if (owner.contains(sponsorGuid)) {
                            // 出差委托更换主办人
                            vars.put(SysVariables.PARALLELSPONSOR, taskNext.getAssignee().split(SysVariables.COLON)[0]);
                            processParam.setSponsorGuid(taskNext.getAssignee().split(SysVariables.COLON)[0]);
                            processParamService.saveOrUpdate(processParam);
                        }
                    }
                }
                variableManager.setVariablesLocal(tenantId, taskNext.getId(), vars);
            }
        } catch (Exception e) {
            LOGGER.warn("********************forwardingHandle发送发生异常", e);
        }
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
                TodoTask todo = new TodoTask();
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
                if ("一般".equals(level)) {
                    urgency = "0";
                } else if ("重要".equals(level)) {
                    urgency = "1";
                } else if ("紧急".equals(level)) {
                    urgency = "2";
                }
                todo.setUrgency(urgency);
                todo.setDocNumber(processParam.getCustomNumber());
                todo.setProcessInstanceId(processInstanceId);
                String url = todoTaskUrlPrefix.replace("index", "readIndex") + "?id=" + info.getId() + "&itemId=" + info.getItemId() + "&processInstanceId=" + info.getProcessInstanceId() + "&type=fromTodo&appName=chaoSong";
                todo.setUrl(url);
                todo.setTaskId(id);
                todo.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                todo.setReceiverId(info.getUserId());
                todo.setReceiverName(info.getUserName());
                todo.setReceiverDepartmentId(info.getUserDeptId());
                todo.setReceiverDepartmentName(info.getUserDeptName());
                todoTaskManager.saveTodoTask(tenantId, todo);
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
            history.setAgentUserDeptId(oldOpinion.getAgentUserDeptId());
            history.setAgentUserDeptName(oldOpinion.getAgentUserDeptName());
            history.setAgentUserId(oldOpinion.getAgentUserId());
            history.setAgentUserName(oldOpinion.getAgentUserName());
            history.setContent(oldOpinion.getContent());
            history.setCreateDate(oldOpinion.getCreateDate());
            history.setSaveDate(sdf.format(new Date()));
            history.setDeptId(oldOpinion.getDeptId());
            history.setDeptName(oldOpinion.getDeptName());
            history.setIsAgent(oldOpinion.getIsAgent());
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
            LOGGER.warn("*********************saveOpinionHistory发生异常", e);
        }
    }

    /**
     * 发送意见填写消息提醒
     *
     * @param processSerialNumber
     */
    @Async
    public void sendMsgRemind(final String tenantId, final String userId, final String processSerialNumber, final String content) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personManager.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            String personIds = msgRemindInfoManager.getRemindConfig(tenantId, userId, "opinionRemind");
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            if (StringUtils.isNotBlank(personIds) && StringUtils.isNotBlank(processParam.getProcessInstanceId())) {
                String title = processParam.getTitle();
                String itemId = processParam.getItemId();
                String todoTaskUrlPrefix = processParam.getTodoTaskUrlPrefix();
                String url = todoTaskUrlPrefix + "?itemId=" + itemId + "&processInstanceId=" + processParam.getProcessInstanceId() + "&type=fromCplane";
                Date date = new Date();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String newPersonIds = "";
                String[] ids = personIds.split(",");
                OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(processParam.getProcessInstanceId());
                for (String id : ids) {
                    // 参与该件的人才提醒
                    if (officeDoneInfo != null && officeDoneInfo.getAllUserId().contains(id)) {
                        newPersonIds = Y9Util.genCustomStr(newPersonIds, id);
                    }
                }
                if (StringUtils.isNotBlank(newPersonIds)) {
                    MsgRemindInfoModel info = new MsgRemindInfoModel();
                    info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    info.setItemId(processParam.getItemId());
                    info.setMsgType(MsgRemindInfoModel.MSG_TYPE_OPINION);
                    info.setProcessInstanceId(processParam.getProcessInstanceId());
                    info.setStartTime(sdf.format(date));
                    info.setSystemName(processParam.getSystemName());
                    info.setTitle(title);
                    info.setTenantId(tenantId);
                    info.setUrl(url);
                    info.setUserName(Y9LoginUserHolder.getUserInfo().getName());
                    info.setTime(date.getTime());
                    info.setReadUserId("");
                    info.setAllUserId(personIds);
                    info.setContent(content);
                    msgRemindInfoManager.saveMsgRemindInfo(tenantId, info);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("********************sendMsgRemind发生异常", e);
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
    public void startProcessHandle(final String tenantId, final String processSerialNumber, final String taskId, final String processInstanceId, final String searchTerm) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            try {
                Map<String, Object> val = new HashMap<String, Object>();
                val.put("val", searchTerm);
                variableManager.setVariableByProcessInstanceId(tenantId, processInstanceId, "searchTerm", val);
            } catch (Exception e) {
                e.printStackTrace();
            }
            opinionRepository.update(processInstanceId, taskId, processSerialNumber);
            transactionFileService.update(processSerialNumber, processInstanceId, taskId);
            transactionHistoryWordService.update(taskId, processSerialNumber);
            draftEntityRepository.deleteByProcessSerialNumber(processSerialNumber);
        } catch (Exception e) {
            LOGGER.warn("**********************startProcessSave发生异常", e);
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
    public void weiXinRemind(final String tenantId, final String userId, final String processSerialNumber, final List<ChaoSong> list) {
        Boolean weiXinSwitch = y9Conf.getApp().getItemAdmin().getWeiXinSwitch();
        if (!weiXinSwitch) {
            LOGGER.info("######################微信提醒开关已关闭,如需微信提醒请更改配置文件######################");
            return;
        }
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String documentTitle = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            Person person = personManager.getPerson(tenantId, userId);
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
                String url = y9Conf.getApp().getItemAdmin().getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：" + code + "##########################");
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                    LOGGER.info("##########################返回状态：" + response + "##########################");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
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
    public void weiXinRemind4ChaoSongInfo(final String tenantId, final String userId, final String processSerialNumber, final List<ChaoSongInfo> list) {
        Boolean weiXinSwitch = y9Conf.getApp().getItemAdmin().getWeiXinSwitch();
        if (!weiXinSwitch) {
            LOGGER.info("######################微信提醒开关已关闭,如需微信提醒请更改配置文件######################");
            return;
        }
        try {
            ProcessParam processParam = processParamService.findByProcessSerialNumber(processSerialNumber);
            String documentTitle = processParam.getTitle();
            String itemId = processParam.getItemId();
            String itemName = processParam.getItemName();
            Person person = personManager.getPerson(tenantId, userId);
            OfficeDoneInfo officeDoneInfo = officeDoneInfoService.findByProcessInstanceId(list.get(0).getProcessInstanceId());
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
                String url = y9Conf.getApp().getItemAdmin().getWeiXinUrl();
                method.setPath(url);
                int code = client.executeMethod(method);
                LOGGER.info("##########################微信接口状态：" + code + "##########################");
                if (code == HttpStatus.SC_OK) {
                    String response = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                    LOGGER.info("##########################返回状态：" + response + "##########################");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
    }
}
