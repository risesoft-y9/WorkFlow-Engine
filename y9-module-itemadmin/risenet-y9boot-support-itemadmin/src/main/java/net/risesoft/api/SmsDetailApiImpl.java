package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SmsDetailApi;
import net.risesoft.entity.SmsDetail;
import net.risesoft.model.itemadmin.SmsDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SmsDetailService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 发送短信详情
 *
 * @author qinman
 * @date 2025/08/22
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/smsDetail", produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsDetailApiImpl implements SmsDetailApi {

    private final SmsDetailService smsDetailService;

    /**
     * 根据流程编号查找流程数据
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param positionId 岗位id
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 短信详情
     * @since 9.6.9
     */
    @Override
    public Y9Result<SmsDetailModel> findByProcessSerialNumberAndPositionId(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SmsDetail smsDetail = smsDetailService.findByProcessSerialNumberAndPositionId(processSerialNumber, positionId);
        if (null == smsDetail) {
            return Y9Result.success(null);
        }
        SmsDetailModel model = new SmsDetailModel();
        Y9BeanUtil.copyProperties(smsDetail, model);
        return Y9Result.success(model);
    }

    /**
     * 保存或更新短信详情
     *
     * @param tenantId 租户ID
     * @param smsDetailModel 短信详情
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.9
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestBody SmsDetailModel smsDetailModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SmsDetail smsDetail = new SmsDetail();
        Y9BeanUtil.copyProperties(smsDetailModel, smsDetail);
        smsDetailService.saveOrUpdate(smsDetail);
        return Y9Result.success();
    }
}
