package net.risesoft.service;

import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomHistoricActivityService {

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param processInstanceId
     * @return
     */
    List<HistoricActivityInstance> getByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例和流程节点类型获取历史节点实例
     *
     * @param processInstanceId
     * @param activityType
     * @return
     */
    List<HistoricActivityInstance> getByProcessInstanceIdAndActivityType(String processInstanceId, String activityType);

    /**
     * Description:
     * 
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricActivityInstance> getByProcessInstanceIdAndYear(String processInstanceId, String year);
}
