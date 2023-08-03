package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface DraftApi {

    /**
     * 彻底删除草稿
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> deleteDraft(String tenantId, String userId, String ids);

    /**
     * 根据人员id和事项id获取删除草稿统计
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return int
     */
    public int getDeleteDraftCount(String tenantId, String userId, String itemId);

    /**
     * 根据流程序列号获取草稿
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程序列号
     * @return Map
     */
    public Map<String, Object> getDraftByProcessSerialNumber(String tenantId, String userId,
        String processSerialNumber);

    /**
     * 根据人员id和事项id获取草稿统计
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @return int
     */
    public int getDraftCount(String tenantId, String userId, String itemId);

    /**
     * 根据人员id和事项id获取草稿统计
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param systemName 系统名称
     * @return int
     */
    public int getDraftCountBySystemName(String tenantId, String userId, String systemName);

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
    public Map<String, Object> getDraftList(String tenantId, String userId, int page, int rows, String title,
        String itemId, boolean delFlag);

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
    public Map<String, Object> getDraftListBySystemName(String tenantId, String userId, int page, int rows,
        String title, String systemName, boolean delFlag);

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
    public Map<String, Object> openDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        boolean mobile);

    /**
     * 还原草稿
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> reduction(String tenantId, String userId, String ids);

    /**
     * 删除草稿
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ids ids
     * @return Map&lt;String, Object&gt;
     */
    public Map<String, Object> removeDraft(String tenantId, String userId, String ids);

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
    public Map<String, Object> saveDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey, String number, String level, String title);

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
    public Map<String, Object> saveDraft(String tenantId, String userId, String itemId, String processSerialNumber,
        String processDefinitionKey, String number, String level, String jijian, String title);

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
    public Map<String, Object> saveDraftAndType(String tenantId, String userId, String itemId,
        String processSerialNumber, String processDefinitionKey, String number, String level, String title,
        String type);
}
