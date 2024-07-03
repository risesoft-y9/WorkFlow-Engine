package net.risesoft.api.itemadmin.position;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.DraftModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Draft4PositionApi {

    /**
     * 根据系统名称计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param systemName 系统id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countBySystemName")
    Y9Result<Integer> countBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("systemName") String systemName);

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Y9Result<Object>
     */
    @PostMapping("/deleteDraft")
    Y9Result<Object> deleteDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @GetMapping("/getDeleteDraftCount")
    Y9Result<Integer> getDeleteDraftCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Y9Result<DraftModel>
     */
    @GetMapping("/getDraftByProcessSerialNumber")
    Y9Result<DraftModel> getDraftByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @GetMapping("/getDraftCount")
    Y9Result<Integer> getDraftCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId);

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
     * @return Y9Page<Map<String, Object>>
     */
    @GetMapping("/getDraftList")
    Y9Page<Map<String, Object>> getDraftList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("page") int page, @RequestParam("rows") int rows,
        @RequestParam(value = "title", required = false) String title, @RequestParam("itemId") String itemId,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 根据系统获取草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param page page
     * @param rows rows
     * @param title 标题
     * @param systemName 系统名称
     * @param delFlag 是否删除
     * @return Y9Page<DraftModel>
     */
    @GetMapping("/getDraftListBySystemName")
    Y9Page<DraftModel> getDraftListBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("page") int page, @RequestParam("rows") int rows,
        @RequestParam(value = "title", required = false) String title, @RequestParam("systemName") String systemName,
        @RequestParam("delFlag") boolean delFlag);

    /**
     * 编辑草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param mobile 是否发送手机端
     * @return Y9Result<OpenDataModel>
     */
    @GetMapping("/openDraft4Position")
    Y9Result<OpenDataModel> openDraft4Position(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("mobile") boolean mobile);

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Y9Result<Object>
     */
    @PostMapping("/reduction")
    Y9Result<Object> reduction(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Y9Result<Object>
     */
    @PostMapping("/removeDraft")
    Y9Result<Object> removeDraft(@RequestParam("tenantId") String tenantId, @RequestParam("ids") String ids);

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
     * @return Y9Result<Object>
     */
    @PostMapping("/saveDraft")
    Y9Result<Object> saveDraft(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId,
        @RequestParam("itemId") String itemId, @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processDefinitionKey") String processDefinitionKey,
        @RequestParam(value = "number", required = false) String number,
        @RequestParam(value = "level", required = false) String level, @RequestParam("title") String title);

}
