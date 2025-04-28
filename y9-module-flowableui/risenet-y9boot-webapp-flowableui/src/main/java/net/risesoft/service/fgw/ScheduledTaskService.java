package net.risesoft.service.fgw;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.DocumentApi;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ButtonOperationService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 自动发送，办结定时任务
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@EnableScheduling
@RequiredArgsConstructor
@ConditionalOnProperty(name = "y9.common.tenantId")
public class ScheduledTaskService {

    private final FormDataApi formDataApi;
    private final DocumentApi documentApi;
    private final PositionApi positionApi;
    private final ProcessParamApi processParamApi;
    private final ErrorLogApi errorLogApi;
    private final VariableApi variableApi;
    private final TaskApi taskApi;
    private final TaskRelatedApi taskRelatedApi;
    private final PushDataService pushDataService;
    private final ButtonOperationService buttonOperationService;
    private final String taskKey = "msecbhfw";// 任务【秘书二处编号发文】key
    private final String completedTaskKey = "bgtfwrbj";// 任务【办公厅发文人办结】key
    private final String actionName = "送拟稿人眷清(自动发送)";// 动作事件名称
    private final String routeToTaskId = "ngrtq";// 任务【拟稿人誊清】key
    private final String tableAlias = "fwd";// 发文单表别名
    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;
    @Value("${y9.common.tenantId}")
    private String tenantId;

    @PostConstruct
    public void init() {
        LOGGER.info("ScheduledTaskService initialized with tenantId: {}", tenantId);
    }

