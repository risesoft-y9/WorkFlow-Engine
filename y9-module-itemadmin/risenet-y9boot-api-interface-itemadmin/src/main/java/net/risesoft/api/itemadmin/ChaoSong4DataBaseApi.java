package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 抄送件接口(database)
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSong4DataBaseApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id 抄送件id
     * @param type 状态类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/changeChaoSongState")
    Y9Result<Object> changeChaoSongState(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id,
        @RequestParam(value = "type", required = false) String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/changeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> changeStatus(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/changeStatus2read")
    Y9Result<Object> changeStatus2read(@RequestParam("tenantId") String tenantId,
        @RequestParam("chaoSongId") String chaoSongId);

    /**
     * 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人外是否有抄送件的数量
     * @since 9.6.6
     */
    @GetMapping("/countByProcessInstanceId")
    Y9Result<Integer> countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人是否有抄送件的数量
     * @since 9.6.6
     */
    @GetMapping("/countByUserIdAndProcessInstanceId")
    Y9Result<Integer> countByUserIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/deleteByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> deleteByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessInstanceId")
    Y9Result<Object> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取抄送件详情
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param id 抄送id
     * @param processInstanceId 抄送的流程实例id
     * @param status 传阅的状态,0未阅,1已阅,2新件
     * @param openNotRead 是否为打开不已阅
     * @param mobile 是否为移动端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是送件对象
     * @since 9.6.6
     */
    @GetMapping("/detail")
    Y9Result<OpenDataModel> detail(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("id") String id,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") Integer status,
        @RequestParam(value = "openNotRead", required = false) Boolean openNotRead,
        @RequestParam("mobile") boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是批阅件计数
     * @since 9.6.6
     */
    @GetMapping("/getDone4OpinionCountByUserId")
    Y9Result<Integer> getDone4OpinionCountByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送未阅件统计
     * @since 9.6.6
     */
    @GetMapping("/getDoneCount")
    Y9Result<Integer> getDoneCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是抄送已阅件列表
     * @since 9.6.6
     */
    @GetMapping("/getDoneList")
    Y9Page<ChaoSongModel> getDoneList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "documentTitle", required = false) String documentTitle, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是除当前人外的其他抄送件列表
     * @since 9.6.6
     */
    @GetMapping("/getListByProcessInstanceId")
    Y9Page<ChaoSongModel> getListByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "userName", required = false) String userName, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId 用户id
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是当前人的抄送件列表
     * @since 9.6.6
     */
    @GetMapping("/getListBySenderIdAndProcessInstanceId")
    Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam(value = "userName", required = false) String userName, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是批阅件列表
     * @since 9.6.6
     */
    @GetMapping("/getOpinionChaosongByUserId")
    Y9Page<ChaoSongModel> getOpinionChaosongByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "documentTitle", required = false) String documentTitle, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送已阅件统计
     * @since 9.6.6
     */
    @GetMapping("/getTodoCount")
    Y9Result<Integer> getTodoCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId);

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是抄送未阅件列表
     * @since 9.6.6
     */
    @GetMapping("/getTodoList")
    Y9Page<ChaoSongModel> getTodoList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "documentTitle", required = false) String documentTitle, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 我的抄送列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 接收人名称
     * @param state 状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是我的抄送列表
     * @since 9.6.6
     */
    @GetMapping("/myChaoSongList")
    Y9Page<ChaoSongModel> myChaoSongList(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "searchName", required = false) String searchName,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 抄送保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param orgUnitId 人员、岗位id
     * @param processInstanceId 抄送的流程实例id
     * @param users 抄送目标orgUnitIds
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param smsPersonId 短信提醒人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("orgUnitId") String orgUnitId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("users") String users, @RequestParam(value = "isSendSms", required = false) String isSendSms,
        @RequestParam(value = "isShuMing", required = false) String isShuMing,
        @RequestParam(value = "smsContent", required = false) String smsContent,
        @RequestParam(value = "smsPersonId", required = false) String smsPersonId);

    /**
     * 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发送人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是个人阅件搜索列表
     * @since 9.6.6
     */
    @GetMapping("/searchAllByUserId")
    Y9Page<ChaoSongModel> searchAllByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("orgUnitId") String orgUnitId,
        @RequestParam(value = "searchName", required = false) String searchName,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 接收人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是监控阅件列表
     * @since 9.6.6
     */
    @GetMapping("/searchAllList")
    Y9Page<ChaoSongModel> searchAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam(value = "searchName", required = false) String searchName,
        @RequestParam(value = "itemId", required = false) String itemId,
        @RequestParam(value = "senderName", required = false) String senderName,
        @RequestParam(value = "userName", required = false) String userName,
        @RequestParam(value = "state", required = false) String state,
        @RequestParam(value = "year", required = false) String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateTitle")
    Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);

}
