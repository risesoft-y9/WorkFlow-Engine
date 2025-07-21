package net.risesoft.service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface DeleteProcessService {

    /**
     * 删除流程实例相关数据
     *
     * @param tenantId 租户ID
     * @param processInstanceId 流程实例ID
     */
    void deleteProcess(final String tenantId, final String processInstanceId);

    /**
     * 删除年度数据
     *
     * @param tenantId 租户ID
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    void deleteYearData(final String tenantId, final String year, final String processInstanceId);

}
