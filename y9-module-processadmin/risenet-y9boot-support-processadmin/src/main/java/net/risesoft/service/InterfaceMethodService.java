package net.risesoft.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.model.itemadmin.InterfaceParamsModel;

/**
 * @author zhangchongjie
 * @date 2024/05/29
 */
public interface InterfaceMethodService {

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
    void dataHandling(String processSerialNumber, String processInstanceId, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, InterfaceModel info, final Integer loopCounter) throws Exception;

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
    void getMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception;

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
    List<Map<String, Object>> getRequestParams(List<InterfaceParamsModel> list, String processSerialNumber,
        String processInstanceId, InterfaceModel info, Integer loopCounter) throws Exception;

    /**
     * 子表单新增数据
     *
     * @param tableName 表名
     * @param parentProcessSerialNumber 父流程编号
     * @param map 返回数据
     * @param paramsList 参数列表
     * @throws Exception
     */
    void insertData(String tableName, String parentProcessSerialNumber, Map<String, Object> map,
        List<InterfaceParamsModel> paramsList, String guid) throws Exception;

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
    void postMethod(final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) throws Exception;

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
    Future<Boolean> saveErrorLog(final String tenantId, final String processInstanceId, final String taskId,
        final String taskKey, final String interfaceAddress, final String msg);
}
