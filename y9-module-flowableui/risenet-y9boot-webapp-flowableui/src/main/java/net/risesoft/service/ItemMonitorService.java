package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @date 2024/12/19
 */
public interface ItemMonitorService {

    /**
     * 获取待办列表数据 #TODO
     * 
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageTodoList(String itemId, Integer page, Integer rows);

    /**
     * 获取在办列表数据 #TODO
     * 
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageDoingList(String itemId, Integer page, Integer rows);

    /**
     * 获取办结列表数据 #TODO
     * 
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageDoneList(String itemId, Integer page, Integer rows);

    /**
     * 获取所有件列表数据
     *
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> pageAllList(String itemId, Integer page, Integer rows);

    /**
     * 获取回收站列表数据 #TODO
     * 
     * @param itemId
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> pageRecycleList(String itemId, Integer page, Integer rows);

}
