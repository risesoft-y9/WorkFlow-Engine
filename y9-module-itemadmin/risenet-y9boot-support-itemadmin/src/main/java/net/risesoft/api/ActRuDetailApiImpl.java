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

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.entity.ActRuDetail;
import net.risesoft.model.itemadmin.ActRuDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ActRuDetailService;
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
     * 根据流程实例id标记流程为办结状态
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
     * 根据流程编号标记流程为办结状态
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> endByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.endByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 根据流程实例和办件状态查找正在办理的流转详细信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(@RequestParam String tenantId,
        @RequestParam String processInstanceId, int status) {
        Y9LoginUserHolder.setTenantId(tenantId);
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
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
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
     * 根据流程编号和办理人id查找办理的流转信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param assignee 办理人id
     * @return {@code Y9Result<ActRuDetailModel>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<ActRuDetailModel> findByProcessSerialNumberAndAssignee(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail =
            actRuDetailService.findByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        ActRuDetailModel model = new ActRuDetailModel();
        Y9BeanUtil.copyProperties(actRuDetail, model);
        return Y9Result.success(model);
    }

    /**
     * 根据流程编号查找正在办理的流转详细信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List<ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam int status) {
        Y9LoginUserHolder.setTenantId(tenantId);
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
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> recoveryByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.recoveryByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据流程编号删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeByProcessSerialNumber(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessSerialNumber(processSerialNumber);
        return Y9Result.success();
    }

    /**
     * 删除指定办理人的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> removeByProcessSerialNumberAndAssignee(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.removeByProcessSerialNumberAndAssignee(processSerialNumber, assignee);
        return Y9Result.success();
    }

    /**
     * 保存或者更新流转信息
     *
     * @param tenantId 租户id
     * @param actRuDetailModel 详情对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId,
        @RequestBody ActRuDetailModel actRuDetailModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ActRuDetail actRuDetail = new ActRuDetail();
        Y9BeanUtil.copyProperties(actRuDetailModel, actRuDetail);
        actRuDetailService.saveOrUpdate(actRuDetail);
        return Y9Result.success();
    }

    /**
     * 恢复整个流程的流转信息（通过改变流程是否结束状态恢复）
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> syncByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        actRuDetailService.syncByProcessInstanceId(processInstanceId);
        return Y9Result.success();
    }
}
