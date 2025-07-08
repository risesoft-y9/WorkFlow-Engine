package net.risesoft.api.itemadmin.opinion;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemOpinionFrameBindModel;
import net.risesoft.pojo.Y9Result;

/**
 * 意见框绑定接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemOpinionFrameBindApi {

    /**
     * 根据事项id获取所有绑定意见框
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @GetMapping("/findByItemId")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId);

    /**
     * 根据事项id和流程定义id获取所有绑定意见框
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @GetMapping("/findByItemIdAndProcessDefinitionId")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionId(
        @RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processDefinitionId") String processDefinitionId);

    /**
     * 根据事项id和任务id获取绑定意见框
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @GetMapping("/findByItemIdAndProcessDefinitionIdAndTaskDefKey")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionIdAndTaskDefKey(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey);

    /**
     * 根据事项id和任务id获取绑定意见框（包含角色信息）
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return {@code Y9Result<List<ItemOpinionFrameBindModel>>} 通用请求返回对象 - data 是绑定意见框列表
     * @since 9.6.6
     */
    @GetMapping("/findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey);

}
