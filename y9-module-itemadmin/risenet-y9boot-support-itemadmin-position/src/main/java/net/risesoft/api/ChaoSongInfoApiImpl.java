package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.ChaoSongModel;
import net.risesoft.model.itemadmin.OpenDataModel;
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
@RequestMapping(value = "/services/rest/chaoSong4Position", produces = MediaType.APPLICATION_JSON_VALUE)
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
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeChaoSongState(@RequestParam String tenantId, @RequestParam String id, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeChaoSongState(id, type);
        return Y9Result.success();
    }

    /**
     * 抄送件状态设为已阅
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeStatus(@RequestParam String tenantId, @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.changeStatus(ids);
        return Y9Result.success();
    }

    /**
     * 根据抄送ID修改状态
     *
     * @param tenantId 租户id
     * @param chaoSongId 抄送id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeStatus2read(@RequestParam String tenantId, @RequestParam String chaoSongId) {
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
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人外是否有抄送件的数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByProcessInstanceId(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String processInstanceId) {
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
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人是否有抄送件的数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.countByUserIdAndProcessInstanceId(positionId, processInstanceId);
        return Y9Result.success(num);
    }

    /**
     * 删除抄送件
     *
     * @param tenantId 租户id
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByIds(@RequestParam String tenantId, @RequestParam @RequestBody String[] ids) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.deleteByIds(ids);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id删除抄送件
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.deleteByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 展开抄送件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param id 抄送id
     * @param processInstanceId 抄送的流程实例id
     * @param status 传阅的状态,0未阅,1已阅,2新件
     * @param openNotRead 是否打开不已阅
     * @param mobile 是否为移动端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是送件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> detail(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String id, @RequestParam String processInstanceId, @RequestParam Integer status,
        Boolean openNotRead, @RequestParam boolean mobile) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        OpenDataModel model = chaoSongInfoService.detail(processInstanceId, status, mobile);
        model.setId(id);
        model.setStatus(status);
        ChaoSongInfo chaoSong = chaoSongInfoService.getById(id);
        if (null != chaoSong && chaoSong.getStatus() != 1 && !Boolean.TRUE.equals(openNotRead)) {
            chaoSongInfoService.changeStatus(id);
        }
        return Y9Result.success(model);
    }

    /**
     * 获取批阅件计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是批阅件计数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getDone4OpinionCountByUserId(@RequestParam String tenantId,
        @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = chaoSongInfoService.getDone4OpinionCountByUserId(positionId);
        return Y9Result.success(num);
    }

    /**
     * 根据人员id获取抄送未阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送未阅件统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getDoneCount(@RequestParam String tenantId, @RequestParam String positionId) {
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是抄送已阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> getDoneList(@RequestParam String tenantId, @RequestParam String positionId,
        String documentTitle, @RequestParam int rows, @RequestParam int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.pageDoneList(positionId, documentTitle, rows, page);
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是除当前人外的其他抄送件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> getListByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String processInstanceId, String userName,
        @RequestParam int rows, @RequestParam int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return chaoSongInfoService.pageByProcessInstanceIdAndUserName(processInstanceId, userName, rows, page);
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是当前人的抄送件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> getListBySenderIdAndProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String senderId, @RequestParam String processInstanceId, String userName, @RequestParam int rows,
        @RequestParam int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.pageBySenderIdAndProcessInstanceId(senderId, processInstanceId, userName, rows,
            page);
    }

    /**
     * 批阅件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是批阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> getOpinionChaosongByUserId(@RequestParam String tenantId,
        @RequestParam String positionId, String documentTitle, @RequestParam int rows, @RequestParam int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.pageOpinionChaosongByUserId(positionId, documentTitle, rows, page);
    }

    /**
     * 根据人员id获取抄送已阅件统计
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送已阅件统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getTodoCount(@RequestParam String tenantId, @RequestParam String positionId) {
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是抄送未阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> getTodoList(@RequestParam String tenantId, @RequestParam String positionId,
        String documentTitle, @RequestParam int rows, @RequestParam int page) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.pageTodoList(positionId, documentTitle, rows, page);
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是我的抄送列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> myChaoSongList(@RequestParam String tenantId, @RequestParam String positionId,
        String searchName, String itemId, String userName, String state, String year, @RequestParam int page,
        @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return chaoSongInfoService.pageMyChaoSongList(searchName, itemId, userName, state, year, rows, page);
    }

    /**
     * 抄送保存
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param positionId 岗位id
     * @param processInstanceId 抄送的流程实例id
     * @param users 抄送目标orgUnitIds
     * @param isSendSms 是否短信提醒
     * @param isShuMing 是否署名
     * @param smsContent 短信内容
     * @param smsPersonId 短信提醒人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> save(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String positionId, @RequestParam String processInstanceId, @RequestParam String users,
        String isSendSms, String isShuMing, String smsContent, String smsPersonId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);
        return chaoSongInfoService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是个人阅件搜索列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> searchAllByUserId(@RequestParam String tenantId, @RequestParam String positionId,
        String searchName, String itemId, String userName, String state, String year, @RequestParam Integer page,
        @RequestParam Integer rows) {
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
     * @return {@code Y9Page<ChaoSongModel>} 通用分页请求返回对象 - rows是监控阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSongModel> searchAllList(@RequestParam String tenantId, String searchName, String itemId,
        String senderName, String userName, String state, String year, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return chaoSongInfoService.searchAllList(searchName, itemId, senderName, userName, state, year, page, rows);
    }

    /**
     * 更新抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateTitle(@RequestParam String tenantId, @RequestParam String processInstanceId,
        @RequestParam String documentTitle) {
        Y9LoginUserHolder.setTenantId(tenantId);
        chaoSongInfoService.updateTitle(processInstanceId, documentTitle);
        return Y9Result.success();
    }
}
