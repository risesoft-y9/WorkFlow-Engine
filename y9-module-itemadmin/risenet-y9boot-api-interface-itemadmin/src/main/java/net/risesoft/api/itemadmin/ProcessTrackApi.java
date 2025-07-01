package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.pojo.Y9Result;

/**
 * 历程
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessTrackApi {

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception 删除导致的异常
     * @since 9.6.6
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id)
        throws Exception;

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象 - data 是流程跟踪信息
     * @since 9.6.6
     */
    @GetMapping("/findByTaskId")
    Y9Result<List<ProcessTrackModel>> findByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象 - data 是流程跟踪信息
     * @since 9.6.6
     */
    @GetMapping("/findByTaskIdAsc")
    Y9Result<List<ProcessTrackModel>> findByTaskIdAsc(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 获取流程图任务节点信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoricActivityInstanceModel>>} 通用请求返回对象 - data 是历史活动实例列表
     * @since 9.6.6
     */
    @GetMapping("/getTaskList")
    Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象- data 是历程信息
     * @since 9.6.6
     */
    @GetMapping("/processTrackList")
    Y9Result<List<HistoryProcessModel>> processTrackList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象- data 是历程信息
     * @since 9.6.6
     */
    @GetMapping("/processTrackListWithActionName")
    Y9Result<List<HistoryProcessModel>> processTrackListWithActionName(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取历程信息
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象 - data 是历程信息列表
     * @since 9.6.6
     */
    @GetMapping("/processTrackList4Simple")
    Y9Result<List<HistoryProcessModel>> processTrackList4Simple(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @param processTrackModel 实体类对象（ProcessTrackModel）
     * @return {@code Y9Result<ProcessTrackModel>} 通用请求返回对象 - data 是流程跟踪信息
     * @throws Exception 保存或更新导致的异常
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<ProcessTrackModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessTrackModel processTrackModel) throws Exception;
}
