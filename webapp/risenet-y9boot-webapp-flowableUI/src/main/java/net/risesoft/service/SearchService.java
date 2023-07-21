package net.risesoft.service;

import java.util.Map;

import net.risesoft.pojo.Y9Page;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
public interface SearchService {

    /**
     * 获取公务邮件列表
     *
     * @param page
     * @param rows
     * @param startDateStr
     * @param endDateStr
     * @param fileType
     * @param userName
     * @param title
     * @return
     */
    Y9Page<Map<String, Object>> getEmailList(Integer page, Integer rows, String startDateStr, String endDateStr, Integer fileType, String userName, String title);

    /**
     * Description: 个人所有件
     * 
     * @param searchTerm
     * @param itemName
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> getSearchList(String searchTerm, String itemName, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 
     * Description: 阅件搜索
     * 
     * @param searchName
     * @param itemName
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Y9Page<Map<String, Object>> getYuejianList(String searchName, String itemName, String userName, String state, String year, Integer page, Integer rows);

}
