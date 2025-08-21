package net.risesoft.service;

import java.util.List;

import org.flowable.engine.history.HistoricProcessInstance;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface CustomHistoricProcessService {

    /**
     * 删除流程实例，在办件暂停，办结件加删除标识
     *
     * @param processInstanceId 流程实例Id
     * @return boolean
     */
    boolean deleteProcessInstance(String processInstanceId);

    /**
     * 根据流程实例Id获取历史流程实例
     *
     * @param processInstanceId 流程实例Id
     * @return HistoricProcessInstance
     */
    HistoricProcessInstance getById(String processInstanceId);

    /**
     * 
     *
     * @param processInstanceId 流程实例Id
     * @param year 年份
     * @return HistoricProcessInstance
     */
    HistoricProcessInstance getByIdAndYear(String processInstanceId, String year);

    /**
     * 根据流程实例获取父流程实例
     *
     * @param processInstanceId 流程实例Id
     * @return HistoricProcessInstance
     */
    HistoricProcessInstance getSuperProcessInstanceById(String processInstanceId);

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param superProcessInstanceId 子流程实例Id
     * @return List<HistoricProcessInstance>
     */
    List<HistoricProcessInstance> listBySuperProcessInstanceId(String superProcessInstanceId);

    /**
     * 彻底删除流程实例，岗位
     *
     * @param processInstanceId 流程实例Id
     * @return boolean
     */
    boolean removeProcess(String processInstanceId);

    /**
     * 
     * @param processInstanceId 流程实例Id
     * @param priority 优先级
     * @throws Exception 抛出异常
     */
    void setPriority(String processInstanceId, String priority) throws Exception;
}