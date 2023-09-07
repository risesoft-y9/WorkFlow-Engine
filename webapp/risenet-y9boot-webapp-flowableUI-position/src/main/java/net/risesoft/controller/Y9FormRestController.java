package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.api.itemadmin.OptionClassApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.api.tenant.TenantApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.OrgUnit;
import net.risesoft.model.Tenant;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequestMapping("/vue/y9form")
public class Y9FormRestController {

    @Autowired
    private TenantApi tenantManager;

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private FormDataApi formDataManager;

    @Autowired
    private OptionClassApi optionClassManager;

    /**
     * 删除子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param guid 主键id
     * @return
     */
    @RequestMapping(value = "/delChildTableRow", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delChildTableRow(String formId, String tableId, String guid) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Map<String, Object> map = formDataManager.delChildTableRow(tenantId, formId, tableId, guid);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取表单所有字段权限
     *
     * @param formId 表单id
     * @param taskDefKey 任务key
     * @param processDefinitionId 流程实例id
     * @return
     */
    @RequestMapping(value = "/getAllFieldPerm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getAllFieldPerm(@RequestParam(required = true) String formId, @RequestParam(required = false) String taskDefKey, @RequestParam(required = true) String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        List<Map<String, Object>> list = formDataManager.getAllFieldPerm(tenantId, userId, formId, taskDefKey, processDefinitionId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @return
     */
    @RequestMapping(value = "/getChildTableData", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getChildTableData(String formId, String tableId, String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Map<String, Object>> list = formDataManager.getChildTableData(tenantId, formId, tableId, processSerialNumber);
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
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
     * @return
     */
    @RequestMapping(value = "/getFieldPerm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getFieldPerm(@RequestParam(required = true) String formId, @RequestParam(required = true) String fieldName, @RequestParam(required = false) String taskDefKey, @RequestParam(required = true) String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        Map<String, Object> map = formDataManager.getFieldPerm(tenantId, userId, formId, fieldName, taskDefKey, processDefinitionId);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取表单数据
     *
     * @param formId 表单id
     * @param processSerialNumber 流程编号
     * @return
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getFormData", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getFormData(@RequestParam(required = true) String formId, @RequestParam(required = true) String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Map<String, Object> map = formDataManager.getFromData(tenantId, formId, processSerialNumber);
            if ((boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.success((Map<String, Object>)map.get("formData"), "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取表单字段
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/getFormField", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Y9FormFieldModel>> getFormField(@RequestParam(required = true) String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            List<Y9FormFieldModel> list = formDataManager.getFormField(tenantId, itemId);
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取表单json数据
     *
     * @param formId 表单id
     * @return
     */
    @RequestMapping(value = "/getFormJson", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<String> getFormJson(@RequestParam(required = true) String formId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String formJson = formDataManager.getFormJson(tenantId, formId);
        return Y9Result.success(formJson, "获取成功");
    }

    /**
     * 获取Y9表单初始化数据
     *
     * @param processSerialNumber 流程编号
     * @return
     */
    @RequestMapping(value = "/getInitData", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getInitData(@RequestParam(required = false) String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String nowDate = sdf.format(date);
        SimpleDateFormat yearsdf = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat sesdf = new SimpleDateFormat("HHmmss");
        String year = yearsdf.format(date);
        String second = sesdf.format(date);
        String itemNumber = "〔" + year + "〕" + second + "号";
        OrgUnit parent = positionApi.getParent(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId());
        Tenant tenant = tenantManager.getById(Y9LoginUserHolder.getTenantId());
        /** 办件表单数据初始化 **/
        map.put("deptName", parent.getName());// 创建部门
        map.put("userName", person.getName());// 创建人
        map.put("createDate", nowDate);// 创建日期
        map.put("mobile", person.getMobile());// 联系电话
        map.put("number", itemNumber);// 编号
        map.put("tenantName", tenant.getName());// 租户名称
        map.put("tenantId", tenant.getId());// 租户名称
        map.put("number", itemNumber);// 编号
        /** 办件表单数据初始化 **/
        map.put("zihao", second + "号");// 编号
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取数据字典值
     *
     * @param type 字典类型
     * @return
     */
    @RequestMapping(value = "/getOptionValueList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getOptionValueList(String type) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<Map<String, Object>> list = optionClassManager.getOptionValueList(tenantId, type);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存子表单数据
     *
     * @param formId 表单id
     * @param tableId 表id
     * @param processSerialNumber 流程编号
     * @param jsonData 表数据
     * @return
     */
    @RequestMapping(value = "/saveChildTableData", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            formDataManager.saveChildTableData(tenantId, formId, tableId, processSerialNumber, jsonData);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存表单数据
     *
     * @param formId 表单id
     * @param jsonData 表单数据
     * @return
     */
    @RequestMapping(value = "/saveFormData", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveFormData(@RequestParam(required = true) String formId, @RequestParam(required = true) String jsonData) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            formDataManager.saveFormData(tenantId, formId, jsonData);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }
}
