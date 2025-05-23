package net.risesoft.controller.gfg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
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

import net.risesoft.api.itemadmin.PreWorkInfoApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.PreWorkModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 前期工作事项接口
 *
 * @author qinman
 * @date 2024/11/07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/preWorkInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class PreWorkInfoRestController {

    private final PreWorkInfoApi preWorkInfoApi;

    /**
     * 删除前期工作事项
     *
     * @param id 主键id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "删除前期工作事项", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/delPreWork")
    public Y9Result<String> delPreWork(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        preWorkInfoApi.delPreWork(tenantId, id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取前期工作事项列表
     *
     * @param processSerialNumber 流程实例id
     * @return Y9Result<List < PreWorkModel>>
     */
    @FlowableLog(operationName = "获取前期工作事项列表")
    @GetMapping(value = "/list")
    public Y9Result<List<PreWorkModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return preWorkInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取前期工作事项列表异常", e);
        }
        return Y9Result.failure("获取前期工作事项失败");
    }

    /**
     * 新增前期工作事项
     *
     * @return Y9Result<PreWorkModel>
     */
    @FlowableLog(operationName = "新增前期工作事项", operationType = FlowableOperationTypeEnum.ADD)
    @GetMapping(value = "/newPreWork")
    public Y9Result<PreWorkModel> newPreWork() {
        PreWorkModel preWorkModel = new PreWorkModel();
        preWorkModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        return Y9Result.success(preWorkModel);
    }

    /**
     * 保存前期工作事项信息
     *
     * @param preWorkModel 前期工作事项信息
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "保存前期工作事项信息", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/save")
    public Y9Result<Object> save(@Valid PreWorkModel preWorkModel) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            if (StringUtils.isBlank(preWorkModel.getId())) {
                preWorkModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            preWorkModel.setInputPersonId(userInfo.getPersonId());
            preWorkModel.setInputPerson(userInfo.getName());
            preWorkModel.setRecordTime(sdf.format(new Date()));
            return preWorkInfoApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), preWorkModel);
        } catch (Exception e) {
            LOGGER.error("保存前期工作事项异常", e);
        }
        return Y9Result.failure("保存失败");
    }
}
