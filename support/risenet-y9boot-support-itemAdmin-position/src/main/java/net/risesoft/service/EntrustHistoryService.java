package net.risesoft.service;

import java.text.ParseException;
import java.util.List;

import org.springframework.data.domain.Page;

import net.risesoft.entity.EntrustHistory;

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
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> findAll(int page, int rows);

    /**
     * 
     * Description: 获取某个用户的委托历史对象集合
     * 
     * @param assigneeId
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> findByAssigneeId(String assigneeId, int page, int rows);

    /**
     * 
     * Description: 获取某个用户的委托历史对象集合
     * 
     * @param ownerId
     * @return
     */
    List<EntrustHistory> list(String ownerId);

    /**
     * Description: 获取某个用户的委托历史对象集合
     * 
     * @param ownerId
     * @param page
     * @param rows
     * @return
     */
    Page<EntrustHistory> list(String ownerId, int page, int rows);

    /**
     * Description: 获取某个用户的某个事项委托历史对象集合
     * 
     * @param ownerId
     * @param itemId
     * @return
     */
    List<EntrustHistory> list(String ownerId, String itemId);

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
