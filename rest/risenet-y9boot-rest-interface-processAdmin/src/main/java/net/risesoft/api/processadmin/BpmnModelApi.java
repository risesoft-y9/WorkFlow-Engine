package net.risesoft.api.processadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface BpmnModelApi {

    /**
     * 生成流程图
     * 
     * @param tenantId 租户id
     * @param processId 流程实例id
     * @return byte[]
     * @throws Exception Exception
     */
    byte[] genProcessDiagram(String tenantId, String processId) throws Exception;

    /**
     * 获取流程图模型
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getBpmnModel(String tenantId, String processInstanceId) throws Exception;

    /**
     * 获取流程图数据
     * 
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Map
     * @throws Exception Exception
     */
    Map<String, Object> getFlowChart(String tenantId, String processInstanceId) throws Exception;

}
