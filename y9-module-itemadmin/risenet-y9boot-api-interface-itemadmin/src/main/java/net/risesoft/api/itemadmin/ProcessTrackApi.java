package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

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
     * @param userId 人员id
     * @param id 唯一标识
     * @return {@code Y9Page<Object>} 通用请求返回对象
     * @throws Exception Exception
     */
    @PostMapping("/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id) throws Exception;

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象
     */
    @GetMapping("/findByTaskId")
    Y9Result<List<ProcessTrackModel>> findByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param taskId 任务id
     * @return {@code Y9Result<List<ProcessTrackModel>>} 通用请求返回对象
     */
    @GetMapping("/findByTaskIdAsc")
    Y9Result<List<ProcessTrackModel>> findByTaskIdAsc(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("taskId") String taskId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象
     */
    @GetMapping("/processTrackList")
    Y9Result<List<HistoryProcessModel>> processTrackList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<HistoryProcessModel>>} 通用请求返回对象
     */
    @GetMapping("/processTrackList4Simple")
    Y9Result<List<HistoryProcessModel>> processTrackList4Simple(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @param processTrack 实体类对象（ProcessTrackModel）
     * @return {@code Y9Page<ProcessTrackModel>} 通用请求返回对象 - data 历程对象
     * @throws Exception Exception
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<ProcessTrackModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessTrackModel processTrack) throws Exception;
}
