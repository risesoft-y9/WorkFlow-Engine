package net.risesoft.api.processadmin;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.model.processadmin.Y9BpmnModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BpmnModelApi {

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Boolean>}
     */
    Y9Result<Object> deleteModel(String tenantId, String modelId);

    /**
     * 根据Model部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return
     */
    Y9Result<Object> deployModel(String tenantId, String modelId);

    /**
     * 生成流程图
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<String>
     * @throws Exception Exception
     */
    Y9Result<String> genProcessDiagram(String tenantId, String processInstanceId);

    /**
     * 获取流程图模型
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Y9BpmnModel>
     */
    Y9Result<Y9BpmnModel> getBpmnModel(String tenantId, String processInstanceId);

    /**
     * 获取流程图数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getFlowChart(String tenantId, String processInstanceId) throws Exception;

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return
     */
    Y9Result<List<Map<String, Object>>> getModelList(String tenantId);

    /**
     * 获取流程设计模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return
     */
    Y9Result<Map<String, Object>> getModelXml(String tenantId, String modelId);

    /**
     * 导入流程模板
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 导入的xml文件
     * @return
     */
    public Map<String, Object> importProcessModel(String tenantId, String userId, MultipartFile file);

    /**
     * 保存模型xml
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param modelId 模板id
     * @param file 模型文件
     * @return
     */
    Y9Result<String> saveModelXml(String tenantId, String userId, String modelId, MultipartFile file);

}
