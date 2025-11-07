package net.risesoft.service.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.Y9FlowableHolder;
import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.model.itemadmin.TaskTimeConfModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.InterfaceMethodService;
import net.risesoft.util.Y9DateTimeUtils;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.sqlddl.DbMetaDataUtil;
import net.risesoft.y9public.entity.Y9FileStore;
import net.risesoft.y9public.service.Y9FileStoreService;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
@Slf4j
@Service
public class InterfaceMethodServiceImpl implements InterfaceMethodService {

    private static final String LOOP_COUNTER = "_loopCounter_";
    private final ErrorLogApi errorLogApi;
    private final ItemInterfaceApi itemInterfaceApi;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9Properties y9Config;
    @jakarta.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public InterfaceMethodServiceImpl(
        ItemInterfaceApi itemInterfaceApi,
        Y9FileStoreService y9FileStoreService,
        Y9Properties y9Config,
        ErrorLogApi errorLogApi) {
        this.itemInterfaceApi = itemInterfaceApi;
        this.y9FileStoreService = y9FileStoreService;
        this.y9Config = y9Config;
        this.errorLogApi = errorLogApi;
    }

    @Override
    public void dataHandling(String processSerialNumber, String processInstanceId, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, InterfaceModel info, final Integer loopCounter) throws Exception {
        if (map == null || paramsList == null || paramsList.isEmpty()) {
            return;
        }

        try {
            List<Map<String, Object>> tableList = getResponseTableList(paramsList);
            DataSource database = Objects.requireNonNull(jdbcTemplate.getDataSource());
            String dialect = DbMetaDataUtil.getDatabaseDialectName(database);

            for (Map<String, Object> tableInfo : tableList) {
                handleTableData(processSerialNumber, map, paramsList, tableInfo, dialect, loopCounter);
            }
        } catch (Exception e) {
            handleDataHandlingException(e, processInstanceId, info);
        }
    }

