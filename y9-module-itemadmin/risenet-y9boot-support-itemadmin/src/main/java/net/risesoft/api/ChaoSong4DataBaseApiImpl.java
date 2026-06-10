package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ChaoSong4DataBaseApi;
import net.risesoft.entity.ChaoSong;
import net.risesoft.enums.ChaoSongStatusEnum;
import net.risesoft.model.itemadmin.ChaoSong4DataBaseModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.chaosong.ChaoSongService;
import net.risesoft.y9.Y9FlowableHolder;

/**
 * 抄送件接口(database)
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/chaoSong4database", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChaoSong4DataBaseApiImpl implements ChaoSong4DataBaseApi {

    private final ChaoSongService chaoSongService;

    /**
     * 改变抄送件意见状态
     *
     * @param id 抄送件id
     * @param type 状态类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeChaoSongState(@RequestParam String id, String type) {
        chaoSongService.changeChaoSongState(id, type);
        return Y9Result.success();
    }

    /**
     * 批量设置抄送件状态为已阅
     *
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeStatus(@RequestBody String[] ids) {
        chaoSongService.changeStatus(ids);
        return Y9Result.success();
    }

    /**
     * 根据抄送ID修改状态为已阅
     *
     * @param chaoSongId 抄送id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> changeStatus2read(@RequestParam String chaoSongId) {
        chaoSongService.changeStatus(chaoSongId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id统计除当前人外的抄送件数量
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人外的抄送件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByProcessInstanceId(@RequestParam String processInstanceId) {
        int num = chaoSongService.countByProcessInstanceId(Y9FlowableHolder.getPositionId(), processInstanceId);
        return Y9Result.success(num);
    }

    /**
     * 根据流程实例id统计当前人抄送件数量
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是除当前人抄送件的数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countByUserIdAndProcessInstanceId(@RequestParam String processInstanceId) {
        int num =
            chaoSongService.countByUserIdAndProcessInstanceId(Y9FlowableHolder.getPositionId(), processInstanceId);
        return Y9Result.success(num);
    }

    /**
     * 批量删除抄送件
     *
     * @param ids 抄送件ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByIds(@RequestBody String[] ids) {
        chaoSongService.deleteByIds(ids);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id删除抄送件
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByProcessInstanceId(@RequestParam String processInstanceId) {
        chaoSongService.deleteByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 抄送件详情
     *
     * @param id 抄送id
     * @param processInstanceId 抄送的流程实例id
     * @param status 传阅的状态,0未阅,1已阅,2新件
     * @param openNotRead 是否打开不已阅
     * @param mobile 是否为移动端
     * @return {@code Y9Result<OpenDataModel>} 通用请求返回对象 - data是送件对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<OpenDataModel> detail(@RequestParam String id, @RequestParam String processInstanceId,
        @RequestParam Integer status, Boolean openNotRead, @RequestParam boolean mobile) {
        OpenDataModel model = chaoSongService.detail(processInstanceId, status, mobile);
        model.setId(id);
        model.setStatus(status);
        ChaoSong chaoSong = chaoSongService.getById(id);
        if (null != chaoSong && chaoSong.getStatus() != ChaoSongStatusEnum.READ && !Boolean.TRUE.equals(openNotRead)) {
            chaoSongService.changeStatus(id);
        }
        return Y9Result.success(model);
    }

    /**
     * 获取批阅件数量（签写过意见的阅件）
     *
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是批阅件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getDone4OpinionCountByUserId() {
        int num = chaoSongService.getDone4OpinionCountByUserId(Y9FlowableHolder.getPositionId());
        return Y9Result.success(num);
    }

    /**
     * 获取未阅件数量
     *
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送未阅件数量
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getDoneCount() {
        int num = chaoSongService.getDoneCountByUserId(Y9FlowableHolder.getPositionId());
        return Y9Result.success(num);
    }

    /**
     * 获取已阅件列表
     *
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是抄送已阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> getDoneList(String documentTitle, @RequestParam int rows,
        @RequestParam int page) {
        return chaoSongService.pageDoneList(Y9FlowableHolder.getPositionId(), documentTitle, rows, page);
    }

    /**
     * 根据流程实例id获取除当前人外的其他抄送件列表
     *
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是除当前人外的其他抄送件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> getListByProcessInstanceId(@RequestParam String processInstanceId,
        String userName, @RequestParam int rows, @RequestParam int page) {
        return chaoSongService.pageByProcessInstanceIdAndUserName(processInstanceId, userName, rows, page);
    }

    /**
     * 根据流程实例id获取当前人的抄送件列表
     *
     * @param processInstanceId 流程实例id
     * @param userName 收件人
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是当前人的抄送件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> getListBySenderIdAndProcessInstanceId(@RequestParam String processInstanceId,
        String userName, @RequestParam int rows, @RequestParam int page) {
        return chaoSongService.pageBySenderIdAndProcessInstanceId(Y9FlowableHolder.getPositionId(), processInstanceId,
            userName, rows, page);
    }

    /**
     * 获取批阅件列表
     *
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是批阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> getOpinionChaosongByUserId(String documentTitle, @RequestParam int rows,
        @RequestParam int page) {
        return chaoSongService.pageOpinionChaosongByUserId(Y9FlowableHolder.getPositionId(), documentTitle, rows, page);
    }

    /**
     * 根据岗位id获取已阅件数量
     *
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是抄送已阅件统计
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getTodoCount() {
        int num = chaoSongService.getTodoCountByUserId(Y9FlowableHolder.getPositionId());
        return Y9Result.success(num);
    }

    /**
     * 获取未阅件列表
     *
     * @param documentTitle 标题
     * @param rows 条数
     * @param page 页码
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是抄送未阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> getTodoList(String documentTitle, @RequestParam int rows,
        @RequestParam int page) {
        return chaoSongService.pageTodoList(Y9FlowableHolder.getPositionId(), documentTitle, rows, page);
    }

    /**
     * 获取我的抄送列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 接收人名称
     * @param state 状态
     * @param year 年度
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是我的抄送列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> myChaoSongList(String searchName, String itemId, String userName,
        Integer state, String year, @RequestParam int page, @RequestParam int rows) {
        return chaoSongService.pageMyChaoSongList(searchName, itemId, userName, state, year, rows, page);
    }

    /**
     * 抄送保存
     *
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
    public Y9Result<Object> save(@RequestParam String processInstanceId, @RequestParam String users, String isSendSms,
        String isShuMing, String smsContent, String smsPersonId) {
        return chaoSongService.save(processInstanceId, users, isSendSms, isShuMing, smsContent, smsPersonId);
    }

    /**
     * 个人阅件综合搜索列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param userName 发送人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是个人阅件综合搜索列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> searchAllByUserId(String searchName, String itemId, String userName,
        Integer state, String year, @RequestParam Integer page, @RequestParam Integer rows) {
        return chaoSongService.searchAllByUserId(searchName, itemId, userName, state, year, page, rows);
    }

    /**
     * 获取监控阅件列表
     *
     * @param searchName 搜索词
     * @param itemId 事项id
     * @param senderName 发送人
     * @param userName 接收人
     * @param state 状态
     * @param year 年份
     * @param page 页码
     * @param rows 条数
     * @return {@code Y9Page<ChaoSong4DataBaseModel>} 通用分页请求返回对象 - rows是监控阅件列表
     * @since 9.6.6
     */
    @Override
    public Y9Page<ChaoSong4DataBaseModel> searchAllList(String searchName, String itemId, String senderName,
        String userName, Integer state, String year, @RequestParam Integer page, @RequestParam Integer rows) {
        return chaoSongService.searchAllList(searchName, itemId, senderName, userName, state, year, page, rows);
    }

    /**
     * 更新抄送件标题
     *
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> updateTitle(@RequestParam String processInstanceId, @RequestParam String documentTitle) {
        chaoSongService.updateTitle(processInstanceId, documentTitle);
        return Y9Result.success();
    }
}