    /**
     * 定时办结，每5分钟执行
     */
    @Scheduled(cron = "0 */5 * * * ?")
    public void completedTask() {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select * from ff_act_ru_detail where TASKDEFKEY = '" + completedTaskKey
            + "' and STATUS = 0 and DELETED = 0 order by LASTTIME asc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String time = sdf.format(new Date());
        LOGGER.info("#######" + time + "  定时办结数量:" + list.size());
        int num = 0;
        for (Map<String, Object> map : list) {
            String processInstanceId = "";
            String taskId = "";
            try {
                processInstanceId = map.get("PROCESSINSTANCEID") == null ? "" : (String)map.get("PROCESSINSTANCEID");
                taskId = map.get("TASKID") == null ? "" : (String)map.get("TASKID");
                String processSerialNumber = (String)map.get("PROCESSSERIALNUMBER");
                Map<String, Object> data =
                    formDataApi.getData4TableAlias(tenantId, processSerialNumber, tableAlias).getData();
                if (data == null || data.isEmpty()) {
                    LOGGER.info("#######发文单查询不到数据processInstanceId:" + processInstanceId);
                    continue;
                }
                String fwwh = data.get("fwwh") == null ? "" : (String)data.get("fwwh");
                String chexiao = data.get("chexiao") == null ? "" : String.valueOf(data.get("chexiao"));
                String fenfa = data.get("fenfa") == null ? "" : String.valueOf(data.get("fenfa"));
                if (!"0".equals(chexiao) || fwwh.equals("") || !"1".equals(fenfa)) {
                    LOGGER.info("#######fwwh为空或chexiao不为0或fenfa不为1   processInstanceId:" + processInstanceId);
                    continue;
                }
                String assignee = map.get("ASSIGNEE") == null ? "" : (String)map.get("ASSIGNEE");

                Position position = positionApi.get(tenantId, assignee).getData();
                Y9LoginUserHolder.setPosition(position);

                // 执行办结前,先查询任务存不存在，避免发生错误
                TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
                if (taskModel == null) {
                    LOGGER.info("#######查询不到该任务，跳过办结taskId:" + taskId);
                    continue;
                }

                // 执行办结
                buttonOperationService.complete(taskId, "办结", "已办结", "");
                num++;

                // 推送双杨的数据
                pushDataService.addPushData(processSerialNumber, processInstanceId, "办结");

                // 加系统自动办结标识
                TaskRelatedModel taskRelatedModel = new TaskRelatedModel();
                taskRelatedModel.setInfoType(TaskRelatedEnum.COMPLETEDTYPE.getValue());
                taskRelatedModel.setTaskId(taskId);
                taskRelatedModel.setProcessInstanceId(processInstanceId);
                taskRelatedModel.setExecutionId(taskModel.getExecutionId());
                taskRelatedModel.setSub(false);
                taskRelatedModel.setProcessSerialNumber(processSerialNumber);
                taskRelatedModel.setMsgContent("(系统自动办结)");
                taskRelatedModel.setSenderId(assignee);
                taskRelatedModel.setSenderName(position.getName());
                taskRelatedApi.saveOrUpdate(tenantId, taskRelatedModel);
            } catch (Exception e) {
                LOGGER.error("#######定时办结异常processInstanceId:" + processInstanceId, e);
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                String time1 = sdf.format(new Date());
                ErrorLogModel errorLogModel = new ErrorLogModel();
                errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLogModel.setCreateTime(time1);
                errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING + "4CompletedTask");
                errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLogModel.setExtendField("定时办结失败");
                errorLogModel.setProcessInstanceId(processInstanceId);
                errorLogModel.setTaskId(taskId);
                errorLogModel.setText(msg);
                errorLogModel.setUpdateTime(time);
                try {
                    errorLogApi.saveErrorLog(tenantId, errorLogModel);
                } catch (Exception e1) {
                    LOGGER.error("#################保存错误日志失败#################", e1);
                }
            }
        }
        LOGGER.info("#######" + time + "  定时办结成功数量:" + num);
    }

    /**
     * 定时发送，每2分钟执行
     */
    @Scheduled(cron = "0 */2 * * * ?")
    public void sendTask() {
        Y9LoginUserHolder.setTenantId(tenantId);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String sql = "select * from ff_act_ru_detail where TASKDEFKEY = '" + taskKey
            + "' and STATUS = 0 and DELETED = 0 order by LASTTIME asc";
        List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
        String time = sdf.format(new Date());
        LOGGER.info("*******" + time + "  定时发送数量:" + list.size());
        int num = 0;
        for (Map<String, Object> map : list) {
            String processInstanceId = "";
            String taskId = "";
            try {
                processInstanceId = map.get("PROCESSINSTANCEID") == null ? "" : (String)map.get("PROCESSINSTANCEID");
                taskId = map.get("TASKID") == null ? "" : (String)map.get("TASKID");
                String processSerialNumber = (String)map.get("PROCESSSERIALNUMBER");
                Map<String, Object> data =
                    formDataApi.getData4TableAlias(tenantId, processSerialNumber, tableAlias).getData();
                if (data == null || data.isEmpty()) {
                    LOGGER.info("*******发文单查询不到数据processInstanceId:" + processInstanceId);
                    continue;
                }
                String fwwh = data.get("fwwh") == null ? "" : (String)data.get("fwwh");
                String chexiao = data.get("chexiao") == null ? "" : String.valueOf(data.get("chexiao"));
                if (!"0".equals(chexiao) || fwwh.equals("")) {
                    LOGGER.info("*******fwwh为空或chexiao不为0   processInstanceId:" + processInstanceId);
                    continue;
                }
                String assignee = map.get("ASSIGNEE") == null ? "" : (String)map.get("ASSIGNEE");
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
                String userChoice = "6:" + processParamModel.getStartor();

                // 设置动作事件名称
                Map<String, Object> vars = new HashMap<>();
                vars.put("val", actionName);
                boolean flag = variableApi.setVariableByProcessInstanceId(tenantId, processInstanceId,
                    SysVariables.ACTIONNAME + ":" + assignee, vars).isSuccess();
                if (!flag) {
                    LOGGER.info("*******设置动作事件失败processInstanceId:" + processInstanceId);
                    continue;
                }

                // 执行发送前,先查询任务存不存在，避免多次发送
                TaskModel taskModel = taskApi.findById(tenantId, taskId).getData();
                if (taskModel == null) {
                    LOGGER.info("*******查询不到该任务，跳过发送taskId:" + taskId);
                    continue;
                }

                // 执行发送
                Y9Result<String> y9Result =
                    documentApi.forwarding(tenantId, assignee, taskId, userChoice, routeToTaskId, "", "");
                if (!y9Result.isSuccess()) {
                    LOGGER.info("*******执行发送失败processInstanceId:" + processInstanceId);
                    continue;
                }
                num++;

                // 推送双杨的数据
                pushDataService.addPushData(processSerialNumber, processInstanceId, "发送");
            } catch (Exception e) {
                LOGGER.error("*******定时发送异常processInstanceId:" + processInstanceId, e);
                final Writer result = new StringWriter();
                final PrintWriter print = new PrintWriter(result);
                e.printStackTrace(print);
                String msg = result.toString();
                String time1 = sdf.format(new Date());
                ErrorLogModel errorLogModel = new ErrorLogModel();
                errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                errorLogModel.setCreateTime(time1);
                errorLogModel.setErrorFlag(ErrorLogModel.ERROR_FLAG_FORWRDING + "4SendTask");
                errorLogModel.setErrorType(ErrorLogModel.ERROR_TASK);
                errorLogModel.setExtendField("定时发送失败");
                errorLogModel.setProcessInstanceId(processInstanceId);
                errorLogModel.setTaskId(taskId);
                errorLogModel.setText(msg);
                errorLogModel.setUpdateTime(time);
                try {
                    errorLogApi.saveErrorLog(tenantId, errorLogModel);
                } catch (Exception e1) {
                    LOGGER.error("#################保存错误日志失败#################", e1);
                }
            }
        }
        LOGGER.info("*******" + time + "  定时发送成功数量:" + num);
    }

}
