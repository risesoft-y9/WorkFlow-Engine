package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.ProcessParamModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessParamApi {

    /**
     * 根据流程实例id删除流程变量
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    void deleteByPprocessInstanceId(String tenantId, String processInstanceId);

    /**
     *
     * Description: 根据流程实例查找流程数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return ProcessParamModel
     */
    ProcessParamModel findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     *
     * Description: 根据流程序列号查找流程数据
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return ProcessParamModel
     */
    ProcessParamModel findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 保存或更新流程数据
     *
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     */
    void saveOrUpdate(String tenantId, ProcessParamModel processParam);

    /**
     * 更新定制流程状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param b b
     */
    void updateCustomItem(String tenantId, String processSerialNumber, boolean b);
}
