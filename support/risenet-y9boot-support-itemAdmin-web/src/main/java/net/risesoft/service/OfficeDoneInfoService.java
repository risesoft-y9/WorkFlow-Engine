package net.risesoft.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import net.risesoft.nosql.elastic.entity.OfficeDoneInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public interface OfficeDoneInfoService {

    /**
     * 监控办结统计
     *
     * @param itemId
     * @return
     */
    int countByItemId(String itemId);

    /**
     * 统计个人办结件
     *
     * @param userId
     * @param itemId
     * @return
     */
    int countByUserId(String userId, String itemId);

    /**
     * 监控在办统计
     *
     * @param itemId
     * @return
     */
    long countDoingByItemId(String itemId);

    /**
     * 根据流程实例id删除办件信息
     *
     * @param processInstanceId
     * @return
     */
    boolean deleteOfficeDoneInfo(String processInstanceId);

    /**
     * 据流程实例id获取办件信息
     *
     * @param processInstanceId
     * @return
     */
    OfficeDoneInfo findByProcessInstanceId(String processInstanceId);

    /**
     * 保存办件信息,不经过kafka消息队列，直接保存
     *
     * @param info
     * @throws Exception
     */
    void saveOfficeDone(OfficeDoneInfo info) throws Exception;

    /**
     * 科室所有件
     *
     * @param deptId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllByDeptId(String deptId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * Description: 个人所有件搜索
     * 
     * @param userId
     * @param title
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllByUserId(String userId, String title, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 监控办件列表
     *
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchAllList(String searchName, String itemId, String userName, String state, String year, Integer page, Integer rows);

    /**
     * 获取监控在办，办结件列表
     *
     * @param title
     * @param itemId
     * @param state
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchByItemId(String title, String itemId, String state, String startdate, String enddate, Integer page, Integer rows);

    /**
     * 获取个人办结件列表
     *
     * @param userId
     * @param title
     * @param itemId
     * @param startdate
     * @param enddate
     * @param page
     * @param rows
     * @return
     */
    Map<String, Object> searchByUserId(String userId, String title, String itemId, String startdate, String enddate, Integer page, Integer rows);
}