    /**
     * 获取响应参数对应的表列表
     */
    private List<Map<String, Object>> getResponseTableList(List<InterfaceParamsModel> paramsList) {
        List<Map<String, Object>> tableList = new ArrayList<>();
        for (InterfaceParamsModel model : paramsList) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)) {
                Map<String, Object> tableMap = new HashMap<>();
                tableMap.put(SysVariables.TABLENAME_KEY, model.getTableName());
                tableMap.put(SysVariables.TABLETYPE_KEY, model.getTableType());
                if (!tableList.contains(tableMap)) {
                    tableList.add(tableMap);
                }
            }
        }
        return tableList;
    }

    /**
     * 处理单个表的数据
     */
    private void handleTableData(String processSerialNumber, Map<String, Object> responseMap,
        List<InterfaceParamsModel> paramsList, Map<String, Object> tableInfo, String dialect, Integer loopCounter)
        throws Exception {
        String tableName = (String)tableInfo.get(SysVariables.TABLENAME_KEY);
        String tableType =
            tableInfo.get(SysVariables.TABLETYPE_KEY) != null ? (String)tableInfo.get(SysVariables.TABLETYPE_KEY) : "1";

        // 处理子表
        if (tableType.equals("2")) {
            handleSubTableData(processSerialNumber, responseMap, paramsList, tableInfo, dialect, loopCounter);
        } else {
            // 处理主表
            updateTableData(processSerialNumber, responseMap, paramsList, tableName, dialect);
        }
    }

    /**
     * 处理子表数据
     */
    private void handleSubTableData(String processSerialNumber, Map<String, Object> responseMap,
        List<InterfaceParamsModel> paramsList, Map<String, Object> tableInfo, String dialect, Integer loopCounter)
        throws Exception {
        String tableName = (String)tableInfo.get(SysVariables.TABLENAME_KEY);
        String guid = processSerialNumber + LOOP_COUNTER + loopCounter;

        StringBuilder sqlStr = getSqlStr(dialect, tableName);
        List<Map<String, Object>> resList = jdbcTemplate.queryForList(sqlStr.toString(), guid);

        if (resList.isEmpty()) {
            // 新增数据
            this.insertData(tableName, processSerialNumber, responseMap, paramsList, guid);
        } else {
            // 更新数据
            updateTableData(guid, responseMap, paramsList, tableName, dialect);
        }
    }

    /**
     * 更新表数据
     */
    private void updateTableData(String guid, Map<String, Object> responseMap, List<InterfaceParamsModel> paramsList,
        String tableName, String dialect) {
        StringBuilder sqlStr = buildUpdateSql(dialect, tableName, responseMap, paramsList, tableName, guid);
        sqlStr.append(" where guid ='").append(guid).append("'");
        String sql = sqlStr.toString();
        jdbcTemplate.execute(sql);
    }

    /**
     * 构建更新SQL语句
     */
    private StringBuilder buildUpdateSql(String dialect, String tableName, Map<String, Object> responseMap,
        List<InterfaceParamsModel> paramsList, String targetTableName, String guid) {
        StringBuilder sqlStr = createUpdateHeader(dialect, tableName);
        boolean isHaveField = false;
        for (InterfaceParamsModel model : paramsList) {
            // 找对应表的响应参数
            if (!isTargetResponseModel(model, targetTableName)) {
                continue;
            }
            String value = getResponseValue(responseMap, model);
            value = processFileValueIfNeeded(model, value, guid);
            if (isHaveField) {
                sqlStr.append(",");
            }
            sqlStr.append(model.getColumnName()).append("=");
            sqlStr.append(StringUtils.isNotBlank(value) ? "'" + value + "'" : "''");
            isHaveField = true;
        }
        return sqlStr;
    }

    /**
     * 创建UPDATE语句头部
     */
    private StringBuilder createUpdateHeader(String dialect, String tableName) {
        StringBuilder sqlStr = new StringBuilder();
        switch (dialect) {
            case SysVariables.ORACLE_KEY:
            case "dm":
            case SysVariables.KINGBASE_KEY:
                sqlStr.append("update \"").append(tableName).append("\" set ");
                break;
            default:
                sqlStr.append("update ").append(tableName).append(" set ");
                break;
        }
        return sqlStr;
    }

    /**
     * 检查是否为目标响应模型
     */
    private boolean isTargetResponseModel(InterfaceParamsModel model, String targetTableName) {
        return model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)
            && model.getTableName().equals(targetTableName);
    }

    /**
     * 处理文件值（如果需要）
     */
    private String processFileValueIfNeeded(InterfaceParamsModel model, String value, String guid) {
        // 处理文件，保存文件，表单存fileStoreId
        if (StringUtils.isNotBlank(model.getFileType()) && StringUtils.isNotBlank(value)) {
            LOGGER.info("********************文件字段处理:{}", model.getParameterName());
            try {
                value = this.saveFile(getProcessSerialNumberFromGuid(guid), value, model.getFileType());
            } catch (Exception e) {
                LOGGER.warn("文件处理失败:{} ", model.getParameterName(), e);
            }
        }
        return value;
    }

    /**
     * 从guid中提取processSerialNumber
     */
    private String getProcessSerialNumberFromGuid(String guid) {
        if (guid.contains(LOOP_COUNTER)) {
            return guid.substring(0, guid.indexOf(LOOP_COUNTER));
        }
        return guid;
    }

    /**
     * 获取响应参数值
     */
    private String getResponseValue(Map<String, Object> responseMap, InterfaceParamsModel model) {
        String parameterName = model.getParameterName();
        String value = "";

        if (responseMap.get(parameterName) != null) {
            if (responseMap.get(parameterName) instanceof ArrayList) {
                value = Y9JsonUtil.writeValueAsString(responseMap.get(parameterName));
            } else {
                value = String.valueOf(responseMap.get(parameterName));
            }
        }

        return value;
    }

    /**
     * 处理数据处理异常
     */
    private void handleDataHandlingException(Exception e, String processInstanceId, InterfaceModel info)
        throws Exception {
        final Writer msgResult = new StringWriter();
        final PrintWriter print = new PrintWriter(msgResult);
        e.printStackTrace(print);
        String msg = msgResult.toString();

        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "dataHandling", "", info.getInterfaceAddress(),
            msg);

        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_dataHandling：" + info.getInterfaceAddress());
        }
    }

    @Override
    public void getMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        try {
            HttpClient client = new HttpClient();
            GetMethod method = createGetMethod(info);
            Y9Result<List<InterfaceParamsModel>> y9Result = getInterfaceParams(itemId, info);
            if (y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
                processGetRequestParameters(method, y9Result, processSerialNumber, processInstanceId, info,
                    loopCounter);
            }
            executeGetRequest(client, method, processInstanceId, processDefinitionId, itemId, taskKey, info,
                processSerialNumber, y9Result, loopCounter);
        } catch (Exception e) {
            handleGetMethodException(e, processInstanceId, taskId, taskKey, info);
        }
    }

    /**
     * 创建GetMethod请求对象
     */
    private GetMethod createGetMethod(InterfaceModel info) {
        GetMethod method = new GetMethod();
        method.setPath(info.getInterfaceAddress());
        // 默认添加请求头
        method.addRequestHeader("auth-positionId", Y9FlowableHolder.getOrgUnitId());
        method.addRequestHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
        return method;
    }

    /**
     * 处理GET请求参数
     */
    private void processGetRequestParameters(GetMethod method, Y9Result<List<InterfaceParamsModel>> y9Result,
        String processSerialNumber, String processInstanceId, InterfaceModel info, Integer loopCounter)
        throws Exception {
        List<Map<String, Object>> list =
            getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info, loopCounter);
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        // 处理请求参数和请求头
        for (InterfaceParamsModel model : y9Result.getData()) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS)
                    || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY)) {
                    String parameterValue = getParameterValue(model, list, loopCounter);
                    nameValuePairs.add(new NameValuePair(model.getParameterName(), parameterValue));
                }
                if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS)) {
                    String parameterValue = getHeaderParameterValue(model, list);
                    method.addRequestHeader(model.getParameterName(), parameterValue);
                }
            }
        }
        if (!nameValuePairs.isEmpty()) {
            method.setQueryString(nameValuePairs.toArray(new NameValuePair[0]));
        }
    }

    /**
     * 执行GET请求
     */
    private void executeGetRequest(HttpClient client, GetMethod method, String processInstanceId,
        String processDefinitionId, String itemId, String taskKey, InterfaceModel info, String processSerialNumber,
        Y9Result<List<InterfaceParamsModel>> y9Result, Integer loopCounter) throws Exception {
        configureGetRequestTimeout(client, processDefinitionId, itemId, taskKey);
        int httpCode = client.executeMethod(method);
        if (httpCode == HttpStatus.SC_OK) {
            String response =
                new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8), StandardCharsets.UTF_8);
            processGetResponse(response, processInstanceId, processSerialNumber, y9Result, info, loopCounter);
        } else {
            handleGetErrorResponse(httpCode, processInstanceId, taskKey, info);
        }
    }

    /**
     * 配置GET请求超时参数
     */
    private void configureGetRequestTimeout(HttpClient client, String processDefinitionId, String itemId,
        String taskKey) {
        int time = 10000;
        TaskTimeConfModel taskTimeConf =
            itemInterfaceApi.getTaskTimeConf(Y9LoginUserHolder.getTenantId(), processDefinitionId, itemId, taskKey)
                .getData();
        if (taskTimeConf != null && taskTimeConf.getTimeoutInterrupt() != null
            && taskTimeConf.getTimeoutInterrupt() > 0) {
            time = taskTimeConf.getTimeoutInterrupt();
        }

        LOGGER.info("*********************设置请求超时参数:" + time);
        // 设置请求超时时间
        client.getHttpConnectionManager().getParams().setConnectionTimeout(time);
        // 设置读取数据超时时间
        client.getHttpConnectionManager().getParams().setSoTimeout(time);
    }

    /**
     * 处理GET响应结果
     */
    private void processGetResponse(String response, String processInstanceId, String processSerialNumber,
        Y9Result<List<InterfaceParamsModel>> y9Result, InterfaceModel info, Integer loopCounter) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Y9Result<Map<String, Object>> result = objectMapper.readValue(response, Y9Result.class);

        if (!result.isSuccess()) {
            handleFailedGetResponse(response, processInstanceId, info);
        } else {
            dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info,
                loopCounter);
        }
    }

    /**
     * 处理失败的GET响应
     */
    private void handleFailedGetResponse(String response, String processInstanceId, InterfaceModel info)
        throws Exception {
        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "", "", info.getInterfaceAddress(), response);
        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_返回结果：" + response + "|" + info.getInterfaceAddress());
        }
    }

    /**
     * 处理GET错误响应状态码
     */
    private void handleGetErrorResponse(int httpCode, String processInstanceId, String taskKey, InterfaceModel info)
        throws Exception {
        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "", taskKey, info.getInterfaceAddress(),
            "httpCode:" + httpCode);
        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
        }
    }

    /**
     * 处理GET方法异常
     */
    private void handleGetMethodException(Exception e, String processInstanceId, String taskId, String taskKey,
        InterfaceModel info) throws Exception {
        final Writer msgResult = new StringWriter();
        final PrintWriter print = new PrintWriter(msgResult);
        e.printStackTrace(print);
        String msg = msgResult.toString();

        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(),
            msg);

        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_getMethod：" + info.getInterfaceAddress());
        }
    }

    @Override
    public List<Map<String, Object>> getRequestParams(List<InterfaceParamsModel> list, String processSerialNumber,
        String processInstanceId, InterfaceModel info, Integer loopCounter) throws Exception {
        try {
            List<Map<String, Object>> tableList = new ArrayList<>();
            for (InterfaceParamsModel model : list) {
                if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put(SysVariables.TABLENAME_KEY, model.getTableName());
                    map.put(SysVariables.TABLETYPE_KEY, model.getTableType());
                    map.put(SysVariables.PARAMSLIST_KEY, new ArrayList<>());
                    if (!tableList.contains(map)) {
                        tableList.add(map);
                    }
                }
            }
            for (Map<String, Object> table : tableList) {
                String tableName = (String)table.get(SysVariables.TABLENAME_KEY);
                DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
                String dialect = DbMetaDataUtil.getDatabaseDialectName(dataSource);
                StringBuilder sqlStr = getSqlStr(dialect, tableName);
                String guid = processSerialNumber;
                if (table.get(SysVariables.TABLETYPE_KEY) != null && table.get(SysVariables.TABLETYPE_KEY).equals("2")
                    && loopCounter != null) {// 子表
                    guid = processSerialNumber + LOOP_COUNTER + loopCounter;// 拼接子表主键id
                }
                List<Map<String, Object>> res_list = jdbcTemplate.queryForList(sqlStr.toString(), guid);
                table.put(SysVariables.PARAMSLIST_KEY, res_list);
            }
            LOGGER.info("*********************请求参数返回结果:listMap={}", Y9JsonUtil.writeValueAsString(tableList));
            return tableList;
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
        StringBuilder sqlStr;
        switch (dialect) {
            case SysVariables.ORACLE_KEY:
            case "dm":
            case SysVariables.KINGBASE_KEY:
                sqlStr = new StringBuilder(String.format("SELECT * FROM \"%s\" where guid =?", tableName));
                break;
            case "mysql":
            default:
                sqlStr = new StringBuilder(String.format("SELECT * FROM %s where guid =?", tableName));
                break;
        }
        return sqlStr;
    }

    @Override
    public void insertData(String tableName, String parentProcessSerialNumber, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, String guid) throws Exception {
        DataSource database = Objects.requireNonNull(jdbcTemplate.getDataSource());
        String dialect = DbMetaDataUtil.getDatabaseDialectName(database);
        InsertSqlInfo sqlInfo = buildInsertSql(dialect, tableName, map, paramsList, guid, parentProcessSerialNumber);
        jdbcTemplate.execute(sqlInfo.sql.toString());
    }

    /**
     * 构建插入SQL语句
     */
    private InsertSqlInfo buildInsertSql(String dialect, String tableName, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, String guid, String parentProcessSerialNumber) throws Exception {
        StringBuilder sqlStr = createInsertHeader(dialect, tableName);
        StringBuilder valuesStr = new StringBuilder(") values (");
        // 添加响应字段
        boolean hasFields = appendResponseFields(sqlStr, valuesStr, map, paramsList, parentProcessSerialNumber);
        // 添加固定字段
        appendFixedFields(sqlStr, valuesStr, guid, parentProcessSerialNumber, hasFields);
        valuesStr.append(")");
        sqlStr.append(valuesStr);
        return new InsertSqlInfo(sqlStr);
    }

    /**
     * 创建INSERT语句头部
     */
    private StringBuilder createInsertHeader(String dialect, String tableName) {
        StringBuilder sqlStr = new StringBuilder();
        switch (dialect) {
            case SysVariables.ORACLE_KEY:
            case "dm":
            case SysVariables.KINGBASE_KEY:
                sqlStr.append("insert into \"").append(tableName).append("\" (");
                break;
            default:
                sqlStr.append("insert into ").append(tableName).append(" (");
                break;
        }
        return sqlStr;
    }

    /**
     * 添加响应字段
     */
    private boolean appendResponseFields(StringBuilder sqlStr, StringBuilder valuesStr, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, String parentProcessSerialNumber) throws Exception {
        boolean isHaveField = false;

        for (InterfaceParamsModel model : paramsList) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)) {
                String value = getResponseValue(map, model);

                // 处理文件字段
                if (StringUtils.isNotBlank(model.getFileType()) && StringUtils.isNotBlank(value)) {
                    LOGGER.info("********************文件字段处理:{}", model.getParameterName());
                    value = this.saveFile(parentProcessSerialNumber, value, model.getFileType());
                }

                if (isHaveField) {
                    sqlStr.append(",");
                    valuesStr.append(",");
                }

                sqlStr.append(model.getColumnName());
                valuesStr.append("'").append(value).append("'");
                isHaveField = true;
            }
        }

        return isHaveField;
    }

    /**
     * 添加固定字段
     */
    private void appendFixedFields(StringBuilder sqlStr, StringBuilder valuesStr, String guid,
        String parentProcessSerialNumber, boolean hasPreviousFields) {
        if (hasPreviousFields) {
            sqlStr.append(",");
            valuesStr.append(",");
        }

        // guid字段
        sqlStr.append("guid");
        valuesStr.append("'").append(guid).append("'");

        // parentProcessSerialNumber字段
        sqlStr.append(",parentProcessSerialNumber");
        valuesStr.append(",'").append(parentProcessSerialNumber).append("'");

        // y9_userId字段
        sqlStr.append(",y9_userId");
        valuesStr.append(",'").append(Y9FlowableHolder.getOrgUnitId()).append("'");
    }

    @Override
    public void postMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception {
        try (CloseableHttpClient httpclient = HttpClients.createDefault()) {
            HttpPost httpPost = createHttpPost(info);
            Y9Result<List<InterfaceParamsModel>> y9Result = getInterfaceParams(itemId, info);
            if (y9Result.isSuccess() && y9Result.getData() != null && !y9Result.getData().isEmpty()) {
                processRequestParameters(httpPost, y9Result, processSerialNumber, processInstanceId, info, loopCounter);
            }
            executeHttpRequest(httpclient, httpPost, processInstanceId, processDefinitionId, itemId, taskKey, info,
                processSerialNumber, y9Result, loopCounter);
        } catch (Exception e) {
            handlePostMethodException(e, processInstanceId, taskId, taskKey, info);
        }
    }

    /**
     * 创建HttpPost请求对象
     */
    private HttpPost createHttpPost(InterfaceModel info) {
        HttpPost httpPost = new HttpPost(info.getInterfaceAddress());
        httpPost.addHeader("Content-Type", "application/json;charset=utf-8");
        // 默认添加请求头
        httpPost.addHeader("auth-positionId", Y9FlowableHolder.getOrgUnitId());
        httpPost.addHeader("auth-tenantId", Y9LoginUserHolder.getTenantId());
        return httpPost;
    }

    /**
     * 获取接口参数
     */
    private Y9Result<List<InterfaceParamsModel>> getInterfaceParams(String itemId, InterfaceModel info) {
        return itemInterfaceApi.getInterfaceParams(Y9LoginUserHolder.getTenantId(), itemId, info.getId());
    }

    /**
     * 处理请求参数
     */
    private void processRequestParameters(HttpPost httpPost, Y9Result<List<InterfaceParamsModel>> y9Result,
        String processSerialNumber, String processInstanceId, InterfaceModel info, Integer loopCounter)
        throws Exception {
        List<Map<String, Object>> list =
            getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info, loopCounter);
        Map<String, Object> paramsMap = buildParamsMap(y9Result.getData(), list, loopCounter);
        // 设置请求头参数
        setRequestHeaders(httpPost, y9Result.getData(), list);
        // 参数加入body
        JSONObject jsonString = new JSONObject(paramsMap);
        StringEntity entity = new StringEntity(jsonString.toString(), Consts.UTF_8);
        httpPost.setEntity(entity);
    }

    /**
     * 构建请求参数映射
     */
    private Map<String, Object> buildParamsMap(List<InterfaceParamsModel> paramsList,
        List<Map<String, Object>> requestParamsList, Integer loopCounter) {
        Map<String, Object> paramsMap = new HashMap<>();

        for (InterfaceParamsModel model : paramsList) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                // 请求参数
                if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS)
                    || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY)) {
                    String parameterValue = getParameterValue(model, requestParamsList, loopCounter);
                    paramsMap.put(model.getParameterName(), parameterValue);
                }
            }
        }

        return paramsMap;
    }

    /**
     * 获取参数值
     */
    private String getParameterValue(InterfaceParamsModel model, List<Map<String, Object>> requestParamsList,
        Integer loopCounter) {
        String parameterValue = "";

        Map<String, Object> targetParamMap = findTargetParamMap(model, requestParamsList);
        if (targetParamMap == null) {
            return parameterValue;
        }

        // 提取基础参数值
        parameterValue = extractBaseParameterValue(targetParamMap, model.getColumnName());

        // 处理数组类型的特殊逻辑
        String tableType = getTableTypeFromParams(targetParamMap);
        if (!"2".equals(tableType) && loopCounter != null) {
            parameterValue = processArrayParameterValue(targetParamMap, model.getColumnName(), loopCounter);
        }

        return parameterValue;
    }

    /**
     * 查找目标参数映射
     */
    private Map<String, Object> findTargetParamMap(InterfaceParamsModel model,
        List<Map<String, Object>> requestParamsList) {
        for (Map<String, Object> map : requestParamsList) {
            String tableName = map.get(SysVariables.TABLENAME_KEY).toString();
            if (!model.getTableName().equals(tableName)) {
                continue;
            }

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get(SysVariables.PARAMSLIST_KEY);

            for (Map<String, Object> paramMap : paramsList) {
                if (paramMap.containsKey(model.getColumnName())) {
                    return paramMap;
                }
            }
        }
        return null;
    }

    /**
     * 提取基础参数值
     */
    private String extractBaseParameterValue(Map<String, Object> paramMap, String columnName) {
        Object value = paramMap.get(columnName);
        if (value instanceof ArrayList) {
            return Y9JsonUtil.writeValueAsString(value);
        } else {
            return value != null ? value.toString() : "";
        }
    }

    /**
     * 从参数映射中获取表类型
     */
    private String getTableTypeFromParams(Map<String, Object> paramMap) {
        Object tableTypeObj = paramMap.get(SysVariables.TABLETYPE_KEY);
        return tableTypeObj != null ? tableTypeObj.toString() : "";
    }

    /**
     * 处理数组参数值
     */
    private String processArrayParameterValue(Map<String, Object> paramMap, String columnName, Integer loopCounter) {
        try {
            Object columnValue = paramMap.get(columnName);
            if (columnValue instanceof String) {
                JSONArray array = JSON.parseArray((String)columnValue);
                String result = array.size() > loopCounter ? array.get(loopCounter).toString() : "";
                LOGGER.info("*****************{}是数组:{}", columnName, Y9JsonUtil.writeValueAsString(columnValue));
                return result;
            }
        } catch (Exception e) {
            LOGGER.warn("解析数组参数失败: {}", columnName, e);
        }
        return "";
    }

    /**
     * 设置请求头参数
     */
    private void setRequestHeaders(HttpPost httpPost, List<InterfaceParamsModel> paramsList,
        List<Map<String, Object>> requestParamsList) {
        for (InterfaceParamsModel model : paramsList) {
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                // 请求头
                if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS)) {
                    String parameterValue = getHeaderParameterValue(model, requestParamsList);
                    httpPost.addHeader(model.getParameterName(), parameterValue);
                }
            }
        }
    }

    /**
     * 获取请求头参数值
     */
    private String getHeaderParameterValue(InterfaceParamsModel model, List<Map<String, Object>> requestParamsList) {
        Map<String, Object> targetParamMap = findTargetHeaderParamMap(model, requestParamsList);
        if (targetParamMap == null) {
            return "";
        }
        return extractParameterValue(targetParamMap, model.getColumnName());
    }

    /**
     * 查找目标请求头参数映射
     */
    private Map<String, Object> findTargetHeaderParamMap(InterfaceParamsModel model,
        List<Map<String, Object>> requestParamsList) {
        for (Map<String, Object> map : requestParamsList) {
            String tableName = map.get(SysVariables.TABLENAME_KEY).toString();
            if (!model.getTableName().equals(tableName)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get(SysVariables.PARAMSLIST_KEY);
            for (Map<String, Object> paramMap : paramsList) {
                if (paramMap.containsKey(model.getColumnName())) {
                    return paramMap;
                }
            }
        }
        return null;
    }

    /**
     * 提取参数值
     */
    private String extractParameterValue(Map<String, Object> paramMap, String columnName) {
        Object value = paramMap.get(columnName);
        if (value instanceof ArrayList) {
            return Y9JsonUtil.writeValueAsString(value);
        } else {
            return value != null ? value.toString() : "";
        }
    }

    /**
     * 执行HTTP请求
     */
    private void executeHttpRequest(CloseableHttpClient httpclient, HttpPost httpPost, String processInstanceId,
        String processDefinitionId, String itemId, String taskKey, InterfaceModel info, String processSerialNumber,
        Y9Result<List<InterfaceParamsModel>> y9Result, Integer loopCounter) throws Exception {
        configureRequestTimeout(httpPost, processDefinitionId, itemId, taskKey);
        CloseableHttpResponse response = httpclient.execute(httpPost);
        int httpCode = response.getStatusLine().getStatusCode();
        if (httpCode == HttpStatus.SC_OK) {
            String resp = EntityUtils.toString(response.getEntity(), "utf-8");
            processResponse(resp, processInstanceId, processSerialNumber, y9Result, info, loopCounter);
        } else {
            handleErrorResponse(httpCode, processInstanceId, taskKey, info);
        }
    }

    /**
     * 配置请求超时参数
     */
    private void configureRequestTimeout(HttpPost httpPost, String processDefinitionId, String itemId, String taskKey)
        throws Exception {
        int time = 10000;
        TaskTimeConfModel taskTimeConf =
            itemInterfaceApi.getTaskTimeConf(Y9LoginUserHolder.getTenantId(), processDefinitionId, itemId, taskKey)
                .getData();
        if (taskTimeConf != null && taskTimeConf.getTimeoutInterrupt() != null
            && taskTimeConf.getTimeoutInterrupt() > 0) {
            time = taskTimeConf.getTimeoutInterrupt();
        }
        String urlStr = httpPost.getURI().toString();
        httpPost.setURI(new URI(urlStr));
        LOGGER.info("*********************设置请求超时参数:{}", time);
        RequestConfig requestConfig = RequestConfig.custom()
            .setConnectTimeout(time)
            .setConnectionRequestTimeout(time)
            .setSocketTimeout(time)
            .build();
        httpPost.setConfig(requestConfig);
    }

    /**
     * 处理响应结果
     */
    private void processResponse(String resp, String processInstanceId, String processSerialNumber,
        Y9Result<List<InterfaceParamsModel>> y9Result, InterfaceModel info, Integer loopCounter) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        Y9Result<Map<String, Object>> result = objectMapper.readValue(resp, Y9Result.class);

        if (!result.isSuccess()) {
            handleFailedResponse(resp, processInstanceId, info);
        } else {
            dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info,
                loopCounter);
        }
    }

    /**
     * 处理失败的响应
     */
    private void handleFailedResponse(String resp, String processInstanceId, InterfaceModel info) throws Exception {
        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "", "", info.getInterfaceAddress(), resp);
        if (info.getAbnormalStop().equals("1")) {// 接口异常中断发送
            throw new Exception("调用接口失败_返回结果：" + resp + "|" + info.getInterfaceAddress());
        }
    }

    /**
     * 处理错误响应状态码
     */
    private void handleErrorResponse(int httpCode, String processInstanceId, String taskKey, InterfaceModel info)
        throws Exception {
        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, "", taskKey, info.getInterfaceAddress(),
            "httpCode:" + httpCode);
        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_返回状态：" + httpCode + "|" + info.getInterfaceAddress());
        }
    }

    /**
     * 处理POST方法异常
     */
    private void handlePostMethodException(Exception e, String processInstanceId, String taskId, String taskKey,
        InterfaceModel info) throws Exception {
        final Writer msgResult = new StringWriter();
        final PrintWriter print = new PrintWriter(msgResult);
        e.printStackTrace(print);
        String msg = msgResult.toString();

        saveErrorLog(Y9LoginUserHolder.getTenantId(), processInstanceId, taskId, taskKey, info.getInterfaceAddress(),
            msg);

        if (info.getAbnormalStop().equals("1")) {
            throw new Exception("调用接口失败_postMethod：" + info.getInterfaceAddress());
        }
    }

    @Override
    public Future<Boolean> saveErrorLog(final String tenantId, final String processInstanceId, final String taskId,
        final String taskKey, final String interfaceAddress, final String msg) {
        try {
            String time = Y9DateTimeUtils.formatCurrentDateTime();
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setErrorFlag("InterfaceCall");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("接口调用失败:任务key【" + taskKey + "】,接口地址:" + interfaceAddress);
            errorLogModel.setProcessInstanceId(processInstanceId);
            errorLogModel.setTaskId(taskId);
            errorLogModel.setText(msg);
            errorLogApi.saveErrorLog(tenantId, errorLogModel);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            LOGGER.error("保存错误日志失败", e);
        }
        return new AsyncResult<>(false);
    }

    private String saveFile(String processSerialNumber, String fileStr, String fileType) throws Exception {
        String fullPath = "/" + Y9Context.getSystemName() + "/" + processSerialNumber;
        byte[] file = Base64.getDecoder().decode(fileStr);
        Y9FileStore y9FileStore = y9FileStoreService.uploadFile(file, fullPath,
            processSerialNumber + Y9DateTimeUtils.formatCurrentDateTimeCompact() + "." + fileType);
        String downloadUrl = "";
        if (y9FileStore != null) {
            downloadUrl = y9Config.getCommon().getProcessAdminBaseUrl() + "/s/" + y9FileStore.getId() + "." + fileType;
        }
        return downloadUrl;
    }

    /**
     * 插入SQL信息封装类
     */
    private static class InsertSqlInfo {
        StringBuilder sql;

        InsertSqlInfo(StringBuilder sql) {
            this.sql = sql;
        }
    }
}