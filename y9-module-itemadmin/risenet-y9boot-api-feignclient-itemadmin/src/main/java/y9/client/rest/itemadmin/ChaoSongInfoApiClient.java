package y9.client.rest.itemadmin;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ChaoSongInfoApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/chaoSongInfo")
public interface ChaoSongInfoApiClient extends ChaoSongInfoApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId
     * @param id
     * @param type
     */
    @Override
    @PostMapping("/changeChaoSongState")
    public void changeChaoSongState(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id,
        @RequestParam("type") String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param ids ids
     */
    @Override
    @PostMapping(value = "/changeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void changeStatus(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestBody String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     */
    @Override
    @PostMapping("/changeStatus2read")
    public void changeStatus2read(@RequestParam("tenantId") String tenantId,
        @RequestParam("chaoSongId") String chaoSongId);

    /**
     * 
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/countByProcessInstanceId")
    public int countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/countByUserIdAndProcessInstanceId")
    public int countByUserIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     */
    @Override
    @PostMapping(value = "/deleteByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    public void deleteByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @return
     */
    @Override
    @PostMapping("/deleteByProcessInstanceId")
    public boolean deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 
     * Description: 展开抄送件
     * 
     * @param tenantId
     * @param userId
     * @param id
     * @param processInstanceId
     * @param status
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/detail")
    public Map<String, Object> detail(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("id") String id, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("status") Integer status, @RequestParam("mobile") boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId
     * @param userId
     * @return
     */
    @Override
    @GetMapping("/getDone4OpinionCountByUserId")
    public int getDone4OpinionCountByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    @Override
    @GetMapping("/getDoneCountByUserId")
    public int getDoneCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 
     * Description: 获取抄送已阅件列表
     * 
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getDoneListByUserId")
    public Map<String, Object> getDoneListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 
     * Description: 根据流程实例获取除当前人外的其他抄送件
     * 
     * @param tenantId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getListByProcessInstanceId")
    public Map<String, Object> getListByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("userName") String userName,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据流程实例获取当前人的抄送件 Description:
     * 
     * @param tenantId
     * @param senderId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getListBySenderIdAndProcessInstanceId")
    public Map<String, Object> getListBySenderIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("userName") String userName, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 批阅件
     *
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getOpinionChaosongByUserId")
    public Map<String, Object> getOpinionChaosongByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @return int
     */
    @Override
    @GetMapping("/getTodoCountByUserId")
    public int getTodoCountByUserId(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId);

    /**
     * 
     * Description: 获取抄送未阅件列表
     * 
     * @param tenantId
     * @param userId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getTodoListByUserId")
    public Map<String, Object> getTodoListByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 
     * Description: 点击抄送按钮之后保存
     * 
     * @param tenantId
     * @param userId
     * @param processInstanceId
     * @param users
     * @param isSendSms
     * @param isShuMing
     * @param smsContent
     * @param smsPersonId
     * @return
     */
    @Override
    @PostMapping("/save")
    public Map<String, Object> save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("users") String users,
        @RequestParam("isSendSms") String isSendSms, @RequestParam("isShuMing") String isShuMing,
        @RequestParam("smsContent") String smsContent, @RequestParam("smsPersonId") String smsPersonId);

    /**
     * 
     * Description: 个人阅件搜索
     * 
     * @param tenantId
     * @param userId
     * @param searchName
     * @param itemId
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/searchAllByUserId")
    public Map<String, Object> searchAllByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("searchName") String searchName,
        @RequestParam("itemId") String itemId, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 监控阅件列表
     *
     * @param tenantId
     * @param searchName
     * @param itemId
     * @param senderName
     * @param userName
     * @param state
     * @param year
     * @param page
     * @param rows
     * @return
     */
    @Override
    @GetMapping("/searchAllList")
    public Map<String, Object> searchAllList(@RequestParam("tenantId") String tenantId,
        @RequestParam("searchName") String searchName, @RequestParam("itemId") String itemId,
        @RequestParam("senderName") String senderName, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year, @RequestParam("page") Integer page,
        @RequestParam("rows") Integer rows);

    /**
     * 更新抄送件标题
     *
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     */
    @Override
    @PostMapping("/updateTitle")
    public void updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);
}
