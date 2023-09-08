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
     * @param tenantId
     * @param formId
     * @param tableId
     * @param guid
     * @return
     */
    public Map<String, Object> delChildTableRow(String tenantId, String formId, String tableId, String guid);

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId
     * @param userId
     * @param formId
     * @param taskDefKey
     * @param processDefinitionId
     * @return
     */
    List<Map<String, Object>> getAllFieldPerm(String tenantId, String userId, String formId, String taskDefKey, String processDefinitionId);

    /**
     * 获取子表数据
     *
     * @param tenantId
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getChildTableData(String tenantId, String formId, String tableId, String processSerialNumber) throws Exception;

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId
     * @param itemId
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber);

    /**
     * 获取字段权限
     *
     * @param tenantId
     * @param userId
     * @param formId
     * @param fieldName
     * @param taskDefKey
     * @param processDefinitionId
     * @return
     */
    Map<String, Object> getFieldPerm(String tenantId, String userId, String formId, String fieldName, String taskDefKey, String processDefinitionId);

    /**
     * 获取表单绑定字段
     *
     * @param tenantId
     * @param itemId
     * @return
     */
    public List<Y9FormFieldModel> getFormField(String tenantId, String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId
     * @param formId
     * @return
     */
    List<Map<String, String>> getFormFieldDefine(String tenantId, String formId);

    /**
     * 获取表单json数据
     *
     * @param tenantId
     * @param formId
     * @return
     */
    public String getFormJson(String tenantId, String formId);

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId
     * @param formId
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> getFromData(String tenantId, String formId, String processSerialNumber);

    /**
     *
     * Description: 保存子表数据
     *
     * @param tenantId
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @throws Exception
     */
    public void saveChildTableData(String tenantId, String formId, String tableId, String processSerialNumber, String jsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param tenantId
     * @param formId
     * @param formJsonData
     * @throws Exception
     */
    void saveFormData(String tenantId, String formId, String formJsonData) throws Exception;
}
