package net.risesoft.controller.gfg;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.validation.Valid;
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

import net.risesoft.api.itemadmin.PaperAttachmentApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.PaperAttachmentModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 纸质附件接口
 *
 * @author qinman
 * @date 2024/11/07
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/paperAttachment", produces = MediaType.APPLICATION_JSON_VALUE)
public class PaperAttachmentRestController {

    private final PaperAttachmentApi paperAttachmentApi;

    /**
     * 删除纸质附件
     *
     * @param ids 附件ids，逗号隔开
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "删除纸质附件", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/delPaperAttachments")
    public Y9Result<String> delPaperAttachments(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        paperAttachmentApi.delFile(tenantId, ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 获取纸质附件列表
     *
     * @param processSerialNumber 流程实例id
     * @return Y9Result<List < PaperFileModel>>
     */
    @FlowableLog(operationName = "获取纸质附件列表")
    @GetMapping(value = "/list")
    public Y9Result<List<PaperAttachmentModel>> list(@RequestParam @NotBlank String processSerialNumber) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            return paperAttachmentApi.findByProcessSerialNumber(tenantId, processSerialNumber);
        } catch (Exception e) {
            LOGGER.error("获取纸质附件列表异常", e);
        }
        return Y9Result.failure("获取纸质附件列表失败");
    }

    /**
     * 保存纸质附件信息
     *
     * @param paperAttachmentModel 纸质附件信息
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "获取纸质附件列表", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/save")
    public Y9Result<Object> save(@Valid PaperAttachmentModel paperAttachmentModel) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
            paperAttachmentModel.setPersonId(userInfo.getPersonId());
            paperAttachmentModel.setPersonName(userInfo.getName());
            paperAttachmentModel.setUploadTime(sdf.format(new Date()));
            return paperAttachmentApi.saveOrUpdate(Y9LoginUserHolder.getTenantId(), paperAttachmentModel);
        } catch (Exception e) {
            LOGGER.error("保存纸质附件异常", e);
        }
        return Y9Result.failure("保存失败");
    }
}
