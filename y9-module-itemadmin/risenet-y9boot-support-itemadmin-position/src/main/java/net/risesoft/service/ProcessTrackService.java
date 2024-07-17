package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.ProcessTrack;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ProcessTrackService {

    /**
     * 根据唯一标示删除历程数据
     *
     * @param id
     */
    void deleteById(String id);

    /**
     * 根据唯一标示查找历程数据
     *
     * @param id
     * @return
     */
    ProcessTrack findOne(String id);

    /**
     * 获取流程图任务节点信息
     *
     * @param processInstanceId
     * @return
     */
    Y9Result<List<HistoricActivityInstanceModel>> getTaskList(String processInstanceId);

    /**
     * 获取历程列表(带自定义历程信息)
     *
     * @param processInstanceId
     * @return
     */
    List<HistoryProcessModel> listByProcessInstanceId(String processInstanceId);

    /**
     * 获取历程列表(带自定义历程信息)
     *
     * @param processInstanceId
     * @return
     */
    List<HistoryProcessModel> listByProcessInstanceId4Simple(String processInstanceId);

    /**
     * 根据任务查找历程数据
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> listByTaskId(String taskId);

    /**
     * 根据任务id获取结束时间为null的自定义历程
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> listByTaskIdAndEndTimeIsNull(String taskId);

    /**
     * 根据任务查找历程数据
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> listByTaskIdAsc(String taskId);

    /**
     * Description: 保存或者更新历程数据
     *
     * @param processTrack
     * @return
     */
    ProcessTrack saveOrUpdate(ProcessTrack processTrack);
}
