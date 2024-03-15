package net.risesoft.api;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.platform.Person;
import net.risesoft.service.FormDataService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 表单接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/formData")
public class FormDataApiImpl implements FormDataApi {

    @Autowired
    private FormDataService formDataService;

    @Autowired
    private PersonApi personManager;

    /**
     * 删除子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 数据id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delChildTableRow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delChildTableRow(String tenantId, String formId, String tableId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delChildTableRow(formId, tableId, guid);
    }

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param formId 表单id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @return List<Map<String, Object>>
     */
    @Override
    @GetMapping(value = "/getAllFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllFieldPerm(String tenantId, String userId, String formId, String taskDefKey,
        String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getAllFieldPerm(formId, taskDefKey, processDefinitionId);
    }

    /**
     * 获取子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return List<Map<String, Object>>
     * @throws Exception
     */
    @Override
    @GetMapping(value = "/getChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getChildTableData(String tenantId, String formId, String tableId,
        String processSerialNumber) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getChildTableData(formId, tableId, processSerialNumber);
    }

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getData(tenantId, itemId, processSerialNumber);
    }

    /**
     * 获取字段权限
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param formId 表单id
     * @param fieldName 字段名
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFieldPerm(String tenantId, String userId, String formId, String fieldName,
        String taskDefKey, String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return List<Y9FormFieldModel>
     */
    @Override
    @GetMapping(value = "/getFormField", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Y9FormFieldModel> getFormField(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormField(itemId);
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return List<Map<String, String>>
     */
    @Override
    @GetMapping(value = "/getFormFieldDefine", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getFormFieldDefine(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormFieldDefine(formId);
    }

    /**
     * 获取表单json数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return String
     */
    @Override
    @GetMapping(value = "/getFormJson", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFormJson(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormJson(formId);
    }

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getFromData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFromData(String tenantId, String formId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFromData(formId, processSerialNumber);
    }

    /**
     * 保存子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @param jsonData json表数据
     */
    @Override
    @PostMapping(value = "/saveChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveChildTableData(String tenantId, String formId, String tableId, String processSerialNumber,
        String jsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);

    }

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveFormData(String tenantId, String formId, String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveFormData(formJsonData, formId);
    }
}