package net.risesoft.controller;

import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 自定义流程变量
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/processParam", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessParamRestController {

    private final ProcessParamApi processParamApi;

    private final ProcessParamService processParamService;

    private final VariableApi variableApi;

    private final ActRuDetailApi actRuDetailApi;

    /**
     * 根据流程实例id获取流程变量
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<ProcessParamModel>
     */
    @GetMapping(value = "/getProcessParam")
    public Y9Result<ProcessParamModel> getProcessParam(@RequestParam String processInstanceId) {
        ProcessParamModel processParamModel =
            processParamApi.findByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId).getData();
        if (processParamModel != null) {
            return Y9Result.success(processParamModel);
        }
        return Y9Result.success(null);
    }

    /**
     * 保存流程变量
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<StartProcessResultModel>
     */
    @PostMapping(value = "/saveActionName")
    public Y9Result<StartProcessResultModel> saveActionName(@RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String actionName) {
        if (StringUtils.isNotBlank(processInstanceId) && StringUtils.isNotBlank(actionName)) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("val", actionName);
            variableApi.setVariableByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId,
                SysVariables.ACTIONNAME + ":" + Y9LoginUserHolder.getPositionId(), vars);
        }
        return Y9Result.success(null);
    }

    /**
     * 保存流程变量
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @param number 编号
     * @param level 紧急程度
     * @param customItem 是否定制流程
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam(required = false) String processInstanceId,
        @RequestParam(required = false) String documentTitle, @RequestParam(required = false) String number,
        @RequestParam(required = false) String level, @RequestParam(required = false) Boolean customItem) {
        return processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, documentTitle, number,
            level, customItem);
    }

    /**
     * 设置待办已读
     *
     * @param actRuDetailId 待办详情id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/setRead")
    public Y9Result<Object> setRead(@RequestParam @NotBlank String actRuDetailId) {
        return actRuDetailApi.setRead(Y9LoginUserHolder.getTenantId(), actRuDetailId);
    }

    /**
     * 更新主办司局信息
     *
     * @param deptId 部门id
     * @param deptName 部门名称
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/updateHostDept")
    public Y9Result<Object> updateHostDept(@RequestParam String deptId, @RequestParam String deptName,
        @RequestParam String processSerialNumber) {
        ProcessParamModel processParamModel =
            processParamApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber).getData();
        if (processParamModel != null) {
            processParamModel.setHostDeptId(deptId);
            processParamModel.setHostDeptName(deptName);
            processParamApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), processParamModel);
        }
        return Y9Result.success();
    }
}
