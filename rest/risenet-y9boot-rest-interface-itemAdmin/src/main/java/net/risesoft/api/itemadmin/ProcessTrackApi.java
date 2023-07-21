package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ProcessTrackModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTrackApi {

    /**
     * 根据唯一标示删除历程数据
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    public void deleteById(String tenantId, String userId, String id) throws Exception;

    /**
     * 根据任务id获取自定义历程
     * 
     * @param tenantId
     * @param taskId
     * @return
     */
    public List<ProcessTrackModel> findByTaskId(String tenantId, String taskId);

    /**
     * 根据任务id获取自定义历程
     * 
     * @param tenantId
     * @param userId
     * @param taskId
     * @return
     */
    public List<ProcessTrackModel> findByTaskIdAsc(String tenantId, String userId, String taskId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> processTrackList(String tenantId, String userId, String processInstanceId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> processTrackList4Simple(String tenantId, String userId, String processInstanceId);

    /**
     * 保存或更新历程
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processTrack 实体类对象（ProcessTrackModel）
     * @return ProcessTrackModel
     * @throws Exception Exception
     */
    public ProcessTrackModel saveOrUpdate(String tenantId, ProcessTrackModel processTrack) throws Exception;
}
