package net.risesoft.controller.sms;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.SmsDetailApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.SmsDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9FlowableHolder;

/**
 * 短信提醒详情
 *
 * @author qinman
 * @date 2025/08/13
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/smsDetail", produces = MediaType.APPLICATION_JSON_VALUE)
public class SmsRestController {

    private final SmsDetailApi smsDetailApi;

    /**
     * 保存短信提醒详情
     * 
     * @param smsDetailModel
     * @return
     */
    @FlowableLog(operationName = "保存短信提醒详情", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<Object> saveOrUpdate(@RequestBody SmsDetailModel smsDetailModel) {
        smsDetailModel.setPositionId(Y9FlowableHolder.getPositionId());
        smsDetailModel.setPositionName(Y9FlowableHolder.getPosition().getName());
        smsDetailModel.setPositionIds("");
        smsDetailApi.saveOrUpdate(smsDetailModel);
        return Y9Result.success();
    }
}