package net.risesoft.controller;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 协作状态列表
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/processInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessInstanceController {

    private final ProcessInstanceApi processInstanceApi;

    /**
     * 获取协作状态列表
     *
     * @param page 页码
     * @param rows 条数
     * @param title 标题
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/processInstanceList")
    public Y9Page<ProcessCooperationModel> processInstanceList(@RequestParam int page, @RequestParam int rows,
        @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return processInstanceApi.processInstanceList(tenantId, positionId, title, page, rows);
    }
}
