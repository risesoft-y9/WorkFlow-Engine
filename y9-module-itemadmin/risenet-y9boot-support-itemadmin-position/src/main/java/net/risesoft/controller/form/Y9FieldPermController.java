package net.risesoft.controller.form;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.Y9FormItemBind;
import net.risesoft.entity.Y9FormItemMobileBind;
import net.risesoft.entity.form.Y9FieldPerm;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FieldPermRepository;
import net.risesoft.repository.jpa.Y9FormItemBindRepository;
import net.risesoft.repository.jpa.Y9FormItemMobileBindRepository;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/y9form/fieldPerm")
public class Y9FieldPermController {

    private final Y9FormItemBindRepository y9FormItemBindRepository;

    private final Y9FormItemMobileBindRepository y9FormItemMobileBindRepository;

    private final Y9FieldPermRepository y9FieldPermRepository;

    private final ProcessDefinitionApi processDefinitionManager;

    /**
     * 获取该字段是否配置权限
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @return
     */
    @RequestMapping(value = "/countPerm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Boolean> countPerm(@RequestParam String formId, @RequestParam String fieldName) {
        int count = y9FieldPermRepository.countByFormIdAndFieldName(formId, fieldName);
        return Y9Result.success(count != 0, "获取成功");
    }

    /**
     * 删除字段角色权限
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @param taskDefKey 任务key
     * @return
     */
    @RequestMapping(value = "/deleteRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteRole(@RequestParam String formId, @RequestParam String fieldName,
        @RequestParam(required = false) String taskDefKey) {
        Y9FieldPerm y9FieldPerm =
            y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm != null) {
            y9FieldPerm.setWriteRoleId("");
            y9FieldPerm.setWriteRoleName("");
            y9FieldPermRepository.save(y9FieldPerm);
        }
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 删除字段节点权限
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @param taskDefKey 任务key
     * @return
     */
    @RequestMapping(value = "/delNodePerm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delNodePerm(@RequestParam String formId, @RequestParam String fieldName,
        @RequestParam(required = false) String taskDefKey) {
        Y9FieldPerm y9FieldPerm =
            y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm != null) {
            y9FieldPermRepository.delete(y9FieldPerm);
        }
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取表单所有配置权限字段
     *
     * @param formId 表单id
     * @return
     */
    @RequestMapping(value = "/getAllPerm", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<String>> getAllPerm(@RequestParam String formId) {
        List<String> list = y9FieldPermRepository.findByFormId(formId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取字段权限配置列表
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @return
     */
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getBpmList(@RequestParam String formId, @RequestParam String fieldName) {
        List<Map<String, Object>> resList = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> list = new ArrayList<>();
        list.add(formId);
        List<Y9FormItemBind> bindList = y9FormItemBindRepository.findByFormIdList(list);
        String processDefinitionId = "";
        if (!bindList.isEmpty()) {
            processDefinitionId = bindList.get(0).getProcessDefinitionId();
        }
        if (bindList.isEmpty()) {// 手机端表单
            List<Y9FormItemMobileBind> bindList1 = y9FormItemMobileBindRepository.findByFormIdList(list);
            if (!bindList1.isEmpty()) {
                processDefinitionId = bindList1.get(0).getProcessDefinitionId();
            }
        }
        if (StringUtils.isNotBlank(processDefinitionId)) {
            resList = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
            for (Map<String, Object> map : resList) {
                Y9FieldPerm y9FieldPerm = y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName,
                    (String)map.get("taskDefKey"));
                map.put("writeRoleId", y9FieldPerm != null ? y9FieldPerm.getWriteRoleId() : "");
                map.put("writeRoleName", y9FieldPerm != null ? y9FieldPerm.getWriteRoleName() : "");
                map.put("id", y9FieldPerm != null ? y9FieldPerm.getId() : "");
            }
        }
        return Y9Result.success(resList, "获取成功");
    }

    /**
     * 保存字段-节点权限
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @param taskDefKey 任务key
     * @return
     */
    @RequestMapping(value = "/saveNodePerm", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveNodePerm(@RequestParam String formId, @RequestParam String fieldName,
        @RequestParam(required = false) String taskDefKey) {
        String processDefinitionId = "";
        Y9FieldPerm y9FieldPerm;
        List<String> list = new ArrayList<>();
        list.add(formId);
        List<Y9FormItemBind> bindList = y9FormItemBindRepository.findByFormIdList(list);
        if (!bindList.isEmpty()) {
            processDefinitionId = bindList.get(0).getProcessDefinitionId();
        }
        if (bindList.isEmpty()) {// 手机端表单
            List<Y9FormItemMobileBind> bindList1 = y9FormItemMobileBindRepository.findByFormIdList(list);
            if (!bindList1.isEmpty()) {
                processDefinitionId = bindList1.get(0).getProcessDefinitionId();
            }
        }
        y9FieldPerm = y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm == null) {
            y9FieldPerm = new Y9FieldPerm();
            y9FieldPerm.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9FieldPerm.setFieldName(fieldName);
        y9FieldPerm.setFormId(formId);
        y9FieldPerm.setProcessDefinitionId(processDefinitionId);
        y9FieldPerm.setTaskDefKey(taskDefKey);
        y9FieldPermRepository.save(y9FieldPerm);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存字段角色权限
     *
     * @param formId 表单id
     * @param fieldName 字段名称
     * @param taskDefKey 任务key
     * @param roleNames 角色名称
     * @param roleIds 角色ids
     * @return
     */
    @RequestMapping(value = "/saveRoleChoice", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveRoleChoice(@RequestParam String formId, @RequestParam String fieldName,
        @RequestParam(required = false) String taskDefKey, @RequestParam String roleNames,
        @RequestParam String roleIds) {
        String processDefinitionId = "";
        Y9FieldPerm y9FieldPerm;
        List<String> list = new ArrayList<>();
        list.add(formId);
        List<Y9FormItemBind> bindList = y9FormItemBindRepository.findByFormIdList(list);
        if (!bindList.isEmpty()) {
            processDefinitionId = bindList.get(0).getProcessDefinitionId();
        }
        y9FieldPerm = y9FieldPermRepository.findByFormIdAndFieldNameAndTaskDefKey(formId, fieldName, taskDefKey);
        if (y9FieldPerm == null) {
            y9FieldPerm = new Y9FieldPerm();
            y9FieldPerm.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        y9FieldPerm.setFieldName(fieldName);
        y9FieldPerm.setFormId(formId);
        y9FieldPerm.setProcessDefinitionId(processDefinitionId);
        y9FieldPerm.setTaskDefKey(taskDefKey);
        y9FieldPerm.setWriteRoleId(roleIds);
        y9FieldPerm.setWriteRoleName(roleNames);
        y9FieldPermRepository.save(y9FieldPerm);
        return Y9Result.successMsg("保存成功");
    }

}
