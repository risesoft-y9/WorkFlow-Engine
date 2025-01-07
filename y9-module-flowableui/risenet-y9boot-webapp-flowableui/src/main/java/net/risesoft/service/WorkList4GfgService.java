package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

public interface WorkList4GfgService {

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchMapStr 查找条件
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> todoList(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList(String itemId, Integer page, Integer rows);

    /**
     * 获取已办列表（包括在办、办结）
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> haveDoneList(String itemId, Integer page, Integer rows);

    /**
     * 获取流程序列号的所有会签流程信息
     *
     * @param processSerialNumber 流程序列号
     * @return List<Map < String, Object>>
     */
    Y9Result<List<Map<String, Object>>> getSignDeptDetailList(String processSerialNumber);

    /**
     * 获取已办列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doneList(String itemId, Integer page, Integer rows);

    /**
     * 获取所有本人经手件的列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allList(String itemId, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> recycleList(String itemId, Integer page, Integer rows);

    /**
     * 待办列表
     *
     * @param itemId 事项Id
     * @param searchMapStr 搜索条件
     * @param page 页数
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> pageSearchList(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel);
}
