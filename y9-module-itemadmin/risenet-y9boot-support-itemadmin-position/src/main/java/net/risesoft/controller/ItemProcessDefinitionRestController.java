package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemProcessDefinition")
public class ItemProcessDefinitionRestController {

    private final RepositoryApi repositoryApi;

    @RequestMapping(value = "/getProcessDefinitionList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ProcessDefinitionModel>> getProcessDefinitionList(@RequestParam String processDefineKey) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ProcessDefinitionModel> pdList = repositoryApi.getProcessDefinitionListByKey(tenantId, processDefineKey);
        return Y9Result.success(pdList, "获取成功");
    }
}