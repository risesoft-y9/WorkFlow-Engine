package net.risesoft.api.itemadmin;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.SmsDetailModel;
import net.risesoft.pojo.Y9Result;

/**
 * 发送短信详情
 *
 * @author qinman
 * @date 2025/08/22
 */
public interface SmsDetailApi {

    /**
     * 根据流程编号查找流程数据
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param positionId 岗位id
     * @return {@code Y9Result<ProcessParamModel>} 通用请求返回对象 -data 短信详情
     * @since 9.6.9
     */
    @GetMapping("/findByProcessSerialNumberAndPositionId")
    Y9Result<SmsDetailModel> findByProcessSerialNumberAndPositionId(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存或更新短信详情
     *
     * @param tenantId 租户ID
     * @param smsDetailModel 短信详情
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.9
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody SmsDetailModel smsDetailModel);
}
