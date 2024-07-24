package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.api.platform.org.DepartmentApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.enums.platform.DepartmentPropCategoryEnum;
import net.risesoft.model.itemadmin.BindFormModel;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.itemadmin.Y9FormOptionValueModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.PersonExt;
import net.risesoft.model.platform.Position;
import net.risesoft.model.platform.Tenant;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 表单数据
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/y9form", produces = MediaType.APPLICATION_JSON_VALUE)
public class Y9FormRestController {

    private final TenantApi tenantApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final PersonApi personApi;

    private final FormDataApi formDataApi;

    private final OptionClassApi optionClassApi;

    private final DepartmentApi departmentApi;

    /**
     * 删除子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 主键id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delChildTableRow")
    public Y9Result<String> delChildTableRow(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String tableId, @RequestParam @NotBlank String guid) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Y9Result<Object> y9Result = formDataApi.delChildTableRow(tenantId, formId, tableId, guid);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除子表单数据失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 删除前置表单数据
     *
     * @param formId 表单id
     * @param guid 主键id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/delPreFormData")
    public Y9Result<Object> delPreFormData(@RequestParam @NotBlank String formId, @RequestParam @NotBlank String guid) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.delPreFormData(tenantId, formId, guid);

    }

    /**
     * 获取表单所有字段权限
     *
     * @param formId 表单id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程实例id
     * @return Y9Result<List < FieldPermModel>>
     */
    @GetMapping(value = "/getAllFieldPerm")
    public Y9Result<List<FieldPermModel>> getAllFieldPerm(@RequestParam @NotBlank String formId,
        @RequestParam(required = false) String taskDefKey, @RequestParam @NotBlank String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return formDataApi.getAllFieldPerm(tenantId, userId, formId, taskDefKey, processDefinitionId);
    }

    /**
     * 获取事项绑定前置表单
     *
     * @param itemId 事项id
     * @return Y9Result<BindFormModel>
     */
    @GetMapping(value = "/getBindPreFormByItemId")
    public Y9Result<BindFormModel> getBindPreFormByItemId(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.getBindPreFormByItemId(tenantId, itemId);
    }

    /**
     * 获取子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/getChildTableData")
    public Y9Result<List<Map<String, Object>>> getChildTableData(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String tableId, @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return formDataApi.getChildTableData(tenantId, formId, tableId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取子表单数据失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取表单字段权限
     *
     * @param formId 表单id
     * @param fieldName 表单字段
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程实例id
     * @return Y9Result<FieldPermModel>
     */
    @GetMapping(value = "/getFieldPerm")
    public Y9Result<FieldPermModel> getFieldPerm(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String fieldName, @RequestParam(required = false) String taskDefKey,
        @RequestParam @NotBlank String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return formDataApi.getFieldPerm(tenantId, userId, formId, fieldName, taskDefKey, processDefinitionId);
    }

    /**
     * 获取表单数据
     *
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getFormData")
    public Y9Result<Map<String, Object>> getFormData(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.getFromData(tenantId, formId, processSerialNumber);
    }

    /**
     * 获取表单字段
     *
     * @param itemId 事项id
     * @return Y9Result<List < Y9FormFieldModel>>
     */
    @GetMapping(value = "/getFormField")
    public Y9Result<List<Y9FormFieldModel>> getFormField(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.getFormField(tenantId, itemId);

    }

    /**
     * 获取表单json数据
     *
     * @param formId 表单id
     * @return Y9Result<String>
     */
    @GetMapping(value = "/getFormJson")
    public Y9Result<String> getFormJson(@RequestParam @NotBlank String formId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.getFormJson(tenantId, formId);
    }

    /**
     * 获取Y9表单初始化数据
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getInitData")
    public Y9Result<Map<String, Object>> getInitData() {
        Map<String, Object> map = new HashMap<>(16);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(date);
        SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
        String year = yearsdf.format(date);
        String second = sesdf.format(date);
        String itemNumber = "〔" + year + "〕" + second + "号";
        OrgUnit parent =
            orgUnitApi.getParent(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId()).getData();
        Tenant tenant = tenantApi.getById(Y9LoginUserHolder.getTenantId()).getData();
        /* 办件表单数据初始化 **/
        map.put("deptName", parent.getName());// 创建部门
        map.put("userName", person.getName());// 创建人
        Position position = Y9LoginUserHolder.getPosition();
        map.put("positionName", position.getName());// 创建岗位
        map.put("duty", position.getName().split("（")[0]);// 职务
        map.put("createDate", nowDate);// 创建日期
        map.put("mobile", person.getMobile());// 联系电话
        map.put("tenantName", tenant.getName());// 租户名称
        map.put("tenantId", tenant.getId());// 租户名称
        map.put("number", itemNumber);// 编号
        map.put("sign", "");// 签名
        PersonExt personExt = personApi
            .getPersonExtByPersonId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPersonId())
            .getData();
        if (personExt != null && personExt.getSign() != null) {
            map.put("sign", personExt.getSign());// 签名
        }
        List<OrgUnit> leaders = departmentApi.listDepartmentPropOrgUnits(Y9LoginUserHolder.getTenantId(),
            parent.getId(), DepartmentPropCategoryEnum.LEADER.getValue()).getData();
        map.put("deptLeader", "未配置");// 岗位所在部门领导
        if (!leaders.isEmpty()) {
            List<Person> personLeaders =
                positionApi.listPersonsByPositionId(Y9LoginUserHolder.getTenantId(), leaders.get(0).getId()).getData();
            map.put("deptLeader", personLeaders.isEmpty() ? leaders.get(0).getName() : personLeaders.get(0).getName());
        }
        /* 办件表单数据初始化 **/
        map.put("zihao", second + "号");// 编号
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取数据字典值
     *
     * @param type 字典类型
     * @return Y9Result<List<Y9FormOptionValueModel>>
     */
    @GetMapping(value = "/getOptionValueList")
    public Y9Result<List<Y9FormOptionValueModel>> getOptionValueList(@RequestParam @NotBlank String type) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return optionClassApi.getOptionValueList(tenantId, type);
    }

    /**
     * 获取前置表单数据
     *
     * @param formId 表单id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/getPreFormDataByFormId")
    public Y9Result<List<Map<String, Object>>> getPreFormDataByFormId(@RequestParam @NotBlank String formId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return formDataApi.getPreFormDataByFormId(tenantId, formId);

    }

    /**
     * 保存子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @param jsonData 表数据
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveChildTableData")
    public Y9Result<String> saveChildTableData(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String tableId, @RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String jsonData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            formDataApi.saveChildTableData(tenantId, formId, tableId, processSerialNumber, jsonData);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存子表单数据失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存表单数据
     *
     * @param formId 表单id
     * @param jsonData 表单数据
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveFormData")
    public Y9Result<String> saveFormData(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String jsonData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            formDataApi.saveFormData(tenantId, formId, jsonData);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存表单数据失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存前置表单数据
     *
     * @param formId 表单id
     * @param itemId 事项id
     * @param jsonData 表单数据
     * @return Y9Result<String>
     */
    @PostMapping(value = "/savePreFormData")
    public Y9Result<String> savePreFormData(@RequestParam @NotBlank String formId,
        @RequestParam @NotBlank String itemId, @RequestParam @NotBlank String jsonData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            String processSerialNumber = formDataApi.savePreFormData(tenantId, itemId, formId, jsonData).getData();
            return Y9Result.success(processSerialNumber, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存前置表单数据失败", e);
        }
        return Y9Result.failure("保存失败");
    }

}
