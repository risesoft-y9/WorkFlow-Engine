package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.permission.RoleApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FieldPerm;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.repository.form.Y9FieldPermRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.service.FormDataService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9FormItemBindService;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.service.form.Y9FormService;
import net.risesoft.service.form.Y9TableService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "formDataService")
public class FormDataServiceImpl implements FormDataService {

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    @Autowired
    private Y9FormItemBindService y9FormItemBindService;

    @Autowired
    private Y9FormFieldService y9FormFieldService;

    @Autowired
    private Y9FormService y9FormService;

    @Autowired
    private Y9FormRepository y9FormRepository;

    @Resource(name = "jdbcTemplate4Tenant")
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private Y9FieldPermRepository y9FieldPermRepository;

    @Autowired
    private RoleApi roleManager;

    @Autowired
    private Y9TableService y9TableService;

    @Override
    public Map<String, Object> delChildTableRow(String formId, String tableId, String guid) {
        return y9FormService.delChildTableRow(formId, tableId, guid);
    }

    @Override
    public List<Map<String, Object>> getAllFieldPerm(String formId, String taskDefKey, String processDefinitionId) {
        List<String> list = y9FieldPermRepository.findByFormId(formId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        for (String fieldName : list) {
            Map<String, Object> map = this.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
            if (map != null) {
                listMap.add(map);
            }
        }
        return listMap;
    }

    @Override
    public List<Map<String, Object>> getChildTableData(String formId, String tableId, String processSerialNumber)
        throws Exception {
        return y9FormService.getChildTableData(formId, tableId, processSerialNumber);
    }

    @Override
    public Map<String, Object> getData(String tenantId, String itemId, String processSerialNumber) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        try {
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryManager.getLatestProcessDefinitionByKey(tenantId, processDefineKey);
            List<Y9FormItemBind> formList =
                y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            List<Map<String, Object>> list = null;
            for (Y9FormItemBind bind : formList) {
                String formId = bind.getFormId();
                // 获取表单绑定的表,可能多个
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                for (String tableName : tableNameList) {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
                    // 只获取主表
                    if (y9Table.getTableType() == 1) {
                        list = jdbcTemplate.queryForList("SELECT * FROM " + tableName.toUpperCase() + " WHERE GUID=?",
                            processSerialNumber);
                        if (list.size() > 0) {
                            retMap.putAll(list.get(0));
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return retMap;
    }

    @Override
    public Map<String, Object> getFieldPerm(String formId, String fieldName, String taskDefKey,
        String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        // 写权限
        resMap.put("writePerm", false);
        resMap.put("fieldName", fieldName);
        try {
            Y9FieldPerm y9FieldPerm = y9FieldPermRepository.findByFormIdAndFieldNameAndProcessDefinitionIdAndTaskDefKey(
                formId, fieldName, processDefinitionId, taskDefKey);
            if (y9FieldPerm != null) {
                resMap.putAll(getFieldPerm(y9FieldPerm));
            } else {
                resMap = null;
                return resMap;
            }
            resMap.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            resMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return resMap;
    }

    /**
     * 解析权限 Description:
     *
     * @param y9FieldPerm
     * @return
     */
    public Map<String, Object> getFieldPerm(Y9FieldPerm y9FieldPerm) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        // 绑定了角色
        if (StringUtils.isNotBlank(y9FieldPerm.getWriteRoleId())) {
            resMap.put("writePerm", false);
            String roleId = y9FieldPerm.getWriteRoleId();
            String[] roleIds = roleId.split(",");
            for (String id : roleIds) {
                boolean b = roleManager.hasRoleByTenantIdAndRoleIdAndOrgUnitId(tenantId, id, person.getPersonId());
                if (b) {
                    resMap.put("writePerm", true);
                    break;
                }
            }
        } else {// 未绑定角色，默认该节点所有人都有写权限
            // 写权限
            resMap.put("writePerm", true);
        }
        return resMap;
    }

    @Override
    public List<Map<String, String>> getFormFieldDefine(String formId) {
        List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
        try {
            List<Y9FormField> formElementList = y9FormFieldService.findByFormId(formId);
            for (Y9FormField formElement : formElementList) {
                Map<String, String> map = new HashMap<String, String>(16);
                String formCtrltype = formElement.getFieldType();
                String disChinaName = formElement.getFieldCnName();
                String formCtrlName = formElement.getFieldName();
                String columnName = formElement.getFieldName();
                map.put("formCtrltype", formCtrltype);
                map.put("disChinaName", disChinaName);
                map.put("formCtrlName", formCtrlName);
                map.put("columnName", columnName);
                if (!listMap.contains(map)) {
                    listMap.add(map);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return listMap;
    }

    @Override
    public String getFormJson(String formId) {
        String formJson = "";
        try {
            Y9Form y9Form = y9FormRepository.findById(formId).orElse(null);
            formJson = y9Form.getFormJson();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formJson;
    }

    @Override
    public Map<String, Object> getFromData(String formId, String processSerialNumber) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = y9FormService.getFormData(formId, processSerialNumber);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void saveChildTableData(String formId, String tableId, String processSerialNumber, String jsonData)
        throws Exception {
        try {
            Map<String, Object> map = new HashMap<String, Object>(16);
            map = y9FormService.saveChildTableData(formId, tableId, processSerialNumber, jsonData);
            if (!(boolean)map.get(UtilConsts.SUCCESS)) {
                throw new Exception("FormDataService saveFormData error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("FormDataService saveFormData error");
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Transactional(readOnly = false)
    public void saveFormData(String formdata, String formId) throws Exception {
        Map<String, Object> mapFormJsonData = Y9JsonUtil.readValue(formdata, Map.class);
        try {
            List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
            Map<String, Object> map = new HashMap<String, Object>(16);
            map.put("name", "form_Id");
            map.put("value", formId);
            listMap.add(map);
            for (String columnName : mapFormJsonData.keySet()) {
                // 根据数据库表名获取列名
                String value = mapFormJsonData.get(columnName).toString();
                map = new HashMap<String, Object>(16);
                map.put("name", columnName);
                map.put("value", value);
                listMap.add(map);
            }
            formdata = Y9JsonUtil.writeValueAsString(listMap);
            map = y9FormService.saveFormData(formdata);
            if (!(boolean)map.get(UtilConsts.SUCCESS)) {
                throw new Exception("FormDataService saveFormData error");
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("FormDataService saveFormData error");
        }
    }
}
