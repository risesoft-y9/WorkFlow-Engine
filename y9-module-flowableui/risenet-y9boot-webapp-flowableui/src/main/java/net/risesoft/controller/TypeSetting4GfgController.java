package net.risesoft.controller;

import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.TypeSettingInfoApi;
import net.risesoft.model.itemadmin.TypeSettingInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 发文单排版信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/typeSetting", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class TypeSetting4GfgController {

    private final TypeSettingInfoApi typeSettingInfoApi;

    /**
     * 获取排版信息
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<TypeSettingInfoModel>>
     */
    @GetMapping(value = "/getList")
    public Y9Result<List<TypeSettingInfoModel>> getList(@RequestParam String processSerialNumber) {
        return typeSettingInfoApi.getList(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

    /**
     * 保存排版信息
     *
     * @param processSerialNumber 流程编号
     * @param jsonData 数据信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveTypeSetting")
    public Y9Result<Object> saveTypeSetting(@RequestParam String processSerialNumber, @RequestParam String jsonData) {
        return typeSettingInfoApi.saveTypeSetting(Y9LoginUserHolder.getTenantId(), processSerialNumber, jsonData);
    }

}
