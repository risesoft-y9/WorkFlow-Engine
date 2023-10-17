package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSongInfoApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id id
     * @param type type
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
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    void deleteByIds(String tenantId, String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    boolean deleteByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 展开抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status 状态
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId, Integer status, boolean mobile);

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
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getDoneListByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

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
    Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName, int rows, int page);

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
    Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId, String processInstanceId, String userName, int rows, int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    int getTodoCountByUserId(String tenantId, String userId);

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
     * 点击抄送按钮之后保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发送短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 收信人
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId);

    /**
     * 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param searchName 搜索名称
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchAllByUserId(String tenantId, String userId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索名称
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String senderName, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    void updateTitle(String tenantId, String processInstanceId, String documentTitle);
}
