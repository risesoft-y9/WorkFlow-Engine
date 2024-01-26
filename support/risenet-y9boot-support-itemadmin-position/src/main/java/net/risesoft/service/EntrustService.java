package net.risesoft.service;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

import net.risesoft.entity.Entrust;
import net.risesoft.model.itemadmin.EntrustModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface EntrustService {

    /**
     * Description: 销假:删除ownerId所有的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param id
     */
    void destroyEntrust(String id);

    /**
     * 销假:删除某个人的某个事项的正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param ownerId
     * @param itemId
     */
    void destroyEntrust(String ownerId, String itemId);

    /**
     * 销假:根据唯一标示删除正在使用中的、或者已经过期的出差委托，并放入委托历史表
     *
     * @param id
     */
    void destroyEntrustById(String id);

    /**
     * 获取某个用户没有删除的委托对象
     *
     * @param userId
     * @return
     */
    List<Entrust> findAll();

    /**
     * Description: 获取某个用户没有删除的委托对象
     *
     * @param ownerId
     * @return
     */
    List<Entrust> findByAssigneeId(String ownerId);

    /**
     * 根据唯一标示获取委托对象
     *
     * @param id
     * @return
     */
    Entrust findOne(String id);

    /**
     * Description:
     *
     * @param ownerId
     * @param itemId
     * @return
     */
    Entrust findOneByOwnerIdAndItemId(String ownerId, String itemId);

    /**
     * Description:
     *
     * @param ownerId
     * @param itemId
     * @param dateTime
     * @return
     */
    Entrust findOneByOwnerIdAndItemIdAndTime(String ownerId, String itemId, String dateTime);

    /**
     * 根据委托人和事项Id查找没有删除的委托对象的数量
     *
     * @param ownerId
     * @param itemId
     * @return
     */
    Integer getCountByOwnerIdAndItemId(String ownerId, String itemId);

    /**
     * 获取委托列表
     *
     * @param positionId
     * @return
     */
    List<EntrustModel> getEntrustList(String positionId);

    /**
     * 获取当前岗被委托记录
     *
     * @param positionId
     * @return
     */
    List<EntrustModel> getMyEntrustList(String positionId);

    /**
     * 获取事项列表
     *
     * @param userId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> itemList(String userId, Integer page, Integer rows);

    /**
     * Description:
     *
     * @param ownerId
     * @return
     */
    List<Entrust> list(String ownerId);

    /**
     * 逻辑删除委托对象
     *
     * @param id
     */
    void removeEntrust(String id);

    /**
     * 保存或者更新委托对象
     *
     * @param entrust
     * @return
     * @throws ParseException
     */
    Entrust saveOrUpdate(Entrust entrust) throws ParseException;
}
