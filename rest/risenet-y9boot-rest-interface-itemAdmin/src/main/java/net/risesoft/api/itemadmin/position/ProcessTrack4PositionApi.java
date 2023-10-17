package net.risesoft.api.itemadmin.position;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.ProcessTrackModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTrack4PositionApi {

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    void deleteById(String tenantId, String id) throws Exception;

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;ProcessTrackModel&gt;
     */
    List<ProcessTrackModel> findByTaskId(String tenantId, String taskId);

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return List&lt;ProcessTrackModel&gt;
     */
    List<ProcessTrackModel> findByTaskIdAsc(String tenantId, String taskId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> processTrackList(String tenantId, String positionId, String processInstanceId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> processTrackList4Simple(String tenantId, String positionId, String processInstanceId);

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @param processTrack 实体类对象（ProcessTrackModel）
     * @return ProcessTrackModel
     * @throws Exception Exception
     */
    ProcessTrackModel saveOrUpdate(String tenantId, ProcessTrackModel processTrack) throws Exception;
}
