package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 */
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
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchMapStr 查找条件
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> todoList4TaskDefKey(String itemId, String taskDefKey, String searchMapStr,
        String queryMapStr, Integer page,
        Integer rows);

    /**
     * 获取督办列表
     *
     * @param itemId 事项Id
     * @param days 过期天数
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList4DuBan(String itemId, Integer days, Integer page, Integer rows);

    /**
     * 获取科室在办列表
     *
     * @param itemId 事项Id
     * @param isBureau 是否委办局
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList4Dept(String itemId, boolean isBureau, Integer page, Integer rows);

    /**
     * 获取所有在办
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList4All(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取已办列表（包括在办、办结）
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> haveDoneList(String itemId, String searchMapStr, Integer page, Integer rows);

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
     * 获取已办列表
     *
     * @param itemId 事项Id
     * @param isBureau 是否委办局数据
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doneList4Dept(String itemId, boolean isBureau, Integer page, Integer rows);

    /**
     * 获取已办列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doneList4All(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取所有本人经手件的列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allList(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> recycleList(String itemId, String searchMapStr, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param itemId 事项Id
     * @param isBureau 是否是委办局数据
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> recycleList4Dept(String itemId, boolean isBureau, Integer page, Integer rows);

    /**
     * 获取回收站列表
     *
     * @param itemId 事项Id
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> recycleList4All(String itemId, Integer page, Integer rows);

    /**
     * 获取待办件列表
     *
     * @param queryParamModel 查询参数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> allTodoList(QueryParamModel queryParamModel);
}
