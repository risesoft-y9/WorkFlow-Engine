package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.Draft4PositionApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "Draft4PositionApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}",
    path = "/services/rest/draft4Position")
public interface Draft4PositionApiClient extends Draft4PositionApi {

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/deleteDraft")
    public Map<String, Object> deleteDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping("/getDeleteDraftCount")
    public int getDeleteDraftCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Map
     */
    @Override
    @GetMapping("/getDraftByProcessSerialNumber")
    public Map<String, Object> getDraftByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping("/getDraftCount")
    public int getDraftCount(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("itemId") String itemId);

    /**
     * 获取草稿列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param page page
     * @param rows rows
     * @param title 标题
     * @param itemId 事项id
     * @param delFlag 是否删除
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDraftList")
    public Map<String, Object> getDraftList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("page") int page, @RequestParam("rows") int rows,
        @RequestParam("title") String title, @RequestParam("itemId") String itemId,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 编辑草稿
     *
     * @param tenantId
     * @param positionId
     * @param itemId
     * @param processSerialNumber
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/openDraft4Position")
    public Map<String, Object> openDraft4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("mobile") boolean mobile);

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/reduction")
    public Map<String, Object> reduction(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/removeDraft")
    public Map<String, Object> removeDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 保存草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 编号
     * @param level 紧急程度
     * @param title 标题
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/saveDraft")
    public Map<String, Object> saveDraft(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey, @RequestParam("number") String number,
        @RequestParam("level") String level, @RequestParam("title") String title);

}
