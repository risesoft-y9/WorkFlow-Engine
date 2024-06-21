package net.risesoft.api.itemadmin.position;

import java.util.Map;

import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSong4PositionApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id id
     * @param type type
     */
    Y9Result<Object> changeChaoSongState(String tenantId, String id, String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    Y9Result<Object> changeStatus(String tenantId, String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    Y9Result<Object> changeStatus2read(String tenantId, String chaoSongId);

    /**
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return int
     */
    Y9Result<Integer> countByProcessInstanceId(String tenantId, String positionId, String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return int
     */
    Y9Result<Integer> countByUserIdAndProcessInstanceId(String tenantId, String positionId, String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    Y9Result<Object> deleteByIds(String tenantId, String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    Y9Result<Object> deleteByProcessInstanceId(String tenantId, String processInstanceId);

    /**
     * Description: 展开抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status status
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    Y9Result<Map<String, Object>> detail(String tenantId, String positionId, String id, String processInstanceId, Integer status, boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    Y9Result<Integer> getDone4OpinionCountByUserId(String tenantId, String positionId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    Y9Result<Integer> getDoneCount(String tenantId, String positionId);

    /**
     * Description: 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> getDoneList(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     * Description: 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>;
     */
    Y9Page<ChaoSongModel> getListByProcessInstanceId(String tenantId, String positionId, String processInstanceId, String userName, int rows, int page);

    /**
     * Description: 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId senderId
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId, String processInstanceId, String userName, int rows, int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> getOpinionChaosongByUserId(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    Y9Result<Integer> getTodoCount(String tenantId, String positionId);

    /**
     * Description: 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> getTodoList(String tenantId, String positionId, String documentTitle, int rows, int page);

    /**
     * 我的抄送列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 接收人名称
     * @param state 状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page&lt;Map&lt;String, Object&gt;&gt;
     */
    Y9Page<ChaoSongModel> myChaoSongList(String tenantId, String positionId, String searchName, String itemId, String userName, String state, String year, int page, int rows);

    /**
     * Description: 点击抄送按钮之后保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发送短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 短信人员id
     * @return Y9Result<Object>
     */
    Y9Result<Object> save(String tenantId, String userId, String positionId, String processInstanceId, String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId);

    /**
     * Description: 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索信息
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> searchAllByUserId(String tenantId, String positionId, String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索信息
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Y9Page<ChaoSongModel>
     */
    Y9Page<ChaoSongModel> searchAllList(String tenantId, String searchName, String itemId, String senderName, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    Y9Result<Object> updateTitle(String tenantId, String processInstanceId, String documentTitle);
}
