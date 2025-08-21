package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

public interface TodoService {

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 页数
     * @param rows 行数
     * @return Map<String, Object>
     */
    Y9Page<Map<String, Object>> list4Mobile(String itemId, String searchTerm, Integer page, Integer rows);

}
