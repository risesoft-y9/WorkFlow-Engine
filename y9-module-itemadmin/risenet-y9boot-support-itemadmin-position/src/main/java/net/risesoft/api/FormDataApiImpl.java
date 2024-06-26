package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FormDataService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

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
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 数据id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/delChildTableRow", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> delChildTableRow(String tenantId, String formId, String tableId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.delChildTableRow(formId, tableId, guid);
        if ((Boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("删除成功");
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 删除前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param guid 主键id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/delPreFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> delPreFormData(String tenantId, String formId, String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.delPreFormData(formId, guid);
        if ((Boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.successMsg("删除成功");
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取事项绑定的表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @return Y9Result<List<BindFormModel>>
     */
    @Override
    @GetMapping(value = "/findFormItemBind", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<BindFormModel>> findFormItemBind(String tenantId, String itemId, String processDefinitionId,
        String taskDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<BindFormModel> res_list = new ArrayList<>();
        List<Y9FormItemBind> list =
            y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
        for (Y9FormItemBind item : list) {
            BindFormModel model = new BindFormModel();
            Y9BeanUtil.copyProperties(item, model);
            res_list.add(model);
        }
        return Y9Result.success(res_list);
    }

    /**
     * 获取表单所有字段权限
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param formId 表单id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程定义id
     * @return List<Map < String, Object>>
     */
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

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<BindFormModel>
     */
    @Override
    @GetMapping(value = "/getBindPreFormByItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<BindFormModel> getBindPreFormByItemId(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.getBindPreFormByItemId(itemId);
        BindFormModel bindFormModel = new BindFormModel();
        bindFormModel.setFormId((String)map.get("formId"));
        bindFormModel.setFormName((String)map.get("formName"));
        return Y9Result.success(bindFormModel);
    }

    /**
     * 获取子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return List<Map < String, Object>>
     * @throws Exception 抛出异常
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
     * @return Y9Result<FieldPermModel>
     */
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

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return Y9Result<List<Y9FormFieldModel>>
     */
    @Override
    @GetMapping(value = "/getFormField", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<Y9FormFieldModel>> getFormField(String tenantId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9FormFieldModel> list = formDataService.getFormField(itemId);
        return Y9Result.success(list);
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return Y9Result<List<FormFieldDefineModel>>
     */
    @Override
    @GetMapping(value = "/getFormFieldDefine", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<FormFieldDefineModel>> getFormFieldDefine(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<FormFieldDefineModel> list = formDataService.getFormFieldDefine(formId);
        return Y9Result.success(list);
    }

    /**
     * 获取表单json数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return Y9Result<String>
     */
    @Override
    @GetMapping(value = "/getFormJson", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> getFormJson(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String json = formDataService.getFormJson(formId);
        return Y9Result.success(json);
    }

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map<String, Object>>
     */
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

    /**
     * 根据表单id获取前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @Override
    @GetMapping(value = "/getPreFormDataByFormId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(String tenantId, String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> list = formDataService.getPreFormDataByFormId(formId);
        return Y9Result.success(list);
    }

    /**
     * 保存子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @param jsonData json表数据
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/saveChildTableData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveChildTableData(String tenantId, String formId, String tableId,
        String processSerialNumber, @RequestBody String jsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);
        return Y9Result.success();
    }

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return Y9Result<Object>
     * @throws Exception 抛出异常
     */
    @Override
    @PostMapping(value = "/saveFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> saveFormData(String tenantId, String formId, @RequestBody String formJsonData)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveFormData(formJsonData, formId);
        return Y9Result.success();
    }

    /**
     * 保存前置表单数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return Y9Result<String>
     * @throws Exception 抛出异常
     */
    @Override
    @PostMapping(value = "/savePreFormData", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<String> savePreFormData(String tenantId, String itemId, String formId,
        @RequestBody String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        System.out.println("***********************savePreFormData   formJsonData****************" + formJsonData);
        String processSerialNumber = formDataService.saveAFormData(itemId, formJsonData, formId);
        return Y9Result.success(processSerialNumber);
    }
}