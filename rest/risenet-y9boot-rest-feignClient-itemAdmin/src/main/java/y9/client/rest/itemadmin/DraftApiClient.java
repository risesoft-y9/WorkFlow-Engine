package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.DraftApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "DraftApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/draft")
public interface DraftApiClient extends DraftApi {

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/deleteDraft")
    public Map<String, Object> deleteDraft(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("ids") String ids);

    /**
     * 根据人员id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping("/getDeleteDraftCount")
    public int getDeleteDraftCount(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程序列号
     * @return Map
     */
    @Override
    @GetMapping("/getDraftByProcessSerialNumber")
    public Map<String, Object> getDraftByProcessSerialNumber(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据人员id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return int
     */
    @Override
    @GetMapping("/getDraftCount")
    public int getDraftCount(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId);

    /**
     * 
     * Description: 根据人员id和事项id获取草稿统计
     * 
     * @param tenantId
     * @param userId
     * @param systemName
     * @return
     */
    @Override
    @GetMapping("/getDraftCountBySystemName")
    public int getDraftCountBySystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("systemName") String systemName);

    /**
     * 获取草稿列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param page page
     * @param rows rows
     * @param title 标题
     * @param itemId 事项id
     * @param delFlag 是否删除
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping("/getDraftList")
    public Map<String, Object> getDraftList(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("page") int page, @RequestParam("rows") int rows, @RequestParam("title") String title, @RequestParam("itemId") String itemId,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 
     * Description: 获取草稿列表
     * 
     * @param tenantId
     * @param userId
     * @param page
     * @param rows
     * @param title
     * @param systemName
     * @param delFlag
     * @return
     */
    @Override
    @GetMapping("/getDraftListBySystemName")
    public Map<String, Object> getDraftListBySystemName(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("page") int page, @RequestParam("rows") int rows, @RequestParam("title") String title, @RequestParam("systemName") String systemName,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 
     * Description: 编辑草稿
     * 
     * @param tenantId
     * @param userId
     * @param itemId
     * @param processSerialNumber
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/openDraft")
    public Map<String, Object> openDraft(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("mobile") boolean mobile);

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/reduction")
    public Map<String, Object> reduction(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("ids") String ids);

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/removeDraft")
    public Map<String, Object> removeDraft(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("ids") String ids);

    /**
     * 保存草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
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
    public Map<String, Object> saveDraft(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("number") String number, @RequestParam("level") String level, @RequestParam("title") String title);

    /**
     * 保存草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 编号
     * @param level 紧急程度
     * @param jijian 急件
     * @param title 标题
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/saveDraft1")
    public Map<String, Object> saveDraft(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("number") String number, @RequestParam("level") String level, @RequestParam("jijian") String jijian, @RequestParam("title") String title);

    /**
     * 保存草稿(带类型)
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 编号
     * @param level 紧急程度
     * @param title 标题
     * @param type 类型
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @PostMapping("/saveDraftAndType")
    public Map<String, Object> saveDraftAndType(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam("number") String number, @RequestParam("level") String level, @RequestParam("title") String title, @RequestParam("type") String type);
}
