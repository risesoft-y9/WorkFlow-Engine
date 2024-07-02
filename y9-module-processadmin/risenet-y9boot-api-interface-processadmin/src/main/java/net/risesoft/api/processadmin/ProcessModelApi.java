package net.risesoft.api.processadmin;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

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
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteModel")
    Y9Result<String> deleteModel(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 根据Model部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deployModel")
    Y9Result<String> deployModel(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 导出model的xml文件
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @param response
     * @return
     */
    @PostMapping(value = "/exportModel")
    void exportModel(@RequestParam String tenantId, @RequestParam String modelId, HttpServletResponse response);

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getModelList")
    Y9Result<List<Map<String, Object>>> getModelList(@RequestParam String tenantId);

    /**
     * 获取模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @GetMapping(value = "/getModelXml")
    Y9Result<String> getModelXml(@RequestParam String tenantId, @RequestParam String modelId);

    /**
     * 保存，导入模型文件
     *
     * @param tenantId 租户id
     * @param file 文件
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     */
    @PostMapping(value = "/saveModelXml")
    Y9Result<Object> saveModelXml(@RequestParam String tenantId, @RequestParam String userId, MultipartFile file);

}
