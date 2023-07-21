package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ProcessTrack;

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
     * 根据任务查找历程数据
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> findByTaskId(String taskId);

    /**
     * 根据任务id获取结束时间为null的自定义历程
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> findByTaskIdAndEndTimeIsNull(String taskId);

    /**
     * 根据任务查找历程数据
     *
     * @param taskId
     * @return
     */
    List<ProcessTrack> findByTaskIdAsc(String taskId);

    /**
     * 根据唯一标示查找历程数据
     *
     * @param id
     * @return
     */
    ProcessTrack findOne(String id);

    /**
     * 获取历程列表(带自定义历程信息)
     *
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> getListMap(String processInstanceId);

    /**
     * 获取历程列表(带自定义历程信息)
     *
     * @param processInstanceId
     * @return
     */
    List<Map<String, Object>> getListMap4Simple(String processInstanceId);

    /**
     * Description: 保存或者更新历程数据
     * 
     * @param processTrack
     * @return
     */
    ProcessTrack saveOrUpdate(ProcessTrack processTrack);
}
