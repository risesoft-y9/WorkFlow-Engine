package net.risesoft.controller.gfg;

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

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.SignDeptDetailApi;
import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.enums.SignDeptDetailStatusEnum;
import net.risesoft.enums.TaskRelatedEnum;
import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.model.processadmin.TaskModel;
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

    private final ActRuDetailApi actRuDetailApi;

    private final TaskApi taskApi;

    private final TaskRelatedApi taskRelatedApi;

    /**
     * 根据主键删除会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SignDeptDetailModel signDeptDetail = signDeptDetailApi.findById(Y9LoginUserHolder.getTenantId(), id).getData();
        List<SignDeptDetailModel> signDeptDetailModels =
            signDeptDetailApi.findByProcessSerialNumber(tenantId, signDeptDetail.getProcessSerialNumber()).getData();
        if (signDeptDetailModels.stream()
            .filter(ssd -> ssd.getStatus().equals(SignDeptDetailStatusEnum.DOING.getValue())).count() == 1) {
            return Y9Result.failure("仅剩一个会签部门，不能删除会签信息");
        }
        /*
         * 1、删除流程参与信息
         */
        actRuDetailApi.deleteByExecutionId(tenantId, signDeptDetail.getExecutionId());
        /*
         * 2、删除会签信息
         */
        signDeptDetailApi.deleteById(Y9LoginUserHolder.getTenantId(), id);
        /*
         * 3、修改历程信息
         */
        List<TaskModel> taskModelList =
            taskApi.findByProcessInstanceId(tenantId, signDeptDetail.getProcessInstanceId()).getData();
        taskModelList.stream().filter(tm -> StringUtils.equals(tm.getExecutionId(), signDeptDetail.getExecutionId()))
            .forEach(tm -> {
                List<TaskRelatedModel> taskRelatedModels = taskRelatedApi.findByTaskId(tenantId, tm.getId()).getData();
                taskRelatedModels.stream()
                    .filter(trm -> StringUtils.equals(trm.getInfoType(), TaskRelatedEnum.ACTIONNAME.getValue()))
                    .forEach(trm -> {
                        trm.setMsgContent("减签");
                        taskRelatedApi.saveOrUpdate(tenantId, trm);
                    });
            });
        return Y9Result.success();
    }

    /**
     * 根据流程序列号获取会签信息
     *
     * @param processSerialNumber 流程序列号
     * @param signDeptDetailId 会签部门详情id
     * @return Y9Result<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/getSignDeptDetail")
    Y9Result<SignDeptDetailModel> getSignDeptDetail(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String signDeptDetailId) {
        String bureauId = "";
        if (StringUtils.isBlank(signDeptDetailId)) {
            bureauId =
                orgUnitApi.getBureau(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getUserInfo().getPositionId())
                    .getData().getId();
        } else {
            bureauId =
                signDeptDetailApi.findById(Y9LoginUserHolder.getTenantId(), signDeptDetailId).getData().getDeptId();
        }
        return signDeptDetailApi.findByProcessSerialNumberAndDeptId4Latest(Y9LoginUserHolder.getTenantId(),
            processSerialNumber, bureauId);
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
     * 根据主键恢复会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/recoverById")
    Y9Result<Object> recoverById(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SignDeptDetailModel ssd = signDeptDetailApi.findById(Y9LoginUserHolder.getTenantId(), id).getData();
        /*
         * 1、恢复流程参与信息
         */
        actRuDetailApi.recoveryByExecutionId(tenantId, ssd.getExecutionId());
        /*
         * 2、恢复会签信息
         */
        signDeptDetailApi.recoverById(Y9LoginUserHolder.getTenantId(), id);
        /*
         * 3、修改历程信息
         */
        List<TaskModel> taskModelList = taskApi.findByProcessInstanceId(tenantId, ssd.getProcessInstanceId()).getData();
        taskModelList.stream().filter(tm -> StringUtils.equals(tm.getExecutionId(), ssd.getExecutionId()))
            .forEach(tm -> {
                List<TaskRelatedModel> taskRelatedModels = taskRelatedApi.findByTaskId(tenantId, tm.getId()).getData();
                taskRelatedModels.stream()
                    .filter(trm -> StringUtils.equals(trm.getInfoType(), TaskRelatedEnum.ACTIONNAME.getValue()))
                    .forEach(trm -> {
                        trm.setMsgContent("恢复");
                        taskRelatedApi.saveOrUpdate(tenantId, trm);
                    });
            });
        return Y9Result.success();
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
