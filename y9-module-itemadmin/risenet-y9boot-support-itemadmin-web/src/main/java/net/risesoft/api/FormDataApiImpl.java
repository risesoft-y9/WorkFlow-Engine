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
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
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
    public Y9Result<Object> delChildTableRow(String tenantId, String formId, String tableId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.delChildTableRow(formId, tableId, guid);
        return Y9Result.successMsg("删除成功");
    }

    @Override
    public Y9Result<Object> delPreFormData(String tenantId, String formId, String guid) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Y9Result<List<BindFormModel>> findFormItemBind(String tenantId, String itemId, String processDefinitionId,
        String taskDefinitionKey) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @GetMapping(value = "/getAllFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<FieldPermModel>> getAllFieldPerm(String tenantId, String userId, String formId,
        String taskDefKey, String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<FieldPermModel> list = formDataService.getAllFieldPerm(formId, taskDefKey, processDefinitionId);
        return Y9Result.success(list);
    }

    @Override
    public Y9Result<BindFormModel> getBindPreFormByItemId(String tenantId, String itemId) {
        // TODO Auto-generated method stub
        return null;
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
    public Y9Result<FieldPermModel> getFieldPerm(String tenantId, String userId, String formId, String fieldName,
        String taskDefKey, String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        FieldPermModel model = formDataService.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
        return Y9Result.success(model);
    }

    @Override
    public Y9Result<List<Y9FormFieldModel>> getFormField(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9FormFieldModel> list = formDataService.getFormField(itemId);
        return Y9Result.success(list);
    }

    @Override
    @GetMapping(value = "/getFormFieldDefine", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<FormFieldDefineModel>> getFormFieldDefine(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<FormFieldDefineModel> list = formDataService.getFormFieldDefine(formId);
        return Y9Result.success(list);
    }

    @Override
    @GetMapping(value = "/getFormJson", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> getFormJson(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String json = formDataService.getFormJson(formId);
        return Y9Result.success(json);
    }

    @SuppressWarnings("unchecked")
    @Override
    @GetMapping(value = "/getFromData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Map<String, Object>> getFromData(String tenantId, String formId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.getFromData(formId, processSerialNumber);
        if ((Boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((Map<String, Object>)map.get("formData"));
        }
        return Y9Result.failure("获取失败");
    }

    @Override
    public Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(String tenantId, String formId) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    @PostMapping(value = "/saveChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveChildTableData(String tenantId, String formId, String tableId,
        String processSerialNumber, String jsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);

        return null;
    }

    @Override
    @PostMapping(value = "/saveFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveFormData(String tenantId, String formId, String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveFormData(formJsonData, formId);
        return null;
    }

    @Override
    public Y9Result<String> savePreFormData(String tenantId, String itemId, String formId, String formJsonData)
        throws Exception {
        // TODO Auto-generated method stub
        return Y9Result.success("");
    }
}