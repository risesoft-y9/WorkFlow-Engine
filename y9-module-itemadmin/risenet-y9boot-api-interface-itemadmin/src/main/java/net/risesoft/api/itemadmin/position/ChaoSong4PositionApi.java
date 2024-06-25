package net.risesoft.api.itemadmin.position;

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
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ChaoSong4PositionApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id id
     * @param type type
     * @return Y9Result<Object>
     */
    @PostMapping("/changeChaoSongState")
    Y9Result<Object> changeChaoSongState(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id, @RequestParam("type") String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/changeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> changeStatus(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     * @return Y9Result<Object>
     */
    @PostMapping("/changeStatus2read")
    Y9Result<Object> changeStatus2read(@RequestParam("tenantId") String tenantId, @RequestParam("chaoSongId") String chaoSongId);

    /**
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countByProcessInstanceId")
    Y9Result<Integer> countByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Integer>
     */
    @GetMapping("/countByUserIdAndProcessInstanceId")
    Y9Result<Integer> countByUserIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/deleteByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> deleteByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @PostMapping("/deleteByProcessInstanceId")
    Y9Result<Object> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * Description: 展开抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id id
     * @param processInstanceId 流程实例id
     * @param status status
     * @param mobile 是否发送手机端
     * @return Y9Result<OpenDataModel>
     */
    @GetMapping("/detail")
    Y9Result<OpenDataModel> detail(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("id") String id, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") Integer status, @RequestParam("mobile") boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<Integer>
     */
    @GetMapping("/getDone4OpinionCountByUserId")
    Y9Result<Integer> getDone4OpinionCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @GetMapping("/getDoneCount")
    Y9Result<Integer> getDoneCount(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * Description: 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/getDoneList")
    Y9Page<ChaoSongModel> getDoneList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * Description: 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>;
     */
    @GetMapping("/getListByProcessInstanceId")
    Y9Page<ChaoSongModel> getListByProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("userName") String userName, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * Description: 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId senderId
     * @param processInstanceId 流程实例id
     * @param userName 用户名称
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/getListBySenderIdAndProcessInstanceId")
    Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId, @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("userName") String userName, @RequestParam("rows") int rows,
        @RequestParam("page") int page);

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/getOpinionChaosongByUserId")
    Y9Page<ChaoSongModel> getOpinionChaosongByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @GetMapping("/getTodoCount")
    Y9Result<Integer> getTodoCount(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId);

    /**
     * Description: 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 文档标题
     * @param rows rows
     * @param page page
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/getTodoList")
    Y9Page<ChaoSongModel> getTodoList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 我的抄送列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 接收人名称
     * @param state 状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/myChaoSongList")
    Y9Page<ChaoSongModel> myChaoSongList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") int page, @RequestParam("rows") int rows);

    /**
     * Description: 点击抄送按钮之后保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param users users
     * @param isSendSms 是否发送短信
     * @param isShuMing isShuMing
     * @param smsContent 短信内容
     * @param smsPersonId 短信人员id
     * @return Y9Result<Object>
     */
    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId, @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("users") String users, @RequestParam("isSendSms") String isSendSms,
        @RequestParam("isShuMing") String isShuMing, @RequestParam("smsContent") String smsContent, @RequestParam("smsPersonId") String smsPersonId);

    /**
     * Description: 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索信息
     * @param itemId 事项id
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/searchAllByUserId")
    Y9Page<ChaoSongModel> searchAllByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId 租户id
     * @param searchName 搜索信息
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 用户名称
     * @param state 状态
     * @param year 年份
     * @param page page
     * @param rows rows
     * @return Y9Page<ChaoSongModel>
     */
    @GetMapping("/searchAllList")
    Y9Page<ChaoSongModel> searchAllList(@RequestParam("tenantId") String tenantId, @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId, @RequestParam("senderName") String senderName, @RequestParam("userName") String userName, @RequestParam("state") String state,
        @RequestParam("year") String year, @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 文档标题
     */
    @PostMapping("/updateTitle")
    Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId, @RequestParam("processInstanceId") String processInstanceId, @RequestParam("documentTitle") String documentTitle);

}
