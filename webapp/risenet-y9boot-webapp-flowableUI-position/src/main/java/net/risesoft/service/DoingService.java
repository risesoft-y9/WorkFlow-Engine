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
     * @return
     */
    Map<String, Object> list(String itemId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页数
     * @param rows 行数
     * @return
     */
    Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows);

    /**
     * 在办列表
     *
     * @param itemId
     * @param tableName
     * @param searchMapStr
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> searchList(String itemId, String tableName, String searchMapStr, Integer page,
        Integer rows);
}
