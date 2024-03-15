package y9.client.rest.itemadmin.position;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.Opinion4PositionApi;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Opinion4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/opinion4Position")
public interface Opinion4PositionApiClient extends Opinion4PositionApi {

    /**
     * 检查当前taskId任务节点是否已经签写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return Boolean
     */
    @Override
    @GetMapping("/checkSignOpinion")
    public Boolean checkSignOpinion(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("taskId") String taskId);

    /**
     * 获取意见框历史记录数量
     *
     * @param tenantId
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return
     */
    @Override
    @GetMapping("/countOpinionHistory")
    int countOpinionHistory(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("opinionFrameMark") String opinionFrameMark);

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @throws Exception Exception
     */
    @Override
    @PostMapping("/delete")
    public void delete(@RequestParam("tenantId") String tenantId, @RequestParam("departmentId") String id)
        throws Exception;

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId
     * @param itemId
     * @param processDefinitionId
     * @return
     */
    @Override
    @GetMapping("/getBindOpinionFrame")
    public List<String> getBindOpinionFrame(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return OpinionModel
     */
    @Override
    @GetMapping("/getById")
    public OpinionModel getById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取意见框历史记录
     *
     * @param tenantId
     * @param processSerialNumber
     * @param opinionFrameMark
     * @return
     */
    @Override
    @GetMapping("/opinionHistoryList")
    List<OpinionHistoryModel> opinionHistoryList(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("opinionFrameMark") String opinionFrameMark);

    /**
     * 获取个人意见列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办），doing（在办），done（办结）
     * @param opinionFrameMark opinionFrameMark
     * @param itemId 事项id
     * @param taskDefinitionKey 任务定义key
     * @param activitiUser activitiUser
     * @return List&lt;Map&lt;String, Object&gt;&gt;
     */
    @Override
    @GetMapping("/personCommentList")
    public List<Map<String, Object>> personCommentList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("taskId") String taskId, @RequestParam("itembox") String itembox,
        @RequestParam("opinionFrameMark") String opinionFrameMark, @RequestParam("itemId") String itemId,
        @RequestParam("taskDefinitionKey") String taskDefinitionKey, @RequestParam("activitiUser") String activitiUser);

    /**
     * 保存意见
     *
     * @param tenantId 租户id
     * @param opinion OpinionModel
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void save(@RequestParam("tenantId") String tenantId, @RequestBody OpinionModel opinion) throws Exception;

    /**
     * 
     * Description: 保存或更新意见
     * 
     * @param tenantId
     * @param userId
     * @param positionId
     * @param opinion
     * @return
     * @throws Exception
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    public OpinionModel saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("positionId") String positionId, @RequestBody OpinionModel opinion) throws Exception;
}
