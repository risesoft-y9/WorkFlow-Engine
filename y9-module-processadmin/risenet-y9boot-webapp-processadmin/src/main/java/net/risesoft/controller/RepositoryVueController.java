package net.risesoft.controller;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.io.IOUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRepositoryService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/repository")
public class RepositoryVueController {

    private final CustomRepositoryService customRepositoryService;

    /**
     * 删除流程定义
     *
     * @param deploymentId 部署id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> delete(@RequestParam @NotBlank String deploymentId) {
        Y9Result<Object> result = customRepositoryService.delete(deploymentId);
        if (result.isSuccess()) {
            return Y9Result.successMsg("删除成功");
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 部署流程定义
     *
     * @param file 部署文件
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/deploy", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deploy(MultipartFile file) {
        Y9Result<Object> result = customRepositoryService.deploy(file);
        if (result.isSuccess()) {
            return Y9Result.successMsg("部署成功");
        }
        return Y9Result.failure("部署失败");
    }

    /**
     * 获取流程定义列表
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ProcessDefinitionModel>> list(@RequestParam(required = false) String resourceId) {
        return customRepositoryService.list(resourceId);
    }

    /**
     * 获取流程实例
     *
     * @param resourceType 资源类型
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param response HttpServletResponse
     */
    @RequestMapping(value = "/process-instance")
    public void loadByProcessInstance(@RequestParam String resourceType,
        @RequestParam(required = false) String processInstanceId, @RequestParam String processDefinitionId,
        HttpServletResponse response) {
        try {
            InputStream resourceAsStream =
                customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
            int ii = 1024;
            byte[] b = new byte[1024];
            int len;
            while ((len = resourceAsStream.read(b, 0, ii)) != -1) {
                response.getOutputStream().write(b, 0, len);
            }
        } catch (IOException e) {
            LOGGER.error("获取流程实例失败", e);
        }
    }

    /**
     * 挂起、激活流程实例
     *
     * @param state 状态
     * @param processDefinitionId 流程定义id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/switchSuspendOrActive", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> switchSuspendOrActive(@RequestParam String state,
        @RequestParam String processDefinitionId) {
        Y9Result<Object> result = customRepositoryService.switchSuspendOrActive(state, processDefinitionId);
        if (result.isSuccess()) {
            return Y9Result.successMsg("操作成功");
        }
        return Y9Result.failure("操作失败");
    }

    /**
     * 获取流程实例的xml
     *
     * @param resourceType 资源类型
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     */
    @RequestMapping(value = "/processInstanceXml")
    public Y9Result<String> getXmlByProcessInstance(@RequestParam String resourceType,
        @RequestParam(required = false) String processInstanceId, @RequestParam String processDefinitionId) {
        InputStream resourceAsStream =
            customRepositoryService.getProcessInstance(resourceType, processInstanceId, processDefinitionId);
        try {
            return Y9Result.success(IOUtils.toString(resourceAsStream, StandardCharsets.UTF_8));
        } catch (IOException e) {
            LOGGER.error("获取流程实例xml失败", e);
        }
        return Y9Result.failure("获取流程实例xml失败");
    }
}
