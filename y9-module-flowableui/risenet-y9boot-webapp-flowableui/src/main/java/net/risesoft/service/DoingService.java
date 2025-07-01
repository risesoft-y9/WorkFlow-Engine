package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

public interface DoingService {

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页数
     * @param rows 行数
     * @return Map<String, Object>
     */
    Y9Page<Map<String, Object>> page4MobileByItemIdAndSearchTerm(String itemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageNewByItemIdAndSearchTerm(String itemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 在办列表
     *
     * @param itemId 事项Id
     * @param searchMapStr 搜索条件
     * @param page 页码
     * @param rows 每页条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageSearchList(String itemId, String searchMapStr, Integer page, Integer rows);
}
