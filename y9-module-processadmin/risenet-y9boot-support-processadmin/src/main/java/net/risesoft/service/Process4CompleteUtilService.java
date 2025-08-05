package net.risesoft.service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface Process4CompleteUtilService {

    /**
     * 删除历史数据
     *
     * @param processInstanceId 流程实例id
     */
    void deleteDoneData(String processInstanceId);

    /**
     * 保存办结数据到数据中心，用于办结件列表查询，截转年度数据
     *
     * @param tenantId 租户id
     * @param year 年份
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param personName 用户名称
     */
    void saveToDataCenter(final String tenantId, final String year, final String userId, final String processInstanceId,
        final String personName);

    /**
     * 办结保存年度历史数据
     *
     * @param year 年度
     * @param processInstanceId 流程实例ID
     */
    void saveYearData(String year, String processInstanceId);
}
