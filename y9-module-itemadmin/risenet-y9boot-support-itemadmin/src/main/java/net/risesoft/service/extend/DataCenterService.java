package net.risesoft.service.extend;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface DataCenterService {

    /**
     * 删除办公信息
     *
     * @param processInstanceId 流程实例ID
     */
    void deleteOfficeInfo(String processInstanceId);

    /**
     * 保存到数据中心
     *
     * @param processInstanceId 流程实例ID
     * @return
     */
    boolean saveToDataCenter(String processInstanceId);

    /**
     * 保存到数据中心
     * 
     * @param processInstanceId 流程实例ID
     * @param processDefinitionId 流程定义ID
     */
    boolean saveToDateCenter1(String processInstanceId, String processDefinitionId);
}
