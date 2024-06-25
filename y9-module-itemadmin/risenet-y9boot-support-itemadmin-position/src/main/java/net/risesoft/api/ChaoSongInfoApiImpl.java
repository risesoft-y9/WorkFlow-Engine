package net.risesoft.api;

import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Position;
import net.risesoft.nosql.elastic.entity.ChaoSongInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ChaoSongInfoService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 抄送件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/chaoSong4Position")
public class ChaoSongInfoApiImpl implements ChaoSong4PositionApi {

    private final ChaoSongInfoService chaoSongInfoService;

    private final PersonApi personManager;

    private final PositionApi positionManager;

    /**
     * 改变抄送件意见状态
     *
     * @param tenantId 租户id
     * @param id 抄送件id
     * @param type 状态类型
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/changeChaoSongState", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> changeChaoSongState(String tenantId, String id, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeChaoSongState(id, type);
        return Y9Result.success();
    }

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/changeStatus", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> changeStatus(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeStatus(ids);
        return Y9Result.success();
    }

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/changeStatus2read", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> changeStatus2read(String tenantId, String chaoSongId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeStatus(chaoSongId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id统计除当前人外是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/countByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> countByProcessInstanceId(String tenantId, String positionId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.countByProcessInstanceId(positionId, processInstanceId);
        return Y9Result.success(num);
    }

    /**
     * 根据流程实例id统计当前人是否有抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/countByUserIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> countByUserIdAndProcessInstanceId(String tenantId, String positionId,
        String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.countByUserIdAndProcessInstanceId(positionId, processInstanceId);
        return Y9Result.success(num);
    }

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids 抄送ids
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/deleteByIds", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> deleteByIds(String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.deleteByIds(ids);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/deleteByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> deleteByProcessInstanceId(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.deleteByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 获取抄送件详情
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 抄送id
     * @param processInstanceId 抄送的流程实例ID
     * @param status 传阅的状态,0未阅,1已阅,2新件
     * @param mobile 是否为移动端
     * @return Map
     */
    @Override
    @GetMapping(value = "/detail", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Map<String, Object>> detail(String tenantId, String positionId, String id, String processInstanceId,
        Integer status, boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        Map<String, Object> map = chaoSongInfoService.detail(processInstanceId, status, mobile);
        map.put("id", id);
        map.put("status", status);
        ChaoSongInfo chaoSong = chaoSongInfoService.findOne(id);
        if (null != chaoSong && chaoSong.getStatus() != 1) {
            chaoSongInfoService.changeStatus(id);
        }
        return Y9Result.success(map);
    }

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/getDone4OpinionCountByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> getDone4OpinionCountByUserId(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.getDone4OpinionCountByUserId(positionId);
        return Y9Result.success(num);
    }

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/getDoneCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> getDoneCount(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.getDoneCountByUserId(positionId);
        return Y9Result.success(num);
    }

    /**
     * 获取抄送已阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/getDoneList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> getDoneList(String tenantId, String positionId, String documentTitle, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.getDoneList(positionId, documentTitle, rows, page);
    }

    /**
     * 根据流程实例获取除当前人外的其他抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ChaoSongModel>
     */
    @Override
    @GetMapping(value = "/getListByProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> getListByProcessInstanceId(String tenantId, String positionId,
        String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return chaoSongInfoService.getListByProcessInstanceId(processInstanceId, userName, rows, page);
    }

    /**
     * 根据流程实例获取当前人的抄送件
     *
     * @param tenantId 租户id
     * @param senderId 用户id
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return Y9Page<ChaoSongModel>
     */
    @Override
    @GetMapping(value = "/getListBySenderIdAndProcessInstanceId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(String tenantId, String senderId,
        String processInstanceId, String userName, int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.getListBySenderIdAndProcessInstanceId(senderId, processInstanceId, userName, rows,
            page);
    }

    /**
     * 批阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 用户id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return Map
     */
    @Override
    @GetMapping(value = "/getOpinionChaosongByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> getOpinionChaosongByUserId(String tenantId, String positionId, String documentTitle,
        int rows, int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.getOpinionChaosongByUserId(positionId, documentTitle, rows, page);
    }

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return Y9Result<Integer>
     */
    @Override
    @GetMapping(value = "/getTodoCount", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Integer> getTodoCount(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.getTodoCountByUserId(positionId);
        return Y9Result.success(num);
    }

    /**
     * 获取抄送未阅件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return Map&lt;String, Object&gt;
     */
    @Override
    @GetMapping(value = "/getTodoList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> getTodoList(String tenantId, String positionId, String documentTitle, int rows,
        int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.getTodoList(positionId, documentTitle, rows, page);
    }

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
    @Override
    @GetMapping(value = "/myChaoSongList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> myChaoSongList(String tenantId, String positionId, String searchName, String itemId,
        String userName, String state, String year, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return chaoSongInfoService.myChaoSongList(searchName, itemId, userName, state, year, rows, page);
    }

    /**
     * 抄送保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param positionId 岗位id
     * @param processInstanceId 抄送的流程实例ID
     * @param users 抄送目标orgUnitIds
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param smsPersonId 短信提醒人id
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> save(String tenantId, String userId, String positionId, String processInstanceId,
        String users, String isSendSms, String isShuMing, String smsContent, String smsPersonId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        Map<String, Object> map =
            chaoSongInfoService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
        if ((boolean)map.get(UtilConsts.SUCCESS)) {
            return Y9Result.success();
        }
        return Y9Result.failure("抄送失败");
    }

    /**
     * 个人阅件搜索
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发送人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<ChaoSongModel>
     */
    @Override
    @GetMapping(value = "/searchAllByUserId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> searchAllByUserId(String tenantId, String positionId, String searchName, String itemId,
        String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return chaoSongInfoService.searchAllByUserId(searchName, itemId, userName, state, year, page, rows);
    }

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
     * @return Y9Page<ChaoSongModel>
     */
    @Override
    @GetMapping(value = "/searchAllList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Page<ChaoSongModel> searchAllList(String tenantId, String searchName, String itemId, String senderName,
        String userName, String state, String year, Integer page, Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.searchAllList(searchName, itemId, senderName, userName, state, year, page, rows);
    }

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return Y9Result<Object>
     */
    @Override
    @PostMapping(value = "/updateTitle", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<Object> updateTitle(String tenantId, String processInstanceId, String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.updateTitle(processInstanceId, documentTitle);
        return Y9Result.success();
    }
}
