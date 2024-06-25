package net.risesoft.api.itemadmin;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSongInfoApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id id
     * @param type type
     */
    @PostMapping("/changeChaoSongState")
    void changeChaoSongState(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id, @RequestParam("type") String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids ids
     */
    @PostMapping(value = "/changeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    void changeStatus(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestBody String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    @PostMapping("/changeStatus2read")
    void changeStatus2read(@RequestParam("tenantId") String tenantId, @RequestParam("chaoSongId") String chaoSongId);

    /**
     * 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return int
     */
    @GetMapping("/countByProcessInstanceId")
    int countByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @return int
     */
    @GetMapping("/countByUserIdAndProcessInstanceId")
    int countByUserIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     */

    @PostMapping(value = "/deleteByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    void deleteByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return boolean
     */
    @PostMapping("/deleteByProcessInstanceId")
    boolean deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 展开抄送件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status 状态
     * @param mobile 是否发送手机端
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/detail")
    Map<String, Object> detail(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("id") String id, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") Integer status, @RequestParam("mobile") boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */

    @GetMapping("/getDone4OpinionCountByUserId")
    int getDone4OpinionCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */

    @GetMapping("/getDoneCountByUserId")
    int getDoneCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getDoneListByUserId")
    Map<String, Object> getDoneListByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getListByProcessInstanceId")
    Map<String, Object> getListByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("userName") String userName, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId 发送人id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getListBySenderIdAndProcessInstanceId")
    Map<String, Object> getListBySenderIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("userName") String userName, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getOpinionChaosongByUserId")
    Map<String, Object> getOpinionChaosongByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    @GetMapping("/getTodoCountByUserId")
    int getTodoCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/getTodoListByUserId")
    Map<String, Object> getTodoListByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 点击抄送按钮之后保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发送短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 收信人
     * @return Map&lt;String, Object&gt;
     */
    @PostMapping("/save")
    Map<String, Object> save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("users") String users, @RequestParam("isSendSms") String isSendSms, @RequestParam("isShuMing") String isShuMing,
        @RequestParam("smsContent") String smsContent, @RequestParam("smsPersonId") String smsPersonId);

    /**
     * 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param searchName 搜索名称
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/searchAllByUserId")
    Map<String, Object> searchAllByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索名称
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Map&lt;String, Object&gt;
     */
    @GetMapping("/searchAllList")
    Map<String, Object> searchAllList(@RequestParam("tenantId") String tenantId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("senderName") String senderName, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    @PostMapping("/updateTitle")
    void updateTitle(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("documentTitle") String documentTitle);

}
