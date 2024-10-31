package net.risesoft.service;

import java.util.Map;

import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;

public interface WorkList4GfgService {

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 页数
     * @param rows 行数
     * @return Map<String, Object>
     */
    Y9Page<Map<String, Object>> page4MobileByItemIdAndSearchTerm(String itemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageNewByItemIdAndSearchTerm(String itemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 待办列表
     *
     * @param itemId 事项Id
     * @param tableName 表名
     * @param searchMapStr 搜索条件
     * @param page 页数
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageSearchList(String itemId, String tableName, String searchMapStr, Integer page,
        Integer rows);

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel);
}
