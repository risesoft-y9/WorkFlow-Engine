package net.risesoft.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Future;

import javax.sql.DataSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.Consts;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.flowable.engine.delegate.event.impl.FlowableSequenceFlowTakenEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.service.delegate.DelegateTask;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.TaskTimeConfModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

import cn.hutool.json.JSONObject;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
@Slf4j
@EnableAsync
@Service(value = "interfaceUtilService")
public class InterfaceUtilService {

    private final ProcessParamApi processParamApi;
    private final ErrorLogApi errorLogApi;
    private final ItemInterfaceApi itemInterfaceApi;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9Properties y9Config;
    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public InterfaceUtilService(ProcessParamApi processParamApi, ErrorLogApi errorLogApi,
        ItemInterfaceApi itemInterfaceApi, Y9FileStoreService y9FileStoreService, Y9Properties y9Config) {
        this.processParamApi = processParamApi;
        this.errorLogApi = errorLogApi;
        this.itemInterfaceApi = itemInterfaceApi;
        this.y9FileStoreService = y9FileStoreService;
        this.y9Config = y9Config;
    }

    /**
     * 异步调用接口
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织id
     * @param processSerialNumber 流程编号
     * @param itemId 流程id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @return
     */
    @Async
    public Future<Boolean> asynInterface(final String tenantId, final String orgUnitId,
        final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        try {
            if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET.getValue())) {
                getMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, taskId, taskKey,
                    loopCounter);

            } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST.getValue())) {
                postMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, taskId, taskKey,
                    loopCounter);
            }
            return new AsyncResult<>(true);
        } catch (Exception e) {
            LOGGER.error("接口调用异常", e);
        }
        return new AsyncResult<>(false);
    }

    /**
     * 接口响应数据处理
     *
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param map
     * @param paramsList
     * @param info
     * @throws Exception
     */
    public void dataHandling(String processSerialNumber, String processInstanceId, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, InterfaceModel info) throws Exception {
        if (map != null && paramsList != null && !paramsList.isEmpty()) {
            try {
                String tableName = "";
                String parentProcessSerialNumber = "";
                for (InterfaceParamsModel model : paramsList) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue())) {
                        tableName = model.getTableName();
                        if (model.getColumnName().equals("parentProcessSerialNumber")) {// 子表，新增数据
                            parentProcessSerialNumber = processSerialNumber;
                            break;
                        }
                    }
                }
                if (!parentProcessSerialNumber.equals("")) {// 子表，新增数据
                    this.insertData(tableName, parentProcessSerialNumber, map, paramsList);
                    return;
                }
                DataSource database = Objects.requireNonNull(jdbcTemplate.getDataSource());
                String dialect = DbMetaDataUtil.getDatabaseDialectName(database);
                StringBuilder sqlStr = new StringBuilder();
                if ("oracle".equals(dialect)) {
                    sqlStr.append("update \"").append(tableName).append("\" set ");
                } else if ("dm".equals(dialect)) {
                    sqlStr.append("update \"").append(tableName).append("\" set ");
                } else if ("mysql".equals(dialect)) {
                    sqlStr.append("update ").append(tableName).append(" set ");
                } else if ("kingbase".equals(dialect)) {
                    sqlStr.append("update \"").append(tableName).append("\" set ");
                }
                boolean isHaveField = false;
                for (InterfaceParamsModel model : paramsList) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue())) {
                        String fieldName = model.getColumnName();
                        String parameterName = model.getParameterName();
                        String value = map.get(parameterName) != null ? (String)map.get(parameterName) : "";
                        // 处理文件，保存文件，表单存fileStoreId
                        if (StringUtils.isNotBlank(model.getFileType()) && StringUtils.isNotBlank(value)) {
                            LOGGER.info("********************文件字段处理:" + model.getParameterName());
                            String downloadUrl = this.saveFile(processSerialNumber, value, model.getFileType());
                            value = downloadUrl;
                        }
                        if (isHaveField) {
                            sqlStr.append(",");
                        }
                        sqlStr.append(fieldName).append("=");
                        sqlStr.append(StringUtils.isNotBlank(value) ? "'" + value + "'" : "''");
                        isHaveField = true;
                    }
                }
                sqlStr.append(" where guid ='").append(processSerialNumber).append("'");
                String sql = sqlStr.toString();
                jdbcTemplate.execute(sql);
            } catch (Exception e) {
                final Writer msgResult = new StringWriter();
                final PrintWriter print = new PrintWriter(msgResult);
                e.printStackTrace(print);
                String msg = msgResult.toString();
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "dataHandling", "",
                    info.getInterfaceAddress(), msg);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_dataHandling：" + info.getInterfaceAddress());
                }
            }

        }
    }

    /**
     * get请求
     *
     * @param processSerialNumber 流程编号
     * @param itemId 流程id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void getMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod();
            method.setPath(info.getInterfaceAddress());
            // 默认添加请求头
            method.addRequestHeader("auth-positionId", Y9LoginUserHolder.getOrgUnitId());
            method.addRequestHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
            Y9Result<List<InterfaceParamsModel>> y9Result =
                itemInterfaceApi.getInterfaceParams(Y9LoginUserHolder.getTenantId(), itemId, info.getId());
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
                List<Map<String, Object>> list =
                    getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info);
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS.getValue())
                            || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String)map.get(model.getColumnName());
                                    if (loopCounter != null) {// 并行子流程，参数从数组里面通过loopCounter获取
                                        try {
                                            JSONArray array = JSON.parseArray((String)map.get(model.getColumnName()));
                                            parameterValue =
                                                array.size() > loopCounter ? array.get(loopCounter).toString() : "";
                                        } catch (Exception e) {
                                            LOGGER.info("*****************" + model.getColumnName() + "不是数组："
                                                + map.get(model.getColumnName()));
                                        }
                                    }
                                    break;
                                }
                            }
                            nameValuePairs.add(new NameValuePair(model.getParameterName(), parameterValue));
                        }
                        // 请求头
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String)map.get(model.getColumnName());
                                    break;
                                }
                            }
                            method.addRequestHeader(model.getParameterName(), parameterValue);
                        }
                    }
                }
            }
            if (!nameValuePairs.isEmpty()) {
                method.setQueryString(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
            }
            int time = 10000;
            TaskTimeConfModel taskTimeConf = itemInterfaceApi
                .getTaskTimeConf(Y9LoginUserHolder.getTenantId(), processDefinitionId, itemId, taskKey).getData();
            if (taskTimeConf != null && taskTimeConf.getTimeoutInterrupt() != null
                && taskTimeConf.getTimeoutInterrupt() > 0) {
                time = taskTimeConf.getTimeoutInterrupt();
            }
            // 设置请求超时时间10s
            client.getHttpConnectionManager().getParams().setConnectionTimeout(time);
            // 设置读取数据超时时间10s
            client.getHttpConnectionManager().getParams().setSoTimeout(time);
            int httpCode = client.executeMethod(method);
            if (httpCode == HttpStatus.SC_OK) {
                String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                // 将JSON字符串转换为Java对象
                Y9Result<Map<String, Object>> result = objectMapper.readValue(response, Y9Result.class);
                if (!result.isSuccess()) {
                    if (info.getAbnormalStop().equals("1")) {// 接口异常中断发送
                        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                            info.getInterfaceAddress(), response);
                        throw new Exception("调用接口失败_返回结果：" + response + "|" + info.getInterfaceAddress());
                    }
                } else {
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info);
                }
                // LOGGER.info("*********************接口返回结果:response={}", response);
            } else {
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                    info.getInterfaceAddress(), "httpCode:" + httpCode);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
                }
            }
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                info.getInterfaceAddress(), msg);
            if (info.getAbnormalStop().equals("1")) {
                throw new Exception("调用接口失败_getMethod：" + info.getInterfaceAddress());
            }
        }
    }

    /**
     * 获取请求参数值
     *
     * @param list
     * @param processSerialNumber
     * @param processInstanceId 流程实例id
     * @param info
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getRequestParams(List<InterfaceParamsModel> list, String processSerialNumber,
        String processInstanceId, InterfaceModel info) throws Exception {
        try {
            String tableName = "";
            for (InterfaceParamsModel model : list) {
                if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                    tableName = model.getTableName();
                    break;
                }
            }
            List<Map<String, Object>> listmap = new ArrayList<>();
            if (!tableName.isEmpty()) {
                DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
                String dialect = DbMetaDataUtil.getDatabaseDialectName(dataSource);
                StringBuilder sqlStr = getSqlStr(dialect, tableName);
                listmap = jdbcTemplate.queryForList(sqlStr.toString(), processSerialNumber);
                LOGGER.info("*********************请求参数返回结果:listmap={}", Y9JsonUtil.writeValueAsString(listmap));
            }
            return listmap;
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "getRequestParams", "",
                info.getInterfaceAddress(), msg);
            if (info.getAbnormalStop().equals("1")) {
                throw new Exception("调用接口失败_getRequestParams：" + info.getInterfaceAddress());
            }
        }
        return new ArrayList<>();
    }

    private StringBuilder getSqlStr(String dialect, String tableName) {
        StringBuilder sqlStr = new StringBuilder();
        if ("oracle".equals(dialect)) {
            sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
        } else if ("dm".equals(dialect)) {
            sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
        } else if ("kingbase".equals(dialect)) {
            sqlStr = new StringBuilder("SELECT * FROM \"" + tableName + "\" where guid =?");
        } else if ("mysql".equals(dialect)) {
            sqlStr = new StringBuilder("SELECT * FROM " + tableName + " where guid =?");
        }
        return sqlStr;
    }

    /**
     * 子表单新增数据
     *
     * @param tableName 表名
     * @param parentProcessSerialNumber 父流程编号
     * @param map 返回数据
     * @param paramsList 参数列表
     * @throws Exception
     */
    public void insertData(String tableName, String parentProcessSerialNumber, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList) throws Exception {
        DataSource database = Objects.requireNonNull(jdbcTemplate.getDataSource());
        String dialect = DbMetaDataUtil.getDatabaseDialectName(database);
        StringBuilder sqlStr = new StringBuilder();
        if ("oracle".equals(dialect)) {
            sqlStr.append("insert into \"" + tableName + "\" (");
        }
        if ("dm".equals(dialect)) {
            sqlStr.append("insert into \"" + tableName + "\" (");

        } else if ("mysql".equals(dialect)) {
            sqlStr.append("insert into " + tableName + " (");

        } else if ("kingbase".equals(dialect)) {
            sqlStr.append("insert into \"" + tableName + "\" (");
        }
        StringBuilder sqlStr1 = new StringBuilder(") values (");
        boolean isHaveField = false;
        for (InterfaceParamsModel model : paramsList) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue())) {
                String fieldName = model.getColumnName();
                String parameterName = model.getParameterName();
                String value = map.get(parameterName) != null ? (String)map.get(parameterName) : "";
                // 处理文件，保存文件，表单存fileStoreId
                if (StringUtils.isNotBlank(model.getFileType()) && StringUtils.isNotBlank(value)) {
                    LOGGER.info("********************文件字段处理:" + model.getParameterName());
                    String downloadUrl = this.saveFile(parentProcessSerialNumber, value, model.getFileType());
                    value = downloadUrl;
                }
                if (isHaveField) {
                    sqlStr.append(",");
                }
                sqlStr.append(fieldName);
                if (isHaveField) {
                    sqlStr1.append(",");
                }
                if (fieldName.equals("guid") || fieldName.equals("GUID")) {
                    sqlStr1.append("'" + Y9IdGenerator.genId(IdType.SNOWFLAKE) + "'");
                } else if (fieldName.equals("parentProcessSerialNumber")) {
                    sqlStr1.append("'" + parentProcessSerialNumber + "'");
                } else if (fieldName.equals("y9_userId")) {
                    sqlStr1.append("'" + Y9LoginUserHolder.getOrgUnitId() + "'");
                } else {
                    sqlStr1.append("'" + value + "'");
                }
                isHaveField = true;
            }
        }
        sqlStr1.append(")");
        sqlStr.append(sqlStr1);
        String sql = sqlStr.toString();
        jdbcTemplate.execute(sql);
    }

    /**
     * 流程启动，办结接口调用
     *
     * @param executionEntity
     * @param variables
     * @param condition
     * @throws Exception
     */
    public void interfaceCallByProcess(ExecutionEntityImpl executionEntity, Map<String, Object> variables,
        String condition) throws Exception {
        String processDefinitionId = executionEntity.getProcessDefinitionId();
        String taskDefinitionKey = "";
        String tenantId = "";
        String processSerialNumber = "";
        String processInstanceId = executionEntity.getProcessInstanceId();
        String itemId = "";
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        Y9Result<List<InterfaceModel>> y9Result = null;
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processSerialNumber = (String)variables.get("processSerialNumber");
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            itemId = processParamModel.getItemId();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, processInstanceId, "", "", "interfaceCallByProcess", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info, processInstanceId,
                        processDefinitionId, "", "", null);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, "", "",
                        null);

                }
            }
        }

    }

    /**
     * 路由经过接口调用
     *
     * @param flow
     * @param condition
     * @throws Exception
     */
    public void interfaceCallBySequenceFlow(FlowableSequenceFlowTakenEventImpl flow, String condition)
        throws Exception {
        String processDefinitionId = flow.getProcessDefinitionId();
        String taskDefinitionKey = flow.getId();
        String tenantId = "";
        String processInstanceId = "";
        String processSerialNumber = "";
        String itemId = "";
        Y9Result<List<InterfaceModel>> y9Result = null;
        String orgUnitId = Y9LoginUserHolder.getOrgUnitId();
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processInstanceId = flow.getProcessInstanceId();
            ProcessParamModel processParamModel =
                processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
            if (processParamModel == null) {// 起草第一步的线，processParamModel为null，不需要调用接口
                LOGGER.info("*********************流程实例ID:{}", processInstanceId);
                return;
            }
            itemId = processParamModel.getItemId();
            processSerialNumber = processParamModel.getProcessSerialNumber();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, processInstanceId, flow.getId(), flow.getId(), "interfaceCallBySequenceFlow", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info, processInstanceId,
                        processDefinitionId, flow.getId(), taskDefinitionKey, null);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, processDefinitionId,
                        flow.getId(), taskDefinitionKey, null);

                }
            }
        }

    }

    /**
     * 任务创建，完成接口调用
     *
     * @param task
     * @param variables
     * @param condition
     * @throws Exception
     */
    public void interfaceCallByTask(DelegateTask task, Map<String, Object> variables, String condition)
        throws Exception {
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String tenantId = "";
        String processSerialNumber = "";
        String itemId = "";
        Integer loopCounter = null;
        String orgUnitId = task.getAssignee();
        Y9Result<List<InterfaceModel>> y9Result = null;
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processSerialNumber = (String)variables.get("processSerialNumber");
            loopCounter = variables.get("loopCounter") != null ? (Integer)variables.get("loopCounter") : null;
            ProcessParamModel processParamModel =
                processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber).getData();
            itemId = processParamModel.getItemId();
            y9Result =
                itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, task.getProcessInstanceId(), task.getId(), task.getTaskDefinitionKey(),
                "interfaceCallByTask", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, orgUnitId, processSerialNumber, itemId, info, task.getProcessInstanceId(),
                        processDefinitionId, task.getId(), task.getTaskDefinitionKey(), loopCounter);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, task.getProcessInstanceId(), processDefinitionId,
                        task.getId(), task.getTaskDefinitionKey(), loopCounter);

                }
            }
        }
    }

    /**
     * post方法调用接口
     *
     * @param processSerialNumber 流程编号
     * @param itemId 流程id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @throws Exception
     */
    @SuppressWarnings("unchecked")
    public void postMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(info.getInterfaceAddress());
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            // 默认添加请求头
            httpPost.addHeader("auth-positionId", Y9LoginUserHolder.getOrgUnitId());
            httpPost.addHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
            Y9Result<List<InterfaceParamsModel>> y9Result =
                itemInterfaceApi.getInterfaceParams(Y9LoginUserHolder.getTenantId(), itemId, info.getId());
            if (y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
                List<Map<String, Object>> list =
                    getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info);
                Map<String, Object> paramsMap = new HashMap<>();
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS.getValue())
                            || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String)map.get(model.getColumnName());
                                    if (loopCounter != null) {// 并行子流程，参数从数组里面通过loopCounter获取
                                        try {
                                            JSONArray array = JSON.parseArray((String)map.get(model.getColumnName()));
                                            parameterValue =
                                                array.size() > loopCounter ? array.get(loopCounter).toString() : "";
                                        } catch (Exception e) {
                                            LOGGER.info("*****************" + model.getColumnName() + "不是数组："
                                                + map.get(model.getColumnName()));
                                        }
                                    }
                                    break;
                                }
                            }
                            paramsMap.put(model.getParameterName(), parameterValue);
                        }
                        // 请求头
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String)map.get(model.getColumnName());
                                    break;
                                }
                            }
                            httpPost.addHeader(model.getParameterName(), parameterValue);
                        }
                    }
                }
                // 参数加入body
                JSONObject jsonString = new JSONObject(paramsMap);
                StringEntity entity = new StringEntity(jsonString.toString(), Consts.UTF_8);
                httpPost.setEntity(entity);
            }
            int time = 10000;
            TaskTimeConfModel taskTimeConf = itemInterfaceApi
                .getTaskTimeConf(Y9LoginUserHolder.getTenantId(), processDefinitionId, itemId, taskKey).getData();
            if (taskTimeConf != null && taskTimeConf.getTimeoutInterrupt() != null
                && taskTimeConf.getTimeoutInterrupt() > 0) {
                time = taskTimeConf.getTimeoutInterrupt();
            }
            String urlStr = httpPost.getURI().toString();
            httpPost.setURI(new URI(urlStr));
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(time)
                .setConnectionRequestTimeout(time).setSocketTimeout(time).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int httpCode = response.getStatusLine().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String resp = EntityUtils.toString(response.getEntity(), "utf-8");
                ObjectMapper objectMapper = new ObjectMapper();
                // 将JSON字符串转换为Java对象
                Y9Result<Map<String, Object>> result = objectMapper.readValue(resp, Y9Result.class);
                if (!result.isSuccess()) {
                    saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                        info.getInterfaceAddress(), resp);
                    if (info.getAbnormalStop().equals("1")) {// 接口异常中断发送
                        throw new Exception("调用接口失败_返回结果：" + resp + "|" + info.getInterfaceAddress());
                    }
                } else {
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info);
                }
                // LOGGER.info("*********************接口返回结果:response={}", resp);
            } else {
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                    info.getInterfaceAddress(), "httpCode:" + httpCode);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
                }
            }
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey,
                info.getInterfaceAddress(), msg);
            if (info.getAbnormalStop().equals("1")) {
                throw new Exception("调用接口失败_postMethod：" + info.getInterfaceAddress());
            }
        } finally {
            if (httpclient != null) {
                try {
                    httpclient.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 保存错误日志
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param interfaceAddress 接口地址
     * @param msg
     * @return
     */
    @Async
    public Future<Boolean> saveErrorLog(final String tenantId, final String processInstanceId, final String taskId,
        final String taskKey, final String interfaceAddress, final String msg) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag("接口调用错误:" + taskKey);
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField(interfaceAddress);
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogModel.setUpdateTime(time);
            errorLogApi.saveErrorLog(tenantId, errorLogModel);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            LOGGER.error("保存错误日志失败", e);
        }
        return new AsyncResult<>(false);
    }

    private String saveFile(String processSerialNumber, String fileStr, String fileType) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String fullPath = "/" + Y9Context.getSystemName() + "/" + processSerialNumber;
        byte[] file = Base64.getDecoder().decode(fileStr);
        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath,
            processSerialNumber + sdf.format(new Date()) + "." + fileType);
        String downloadUrl = "";
        if (y9FileStore != null) {
            downloadUrl = y9Config.getCommon().getProcessAdminBaseUrl() + "/s/" + y9FileStore.getId() + "." + fileType;
        }
        return downloadUrl;
    }

    /**
     * 同步调用接口
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @throws Exception
     */
    public void syncInterface(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET.getValue())) {
            getMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, taskId, taskKey,
                loopCounter);

        } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST.getValue())) {
            postMethod(processSerialNumber, itemId, info, processInstanceId, processDefinitionId, taskId, taskKey,
                loopCounter);
        }
    }

}
