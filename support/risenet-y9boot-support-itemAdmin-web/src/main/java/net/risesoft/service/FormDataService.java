package net.risesoft.service;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface FormDataService {

    /**
     * 删除子表数据
     * 
     * @param formId
     * @param tableId
     * @param guid
     * @return
     */
    public Map<String, Object> delChildTableRow(String formId, String tableId, String guid);

    /**
     * 获取表单所有字段权限
     * 
     * @param formId
     * @param taskDefKey
     * @param processDefinitionId
     * @return
     */
    public List<Map<String, Object>> getAllFieldPerm(String formId, String taskDefKey, String processDefinitionId);

    /**
     * Description: 获取子表数据
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getChildTableData(String formId, String tableId, String processSerialNumber) throws Exception;

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
     * @param formId
     * @param fieldName
     * @param taskDefKey
     * @param processDefinitionId
     * @return
     */
    public Map<String, Object> getFieldPerm(String formId, String fieldName, String taskDefKey, String processDefinitionId);

    /**
     * 根据表单id获取绑定字段信息
     * 
     * @param formId
     * @return
     */
    List<Map<String, String>> getFormFieldDefine(String formId);

    /**
     * 获取表单json数据
     * 
     * @param formId
     * @return
     */
    public String getFormJson(String formId);

    /**
     * 根据表单id获取表单数据
     * 
     * @param formId
     * @param processSerialNumber
     * @return
     */
    public Map<String, Object> getFromData(String formId, String processSerialNumber);

    /**
     * Description: 保存子表数据
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @throws Exception
     */
    public void saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData) throws Exception;

    /**
     * 保存表单数据
     * 
     * @param formdata
     * @param formId
     * @param actionType
     * @throws Exception
     */
    void saveFormData(String formdata, String formId) throws Exception;
}
