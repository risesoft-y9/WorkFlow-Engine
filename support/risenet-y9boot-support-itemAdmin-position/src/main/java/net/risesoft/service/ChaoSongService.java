package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.ChaoSong;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ChaoSongService {

    /**
     * 改变抄送件意见状态
     *
     * @param id
     * @param type
     */
    void changeChaoSongState(String id, String type);

    /**
     * 根据传进来的id和状态设置抄送状态
     *
     * @param id
     * @param status
     */
    void changeStatus(String id, Integer status);

    /**
     * 根据传进来的id数组和状态设置抄送状态
     *
     * @param ids
     * @param status
     */
    void changeStatus(String[] ids, Integer status);

    /**
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     * 
     * @param userId
     * @param processInstanceId
     * @return
     */
    int countByProcessInstanceId(String userId, String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param userId
     * @param processInstanceId
     * @return
     */
    int countByUserIdAndProcessInstanceId(String userId, String processInstanceId);

    /**
     * Description: 根据流程实例id删除抄送件
     * 
     * @param processInstanceId
     * @param year
     * @return
     */
    boolean deleteByProcessInstanceId(String processInstanceId, String year);

    /**
     * 根据传进来的id数组和状态设置抄送状态
     * 
     * Description:
     * 
     * @param ids
     * @param processInstanceId
     */
    void deleteList(String[] ids, String processInstanceId);

    /**
     * Description: 查看抄送件详情
     * 
     * @param processInstanceId
     * @param status
     * @param mobile
     * @return
     */
    Map<String, Object> detail(String processInstanceId, Integer status, boolean mobile);

    /**
     * 根据id查找抄送件
     *
     * @param id
     * @return
     */
    ChaoSong findOne(String id);

    /**
     * 获取批阅件计数
     *
     * @param userId
     * @return
     */
    int getDone4OpinionCountByUserId(String userId);

    /**
     * 根据人员唯一标示查找已阅数量
     *
     * @param userId
     * @return
     */
    int getDoneCountByUserId(String userId);

    /**
     * Description: 根据人员唯一标示查找已阅数量
     * 
     * @param userId
     * @param itemId
     * @return
     */
    int getDoneCountByUserIdAndItemId(String userId, String itemId);

    /**
     * 
     * Description: 根据人员唯一标示查找已阅数量
     * 
     * @param userId
     * @param systemName
     * @return
     */
    int getDoneCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * Description: 查找抄送目标所有的抄送已阅件
     * 
     * @param userId
     * @param year
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getDoneListByUserId(String userId, String year, String documentTitle, int rows, int page);

    /**
     * Description: 查找抄送目标所有的抄送已阅件
     * 
     * @param userId
     * @param itemId
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getDoneListByUserIdAndItemId(String userId, String itemId, int rows, int page);

    /**
     * Description: 查找抄送目标所有的抄送已阅件
     * 
     * @param userId
     * @param systemName
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getDoneListByUserIdAndSystemName(String userId, String systemName, int rows, int page);

    /**
     * Description: 根据流程实例获取除当前人外的其他抄送件
     * 
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getListByProcessInstanceId(String processInstanceId, String userName, int rows, int page);

    /**
     * Description: 根据流程实例获取当前人的抄送件
     * 
     * @param senderId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getListBySenderIdAndProcessInstanceId(String senderId, String processInstanceId,
        String userName, int rows, int page);

    /**
     * Description: 批阅件列表
     * 
     * @param userId
     * @param year
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getOpinionChaosongByUserId(String userId, String year, String documentTitle, int rows,
        int page);

    /**
     * 获取未查看的抄送件的数量
     *
     * @param userId
     * @return
     */
    int getTodoCount4NewByUserId(String userId);

    /**
     * 根据人员唯一标示查找待阅数量
     *
     * @param userId
     * @return
     */
    int getTodoCountByUserId(String userId);

    /**
     * Description: 根据人员唯一标示查找待阅数量
     * 
     * @param userId
     * @param itemId
     * @return
     */
    int getTodoCountByUserIdAndItemId(String userId, String itemId);

    /**
     * Description: 根据人员唯一标示查找待阅数量
     * 
     * @param userId
     * @param systemName
     * @return
     */
    int getTodoCountByUserIdAndSystemName(String userId, String systemName);

    /**
     * Description: 查找抄送目标所有的抄送待阅件
     * 
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getTodoListByUserId(String userId, String documentTitle, int rows, int page);

    /**
     * Description: 查找抄送目标所有的抄送待阅件
     * 
     * @param userId
     * @param itemId
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getTodoListByUserIdAndItemId(String userId, String itemId, int rows, int page);

    /**
     * 
     * Description: 查找抄送目标所有的抄送待阅件
     * 
     * @param userId
     * @param itemId
     * @param title
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getTodoListByUserIdAndItemIdAndTitle(String userId, String itemId, String title, int rows,
        int page);

    /**
     * 
     * Description: 查找抄送目标所有的抄送待阅件
     * 
     * @param userId
     * @param systemName
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getTodoListByUserIdAndSystemName(String userId, String systemName, int rows, int page);

    /**
     * 
     * Description: 查找抄送目标所有的抄送待阅件
     * 
     * @param userId
     * @param systemName
     * @param title
     * @param rows
     * @param page
     * @return
     */
    Map<String, Object> getTodoListByUserIdAndSystemNameAndTitle(String userId, String systemName, String title,
        int rows, int page);

    /**
     * 保存抄送
     *
     * @param chaoSong
     * @return
     */
    ChaoSong save(ChaoSong chaoSong);

    /**
     * 批量保存抄送
     *
     * @param chaoSongList
     * @return
     */
    void save(List<ChaoSong> chaoSongList);

    /**
     * Description: 根据选择的人员保存抄送
     * 
     * @param processInstanceId
     * @param users
     * @param isSendSms
     * @param isShuMing
     * @param smsContent
     * @param smsPersonId
     * @return
     */
    Map<String, Object> save(String processInstanceId, String users, String isSendSms, String isShuMing,
        String smsContent, String smsPersonId);

    /**
     * 更新抄送件标题
     *
     * @param processInstanceId
     * @param documentTitle
     */
    void updateTitle(String processInstanceId, String documentTitle);
}
