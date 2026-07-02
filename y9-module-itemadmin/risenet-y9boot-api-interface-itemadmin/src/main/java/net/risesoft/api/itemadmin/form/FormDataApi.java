package net.risesoft.api.itemadmin.form;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.pojo.Y9Result;

/**
 * 表单接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface FormDataApi {

    @GetMapping("/copy")
    Y9Result<Object> copy(@RequestParam String sourceProcessSerialNumber,
        @RequestParam String targetProcessSerialNumber);

    /**
     * 删除子表数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 数据id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delChildTableRow")
    Y9Result<Object> delChildTableRow(@RequestParam String formId, @RequestParam String tableId,
        @RequestParam String guid);

    /**
     * 删除前置表单数据
     *
     * @param formId 表单id
     * @param guid 主键id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delPreFormData")
    Y9Result<Object> delPreFormData(@RequestParam String formId, @RequestParam String guid);

    /**
     * 获取事项绑定的表单
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @return {@code Y9Result<List<BindFormModel>>} 通用请求返回对象 - data 是事项绑定表单
     * @since 9.6.6
     */
    @GetMapping("/findFormItemBind")
    Y9Result<List<BindFormModel>> findFormItemBind(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefinitionKey);

    /**
     * 获取岗位指定表单的所有字段权限
     *
     * @param formId 表单id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<List<FieldPermModel>>} 通用请求返回对象 - data 是表单所有字段权限列表
     * @since 9.6.6
     */
    @GetMapping("/getAllFieldPerm")
    Y9Result<List<FieldPermModel>> getAllFieldPerm(@RequestParam String formId, @RequestParam String taskDefKey,
        @RequestParam String processDefinitionId);

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param itemId 事项id
     * @return {@code Y9Result<BindFormModel>} 通用请求返回对象 - data 是前置表单
     * @since 9.6.6
     */
    @GetMapping("/getBindPreFormByItemId")
    Y9Result<BindFormModel> getBindPreFormByItemId(@RequestParam String itemId);

    /**
     * 获取子表数据，一个表单是一个子表
     *
     * @param formId 表单id
     * @param parentProcessSerialNumber 父流程编号
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是子表数据
     * @throws Exception Exception
     * @since 9.6.6
     */
    @GetMapping("/getChildFormData")
    Y9Result<List<Map<String, Object>>> getChildFormData(@RequestParam String formId,
        @RequestParam String parentProcessSerialNumber) throws Exception;

    /**
     * 获取子表数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是子表数据
     * @throws Exception Exception
     * @since 9.6.6
     */
    @GetMapping("/getChildTableData")
    Y9Result<List<Map<String, Object>>> getChildTableData(@RequestParam String formId, @RequestParam String tableId,
        @RequestParam String processSerialNumber) throws Exception;

    /**
     * 根据事项id和流程编号获取数据
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getData")
    Y9Result<Map<String, Object>> getData(@RequestParam String itemId, @RequestParam String processSerialNumber);

    /**
     * 根据表别名以及流程序列号获取表数据
     *
     * @param guid 唯一标识
     * @param tableAlias 表别名
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是表数据
     */
    @GetMapping(value = "/getData4TableAlias")
    Y9Result<Map<String, Object>> getData4TableAlias(@RequestParam String guid, @RequestParam String tableAlias);

    /**
     * 根据事项id和流程编号集合获取数据
     *
     * @param itemId 事项id
     * @param processSerialNumbers 流程编号集合
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getDataByProcessSerialNumbers")
    Y9Result<Map<String, Map<String, Object>>> getDataByProcessSerialNumbers(@RequestParam String itemId,
        @RequestParam List<String> processSerialNumbers);

    /**
     * 获取字段权限
     *
     * @param formId 表单id
     * @param fieldName 字段名
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<FieldPermModel>} 通用请求返回对象 - data 是字段权限
     * @since 9.6.6
     */
    @GetMapping("/getFieldPerm")
    Y9Result<FieldPermModel> getFieldPerm(@RequestParam String formId, @RequestParam String fieldName,
        @RequestParam String taskDefKey, @RequestParam String processDefinitionId);

    /**
     * 根据表单id获取表单数据
     *
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是表单数据
     * @since 9.6.6
     */
    @GetMapping("/getFormData")
    Y9Result<Map<String, Object>> getFormData(@RequestParam String formId, @RequestParam String processSerialNumber);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param itemId 事项id
     * @return {@code Y9Result<List<Y9FormFieldModel>>} 通用请求返回对象 - data 是表单绑定字段列表
     * @since 9.6.6
     */
    @GetMapping("/getFormField")
    Y9Result<List<Y9FormFieldModel>> getFormField(@RequestParam String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param formId 表单id
     * @return {@code Y9Result<List<FormFieldDefineModel>>} 通用请求返回对象 - data 是绑定字段信息列表
     * @since 9.6.6
     */
    @GetMapping("/getFormFieldDefine")
    Y9Result<List<FormFieldDefineModel>> getFormFieldDefine(@RequestParam String formId);

    /**
     * 获取表单json数据
     *
     * @param formId 表单id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是表单json数据
     * @since 9.6.6
     */
    @GetMapping("/getFormJson")
    Y9Result<String> getFormJson(@RequestParam String formId);

    /**
     * 根据表单id获取前置表单数据
     *
     * @param formId 表单id
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是前置表单数据
     * @since 9.6.6
     */
    @GetMapping("/getPreFormDataByFormId")
    Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(@RequestParam String formId);

    @PostMapping(value = "/insertFormData")
    Y9Result<String> insertFormData(@RequestParam String tableName, @RequestParam String guid,
        @RequestBody String formJsonData);

    /**
     * 保存子表数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @param jsonData json表数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/saveChildTableData")
    Y9Result<Object> saveChildTableData(@RequestParam String formId, @RequestParam String tableId,
        @RequestParam String processSerialNumber, @RequestBody String jsonData) throws Exception;

    /**
     * 保存子表单数据，一个表单是一个子表
     *
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/saveChildTableData1")
    Y9Result<Object> saveChildTableData(@RequestParam String formId, @RequestBody String formJsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping(value = "/saveFormData")
    Y9Result<Object> saveFormData(@RequestParam String formId, @RequestBody String formJsonData) throws Exception;

    /**
     * 保存前置表单数据
     *
     * @param itemId 事项id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping(value = "/savePreFormData")
    Y9Result<String> savePreFormData(@RequestParam String itemId, @RequestParam String formId,
        @RequestBody String formJsonData) throws Exception;

    @PostMapping(value = "/updateFormData")
    Y9Result<String> updateFormData(@RequestParam String guid, @RequestBody String formJsonData);

}
