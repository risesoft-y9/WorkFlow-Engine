package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.core.ActRuDetailService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 流转信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/actRuDetail", produces = MediaType.APPLICATION_JSON_VALUE)
public class ActRuDetailApiImpl implements ActRuDetailApi {

    private final ActRuDetailService actRuDetailService;

    /**
     * 流程办结监听-->根据流程实例id标记流程为办结状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> endByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.endByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据执行实例id标记流程为删除状态
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByExecutionId(@RequestParam String executionId) {
        actRuDetailService.deleteByExecutionId(executionId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例和办件状态查找正在办理的流转详细信息
     *
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(@RequestParam String processInstanceId,
        @RequestParam ActRuDetailStatusEnum status) {
        List<ActRuDetail> actRuDetailList =
            actRuDetailService.listByProcessInstanceIdAndStatus(processInstanceId, status);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据流程编号查找正在办理的流转详细信息
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(@RequestParam String processSerialNumber) {
        List<ActRuDetail> actRuDetailList = actRuDetailService.listByProcessSerialNumber(processSerialNumber);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据流程编号查找正在办理的流转详细信息
     *
     * @param processSerialNumber 流程编号
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List<ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam String processSerialNumber,
        @RequestParam ActRuDetailStatusEnum status) {
        List<ActRuDetail> actRuDetailList =
            actRuDetailService.listByProcessSerialNumberAndStatus(processSerialNumber, status);
        List<ActRuDetailModel> modelList = new ArrayList<>();
        ActRuDetailModel model;
        for (ActRuDetail actRuDetail : actRuDetailList) {
            model = new ActRuDetailModel();
            Y9BeanUtil.copyProperties(actRuDetail, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 恢复整个流程的流转信息（通过改变流程是否结束状态恢复）
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recoveryByProcessInstanceId(@RequestParam String processInstanceId) {
        actRuDetailService.recoveryByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 恢复整个流程的流转信息（通过改变流程是否结束状态恢复）
     *
     * @param processSerialNumber 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recoveryByProcessSerialNumber(@RequestParam String processSerialNumber) {
        actRuDetailService.recoveryByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 恢复会签流程的流转信息（通过改变流程是否结束状态恢复）
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recoveryByExecutionId(@RequestParam String executionId) {
        actRuDetailService.recoveryByExecutionId(executionId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id删除整个流程的办件详情
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeByProcessInstanceId(@RequestParam String processInstanceId) {
        actRuDetailService.removeByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程编号删除整个流程的办件详情
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeByProcessSerialNumber(@RequestParam String processSerialNumber) {
        actRuDetailService.removeByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 根据流程编号删除整个流程的办件详情(逻辑删除)
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteByProcessSerialNumber(@RequestParam String processSerialNumber) {
        actRuDetailService.deletedByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 保存或者更新流转信息
     *
     * @param actRuDetailModel 详情对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestBody ActRuDetailModel actRuDetailModel) {
        ActRuDetail actRuDetail = new ActRuDetail();
        Y9BeanUtil.copyProperties(actRuDetailModel, actRuDetail);
        actRuDetailService.saveOrUpdate(actRuDetail);
        return Y9Result.success();
    }

    /**
     * 签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> claim(@RequestParam String taskId, @RequestParam String assignee) {
        return actRuDetailService.claim(taskId, assignee);
    }

    /**
     * 撤销签收
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> unClaim(@RequestParam String taskId) {
        return actRuDetailService.unClaim(taskId);
    }

    /**
     * 撤销签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> refuseClaim(@RequestParam String taskId, @RequestParam String assignee) {
        return actRuDetailService.refuseClaim(taskId, assignee);
    }

    @Override
    public Y9Result<Object> todo2doing(@RequestParam String taskId, @RequestParam String assignee) {
        return actRuDetailService.todo2doing(taskId, assignee);
    }

    /**
     * 恢复整个流程的流转信息（通过改变流程是否结束状态恢复）
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> syncByProcessInstanceId(@RequestParam String processInstanceId) {
        actRuDetailService.syncByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    @Override
    public Y9Result<Object> setRead(String id) {
        actRuDetailService.setRead(id);
        return Y9Result.success();
    }
}
