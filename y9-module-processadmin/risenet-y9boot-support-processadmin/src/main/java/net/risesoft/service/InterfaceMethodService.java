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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ErrorLogApi;
import net.risesoft.api.itemadmin.ItemInterfaceApi;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ErrorLogModel;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;
import net.risesoft.model.itemadmin.TaskTimeConfModel;
import net.risesoft.pojo.Y9Result;
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
@Service(value = "interfaceMethodService")
public class InterfaceMethodService {

    private final ErrorLogApi errorLogApi;
    private final ItemInterfaceApi itemInterfaceApi;
    private final Y9FileStoreService y9FileStoreService;
    private final Y9Properties y9Config;
    @javax.annotation.Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    public InterfaceMethodService(ItemInterfaceApi itemInterfaceApi, Y9FileStoreService y9FileStoreService,
        Y9Properties y9Config, ErrorLogApi errorLogApi) {
        this.itemInterfaceApi = itemInterfaceApi;
        this.y9FileStoreService = y9FileStoreService;
        this.y9Config = y9Config;
        this.errorLogApi = errorLogApi;
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
        List<InterfaceParamsModel> paramsList, InterfaceModel info, final Integer loopCounter) throws Exception {
        if (map != null && paramsList != null && !paramsList.isEmpty()) {
            try {
                List<Map<String, Object>> tableList = new ArrayList<>();
                for (InterfaceParamsModel model : paramsList) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)) {
                        Map<String, Object> tableMap = new HashMap<>();
                        tableMap.put("tableName", model.getTableName());
                        tableMap.put("tableType", model.getTableType());
                        if (!tableList.contains(tableMap)) {
                            tableList.add(tableMap);
                        }
                    }
                }
                DataSource database = Objects.requireNonNull(jdbcTemplate.getDataSource());
                String dialect = DbMetaDataUtil.getDatabaseDialectName(database);
                String guid = "";// 拼接子表主键id
                for (Map<String, Object> map1 : tableList) {
                    String tableName = (String)map1.get("tableName");
                    String tableType = map1.get("tableType") != null ? (String)map1.get("tableType") : "1";
                    if (tableType.equals("2")) {// 子表
                        guid = processSerialNumber + "_loopCounter_" + loopCounter;
                        StringBuilder sqlStr = getSqlStr(dialect, tableName);
                        List<Map<String, Object>> res_list = jdbcTemplate.queryForList(sqlStr.toString(), guid);
                        if (res_list.isEmpty()) {
                            // 新增数据
                            this.insertData(tableName, processSerialNumber, map, paramsList, guid);
                            continue;
                        }
                    }
                    StringBuilder sqlStr = new StringBuilder();
                    switch (dialect) {
                        case "oracle":
                        case "dm":
                        case "kingbase":
                            sqlStr.append("update \"").append(tableName).append("\" set ");
                            break;
                        default:
                            sqlStr.append("update ").append(tableName).append(" set ");
                            break;
                    }
                    boolean isHaveField = false;
                    for (InterfaceParamsModel model : paramsList) {
                        // 找对应表的响应参数
                        if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)
                            && model.getTableName().equals(tableName)) {
                            String fieldName = model.getColumnName();
                            String parameterName = model.getParameterName();
                            String value = "";
                            if (map.get(parameterName) != null) {
                                if (map.get(parameterName) instanceof ArrayList) {
                                    value = Y9JsonUtil.writeValueAsString(map.get(parameterName));
                                } else {
                                    value = String.valueOf(map.get(parameterName));
                                }
                            }
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
                    if (tableType.equals("2")) {// 子表,切换主键
                        processSerialNumber = guid;
                    }
                    sqlStr.append(" where guid ='").append(processSerialNumber).append("'");
                    String sql = sqlStr.toString();
                    jdbcTemplate.execute(sql);
                }
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
                    getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info, loopCounter);
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS)
                            || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY)) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                String tableName = map.get("tableName").toString();
                                String tableType = map.get("tableType") != null ? map.get("tableType").toString() : "1";
                                List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get("paramsList");
                                if (model.getTableName().equals(tableName)) {
                                    for (Map<String, Object> paramMap : paramsList) {
                                        if (paramMap.containsKey(model.getColumnName())) {
                                            if (paramMap.get(model.getColumnName()) instanceof ArrayList) {
                                                parameterValue =
                                                    Y9JsonUtil.writeValueAsString(paramMap.get(model.getColumnName()));
                                            } else {
                                                parameterValue = (String)paramMap.get(model.getColumnName());
                                            }
                                            if (!tableType.equals("2") && loopCounter != null) {// 不是子表，并且并行子流程，主表参数从数组里面通过loopCounter获取
                                                try {
                                                    JSONArray array =
                                                        JSON.parseArray((String)map.get(model.getColumnName()));
                                                    parameterValue = array.size() > loopCounter
                                                        ? array.get(loopCounter).toString() : "";
                                                    LOGGER.info("*****************" + model.getColumnName() + "是数组:"
                                                        + Y9JsonUtil
                                                            .writeValueAsString(map.get(model.getColumnName())));
                                                } catch (Exception e) {

                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            nameValuePairs.add(new NameValuePair(model.getParameterName(), parameterValue));
                        }
                        // 请求头
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS)) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                String tableName = map.get("tableName").toString();
                                List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get("paramsList");
                                if (model.getTableName().equals(tableName)) {
                                    for (Map<String, Object> paramMap : paramsList) {
                                        if (paramMap.containsKey(model.getColumnName())) {
                                            if (paramMap.get(model.getColumnName()) instanceof ArrayList) {
                                                parameterValue =
                                                    Y9JsonUtil.writeValueAsString(paramMap.get(model.getColumnName()));
                                            } else {
                                                parameterValue = (String)paramMap.get(model.getColumnName());
                                            }
                                        }
                                    }
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
            LOGGER.info("*********************设置请求超时参数:" + time);
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
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info,
                        loopCounter);
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
        String processInstanceId, InterfaceModel info, Integer loopCounter) throws Exception {
        try {
            List<Map<String, Object>> tableList = new ArrayList<>();
            for (InterfaceParamsModel model : list) {
                if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                    Map<String, Object> map = new HashMap<>();
                    map.put("tableName", model.getTableName());
                    map.put("tableType", model.getTableType());
                    map.put("paramsList", new ArrayList<>());
                    if (!tableList.contains(map)) {
                        tableList.add(map);
                    }
                }
            }
            for (Map<String, Object> table : tableList) {
                String tableName = (String)table.get("tableName");
                DataSource dataSource = Objects.requireNonNull(jdbcTemplate.getDataSource());
                String dialect = DbMetaDataUtil.getDatabaseDialectName(dataSource);
                StringBuilder sqlStr = getSqlStr(dialect, tableName);
                String guid = processSerialNumber;
                if (table.get("tableType") != null && table.get("tableType").equals("2") && loopCounter != null) {// 子表
                    guid = processSerialNumber + "_loopCounter_" + loopCounter;// 拼接子表主键id
                }
                List<Map<String, Object>> res_list = jdbcTemplate.queryForList(sqlStr.toString(), guid);
                table.put("paramsList", res_list);
            }
            LOGGER.info("*********************请求参数返回结果:listmap={}", Y9JsonUtil.writeValueAsString(tableList));
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
        List<InterfaceParamsModel> paramsList, String guid) throws Exception {
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
            if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_RESPONSE)) {
                String fieldName = model.getColumnName();
                String parameterName = model.getParameterName();
                String value = "";
                if (map.get(parameterName) != null) {
                    if (map.get(parameterName) instanceof ArrayList) {
                        value = Y9JsonUtil.writeValueAsString(map.get(parameterName));
                    } else {
                        value = String.valueOf(map.get(parameterName));
                    }
                }
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
                sqlStr1.append("'" + value + "'");
                isHaveField = true;
            }
        }

        sqlStr.append(",");
        sqlStr.append("guid");
        sqlStr1.append(",");
        sqlStr1.append("'" + guid + "'");

        sqlStr.append(",");
        sqlStr.append("parentProcessSerialNumber");
        sqlStr1.append(",");
        sqlStr1.append("'" + parentProcessSerialNumber + "'");

        sqlStr.append(",");
        sqlStr.append("y9_userId");
        sqlStr1.append(",");
        sqlStr1.append("'" + Y9LoginUserHolder.getOrgUnitId() + "'");

        sqlStr1.append(")");
        sqlStr.append(sqlStr1);
        String sql = sqlStr.toString();
        jdbcTemplate.execute(sql);
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
                    getRequestParams(y9Result.getData(), processSerialNumber, processInstanceId, info, loopCounter);
                Map<String, Object> paramsMap = new HashMap<>();
                for (InterfaceParamsModel model : y9Result.getData()) {
                    if (model.getBindType().equals(ItemInterfaceTypeEnum.INTERFACE_REQUEST)) {
                        // 请求参数
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.PARAMS)
                            || model.getParameterType().equals(ItemInterfaceTypeEnum.BODY)) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                String tableName = map.get("tableName").toString();
                                String tableType = map.get("tableType") != null ? map.get("tableType").toString() : "";
                                List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get("paramsList");
                                if (model.getTableName().equals(tableName)) {
                                    for (Map<String, Object> paramMap : paramsList) {
                                        if (paramMap.containsKey(model.getColumnName())) {
                                            if (paramMap.get(model.getColumnName()) instanceof ArrayList) {
                                                parameterValue =
                                                    Y9JsonUtil.writeValueAsString(paramMap.get(model.getColumnName()));
                                            } else {
                                                parameterValue = (String)paramMap.get(model.getColumnName());
                                            }
                                            if (!tableType.equals("2") && loopCounter != null) {// 不是子表，并且并行子流程，主表参数从数组里面通过loopCounter获取
                                                try {
                                                    JSONArray array =
                                                        JSON.parseArray((String)map.get(model.getColumnName()));
                                                    parameterValue = array.size() > loopCounter
                                                        ? array.get(loopCounter).toString() : "";
                                                    LOGGER.info("*****************" + model.getColumnName() + "是数组:"
                                                        + Y9JsonUtil
                                                            .writeValueAsString(map.get(model.getColumnName())));
                                                } catch (Exception e) {

                                                }
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                            paramsMap.put(model.getParameterName(), parameterValue);
                        }
                        // 请求头
                        if (model.getParameterType().equals(ItemInterfaceTypeEnum.HEADERS)) {
                            String parameterValue = "";
                            for (Map<String, Object> map : list) {
                                String tableName = map.get("tableName").toString();
                                List<Map<String, Object>> paramsList = (List<Map<String, Object>>)map.get("paramsList");
                                if (model.getTableName().equals(tableName)) {
                                    for (Map<String, Object> paramMap : paramsList) {
                                        if (paramMap.containsKey(model.getColumnName())) {
                                            if (paramMap.get(model.getColumnName()) instanceof ArrayList) {
                                                parameterValue =
                                                    Y9JsonUtil.writeValueAsString(paramMap.get(model.getColumnName()));
                                            } else {
                                                parameterValue = (String)paramMap.get(model.getColumnName());
                                            }
                                        }
                                    }
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
            LOGGER.info("*********************设置请求超时参数:" + time);
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
                    dataHandling(processSerialNumber, processInstanceId, result.getData(), y9Result.getData(), info,
                        loopCounter);
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
    public Future<Boolean> saveErrorLog(final String tenantId, final String processInstanceId, final String taskId,
        final String taskKey, final String interfaceAddress, final String msg) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String time = sdf.format(new Date());
            ErrorLogModel errorLogModel = new ErrorLogModel();
            errorLogModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            errorLogModel.setCreateTime(time);
            errorLogModel.setErrorFlag("InterfaceCall");
            errorLogModel.setErrorType(ErrorLogModel.ERROR_PROCESS_INSTANCE);
            errorLogModel.setExtendField("接口调用失败:任务key【" + taskKey + "】,接口地址:" + interfaceAddress);
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
}
