package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSongApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id id
     * @param type 类型
     */
    void changeChaoSongState(String tenantId, String id, String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids ids
     */
    void changeStatus(String tenantId, String userId, String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    void changeStatus2read(String tenantId, String chaoSongId);

    /**
     * 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return int
     */
    int countByProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return int
     */
    int countByUserIdAndProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param year 年份
     * @return boolean
     */
    boolean deleteByProcessInstanceId(String tenantId, String processInstanceId, String year);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     * @param processInstanceId 流程实例id
     */
    void deleteList(String tenantId, String[] ids, String processInstanceId);

    /**
     * 展开抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status 状态
     * @param mobile 是否发送手机
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId, Integer status,
        boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    int getDone4OpinionCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    int getDoneCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @return int
     */
    int getDoneCountByUserIdAndItemId(String tenantId, String userId, String itemId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return int
     */
    int getDoneCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param year 年份
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListByUserId(String tenantId, String userId, String year, String documentTitle, int rows,
        int page);

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @param rows 条数
     * @param page 页数
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows, int page);

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListByUserIdAndSystemName(String tenantId, String userId, String systemName, int rows,
        int page);

    /**
     * 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName, int rows,
        int page);

    /**
     * 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId 发送人id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId,
        String processInstanceId, String userName, int rows, int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param year 年份
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String year, String documentTitle,
        int rows, int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    int getTodoCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @return int
     */
    int getTodoCountByUserIdAndItemId(String tenantId, String userId, String itemId);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @return int
     */
    int getTodoCountByUserIdAndSystemName(String tenantId, String userId, String systemName);

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getTodoListByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param itemId 事项id
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getTodoListByUserIdAndItemId(String tenantId, String userId, String itemId, int rows, int page);

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param title 标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getTodoListByUserIdAndSystemName(String tenantId, String userId, String systemName,
        String title, int rows, int page);

    /**
     * 点击抄送按钮之后保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发生短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 发送人
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users, String isSendSms,
        String isShuMing, String smsContent, String smsPersonId);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    void updateTitle(String tenantId, String processInstanceId, String documentTitle);
}
