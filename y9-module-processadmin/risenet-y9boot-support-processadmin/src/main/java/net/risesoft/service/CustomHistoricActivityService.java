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
     * @param processInstanceId 流程实例ID
     * @param executionId 执行id
     * @param year 年丰
     * @return Y9Result<List<HistoricActivityInstanceModel>>
     */
    Y9Result<List<HistoricActivityInstanceModel>> getTaskListByExecutionId(String processInstanceId, String executionId,
        String year);

    /**
     * 根据流程实例获取历史节点实例
     *
     * @param processInstanceId 流程实例id
     * @return List<HistoricActivityInstance>
     */
    List<HistoricActivityInstance> listByProcessInstanceId(String processInstanceId);

    /**
     *
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return List<HistoricActivityInstance>
     */
    List<HistoricActivityInstance> listByProcessInstanceIdAndYear(String processInstanceId, String year);
}
