package net.risesoft.api.processadmin;

import java.util.List;
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface WorkflowApi {

    /**
     * 获取当前任务节点的目标节点
     *
     * @param tenantId 租户id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    public List<Map<String, String>> getCurrentTaskTargets(String tenantId, String processDefinitionId, String taskDefKey);
}
