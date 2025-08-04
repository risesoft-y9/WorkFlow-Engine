package net.risesoft.service.entrust;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.entrust.EntrustHistory;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface EntrustHistoryService {

    /**
     *
     * Description: 获取某个用户的委托历史对象集合
     *
     * @param ownerId
     * @return
     */
    List<EntrustHistory> listByOwnerId(String ownerId);

    /**
     * Description: 获取某个用户的某个事项委托历史对象集合
     *
     * @param ownerId
     * @param itemId
     * @return
     */
    List<EntrustHistory> listByOwnerIdAndItemId(String ownerId, String itemId);

    /**
     *
     * Description: 获取某个用户的委托历史对象集合
     *
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> pageAll(int page, int rows);

    /**
     *
     * Description: 获取某个用户的委托历史对象集合
     *
     * @param assigneeId
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> pageByAssigneeId(String assigneeId, int page, int rows);

    /**
     * Description: 获取某个用户的委托历史对象集合
     *
     * @param ownerId
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> pageByOwnerId(String ownerId, int page, int rows);

    /**
     *
     * Description: 保存委托历史对象
     *
     * @param entrustHistory
     * @return
     * @throws ParseException
     */
    EntrustHistory save(EntrustHistory entrustHistory) throws ParseException;
}
