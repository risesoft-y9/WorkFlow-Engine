package y9.client.rest.itemadmin.position;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "ChaoSong4PositionApiClient", name = "${y9.service.itemAdmin.name:itemAdmin}",
    url = "${y9.service.itemAdmin.directUrl:}",
    path = "/${y9.service.itemAdmin.name:itemAdmin}/services/rest/chaoSong4Position")
public interface ChaoSong4PositionApiClient extends ChaoSong4PositionApi {

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId
     * @param id
     * @param type
     * @return
     */
    @Override
    @PostMapping("/changeChaoSongState")
    public Y9Result<Object> changeChaoSongState(@RequestParam("tenantId") String tenantId,
        @RequestParam("id") String id, @RequestParam("type") String type);

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return
     */
    @Override
    @PostMapping(value = "/changeStatus", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> changeStatus(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     * @return
     */
    @Override
    @PostMapping("/changeStatus2read")
    public Y9Result<Object> changeStatus2read(@RequestParam("tenantId") String tenantId,
        @RequestParam("chaoSongId") String chaoSongId);

    /**
     *
     * Description: 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/countByProcessInstanceId")
    public Y9Result<Integer> countByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @return
     */
    @Override
    @GetMapping("/countByUserIdAndProcessInstanceId")
    public Y9Result<Integer> countByUserIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids ids
     * @return
     */
    @Override
    @PostMapping(value = "/deleteByIds", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> deleteByIds(@RequestParam("tenantId") String tenantId, @RequestBody String[] ids);

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
    public Y9Result<Object> deleteByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     *
     * Description: 展开抄送件
     *
     * @param tenantId
     * @param positionId
     * @param id
     * @param processInstanceId
     * @param status
     * @param mobile
     * @return
     */
    @Override
    @GetMapping("/detail")
    public Y9Result<Map<String, Object>> detail(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("id") String id,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") Integer status,
        @RequestParam("mobile") boolean mobile);

    /**
     * 获取批阅件计数
     *
     * @param tenantId
     * @param positionId
     * @return
     */
    @Override
    @GetMapping("/getDone4OpinionCountByUserId")
    public Y9Result<Integer> getDone4OpinionCountByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @Override
    @GetMapping("/getDoneCount")
    public Y9Result<Integer> getDoneCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     *
     * Description: 获取抄送已阅件列表
     *
     * @param tenantId
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getDoneList")
    public Y9Page<ChaoSongModel> getDoneList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     *
     * Description: 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId
     * @param positionId
     * @param processInstanceId
     * @param userName
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getListByProcessInstanceId")
    public Y9Page<ChaoSongModel> getListByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("userName") String userName, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     *
     * Description: 根据流程实例获取当前人的抄送件
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
    public Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("senderId") String senderId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("userName") String userName, @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 批阅件
     *
     * @param tenantId
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getOpinionChaosongByUserId")
    public Y9Page<ChaoSongModel> getOpinionChaosongByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return int
     */
    @Override
    @GetMapping("/getTodoCount")
    public Y9Result<Integer> getTodoCount(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId);

    /**
     *
     * Description: 获取抄送未阅件列表
     *
     * @param tenantId
     * @param positionId
     * @param documentTitle
     * @param rows
     * @param page
     * @return
     */
    @Override
    @GetMapping("/getTodoList")
    public Y9Page<ChaoSongModel> getTodoList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("documentTitle") String documentTitle,
        @RequestParam("rows") int rows, @RequestParam("page") int page);

    /**
     *
     */
    @Override
    @GetMapping("/myChaoSongList")
    public Y9Page<ChaoSongModel> myChaoSongList(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName,
        @RequestParam("itemId") String itemId, @RequestParam("userName") String userName,
        @RequestParam("state") String state, @RequestParam("year") String year, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     *
     * Description: 点击抄送按钮之后保存
     *
     * @param tenantId
     * @param userId
     * @param positionId
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
    public Y9Result<Object> save(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("positionId") String positionId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("users") String users, @RequestParam("isSendSms") String isSendSms,
        @RequestParam("isShuMing") String isShuMing, @RequestParam("smsContent") String smsContent,
        @RequestParam("smsPersonId") String smsPersonId);

    /**
     *
     * Description: 个人阅件搜索
     *
     * @param tenantId
     * @param positionId
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
    public Y9Page<ChaoSongModel> searchAllByUserId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("searchName") String searchName,
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
    public Y9Page<ChaoSongModel> searchAllList(@RequestParam("tenantId") String tenantId,
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
     * @return
     */
    @Override
    @PostMapping("/updateTitle")
    public Y9Result<Object> updateTitle(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("documentTitle") String documentTitle);
}
