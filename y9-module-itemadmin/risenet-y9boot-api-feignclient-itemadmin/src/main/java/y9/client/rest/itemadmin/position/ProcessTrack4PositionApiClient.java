package y9.client.rest.itemadmin.position;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.model.itemadmin.HistoricActivityInstanceModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ProcessTrack4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/processTrack4Position")
public interface ProcessTrack4PositionApiClient extends ProcessTrack4PositionApi {

    /**
     * 根据唯一标示删除历程数据
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/deleteById")
    public void deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id) throws Exception;

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/findByTaskId")
    public List<ProcessTrackModel> findByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据任务id获取自定义历程
     *
     * @param tenantId
     * @param taskId
     * @return
     */
    @Override
    @GetMapping("/findByTaskIdAsc")
    public List<ProcessTrackModel> findByTaskIdAsc(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    @Override
    @GetMapping("/getTaskList")
    public Y9Result<List<HistoricActivityInstanceModel>> getTaskList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/processTrackList")
    public Map<String, Object> processTrackList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 分页生成历程列表(包含每个任务节点的特殊操作的历程)
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/processTrackList4Simple")
    public Map<String, Object> processTrackList4Simple(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 保存或更新历程
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processTrack 实体类对象（ProcessTrackModel）
     * @return ProcessTrackModel
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ProcessTrackModel saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessTrackModel processTrack) throws Exception;
}
