package net.risesoft.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.FlowableUrgeInfoService;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/urgeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class UrgeInfoRestController {

    private final FlowableUrgeInfoService flowableUrgeInfoService;

    /**
     * 删除催办信息
     * 
     * @param id 催办信息唯一标示
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam @NotBlank String id) {
        return flowableUrgeInfoService.deleteById(id);
    }

    /**
     * 查询流程催办信息
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<Object>
     */
    @GetMapping(value = "/list")
    Y9Result<List<UrgeInfoModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        return Y9Result.success(flowableUrgeInfoService.findByProcessSerialNumber(processSerialNumber));
    }

    /**
     * 保存催办信息
     *
     * @param processSerialNumbers isSub:processSerialNumber:executionId 组成的数据
     * @param msgContent 催办消息
     * @return
     */
    @PostMapping(value = "/save")
    Y9Result<Object> save(@RequestParam String[] processSerialNumbers, @RequestParam("msgContent") String msgContent) {
        return flowableUrgeInfoService.save(processSerialNumbers, msgContent);
    }
}
