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

import net.risesoft.api.platform.permission.PersonRoleApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.form.Y9FieldPerm;
import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.model.itemadmin.FieldPermModel;
import net.risesoft.model.itemadmin.Y9FormFieldModel;
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
import net.risesoft.y9.util.Y9BeanUtil;

import y9.client.rest.processadmin.RepositoryApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
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
    private RepositoryApiClient repositoryManager;

    @Autowired
    private Y9FieldPermRepository y9FieldPermRepository;

    @Autowired
    private PersonRoleApi personRoleApi;

    @Autowired
    private Y9TableService y9TableService;

    @Override
    public Map<String, Object> delChildTableRow(String formId, String tableId, String guid) {
        return y9FormService.delChildTableRow(formId, tableId, guid);
    }

    @Override
    public List<FieldPermModel> getAllFieldPerm(String formId, String taskDefKey, String processDefinitionId) {
        List<String> list = y9FieldPermRepository.findByFormId(formId);
        List<FieldPermModel> listMap = new ArrayList<>();
        for (String fieldName : list) {
            FieldPermModel model = this.getFieldPerm(formId, fieldName, taskDefKey, processDefinitionId);
            if (model != null) {
                listMap.add(model);
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
                List<String> tableNameList = y9FormRepository.findBindTableName(formId);
                for (String tableName : tableNameList) {
                    Y9Table y9Table = y9TableService.findByTableName(tableName);
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
    public FieldPermModel getFieldPerm(String formId, String fieldName, String taskDefKey, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Y9LoginUserHolder.setTenantId(tenantId);
        FieldPermModel model = new FieldPermModel();
        model.setFieldName(fieldName);
        model.setWritePerm(false);
        Y9FieldPerm y9FieldPerm =
            y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm != null) {
            model = getFieldPerm(y9FieldPerm);
        } else {
            model = null;
            return model;
        }

        return model;
    }

    /**
     * Description: 解析权限
     *
     * @param y9FieldPerm
     * @return
     */
    public FieldPermModel getFieldPerm(Y9FieldPerm y9FieldPerm) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        FieldPermModel model = new FieldPermModel();
        model.setFieldName(y9FieldPerm.getFieldName());
        if (StringUtils.isNotBlank(y9FieldPerm.getWriteRoleId())) {
            model.setWritePerm(false);
            String roleId = y9FieldPerm.getWriteRoleId();
            String[] roleIds = roleId.split(",");
            for (String id : roleIds) {
                boolean b = personRoleApi.hasRole(tenantId, id, userInfo.getPersonId()).getData();
                if (b) {
                    model.setWritePerm(true);
                    break;
                }
            }
        } else {// 未绑定角色，默认该节点所有人都有写权限
            model.setWritePerm(true);
        }
        return model;
    }

    @Override
    public List<Y9FormFieldModel> getFormField(String itemId) {
        List<Y9FormFieldModel> list = new ArrayList<Y9FormFieldModel>();
        try {
            SpmApproveItem item = spmApproveItemService.findById(itemId);
            String processDefineKey = item.getWorkflowGuid();
            ProcessDefinitionModel processDefinition =
                repositoryManager.getLatestProcessDefinitionByKey(Y9LoginUserHolder.getTenantId(), processDefineKey);
            List<Y9FormItemBind> formList =
                y9FormItemBindService.findByItemIdAndProcDefIdAndTaskDefKeyIsNull(itemId, processDefinition.getId());
            for (Y9FormItemBind form : formList) {
                List<Y9FormField> formElementList = y9FormFieldService.findByFormId(form.getFormId());
                for (Y9FormField formElement : formElementList) {
                    if (StringUtils.isNotBlank(formElement.getQuerySign()) && formElement.getQuerySign().equals("1")) {
                        Y9FormFieldModel model = new Y9FormFieldModel();
                        Y9BeanUtil.copyProperties(formElement, model);
                        if (!list.contains(model)) {
                            list.add(model);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
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
