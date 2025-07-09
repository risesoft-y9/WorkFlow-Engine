package net.risesoft.api.itemadmin.opinion;

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
 * 意见接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OpinionApi {

    /**
     * 检查当前taskId任务节点是否已经签写意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - data 是是否已经签写意见
     * @since 9.6.6
     */
    @GetMapping("/checkSignOpinion")
    Y9Result<Boolean> checkSignOpinion(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("taskId") String taskId);

    /**
     * 获取意见框历史记录数量
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是意见框历史记录数量
     * @since 9.6.6
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping("/delete")
    Y9Result<Object> delete(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id) throws Exception;

    /**
     * 获取事项绑定的意见框列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义Id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是事项意见框绑定信息
     * @since 9.6.6
     */
    @GetMapping("/getBindOpinionFrame")
    Y9Result<List<ItemOpinionFrameBindModel>> getBindOpinionFrame(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据id获取意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @since 9.6.6
     */
    @GetMapping("/getById")
    Y9Result<OpinionModel> getById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 获取意见框历史记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param opinionFrameMark 意见框标识
     * @return {@code Y9Result<List<OpinionHistoryModel>>} 通用请求返回对象 - data 是历史意见列表
     * @since 9.6.6
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
     * @param opinionFrameMark 意见框标识
     * @param itemId 事项id
     * @param taskDefinitionKey 任务定义key
     * @param activitiUser 人员id
     * @param orderByUser 是否根据人员排序
     * @return {@code Y9Result<List<OpinionListModel>>} 通用请求返回对象 - data 是意见列表
     * @since 9.6.6
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
     * @param opinionModel 意见信息
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping(value = "/save", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> save(@RequestParam("tenantId") String tenantId, @RequestBody OpinionModel opinionModel)
        throws Exception;

    /**
     * 保存或更新意见
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param orgUnitId 人员、岗位id
     * @param opinionModel 意见信息
     * @return {@code Y9Result<OpinionModel>} 通用请求返回对象 - data 是意见信息
     * @throws Exception Exception
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<OpinionModel> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("orgUnitId") String orgUnitId,
        @RequestBody OpinionModel opinionModel) throws Exception;

    /**
     * 更新意见
     *
     * @param tenantId 租户id
     * @param id 唯一标识
     * @param content 意见内容
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateOpinion")
    Y9Result<Object> updateOpinion(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id,
        @RequestParam("content") String content);
}
