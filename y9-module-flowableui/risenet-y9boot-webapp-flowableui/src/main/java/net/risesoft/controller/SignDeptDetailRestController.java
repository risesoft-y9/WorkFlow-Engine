package net.risesoft.controller;

import java.util.Collections;
import java.util.List;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 常用语
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/signDeptDetail", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignDeptDetailRestController {

    private final SignDeptDetailApi signDeptDetailApi;

    private final OrgUnitApi orgUnitApi;

    /**
     * 根据主键删除会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam @NotBlank String id) {
        return signDeptDetailApi.deleteById(Y9LoginUserHolder.getTenantId(), id);
    }

    /**
     * 根据流程序列号获取会签信息
     *
     * @param processSerialNumber 流程实例id
     * @return Y9Result<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/getSignDeptDetail")
    Y9Result<SignDeptDetailModel> getSignDeptDetail(@RequestParam @NotBlank String processSerialNumber) {
        OrgUnit bureau = orgUnitApi
            .getBureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPositionId()).getData();
        return signDeptDetailApi.findByProcessSerialNumberAndDeptId4Latest(Y9LoginUserHolder.getTenantId(),
            processSerialNumber, bureau.getId());
    }

    /**
     * 根据流程序列号和唯一标示获取会签信息
     *
     * @param processSerialNumber 流程实序列号
     * @param signDepIdtDetailId 唯一标示
     * @return Y9Result<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/getSignDeptDetailById")
    Y9Result<List<SignDeptDetailModel>> getSignDeptDetailById(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String signDepIdtDetailId) {
        if (StringUtils.isBlank(signDepIdtDetailId)) {
            return signDeptDetailApi.findByProcessSerialNumberAndStatus(Y9LoginUserHolder.getTenantId(),
                processSerialNumber, SignDeptDetailStatusEnum.DONE.getValue());
        }
        return Y9Result.success(Collections
            .singletonList(signDeptDetailApi.findById(Y9LoginUserHolder.getTenantId(), signDepIdtDetailId).getData()));
    }

    /**
     * 保存会签信息
     *
     * @param jsonData 会签部门信息
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam @NotBlank String jsonData) {
        SignDeptDetailModel signDeptDetailModel = Y9JsonUtil.readValue(jsonData, SignDeptDetailModel.class);
        return signDeptDetailApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getUserInfo().getPositionId(), signDeptDetailModel);
    }

}
