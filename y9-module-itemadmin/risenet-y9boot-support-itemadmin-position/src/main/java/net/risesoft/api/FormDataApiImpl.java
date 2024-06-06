package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.platform.Person;
import net.risesoft.service.FormDataService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 表单接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/formData")
public class FormDataApiImpl implements FormDataApi {

    private final FormDataService formDataService;

    private final PersonApi personManager;

    private final Y9FormItemBindService y9FormItemBindService;

    /**
     * 删除子表数据
     *
     * @param tenantId 租户id
     * @param formId   表单id
     * @param tableId  表id
     * @param guid     数据id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delChildTableRow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delChildTableRow(String tenantId, String formId, String tableId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delChildTableRow(formId, tableId, guid);
    }

    /**
     * 删除前置表单数据
     *
     * @param tenantId 租户id
     * @param formId   表单id
     * @param guid     主键id
     * @return Map<String, Object>
     */
    @Override
    @PostMapping(value = "/delPreFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> delPreFormData(String tenantId, String formId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delPreFormData(formId, guid);
    }

    /**
     * 获取事项绑定的表单
     *
     * @param tenantId            租户id
     * @param itemId              事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey   任务key
     * @return List<Map < String, Object>>
     */
    @Override
    @GetMapping(value = "/findFormItemBind", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> findFormItemBind(String tenantId, String itemId, String processDefinitionId, String taskDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> res_list = new ArrayList<>();
        List<Y9FormItemBind> list = y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        for (Y9FormItemBind item : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("formId", item.getFormId());
            map.put("formName", item.getFormName());
            res_list.add(map);
        }
        return res_list;
    }

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId            租户id
     * @param userId              人员id
     * @param formId              表单id
     * @param taskDefKey          任务key
     * @param processDefinitionId 流程定义id
     * @return List<Map < String, Object>>
     */
    @Override
    @GetMapping(value = "/getAllFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getAllFieldPerm(String tenantId, String userId, String formId, String taskDefKey, String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getAllFieldPerm(formId, taskDefKey, processDefinitionId);
    }

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param tenantId 租户id
     * @param itemId   事项id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getBindPreFormByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getBindPreFormByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getBindPreFormByItemId(itemId);
    }

    /**
     * 获取子表数据
     *
     * @param tenantId            租户id
     * @param formId              表单id
     * @param tableId             表id
     * @param processSerialNumber 流程编号
     * @return List<Map < String, Object>>
     * @throws Exception 抛出异常
     */
    @Override
    @GetMapping(value = "/getChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getChildTableData(String tenantId, String formId, String tableId, String processSerialNumber) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getChildTableData(formId, tableId, processSerialNumber);
    }

    /**
     * 根据事项id和流程序列号获取数据
     *
     * @param tenantId            租户id
     * @param itemId              事项id
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
     * @param tenantId            租户id
     * @param userId              人员id
     * @param formId              表单id
     * @param fieldName           字段名
     * @param taskDefKey          任务key
     * @param processDefinitionId 流程定义id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getFieldPerm", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFieldPerm(String tenantId, String userId, String formId, String fieldName, String taskDefKey, String processDefinitionId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        return formDataService.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param itemId   事项id
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
     * @param formId   表单id
     * @return List<Map < String, String>>
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
     * @param formId   表单id
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
     * @param tenantId            租户id
     * @param formId              表单id
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
     * 根据表单id获取前置表单数据
     *
     * @param tenantId 租户id
     * @param formId   表单id
     * @return List<Map < String, Object>>
     */
    @Override
    @GetMapping(value = "/getPreFormDataByFormId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<Map<String, Object>> getPreFormDataByFormId(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.getPreFormDataByFormId(formId);
    }

    /**
     * 保存子表数据
     *
     * @param tenantId            租户id
     * @param formId              表单id
     * @param tableId             表id
     * @param processSerialNumber 流程编号
     * @param jsonData            json表数据
     */
    @Override
    @PostMapping(value = "/saveChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveChildTableData(String tenantId, String formId, String tableId, String processSerialNumber, @RequestBody String jsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);

    }

    /**
     * 保存表单数据
     *
     * @param tenantId     租户id
     * @param formId       表单id
     * @param formJsonData json表数据
     * @throws Exception 抛出异常
     */
    @Override
    @PostMapping(value = "/saveFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public void saveFormData(String tenantId, String formId, @RequestBody String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveFormData(formJsonData, formId);
    }

    /**
     * 保存前置表单数据
     *
     * @param tenantId     租户id
     * @param itemId       事项id
     * @param formId       表单id
     * @param formJsonData json表数据
     * @throws Exception 抛出异常
     */
    @Override
    @PostMapping(value = "/savePreFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public String savePreFormData(String tenantId, String itemId, String formId, @RequestBody String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        System.out.println("***********************savePreFormData   formJsonData****************" + formJsonData);
        return formDataService.saveAFormData(itemId, formJsonData, formId);
    }
}