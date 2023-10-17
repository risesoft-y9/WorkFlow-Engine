package net.risesoft.api.itemadmin.position;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface Draft4PositionApi {

    /**
     * 彻底删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> deleteDraft(String tenantId, String ids);

    /**
     * 根据岗位id和事项id获取删除草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return int
     */
    int getDeleteDraftCount(String tenantId, String positionId, String itemId);

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return Map
     */
    Map<String, Object> getDraftByProcessSerialNumber(String tenantId, String processSerialNumber);

    /**
     * 根据岗位id和事项id获取草稿统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @return int
     */
    int getDraftCount(String tenantId, String positionId, String itemId);

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
    Map<String, Object> getDraftList(String tenantId, String positionId, int page, int rows, String title, String itemId, boolean delFlag);

    /**
     * 编辑草稿
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> openDraft4Position(String tenantId, String positionId, String itemId, String processSerialNumber, boolean mobile);

    /**
     * 还原草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> reduction(String tenantId, String ids);

    /**
     * 删除草稿
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> removeDraft(String tenantId, String ids);

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
    Map<String, Object> saveDraft(String tenantId, String positionId, String itemId, String processSerialNumber, String processDefinitionKey, String number, String level, String title);

}
