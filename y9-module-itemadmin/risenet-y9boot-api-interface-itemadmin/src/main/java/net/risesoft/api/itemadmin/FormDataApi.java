package net.risesoft.api.itemadmin;

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
     * @return Y9Result<Object>
     */
    @PostMapping("/delChildTableRow")
    Y9Result<Object> delChildTableRow(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId,
        @RequestParam("tableId") String tableId, @RequestParam("guid") String guid);

    /**
     * 删除前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param guid 主键id
     * @return Y9Result<Object>
     */
    @PostMapping("/delPreFormData")
    Y9Result<Object> delPreFormData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId,
        @RequestParam("guid") String guid);

    /**
     * 获取事项绑定表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @return
     */
    @GetMapping("/findFormItemBind")
    Y9Result<List<BindFormModel>> findFormItemBind(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam("taskDefinitionKey") String taskDefinitionKey);

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param formId 表单Id
     * @param taskDefKey taskDefKey
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List<FieldPermModel>>
     */
    @GetMapping("/getAllFieldPerm")
    Y9Result<List<FieldPermModel>> getAllFieldPerm(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("formId") String formId,
        @RequestParam("taskDefKey") String taskDefKey, @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<BindFormModel>
     */
    @GetMapping("/getBindPreFormByItemId")
    Y9Result<BindFormModel> getBindPreFormByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId);

    /**
     * 获取子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param tableId 对应的表id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<Map<String, Object>>>
     * @throws Exception Exception
     */
    @GetMapping("/getChildTableData")
    Y9Result<List<Map<String, Object>>> getChildTableData(@RequestParam("tenantId") String tenantId,
        @RequestParam("formId") String formId, @RequestParam("tableId") String tableId,
        @RequestParam("processSerialNumber") String processSerialNumber) throws Exception;

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping("/getData")
    Y9Result<Map<String, Object>> getData(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取字段权限
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param formId 表单Id
     * @param fieldName 字段名称
     * @param taskDefKey 任务定义key
     * @param processDefinitionId 流程定义id
     * @return Y9Result<FieldPermModel>
     */
    @GetMapping("/getFieldPerm")
    Y9Result<FieldPermModel> getFieldPerm(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("formId") String formId,
        @RequestParam("fieldName") String fieldName, @RequestParam("taskDefKey") String taskDefKey,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 获取表单绑定字段
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<List<Y9FormFieldModel>>
     */
    @GetMapping("/getFormField")
    Y9Result<List<Y9FormFieldModel>> getFormField(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @return Y9Result<List<FormFieldDefineModel>>
     */
    @GetMapping("/getFormFieldDefine")
    Y9Result<List<FormFieldDefineModel>> getFormFieldDefine(@RequestParam("tenantId") String tenantId,
        @RequestParam("formId") String formId);

    /**
     * 获取表单json数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @return Y9Result<String>
     */
    @GetMapping("/getFormJson")
    Y9Result<String> getFormJson(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId);

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping("/getFromData")
    Y9Result<Map<String, Object>> getFromData(@RequestParam("tenantId") String tenantId,
        @RequestParam("formId") String formId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据表单id获取前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping("/getPreFormDataByFormId")
    Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(@RequestParam("tenantId") String tenantId,
        @RequestParam("formId") String formId);

    /**
     * Description: 保存子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param tableId 对应的表id
     * @param processSerialNumber 流程序列号
     * @param jsonData 数据
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping("/saveChildTableData")
    Y9Result<Object> saveChildTableData(@RequestParam("tenantId") String tenantId,
        @RequestParam("formId") String formId, @RequestParam("tableId") String tableId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestBody String jsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单Id
     * @param formJsonData 表单数据
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping(value = "/saveFormData")
    Y9Result<Object> saveFormData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId,
        @RequestBody String formJsonData) throws Exception;

    /**
     * 保存前置表单数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return Y9Result<String>
     * @throws Exception Exception
     */
    @PostMapping(value = "/savePreFormData")
    Y9Result<String> savePreFormData(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("formId") String formId, @RequestBody String formJsonData) throws Exception;

}
