package net.risesoft.api.itemadmin;

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
     * @return Y9Result<List<ItemOpinionFrameBindModel>>
     */
    @GetMapping("/findByItemId")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId);

    /**
     * 根据流程定义id，事项id获取绑定意见框
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return Y9Result<List<ItemOpinionFrameBindModel>>
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
     * @param taskDefKey taskDefKey
     * @return Y9Result<<ItemOpinionFrameBindModel>>
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
     * @param taskDefKey taskDefKey
     * @return Y9Result<List<ItemOpinionFrameBindModel>>
     */
    @GetMapping("/findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole")
    Y9Result<List<ItemOpinionFrameBindModel>> findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(
        @RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("itemId") String itemId, @RequestParam("processDefinitionId") String processDefinitionId,
        @RequestParam(value = "taskDefKey", required = false) String taskDefKey);

}
