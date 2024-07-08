package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemProcessDefinition", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemProcessDefinitionRestController {

    private final RepositoryApi repositoryApi;

    @GetMapping(value = "/getProcessDefinitionList")
    public Y9Result<List<ProcessDefinitionModel>> getProcessDefinitionList(@RequestParam String processDefineKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ProcessDefinitionModel> pdList =
            repositoryApi.getProcessDefinitionListByKey(tenantId, processDefineKey).getData();
        return Y9Result.success(pdList, "获取成功");
    }
}