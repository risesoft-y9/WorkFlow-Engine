package net.risesoft.service;

import java.util.Map;

import net.risesoft.model.ChaoSongModel;
import net.risesoft.model.itemadmin.OfficeFollowModel;
import net.risesoft.pojo.Y9Page;

public interface WorkList4ddyjsService {

    /**
     * 获取在办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doingList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 获取办结列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> doneList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows);

    /**
     * 关注列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 当前页
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<OfficeFollowModel> followList(String itemId, String searchTerm, Integer page, Integer rows);

    /**
     * 获取上会台账列表
     *
     * @param userName 发起人
     * @param deptName 部门名称
     * @param title 标题
     * @param meetingType 会议类型
     * @param page 当前页
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> getMeetingList(String userName, String deptName, String title, String meetingType,
        Integer page, Integer rows);

    /**
     * 我的在办事项
     *
     * @param page 当前页
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> homeDoingList(Integer page, Integer rows);

    /**
     * 首页办结事项
     *
     * @param page 当前页
     * @param rows 条数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> homeDoneList(Integer page, Integer rows);

    /**
     * 我的传阅列表
     *
     * @param searchName 搜索名称
     * @param itemId 事项Id
     * @param userName 发起人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<ChaoSongModel> myChaoSongList(String searchName, String itemId, String userName, String state, String year,
        Integer page, Integer rows);

    /**
     * 获取待办列表
     *
     * @param itemId 事项Id
     * @param searchTerm 搜索词
     * @param page 页数
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    Y9Page<Map<String, Object>> todoList(String itemId, String searchItemId, String searchTerm, Integer page,
        Integer rows);
}
