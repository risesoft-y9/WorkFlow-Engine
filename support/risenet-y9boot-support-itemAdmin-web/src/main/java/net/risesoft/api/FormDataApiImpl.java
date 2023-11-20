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
import net.risesoft.api.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.service.FormDataService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/formData")
public class FormDataApiImpl implements FormDataApi {

    @Autowired
    private FormDataService formDataService;

    @Autowired
    private PersonApi personManager;

    @Override
    @PostMapping(value = "/delChildTableRow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delChildTableRow(String tenantId, String formId, String tableId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delChildTableRow(formId, tableId, guid);
    }

    @Override
    @GetMapping(value = "/getAllFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllFieldPerm(String tenantId, String userId, String formId, String taskDefKey,
        String processDefinitionId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getAllFieldPerm(formId, taskDefKey, processDefinitionId);
    }

    @Override
    @GetMapping(value = "/getChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getChildTableData(String tenantId, String formId, String tableId,
        String processSerialNumber) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getChildTableData(formId, tableId, processSerialNumber);
    }

    @Override
    @GetMapping(value = "/getData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getData(tenantId, itemId, processSerialNumber);
    }

    @Override
    @GetMapping(value = "/getFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFieldPerm(String tenantId, String userId, String formId, String fieldName,
        String taskDefKey, String processDefinitionId) {
        Person person = personManager.getPerson(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
    }

    @Override
    public List<Y9FormFieldModel> getFormField(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormField(itemId);
    }

    @Override
    @GetMapping(value = "/getFormFieldDefine", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, String>> getFormFieldDefine(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormFieldDefine(formId);
    }

    @Override
    @GetMapping(value = "/getFormJson", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFormJson(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFormJson(formId);
    }

    @Override
    @GetMapping(value = "/getFromData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFromData(String tenantId, String formId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getFromData(formId, processSerialNumber);
    }

    @Override
    @PostMapping(value = "/saveChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveChildTableData(String tenantId, String formId, String tableId, String processSerialNumber,
        String jsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);

    }

    @Override
    @PostMapping(value = "/saveFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveFormData(String tenantId, String formId, String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveFormData(formJsonData, formId);
    }
}