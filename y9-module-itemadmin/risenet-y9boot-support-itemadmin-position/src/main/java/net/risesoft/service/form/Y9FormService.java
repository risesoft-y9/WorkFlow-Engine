package net.risesoft.service.form;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.form.Y9Form;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
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
    Y9Result<Object> delChildTableRow(String formId, String tableId, String guid);

    /**
     * 删除表单数据
     *
     * @param formId
     * @param guid
     * @return
     */
    Y9Result<Object> delPreFormData(String formId, String guid);

    /**
     * 根据id删除表单
     *
     * @param id
     * @return
     */
    Y9Result<Object> delete(String id);

    /**
     * Description:
     *
     * @param y9TableId
     * @param guid
     * @return
     */
    boolean deleteByGuid(String y9TableId, String guid);

    /**
     * 根据id获取表单信息
     *
     * @param id
     * @return
     */
    Y9Form findById(String id);

    /**
     * 获取表单是否有数据
     *
     * @param guid
     * @param formId
     * @return
     */
    Map<String, Object> getData(String guid, String formId);

    /**
     * 获取表单数据
     *
     * @param formId
     * @param guid
     * @return
     */
    Map<String, Object> getFormData(String formId, String guid);

    /**
     * 获取表单数据
     *
     * @param formId
     * @param guid
     * @return
     */
    Map<String, Object> getFormData4Var(String formId, String guid);

    /**
     * 获取表单绑定字段信息
     *
     * @param id
     * @return
     */
    String getFormField(String id);

    /**
     * 获取所有表单信息
     *
     * @return
     */
    List<Y9Form> listAll();

    /**
     * Description: 获取子表数据，一个表单是一个子表
     *
     * @param formId
     * @param parentProcessSerialNumber
     * @return
     * @throws Exception
     */
    List<Map<String, Object>> listChildFormData(String formId, String parentProcessSerialNumber);

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
     * 获取表单数据列表
     *
     * @param formId
     * @return
     */
    List<Map<String, Object>> listFormData(String formId);

    /**
     *
     * Description: 根据应用id分页获取表单列表
     *
     * @param systemName
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageFormList(String systemName, int page, int rows);

    /**
     * 保存子表数据
     *
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @return
     */
    Y9Result<Object> saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData);

    /**
     * 保存子表数据，一个表单是一个子表
     *
     * @param formId
     * @param jsonData
     * @return
     */
    Y9Result<Object> saveChildTableData(String formId, String jsonData);

    /**
     * 保存表单数据
     *
     * @param formdata
     * @return
     */
    Y9Result<Object> saveFormData(String formdata);

    /**
     * 保存绑定表单字段信息
     *
     * @param formId
     * @param fieldJson
     * @return
     */
    Y9Result<Object> saveFormField(String formId, String fieldJson);

    /**
     * 保存表单json
     *
     * @param id
     * @param formJson
     * @return
     */
    Y9Result<Object> saveFormJson(String id, String formJson);

    /**
     * 保存表单信息
     *
     * @param form
     * @return
     */
    Y9Result<Object> saveOrUpdate(Y9Form form);

}
