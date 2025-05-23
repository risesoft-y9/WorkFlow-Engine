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

import net.risesoft.api.itemadmin.AfterWorkInfoApi;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.AfterWorkModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 核稿后工作事项接口
 *
 * @author qinman
 * @date 2024/11/07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/afterWorkInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class AfterWorkInfoRestController {

    private final AfterWorkInfoApi afterWorkInfoApi;

    /**
     * 删除核稿后工作事项
     *
     * @param id 主键id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "删除核稿后工作事项", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/delAfterWork")
    public Y9Result<String> delAfterWork(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        afterWorkInfoApi.delAfterWork(tenantId, id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取核稿后工作事项列表
     *
     * @param processSerialNumber 流程实例id
     * @return Y9Result<List < AfterWorkModel>>
     */
    @FlowableLog(operationName = "获取核稿后工作事项列表")
    @GetMapping(value = "/list")
    public Y9Result<List<AfterWorkModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return afterWorkInfoApi.findByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取核稿后工作事项列表异常", e);
        }
        return Y9Result.failure("获取核稿后工作事项列表失败");
    }

    /**
     * 新增核稿后工作事项
     *
     * @return Y9Result<AfterWorkModel>
     */
    @FlowableLog(operationName = "新增核稿后工作事项", operationType = FlowableOperationTypeEnum.ADD)
    @GetMapping(value = "/newAfterWork")
    public Y9Result<AfterWorkModel> newAfterWork() {
        AfterWorkModel afterWorkModel = new AfterWorkModel();
        afterWorkModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        return Y9Result.success(afterWorkModel);
    }

    /**
     * 保存核稿后工作事项信息
     *
     * @param afterWorkModel 核稿后工作事项信息
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "保存核稿后工作事项信息", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/save")
    public Y9Result<Object> save(@Valid AfterWorkModel afterWorkModel) {
        try {
            if (StringUtils.isBlank(afterWorkModel.getId())) {
                afterWorkModel.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            afterWorkModel.setInputPersonId(userInfo.getPersonId());
            afterWorkModel.setInputPerson(userInfo.getName());
            afterWorkModel.setRecordTime(sdf.format(new Date()));
            return afterWorkInfoApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), afterWorkModel);
        } catch (Exception e) {
            LOGGER.error("保存核稿后工作事项异常", e);
        }
        return Y9Result.failure("保存失败");
    }
}
