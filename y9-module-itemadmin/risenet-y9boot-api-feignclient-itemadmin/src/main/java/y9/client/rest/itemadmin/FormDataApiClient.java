package y9.client.rest.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.model.itemadmin.Y9FormFieldModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "FormDataApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}", url = "${y9.service.itemAdmin.directUrl:}", path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/formData")
public interface FormDataApiClient extends FormDataApi {

    /**
     * 删除子表数据
     *
     * @param tenantId
     * @param formId
     * @param tableId
     * @param guid
     * @return
     */
    @Override
    @PostMapping("/delChildTableRow")
    public Map<String, Object> delChildTableRow(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestParam("tableId") String tableId, @RequestParam("guid") String guid);

    @Override
    @PostMapping("/delPreFormData")
    public Map<String, Object> delPreFormData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestParam("guid") String guid);

    @Override
    @GetMapping("/findFormItemBind")
    List<Map<String, Object>> findFormItemBind(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId, @RequestParam("taskDefinitionKey") String taskDefinitionKey);

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
    @Override
    @GetMapping("/getAllFieldPerm")
    List<Map<String, Object>> getAllFieldPerm(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("formId") String formId, @RequestParam("taskDefKey") String taskDefKey, @RequestParam("processDefinitionId") String processDefinitionId);

    @Override
    @GetMapping("/getBindPreFormByItemId")
    Map<String, Object> getBindPreFormByItemId(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

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
    @Override
    @GetMapping("/getChildTableData")
    public List<Map<String, Object>> getChildTableData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestParam("tableId") String tableId, @RequestParam("processSerialNumber") String processSerialNumber) throws Exception;

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId
     * @param itemId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/getData")
    public Map<String, Object> getData(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber);

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
    @Override
    @GetMapping("/getFieldPerm")
    Map<String, Object> getFieldPerm(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("formId") String formId, @RequestParam("fieldName") String fieldName, @RequestParam("taskDefKey") String taskDefKey,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 获取表单字段信息
     */
    @Override
    @GetMapping("/getFormField")
    List<Y9FormFieldModel> getFormField(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId
     * @param formId
     * @return
     */
    @Override
    @GetMapping("/getFormFieldDefine")
    List<Map<String, String>> getFormFieldDefine(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId);

    /**
     * 获取表单json数据
     *
     * @param tenantId
     * @param formId
     * @return
     */
    @Override
    @GetMapping("/getFormJson")
    public String getFormJson(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId);

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId
     * @param formId
     * @param processSerialNumber
     * @return
     */
    @Override
    @GetMapping("/getFromData")
    public Map<String, Object> getFromData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestParam("processSerialNumber") String processSerialNumber);

    @Override
    @GetMapping("/getPreFormDataByFormId")
    List<Map<String, Object>> getPreFormDataByFormId(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId);

    /**
     * 保存子表数据 Description:
     *
     * @param tenantId
     * @param formId
     * @param tableId
     * @param processSerialNumber
     * @param jsonData
     * @throws Exception
     */
    @Override
    @PostMapping("/saveChildTableData")
    public void saveChildTableData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestParam("tableId") String tableId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestBody String jsonData) throws Exception;

    /**
     * 保存表单数据
     *
     * @param tenantId
     * @param formId
     * @param formJsonData
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveFormData")
    void saveFormData(@RequestParam("tenantId") String tenantId, @RequestParam("formId") String formId, @RequestBody String formJsonData) throws Exception;

    /**
     * 保存前置表单数据
     *
     * @param tenantId
     * @param itemId
     * @param formId
     * @param formJsonData
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/savePreFormData")
    String savePreFormData(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId, @RequestParam("formId") String formId, @RequestBody String formJsonData) throws Exception;
}
