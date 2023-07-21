package net.risesoft.api.itemadmin.position;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSong4PositionApi {

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
     * @param ids ids
     */
    public void changeStatus(String tenantId, String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    public void changeStatus2read(String tenantId, String chaoSongId);

    /**
     *
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @return
     */
    public int countByProcessInstanceId(String tenantId, String positionId, String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @return
     */
    public int countByUserIdAndProcessInstanceId(String tenantId, String positionId, String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
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
     *
     * Description: 展开抄送件
     *
     * @param tenantId
     * @param positionId
     * @param id
     * @param processInstanceId
     * @param status
     * @param mobile
     * @return
     */
    public Map<String, Object> detail(String tenantId, String positionId, String id, String processInstanceId, Integer status, boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    public int getDone4OpinionCountByUserId(String tenantId, String positionId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    public int getDoneCount(String tenantId, String positionId);

    /**
     *
     * Description: 获取抄送已阅件列表
     *
     * @param tenantId
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getDoneList(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     *
     * Description: 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getListByProcessInstanceId(String tenantId, String positionId, String processInstanceId, String userName, int rows, int page);

    /**
     *
     * Description: 根据流程实例获取当前人的抄送件
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
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getOpinionChaosongByUserId(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    public int getTodoCount(String tenantId, String positionId);

    /**
     *
     * Description: 获取抄送未阅件列表
     *
     * @param tenantId
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    public Map<String, Object> getTodoList(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     *
     * Description: 点击抄送按钮之后保存
     *
     * @param tenantId
     * @param userId
     * @param positionId
     * @param processInstanceId
     * @param users
     * @param isSendSms
     * @param isShuMing
     * @param smsContent
     * @param smsPersonId
     * @return
     */
    public Map<String, Object> save(String tenantId, String userId, String positionId, String processInstanceId, String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId);

    /**
     *
     * Description: 个人阅件搜索
     *
     * @param tenantId
     * @param positionId
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    public Map<String, Object> searchAllByUserId(String tenantId, String positionId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

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
