package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9Form;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface Y9FormService {

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
     * 根据id删除表单
     * 
     * @param id
     * @return
     */
    public Map<String, Object> delete(String id);

    /**
     * Description:
     * 
     * @param y9TableId
     * @param guid
     * @return
     */
    boolean deleteByGuid(String y9TableId, String guid);

    /**
     * 获取所有表单信息
     * 
     * @param id
     * @return
     */
    public List<Y9Form> findAll();

    /**
     * 根据id获取表单信息
     * 
     * @param id
     * @return
     */
    public Y9Form findById(String id);

    /**
     * Description: 获取子表数据
     * 
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @return
     * @throws Exception
     */
    public List<Map<String, Object>> getChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception;

    /**
     * 获取表单是否有数据
     * 
     * @param guid
     * @param formId
     * @return
     */
    public Map<String, Object> getData(String guid, String formId);

    /**
     * 获取表单数据
     * 
     * @param formId
     * @param guid
     * @return
     */
    public Map<String, Object> getFormData(String formId, String guid);

    /**
     * 获取表单绑定字段信息
     * 
     * @param id
     * @return
     */
    public String getFormField(String id);

    /**
     * Description: 根据应用id分页获取表单列表
     * 
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> getFormList(String systemName, int page, int rows);

    /**
     * 保存子表数据
     * 
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @return
     */
    public Map<String, Object> saveChildTableData(String formId, String tableId, String processSerialNumber,
        String jsonData);

    /**
     * 保存表单数据
     * 
     * @param formdata
     * @return
     */
    public Map<String, Object> saveFormData(String formdata);

    /**
     * 保存绑定表单字段信息
     * 
     * @param formId
     * @param fieldJson
     * @return
     */
    public Map<String, Object> saveFormField(String formId, String fieldJson);

    /**
     * 保存表单json
     * 
     * @param id
     * @param formJson
     * @return
     */
    public Map<String, Object> saveFormJson(String id, String formJson);

    /**
     * 保存表单信息
     * 
     * @param form
     * @return
     */
    public Map<String, Object> saveOrUpdate(Y9Form form);

    /**
     * 更新表单文件
     * 
     * @param formId
     * @param tenantId
     * @return
     */
    public boolean updateFormFile(String formId, String tenantId);

}
