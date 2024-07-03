package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.model.itemadmin.OpinionHistoryModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Opinion4PositionApi {

    /**
     * 检查当前taskId任务节点是否已经签写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return Y9Result<Boolean>
     */
    @GetMapping("/checkSignOpinion")
    Y9Result<Boolean> checkSignOpinion(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("taskId") String taskId);

    /**
     * 获取意见框历史记录数量
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countOpinionHistory")
    Y9Result<Integer> countOpinionHistory(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("opinionFrameMark") String opinionFrameMark);

    /**
     * 删除意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id) throws Exception;

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @return Y9Result<List<ItemOpinionFrameBindModel>>
     */
    @GetMapping("/getBindOpinionFrame")
    Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return Y9Result<OpinionModel>
     */
    @GetMapping("/getById")
    Y9Result<OpinionModel> getById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取意见框历史记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框Id
     * @return Y9Result<List<OpinionHistoryModel>>
     */
    @GetMapping("/opinionHistoryList")
    Y9Result<List<OpinionHistoryModel>> opinionHistoryList(@RequestParam("tenantId") String tenantId,
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
     * @param orderByUser orderByUser
     * @return
     */
    @GetMapping("/personCommentList")
    Y9Result<List<OpinionListModel>> personCommentList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam(value = "taskId", required = false) String taskId, @RequestParam("itembox") String itembox,
        @RequestParam("opinionFrameMark") String opinionFrameMark, @RequestParam("itemId") String itemId,
        @RequestParam(value = "taskDefinitionKey", required = false) String taskDefinitionKey,
        @RequestParam(value = "activitiUser", required = false) String activitiUser,
        @RequestParam(value = "orderByUser", required = false) String orderByUser);

    /**
     * 保存意见
     *
     * @param tenantId 租户id
     * @param opinion OpinionModel
     * @return Y9Result<Object>
     * @throws Exception Exception
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> save(@RequestParam("tenantId") String tenantId, @RequestBody OpinionModel opinion)
        throws Exception;

    /**
     * Description: 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param opinion 意见实体
     * @return Y9Result<OpinionModel>
     * @throws Exception Exception
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<OpinionModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("positionId") String positionId,
        @RequestBody OpinionModel opinion) throws Exception;

}
