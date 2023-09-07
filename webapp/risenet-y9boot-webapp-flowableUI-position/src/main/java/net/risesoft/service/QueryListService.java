package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

public interface QueryListService {

    /**
     * 综合查询列表
     *
     * @param itemId 事项id
     * @param state 状态
     * @param createDate 日期
     * @param tableName 表名
     * @param searchMapStr 查询字段信息
     * @param page 页码
     * @param rows 条数
     * @return
     */
    Y9Page<Map<String, Object>> queryList(String itemId, String state, String createDate, String tableName, String searchMapStr, Integer page, Integer rows);
}
