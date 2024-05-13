package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.Y9FormFieldModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface FormDataApi {

    /**
     * 删除子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param tableId 对应的表id
     * @param guid guid
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> delChildTableRow(String tenantId, String formId, String tableId, String guid);

    /**
     * 删除前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param guid 主键id
     * @return
     */
    Map<String, Object> delPreFormData(String tenantId, String formId, String guid);

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param formId 表单Id
     * @param taskDefKey taskDefKey
     * @param processDefinitionId 流程定义id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, Object>> getAllFieldPerm(String tenantId, String userId, String formId, String taskDefKey, String processDefinitionId);

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return
     */
    Map<String, Object> getBindPreFormByItemId(String tenantId, String itemId);

    /**
     * 获取子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param tableId 对应的表id
     * @param processSerialNumber 流程序列号
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     * @throws Exception Exception
     */
    List<Map<String, Object>> getChildTableData(String tenantId, String formId, String tableId, String processSerialNumber) throws Exception;

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber);

    /**
     * 获取字段权限
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param formId 表单Id
     * @param fieldName 字段名称
     * @param taskDefKey 任务定义key
     * @param processDefinitionId 流程定义id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getFieldPerm(String tenantId, String userId, String formId, String fieldName, String taskDefKey, String processDefinitionId);

    /**
     * 获取表单绑定字段
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return List&lt;Y9FormFieldModel&gt;
     */
    List<Y9FormFieldModel> getFormField(String tenantId, String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    List<Map<String, String>> getFormFieldDefine(String tenantId, String formId);

    /**
     * 获取表单json数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @return String
     */
    String getFormJson(String tenantId, String formId);

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param processSerialNumber 流程序列号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getFromData(String tenantId, String formId, String processSerialNumber);

    /**
     * 根据表单id获取前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return
     */
    List<Map<String, Object>> getPreFormDataByFormId(String tenantId, String formId);

    /**
     *
     * Description: 保存子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param tableId 对应的表id
     * @param processSerialNumber 流程序列号
     * @param jsonData 数据
     * @throws Exception Exception
     */
    void saveChildTableData(String tenantId, String formId, String tableId, String processSerialNumber, String jsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param formJsonData 表单数据
     * @throws Exception Exception
     */
    void saveFormData(String tenantId, String formId, String formJsonData) throws Exception;

    /**
     * 保存前置表单数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @throws Exception
     */
    String savePreFormData(String tenantId, String itemId, String formId, String formJsonData) throws Exception;
}
