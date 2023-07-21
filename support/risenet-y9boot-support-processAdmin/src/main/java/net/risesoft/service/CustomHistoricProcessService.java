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
     * @param processInstanceId
     * @return
     */
    boolean deleteProcessInstance(String processInstanceId);

    /**
     * 获取删除实例列表
     *
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    List<HistoricProcessInstance> deleteProList(String itemId, Integer page, Integer rows);

    /**
     * 根据流程实例Id获取历史流程实例
     *
     * @param processInstanceId
     * @return
     */
    HistoricProcessInstance getById(String processInstanceId);

    /**
     * Description:
     * 
     * @param processInstanceId
     * @param year
     * @return
     */
    HistoricProcessInstance getById(String processInstanceId, String year);

    /**
     * 根据父流程实例获取所有历史子流程实例
     *
     * @param superProcessInstanceId
     * @return
     */
    List<HistoricProcessInstance> getBySuperProcessInstanceId(String superProcessInstanceId);

    /**
     * 获取所有回收站列表
     *
     * @param title
     * @param page
     * @param rows
     * @return
     */
    List<HistoricProcessInstance> getRecycleAll(String title, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param itemId
     * @param title
     * @param page
     * @param rows
     * @return
     */
    List<HistoricProcessInstance> getRecycleByItemId(String itemId, String title, Integer page, Integer rows);

    /**
     * 根据人员id获取回收站列表
     *
     * @param title
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    List<HistoricProcessInstance> getRecycleByUserId(String title, String userId, Integer page, Integer rows);

    /**
     * 获取所有回收站统计
     *
     * @param title
     * @return
     */
    int getRecycleCount(String title);

    /**
     * 获取回收站统计
     *
     * @param itemId
     * @param title
     * @return
     */
    int getRecycleCountByItemId(String itemId, String title);

    /**
     * 根据人员id获取回收站统计
     *
     * @param title
     * @param userId
     * @return
     */
    int getRecycleCountByUserId(String title, String userId);

    /**
     * 根据流程实例获取父流程实例
     *
     * @param processInstanceId
     * @return
     */
    HistoricProcessInstance getSuperProcessInstanceById(String processInstanceId);

    /**
     * 恢复，激活流程实例
     *
     * @param processInstanceId
     * @return
     */
    boolean recoveryProcessInstance(String processInstanceId);

    /**
     * 彻底删除流程实例
     *
     * @param processInstanceId
     * @return
     */
    boolean removeProcess(String processInstanceId);

    /**
     * 彻底删除流程实例，岗位
     *
     * @param processInstanceId
     * @return
     */
    boolean removeProcess4Position(String processInstanceId);

    /**
     * Description:
     * 
     * @param processInstanceId
     * @param priority
     * @throws Exception
     */
    void setPriority(String processInstanceId, String priority) throws Exception;
}
