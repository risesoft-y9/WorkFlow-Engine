package net.risesoft.service;

import cn.hutool.json.JSONObject;
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
import net.risesoft.pojo.Y9Result;
import net.risesoft.util.DbMetaDataUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
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
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
@Slf4j
@EnableAsync
@Service(value = "interfaceUtilService")
public class InterfaceUtilService {

    private final JdbcTemplate jdbcTemplate;

    private final ProcessParamApi processParamApi;

    private final ErrorLogApi errorLogApi;

    private final ItemInterfaceApi itemInterfaceApi;

    public InterfaceUtilService(@Qualifier("jdbcTemplate4Tenant") JdbcTemplate jdbcTemplate, ProcessParamApi processParamApi, ErrorLogApi errorLogApi, ItemInterfaceApi itemInterfaceApi) {
        this.jdbcTemplate = jdbcTemplate;
        this.processParamApi = processParamApi;
        this.errorLogApi = errorLogApi;
        this.itemInterfaceApi = itemInterfaceApi;
    }

    /**
     * 异步调用接口
     *
     * @param tenantId
     * @param processInstanceId
     * @param itemId
     * @param info
     * @return
     */
    @Async
    public Future<Boolean> asynInterface(final String tenantId, final String positionId, final String processSerialNumber, final String itemId, final InterfaceModel info, final String processInstanceId, final String taskId, final String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        try {
            if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET.getValue())) {
                getMethod(processSerialNumber, itemId, info, processInstanceId, taskId, taskKey);

            } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST.getValue())) {
                postMethod(processSerialNumber, itemId, info, processInstanceId, taskId, taskKey);
            }
            return new AsyncResult<>(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    /**
     * 接口响应数据处理
     *
     * @param processSerialNumber
     * @param processInstanceId
     * @param map
     * @param paramsList
     * @param info
     * @throws Exception
     */
    public void dataHandling(String processSerialNumber, String processInstanceId, Map<String, Object> map, List<InterfaceParamsModel> paramsList, InterfaceModel info) throws Exception {
        if (map != null && paramsList != null && paramsList.size() > 0) {
            Connection connection = null;
            try {
                String tableName = "";
                for (InterfaceParamsModel model : paramsList) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue())) {
                        tableName = model.getTableName();
                        break;
                    }
                }
                connection = jdbcTemplate.getDataSource().getConnection();
                DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
                String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
                StringBuffer sqlStr = new StringBuffer("");
                if ("oracle".equals(dialect)) {
                    sqlStr.append("update \"" + tableName + "\" set ");

                } else if ("dm".equals(dialect)) {
                    sqlStr.append("update \"" + tableName + "\" set ");

                } else if ("mysql".equals(dialect)) {
                    sqlStr.append("update " + tableName + " set ");

                } else if ("kingbase".equals(dialect)) {
                    sqlStr.append("update \"" + tableName + "\" set ");
                }
                boolean isHaveField = false;
                for (InterfaceParamsModel model : paramsList) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE.getValue())) {
                        if (isHaveField) {
                            sqlStr.append(",");
                        }
                        String fieldName = model.getColumnName();
                        String parameterName = model.getParameterName();
                        sqlStr.append(fieldName + "=");
                        sqlStr.append(StringUtils.isNotBlank((String) map.get(parameterName)) ? "'" + map.get(parameterName) + "'" : "''");
                        isHaveField = true;
                    }
                }
                StringBuffer sqlStr1 = new StringBuffer("");
                sqlStr1.append(" where guid ='" + processSerialNumber + "'");
                sqlStr.append(sqlStr1);
                String sql = sqlStr.toString();
                System.out.println("******************************sql:" + sql);
                jdbcTemplate.execute(sql);
            } catch (Exception e) {
                final Writer msgResult = new StringWriter();
                final PrintWriter print = new PrintWriter(msgResult);
                e.printStackTrace(print);
                String msg = msgResult.toString();
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "dataHandling", "", info.getInterfaceAddress(), msg);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_dataHandling：" + info.getInterfaceAddress());
                }
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        }
    }

    /**
     * get方法调用接口
     *
     * @param tenantId
     * @param processInstanceId
     * @param itemId
     * @param info
     * @return
     */
    @SuppressWarnings("unchecked")
    public void getMethod(final String processSerialNumber, final String itemId, final InterfaceModel info, final String processInstanceId, final String taskId, final String taskKey) throws Exception {
        try {
            HttpClient client = new HttpClient();
            GetMethod method = new GetMethod();
            method.setPath(info.getInterfaceAddress());
            // 默认添加请求头
            method.addRequestHeader("auth-positionId", Y9LoginUserHolder.getPositionId());
            method.addRequestHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
            Y9Result<List<InterfaceParamsModel>> y9Result = itemInterfaceApi.getInterfaceParams(Y9LoginUserHolder.getTenantId(), itemId, info.getId());
            List<NameValuePair> nameValuePairs = new ArrayList<>();
            if (y9Result.isSuccess() && y9Result.getData() != null && y9Result.getData().size() > 0) {
                List<Map<String, Object>> list = getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info);
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS.getValue()) || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String) map.get(model.getColumnName());
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
                                    parameterValue = (String) map.get(model.getColumnName());
                                    break;
                                }
                            }
                            method.addRequestHeader(model.getParameterName(), parameterValue);
                        }
                    }
                }
            }
            if (nameValuePairs != null && !nameValuePairs.isEmpty()) {
                method.setQueryString(nameValuePairs.toArray(new NameValuePair[nameValuePairs.size()]));
            }
            // 设置请求超时时间10s
            client.getHttpConnectionManager().getParams().setConnectionTimeout(10000);
            // 设置读取数据超时时间10s
            client.getHttpConnectionManager().getParams().setSoTimeout(10000);
            int httpCode = client.executeMethod(method);
            if (httpCode == HttpStatus.SC_OK) {
                String response = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
                ObjectMapper objectMapper = new ObjectMapper();
                // 将JSON字符串转换为Java对象
                Y9Result<Map<String, Object>> result = objectMapper.readValue(response, Y9Result.class);
                if (!result.isSuccess()) {
                    if (info.getAbnormalStop().equals("1")) {// 接口异常中断发送
                        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), response);
                        throw new Exception("调用接口失败_返回结果：" + response + "|" + info.getInterfaceAddress());
                    }
                } else {
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info);
                }
                LOGGER.info("*********************接口返回结果:response={}", response);
            } else {
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), "httpCode:" + httpCode);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
                }
            }
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), msg);
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
     * @param processInstanceId
     * @param info
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getRequestParams(List<InterfaceParamsModel> list, String processSerialNumber, String processInstanceId, InterfaceModel info) throws Exception {
        Connection connection = null;
        try {
            String tableName = "";
            for (InterfaceParamsModel model : list) {
                if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                    tableName = model.getTableName();
                    break;
                }
            }
            connection = jdbcTemplate.getDataSource().getConnection();
            DbMetaDataUtil dbMetaDataUtil = new DbMetaDataUtil();
            String dialect = dbMetaDataUtil.getDatabaseDialectName(connection);
            StringBuffer sqlStr = new StringBuffer();
            if ("oracle".equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
            } else if ("dm".equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
            } else if ("kingbase".equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM \"" + tableName + "\" where guid =?");
            } else if ("mysql".equals(dialect)) {
                sqlStr = new StringBuffer("SELECT * FROM " + tableName + " where guid =?");
            }
            List<Map<String, Object>> listmap = jdbcTemplate.queryForList(sqlStr.toString(), processSerialNumber);
            LOGGER.info("*********************请求参数返回结果:listmap={}", Y9JsonUtil.writeValueAsString(listmap));
            return listmap;
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "getRequestParams", "", info.getInterfaceAddress(), msg);
            if (info.getAbnormalStop().equals("1")) {
                throw new Exception("调用接口失败_getRequestParams：" + info.getInterfaceAddress());
            }
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
        return new ArrayList<Map<String, Object>>();
    }

    /**
     * 流程启动，办结接口调用
     *
     * @param executionEntity
     * @param variables
     * @param string
     */
    public void interfaceCallByProcess(ExecutionEntityImpl executionEntity, Map<String, Object> variables, String condition) throws Exception {
        String processDefinitionId = executionEntity.getProcessDefinitionId();
        String taskDefinitionKey = "";
        String tenantId = "";
        String processSerialNumber = "";
        String processInstanceId = executionEntity.getProcessInstanceId();
        String itemId = "";
        String positionId = Y9LoginUserHolder.getPositionId();
        Y9Result<List<InterfaceModel>> y9Result = null;
        try {
            tenantId = (String) variables.get("tenantId");
            processSerialNumber = (String) variables.get("processSerialNumber");
            ProcessParamModel processParamModel = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
            itemId = processParamModel.getItemId();
            y9Result = itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, processInstanceId, "", "", "interfaceCallByProcess", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && y9Result.getData().size() > 0) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, positionId, processSerialNumber, itemId, info, processInstanceId, "", "");

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, "", "");

                }
            }
        }

    }

    /**
     * 路由经过接口调用
     *
     * @param entity0
     * @param condition
     */
    public void interfaceCallBySequenceFlow(FlowableSequenceFlowTakenEventImpl flow, String condition) throws Exception {
        String processDefinitionId = flow.getProcessDefinitionId();
        String taskDefinitionKey = flow.getId();
        String tenantId = "";
        String processInstanceId = "";
        String processSerialNumber = "";
        String itemId = "";
        Y9Result<List<InterfaceModel>> y9Result = null;
        String positionId = Y9LoginUserHolder.getPositionId();
        try {
            tenantId = FlowableTenantInfoHolder.getTenantId();
            processInstanceId = flow.getProcessInstanceId();
            ProcessParamModel processParamModel = processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
            itemId = processParamModel.getItemId();
            processSerialNumber = processParamModel.getProcessSerialNumber();
            y9Result = itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, processInstanceId, flow.getId(), flow.getId(), "interfaceCallBySequenceFlow", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && y9Result.getData().size() > 0) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, positionId, processSerialNumber, itemId, info, processInstanceId, flow.getId(), taskDefinitionKey);

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, processInstanceId, flow.getId(), taskDefinitionKey);

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
    public void interfaceCallByTask(DelegateTask task, Map<String, Object> variables, String condition) throws Exception {
        String processDefinitionId = task.getProcessDefinitionId();
        String taskDefinitionKey = task.getTaskDefinitionKey();
        String tenantId = "";
        String processSerialNumber = "";
        String itemId = "";
        String positionId = task.getAssignee();
        Y9Result<List<InterfaceModel>> y9Result = null;
        try {
            tenantId = (String) variables.get("tenantId");
            processSerialNumber = (String) variables.get("processSerialNumber");
            ProcessParamModel processParamModel = processParamApi.findByProcessSerialNumber(tenantId, processSerialNumber);
            itemId = processParamModel.getItemId();
            y9Result = itemInterfaceApi.getInterface(tenantId, itemId, taskDefinitionKey, processDefinitionId, condition);
        } catch (Exception e) {
            final Writer result = new StringWriter();
            final PrintWriter print = new PrintWriter(result);
            e.printStackTrace(print);
            String msg = result.toString();
            saveErrorLog(tenantId, task.getProcessInstanceId(), task.getId(), task.getTaskDefinitionKey(), "interfaceCallByTask", msg);
        }
        if (y9Result != null && y9Result.isSuccess() && y9Result.getData() != null && y9Result.getData().size() > 0) {
            for (InterfaceModel info : y9Result.getData()) {
                if (info.getAsyn().equals("1")) {
                    asynInterface(tenantId, positionId, processSerialNumber, itemId, info, task.getProcessInstanceId(), task.getId(), task.getTaskDefinitionKey());

                } else if (info.getAsyn().equals("0")) {
                    syncInterface(processSerialNumber, itemId, info, task.getProcessInstanceId(), task.getId(), task.getTaskDefinitionKey());

                }
            }
        }
    }

    /**
     * post方法调用接口
     *
     * @param tenantId
     * @param processInstanceId
     * @param itemId
     * @param info
     * @return
     */
    @SuppressWarnings("unchecked")
    public void postMethod(final String processSerialNumber, final String itemId, final InterfaceModel info, final String processInstanceId, final String taskId, final String taskKey) throws Exception {
        CloseableHttpClient httpclient = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(info.getInterfaceAddress());
            httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
            // 默认添加请求头
            httpPost.addHeader("auth-positionId", Y9LoginUserHolder.getPositionId());
            httpPost.addHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
            Y9Result<List<InterfaceParamsModel>> y9Result = itemInterfaceApi.getInterfaceParams(Y9LoginUserHolder.getTenantId(), itemId, info.getId());
            if (y9Result.isSuccess() && y9Result.getData() != null && y9Result.getData().size() > 0) {
                List<Map<String, Object>> list = getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info);
                Map<String, Object> paramsMap = new HashMap<String, Object>();
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST.getValue())) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS.getValue()) || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY.getValue())) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                if (map.containsKey(model.getColumnName())) {
                                    parameterValue = (String) map.get(model.getColumnName());
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
                                    parameterValue = (String) map.get(model.getColumnName());
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
            String urlStr = httpPost.getURI().toString();
            httpPost.setURI(new URI(urlStr));
            RequestConfig requestConfig = RequestConfig.custom().setConnectTimeout(10000).setConnectionRequestTimeout(10000).setSocketTimeout(10000).build();
            httpPost.setConfig(requestConfig);
            CloseableHttpResponse response = httpclient.execute(httpPost);
            int httpCode = response.getStatusLine().getStatusCode();
            if (httpCode == HttpStatus.SC_OK) {
                String resp = EntityUtils.toString(response.getEntity(), "utf-8");
                ObjectMapper objectMapper = new ObjectMapper();
                // 将JSON字符串转换为Java对象
                Y9Result<Map<String, Object>> result = objectMapper.readValue(resp, Y9Result.class);
                if (!result.isSuccess()) {
                    saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), resp);
                    if (info.getAbnormalStop().equals("1")) {// 接口异常中断发送
                        throw new Exception("调用接口失败_返回结果：" + resp + "|" + info.getInterfaceAddress());
                    }
                } else {
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info);
                }
                LOGGER.info("*********************接口返回结果:response={}", resp);
            } else {
                saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), "httpCode:" + httpCode);
                if (info.getAbnormalStop().equals("1")) {
                    throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
                }
            }
        } catch (Exception e) {
            final Writer msgResult = new StringWriter();
            final PrintWriter print = new PrintWriter(msgResult);
            e.printStackTrace(print);
            String msg = msgResult.toString();
            saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(), msg);
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
     * @param tenantId
     * @param processInstanceId
     * @param taskId
     * @param taskKey
     * @param interfaceAddress
     * @param msg
     * @return
     */
    @Async
    public Future<Boolean> saveErrorLog(final String tenantId, final String processInstanceId, final String taskId, final String taskKey, final String interfaceAddress, final String msg) {
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
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

    /**
     * 同步调用接口
     *
     * @param tenantId
     * @param processInstanceId
     * @param itemId
     * @param info
     * @return
     */
    public void syncInterface(final String processSerialNumber, final String itemId, final InterfaceModel info, final String processInstanceId, final String taskId, final String taskKey) throws Exception {
        if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET.getValue())) {
            getMethod(processSerialNumber, itemId, info, processInstanceId, taskId, taskKey);

        } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST.getValue())) {
            postMethod(processSerialNumber, itemId, info, processInstanceId, taskId, taskKey);
        }
    }

}
