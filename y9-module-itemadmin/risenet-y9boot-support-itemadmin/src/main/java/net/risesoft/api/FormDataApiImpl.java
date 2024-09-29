package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.FormFieldDefineModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FormDataService;
import net.risesoft.service.config.Y9FormItemBindService;
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
@RequestMapping(value = "/services/rest/formData", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class FormDataApiImpl implements FormDataApi {

    private final FormDataService formDataService;

    private final OrgUnitApi orgUnitApi;

    private final Y9FormItemBindService y9FormItemBindService;

    /**
     * 删除子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 数据id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delChildTableRow(@RequestParam String tenantId, @RequestParam String formId,
        @RequestParam String tableId, @RequestParam String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delChildTableRow(formId, tableId, guid);
    }

    /**
     * 删除前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param guid 主键id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delPreFormData(@RequestParam String tenantId, @RequestParam String formId,
        @RequestParam String guid) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return formDataService.delPreFormData(formId, guid);
    }

    /**
     * 获取事项绑定的表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefinitionKey 任务key
     * @return {@code Y9Result<List<BindFormModel>>} 通用请求返回对象 - data 是事项绑定表单
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<BindFormModel>> findFormItemBind(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, String taskDefinitionKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<BindFormModel> res_list = new ArrayList<>();
        List<Y9FormItemBind> list =
            y9FormItemBindService.listByItemIdAndProcDefIdAndTaskDefKey(itemId, processDefinitionId, taskDefinitionKey);
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
     * @return {@code Y9Result<List<FieldPermModel>>} 通用请求返回对象 - data 是表单所有字段权限列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FieldPermModel>> getAllFieldPerm(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String formId, @RequestParam String taskDefKey, @RequestParam String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        List<FieldPermModel> list = formDataService.listAllFieldPerm(formId, taskDefKey, processDefinitionId);
        return Y9Result.success(list);
    }

    /**
     * 根据事项id获取绑定前置表单
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<BindFormModel>} 通用请求返回对象 - data 是前置表单
     * @since 9.6.6
     */
    @Override
    public Y9Result<BindFormModel> getBindPreFormByItemId(@RequestParam String tenantId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.getBindPreFormByItemId(itemId);
        BindFormModel bindFormModel = new BindFormModel();
        bindFormModel.setFormId((String)map.get("formId"));
        bindFormModel.setFormName((String)map.get("formName"));
        return Y9Result.success(bindFormModel);
    }

    /**
     * 获取子表数据（一个表单为一个子表）
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param userId 人员、岗位id
     * @param parentProcessSerialNumber 父流程编号
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是子表数据
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Map<String, Object>>> getChildFormData(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String formId, @RequestParam String parentProcessSerialNumber)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(userId);
        List<Map<String, Object>> list = formDataService.listChildFormData(formId, parentProcessSerialNumber);
        return Y9Result.success(list);
    }

    /**
     * 获取子表数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是子表数据
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Map<String, Object>>> getChildTableData(@RequestParam String tenantId,
        @RequestParam String formId, @RequestParam String tableId, @RequestParam String processSerialNumber)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> list = formDataService.listChildTableData(formId, tableId, processSerialNumber);
        return Y9Result.success(list);
    }

    /**
     * 根据事项id和流程编号获取数据
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Map<String, Object>> getData(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.getData(tenantId, itemId, processSerialNumber);
        return Y9Result.success(map);
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
     * @return {@code Y9Result<FieldPermModel>} 通用请求返回对象 - data 是字段权限
     * @since 9.6.6
     */
    @Override
    public Y9Result<FieldPermModel> getFieldPerm(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String formId, @RequestParam String fieldName, @RequestParam String taskDefKey,
        @RequestParam String processDefinitionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        FieldPermModel model = formDataService.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
        return Y9Result.success(model);
    }

    /**
     * 根据表单id获取表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Map<String, Object>>} 通用请求返回对象 - data 是表单数据
     * @since 9.6.6
     */
    @SuppressWarnings("unchecked")
    @Override
    public Y9Result<Map<String, Object>> getFormData(@RequestParam String tenantId, @RequestParam String formId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> map = formDataService.getFormData(formId, processSerialNumber);
        if ((Boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success((Map<String, Object>)map.get("formData"));
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<List<Y9FormFieldModel>>} 通用请求返回对象 - data 是表单绑定字段列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Y9FormFieldModel>> getFormField(@RequestParam String tenantId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9FormFieldModel> list = formDataService.listFormFieldByItemId(itemId);
        return Y9Result.success(list);
    }

    /**
     * 根据表单id获取绑定字段信息
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return {@code Y9Result<List<FormFieldDefineModel>>} 通用请求返回对象 - data 是绑定字段信息列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FormFieldDefineModel>> getFormFieldDefine(@RequestParam String tenantId,
        @RequestParam String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<FormFieldDefineModel> list = formDataService.listFormFieldDefineByFormId(formId);
        return Y9Result.success(list);
    }

    /**
     * 获取表单json数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是表单json数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getFormJson(@RequestParam String tenantId, @RequestParam String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String json = formDataService.getFormJson(formId);
        return Y9Result.success(json);
    }

    /**
     * 根据表单id获取前置表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @return {@code Y9Result<List<Map<String, Object>>>} 通用请求返回对象 - data 是前置表单数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(@RequestParam String tenantId,
        @RequestParam String formId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> list = formDataService.listPreFormDataByFormId(formId);
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveChildTableData(@RequestParam String tenantId, @RequestParam String formId,
        @RequestParam String tableId, @RequestParam String processSerialNumber, @RequestBody String jsonData)
        throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);
        return Y9Result.success();
    }

    /**
     * 保存子表数据，一个表单是一个子表
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveChildTableData(@RequestParam String tenantId, @RequestParam String formId,
        @RequestBody String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        formDataService.saveChildTableData(formId, formJsonData);
        return Y9Result.success();
    }

    /**
     * 保存表单数据
     *
     * @param tenantId 租户id
     * @param formId 表单id
     * @param formJsonData json表数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveFormData(@RequestParam String tenantId, @RequestParam String formId,
        @RequestBody String formJsonData) throws Exception {
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
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> savePreFormData(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String formId, @RequestBody String formJsonData) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        String processSerialNumber = formDataService.saveAFormData(itemId, formJsonData, formId);
        return Y9Result.success(processSerialNumber);
    }
}