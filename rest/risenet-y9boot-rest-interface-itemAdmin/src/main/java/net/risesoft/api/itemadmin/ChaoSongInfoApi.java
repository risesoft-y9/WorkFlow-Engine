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
     * @param tenantId
     * @param id
     * @param type
     */
    public void changeChaoSongState(String tenantId, String id, String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids ids
     */
    public void changeStatus(String tenantId, String userId, String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    public void changeStatus2read(String tenantId, String chaoSongId);

    /**
     * 根据流程实例id统计除当前人外是否有抄送件
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    public int countByProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    public int countByUserIdAndProcessInstanceId(String tenantId, String userId, String processInstanceId);

    /**
     * 删除抄送件
     * 
     * @param tenantId
     * @param ids
     */
    public void deleteByIds(String tenantId, String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    public boolean deleteByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * 展开抄送件
     * 
     * @param tenantId
     * @param userId
     * @param id
     * @param processInstanceId
     * @param status
     * @param mobile
     * @return
     */
    public Map<String, Object> detail(String tenantId, String userId, String id, String processInstanceId, Integer status, boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId
     * @param userId
     * @return
     */
    public int getDone4OpinionCountByUserId(String tenantId, String userId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    public int getDoneCountByUserId(String tenantId, String userId);

    /**
     * 获取抄送已阅件列表
     * 
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getDoneListByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

    /**
     * 根据流程实例获取除当前人外的其他抄送件
     * 
     * @param tenantId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getListByProcessInstanceId(String tenantId, String processInstanceId, String userName, int rows, int page);

    /**
     * 根据流程实例获取当前人的抄送件
     * 
     * @param tenantId
     * @param senderId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId, String processInstanceId, String userName, int rows, int page);

    /**
     * 批阅件
     *
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getOpinionChaosongByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    public int getTodoCountByUserId(String tenantId, String userId);

    /**
     * 获取抄送未阅件列表
     * 
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getTodoListByUserId(String tenantId, String userId, String documentTitle, int rows, int page);

    /**
     * 点击抄送按钮之后保存
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param users
     * @param isSendSms
     * @param isShuMing
     * @param smsContent
     * @param smsPersonId
     * @return
     */
    public Map<String, Object> save(String tenantId, String userId, String processInstanceId, String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId);

    /**
     * 个人阅件搜索
     * 
     * @param tenantId
     * @param userId
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> searchAllByUserId(String tenantId, String userId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId
     * @param searchName
     * @param itemId
     * @param senderName
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> searchAllList(String tenantId, String searchName, String itemId, String senderName, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     */
    public void updateTitle(String tenantId, String processInstanceId, String documentTitle);
}
