package net.risesoft.service;

import net.risesoft.model.itemadmin.ProcessParamModel;

/**
 * 流程数据进入数据中心，用于综合搜索
 *
 * @author 10858
 */
public interface Process4SearchService {

    /**
     * 重定位，串行送下一人，修改办件信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     */
    void saveToDataCenter(final String tenantId, final String taskId, final String processInstanceId);

    /**
     * 并行加签，修改办件信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param processParam 流程参数
     */
    void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParamModel processParam);
}
