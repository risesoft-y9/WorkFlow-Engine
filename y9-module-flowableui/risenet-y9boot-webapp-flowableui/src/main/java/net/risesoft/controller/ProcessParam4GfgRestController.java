package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.processadmin.VariableApi;
import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessParamService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 自定义流程变量
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/processParam/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessParam4GfgRestController {

    private final ProcessParamService processParamService;

    private final VariableApi variableApi;

    private final TaskApi taskApi;

    private final TaskRelatedApi taskRelatedApi;

    /**
     * 保存流程变量
     *
     * @param itemId              事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId   流程实例id
     * @param documentTitle       标题
     * @param number              编号
     * @param level               紧急程度
     * @param customItem          是否定制流程
     * @return Y9Result<StartProcessResultModel>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<StartProcessResultModel> saveOrUpdate(@RequestParam @NotBlank String itemId,
                                                          @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String theTaskKey, @RequestParam(required = false) String processInstanceId,
                                                          @RequestParam(required = false) String documentTitle, @RequestParam(required = false) String number,
                                                          @RequestParam(required = false) String level, @RequestParam(required = false) Boolean customItem) {
        return processParamService.saveOrUpdate(itemId, processSerialNumber, processInstanceId, documentTitle, number,
                level, customItem, theTaskKey);
    }


    /**
     * 保存流程变量
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<StartProcessResultModel>
     */
    @PostMapping(value = "/saveActionName")
    public Y9Result<StartProcessResultModel> saveActionName(@RequestParam(required = false) String processInstanceId, @RequestParam (required = false) String actionName) {
        if (StringUtils.isNotBlank(processInstanceId) && StringUtils.isNotBlank(actionName)) {
            Map<String, Object> vars = new HashMap<>();
            vars.put("val", actionName);
            variableApi.setVariableByProcessInstanceId(Y9LoginUserHolder.getTenantId(), processInstanceId, SysVariables.ACTIONNAME + ":" + Y9LoginUserHolder.getPositionId(), vars);
        }
        return Y9Result.success(null);
    }
}
