package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
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
    Y9Result<Object> delChildTableRow(String formId, String tableId, String guid);

    /**
     * 删除前置表单数据
     *
     * @param formId
     * @param guid
     * @return
     */
    Y9Result<Object> delPreFormData(String formId, String guid);

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param itemId
     * @return
     */
    Map<String, Object> getBindPreFormByItemId(String itemId);

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId
     * @param itemId
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber);

    /**
     * 获取字段权限
     *
     * @param formId
     * @param fieldName
     * @param taskDefKey
     * @param processDefinitionId
     * @return
     */
    FieldPermModel getFieldPerm(String formId, String fieldName, String taskDefKey, String processDefinitionId);

    /**
     * 获取表单json数据
     *
     * @param formId
     * @return
     */
    String getFormJson(String formId);

    /**
     * 根据表单id获取表单数据
     *
     * @param formId
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> getFromData(String formId, String processSerialNumber);

    /**
     * 获取表单所有字段权限
     *
     * @param formId
     * @param taskDefKey
     * @param processDefinitionId
     * @return List<FieldPermModel>
     */
    List<FieldPermModel> listAllFieldPerm(String formId, String taskDefKey, String processDefinitionId);

    /**
     * Description: 获取子表数据，一个表单是一个子表
     *
     * @param formId
     * @param parentProcessSerialNumber
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listChildFormData(String formId, String parentProcessSerialNumber) throws Exception;

    /**
     * Description: 获取子表数据
     *
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception;

    /**
     * 获取表单绑定字段信息
     *
     * @param itemId
     * @return
     */
    List<Y9FormFieldModel> listFormFieldByItemId(String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param formId
     * @return
     */
    List<FormFieldDefineModel> listFormFieldDefineByFormId(String formId);

    /**
     * 根据表单id获取前置表单数据
     *
     * @param formId
     * @return
     */
    List<Map<String, Object>> listPreFormDataByFormId(String formId);

    /**
     * 保存前置表单数据
     *
     * @param itemId
     * @param formdata
     * @param formId
     */
    String saveAFormData(String itemId, String formdata, String formId) throws Exception;

    /**
     * Description: 保存子表数据
     *
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @throws Exception
     */
    void saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData)
        throws Exception;

    /**
     * Description: 保存子表数据，一个表单是一个子表
     *
     * @param formId
     * @param jsonData
     * @throws Exception
     */
    void saveChildTableData(String formId, String jsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param formdata
     * @param formId
     * @throws Exception
     */
    void saveFormData(String formdata, String formId) throws Exception;
}
