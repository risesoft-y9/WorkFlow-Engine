package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface DoneService {

    /**
     * 获取办结列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页
     * @param rows 行数
     * @return
     */
    Y9Page<Map<String, Object>> list(String itemId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取办结列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页
     * @param rows 行数
     * @return
     */
    Y9Page<Map<String, Object>> listNew(String itemId, String searchTerm, Integer page, Integer rows);
}
