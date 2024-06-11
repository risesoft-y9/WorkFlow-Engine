package net.risesoft.service;

import net.risesoft.pojo.Y9Page;


import java.util.Map;

public interface SearchService {

    /**
     * 个人所有件
     *
     * @param searchTerm 搜索条件
     * @param itemId     事项id
     * @param userName   发起人
     * @param state      状态
     * @param year       年度
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param page       页码
     * @param rows       每页条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> getSearchList(String searchTerm, String itemId, String userName, String state,
                                              String year, String startDate, String endDate, Integer page, Integer rows);

    /**
     * 阅件搜索
     *
     * @param searchName 搜索条件
     * @param itemId     事项id
     * @param state      状态
     * @param year       年度
     * @param page       页码
     * @param rows       每页条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> getYuejianList(String searchName, String itemId, String userName, String state,
                                               String year, Integer page, Integer rows);

}
