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
     * @param tenantId
     * @param processInstanceId
     */
    void deleteByPprocessInstanceId(String tenantId, String processInstanceId);

    /**
     * 
     * Description: 根据流程实例查找流程数据
     * 
     * @param tenantId
     * @param processInstanceId
     * @return
     */
    ProcessParamModel findByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 
     * Description: 根据流程序列号查找流程数据
     * 
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    ProcessParamModel findByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 保存或更新流程数据
     * 
     * @param tenantId 租户ID
     * @param processParam 流程数据对象
     * @return
     * @throws Exception
     */
    void saveOrUpdate(String tenantId, ProcessParamModel processParam);

    /**
     * 更新定制流程状态
     *
     * @param tenantId
     * @param processSerialNumber
     * @param b
     */
    void updateCustomItem(String tenantId, String processSerialNumber, boolean b);
}
