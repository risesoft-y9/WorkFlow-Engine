package net.risesoft.api.processadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import net.risesoft.model.processadmin.FlowableBpmnModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流程设计接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessModelApi {

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteModel")
    Y9Result<Object> deleteModel(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 根据modelId部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/deployModel")
    Y9Result<Object> deployModel(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<FlowableBpmnModel>>} 通用请求返回对象 - data 模型列表
     * @since 9.6.6
     */
    @GetMapping(value = "/getModelList")
    Y9Result<List<FlowableBpmnModel>> getModelList(@RequestParam String tenantId);

    /**
     * 获取模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 模型xml
     * @since 9.6.6
     */
    @GetMapping(value = "/getModelXml")
    Y9Result<String> getModelXml(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 导入模型文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 文件
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping(value = "/saveModelXml")
    Y9Result<Object> saveModelXml(@RequestParam String tenantId, @RequestParam String userId, MultipartFile file);

}
