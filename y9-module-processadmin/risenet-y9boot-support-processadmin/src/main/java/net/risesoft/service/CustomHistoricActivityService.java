package net.risesoft.service;

import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;

import net.risesoft.model.processadmin.HistoricActivityInstanceModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomHistoricActivityService {

    /**
     * 根据流程实例ID和执行ID获取历史节点实例
     *
     * @param processInstanceId
     * @param executionId
     * @param year
     * @return
     */
    Y9Result<List<HistoricActivityInstanceModel>> getTaskListByExecutionId(String processInstanceId, String executionId,
        String year);

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param processInstanceId
     * @return
     */
    List<HistoricActivityInstance> listByProcessInstanceId(String processInstanceId);

    /**
     * 根据流程实例和流程节点类型获取历史节点实例
     *
     * @param processInstanceId
     * @param activityType
     * @return
     */
    List<HistoricActivityInstance> listByProcessInstanceIdAndActivityType(String processInstanceId,
        String activityType);

    /**
     * Description:
     *
     * @param processInstanceId
     * @param year
     * @return
     */
    List<HistoricActivityInstance> listByProcessInstanceIdAndYear(String processInstanceId, String year);
}
