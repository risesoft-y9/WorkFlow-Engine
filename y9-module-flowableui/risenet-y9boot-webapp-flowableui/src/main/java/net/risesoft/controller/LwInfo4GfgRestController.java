package net.risesoft.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.LwInfoApi;
import net.risesoft.model.itemadmin.LwInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 来文信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/lwInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class LwInfo4GfgRestController {

    private final LwInfoApi lwInfoApi;

    /**
     * 获取来文信息列表
     *
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<LwInfoModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<LwInfoModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        return lwInfoApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(), processSerialNumber);
    }

}
