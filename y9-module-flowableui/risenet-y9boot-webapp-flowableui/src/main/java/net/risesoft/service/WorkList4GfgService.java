package net.risesoft.service;

import java.util.Map;

import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import org.springframework.web.bind.annotation.RequestParam;

public interface WorkList4GfgService {

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param page   页数
     * @param rows   行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> todoList(String itemId, Integer page, Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param page   页数
     * @param rows   行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList(String itemId, Integer page, Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param page   页数
     * @param rows   行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param page   页数
     * @param rows   行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> deleteList(String itemId, Integer page, Integer rows);

    /**
     * 待办列表
     *
     * @param itemId       事项Id
     * @param tableName    表名
     * @param searchMapStr 搜索条件
     * @param page         页数
     * @param rows         条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageSearchList(String itemId, String tableName, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel);
}
