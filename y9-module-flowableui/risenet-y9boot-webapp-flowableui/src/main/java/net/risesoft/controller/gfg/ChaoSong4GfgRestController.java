package net.risesoft.controller.gfg;

import java.util.Arrays;
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

import net.risesoft.api.itemadmin.DocumentCopyApi;
import net.risesoft.api.itemadmin.OpinionCopyApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.DocumentCopyModel;
import net.risesoft.model.itemadmin.OpinionCopyModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 抄送
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/chaoSong/gfg", produces = MediaType.APPLICATION_JSON_VALUE)
public class ChaoSong4GfgRestController {

    private final DocumentCopyApi documentCopyApi;

    private final OpinionCopyApi opinionCopyApi;

    /**
     * 删除会签意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "删除会签意见", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteById")
    public Y9Result<String> deleteById(@RequestParam @NotBlank String id) {
        try {
            opinionCopyApi.deleteById(Y9LoginUserHolder.getTenantId(), id);
            return Y9Result.successMsg("刪除成功");
        } catch (Exception e) {
            LOGGER.error("删除意见失败！", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 批量删除自己的传签件
     *
     * @param processSerialNumbers 传签件流程序列号
     * @return Y9Result<String>
     */
    @FlowableLog(operationName = "批量删除自己的传签件", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteByProcessSerialNumber")
    public Y9Result<String> deleteByProcessSerialNumber(@RequestParam String[] processSerialNumbers) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            Arrays.stream(processSerialNumbers)
                .forEach(processSerialNumber -> documentCopyApi.deleteByProcessSerialNumber(tenantId,
                    Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), processSerialNumber));
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("deleteList error", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 传签件列表
     * 
     * @return Y9Page<DocumentCopyModel>
     */
    @FlowableLog(operationName = "传签件列表")
    @GetMapping(value = "/list4Receive")
    public Y9Page<DocumentCopyModel> list4Receive(QueryParamModel queryParamModel) {
        return documentCopyApi.findByUserId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), queryParamModel);
    }

    /**
     * 传签记录
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<DocumentCopyModel>>
     */
    @FlowableLog(operationName = "传签记录")
    @GetMapping(value = "/list4Sender")
    public Y9Result<List<DocumentCopyModel>> list4Sender(@RequestParam @NotBlank String processSerialNumber) {
        return documentCopyApi.findByProcessSerialNumberAndSenderId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), processSerialNumber);
    }

    /**
     * 传签意见
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<OpinionCopyModel>>
     */
    @FlowableLog(operationName = "传签意见")
    @GetMapping(value = "/opinionCopyList")
    public Y9Result<List<OpinionCopyModel>> opinionCopyList(@RequestParam @NotBlank String processSerialNumber) {
        return opinionCopyApi.findByProcessSerialNumber(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), processSerialNumber);
    }

    /**
     * 保存意见
     *
     * @param jsonData 意见实体json
     * @return Y9Result<OpinionModel>
     */
    @FlowableLog(operationName = "保存传签意见", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<OpinionCopyModel> save(@RequestParam @NotBlank String jsonData) {
        try {
            UserInfo person = Y9LoginUserHolder.getUserInfo();
            String userId = person.getPersonId(), tenantId = person.getTenantId();
            OpinionCopyModel opinion = Y9JsonUtil.readValue(jsonData, OpinionCopyModel.class);
            String positionId = Y9LoginUserHolder.getPositionId();
            return opinionCopyApi.saveOrUpdate(tenantId, userId, positionId, opinion);
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 保存抄送信息
     *
     * @param processSerialNumber 流程编号
     * @param users 收件人
     * @param opinion 传签意见
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "抄送", operationType = FlowableOperationTypeEnum.ADD)
    @PostMapping(value = "/save")
    public Y9Result<Object> save(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String users, @RequestParam(required = false) String opinion) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            Y9Result<Object> y9Result = documentCopyApi.save(Y9LoginUserHolder.getTenantId(), userId,
                Y9LoginUserHolder.getPositionId(), processSerialNumber, users, opinion);
            if (y9Result.isSuccess()) {
                return Y9Result.success("传签成功");
            } else {
                return y9Result;
            }
        } catch (Exception e) {
            LOGGER.error("传签", e);
        }
        return Y9Result.failure("传签失败");
    }

    @FlowableLog(operationName = "撤销传签件", operationType = FlowableOperationTypeEnum.CANCEL)
    @PostMapping(value = "/setStatus")
    public Y9Result<Object> setStatus(@RequestParam @NotBlank String id, @RequestParam Integer status) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String userId = person.getPersonId();
        try {
            Y9Result<Object> y9Result = documentCopyApi.setStatus(Y9LoginUserHolder.getTenantId(), userId,
                Y9LoginUserHolder.getPositionId(), id, status);
            if (y9Result.isSuccess()) {
                return Y9Result.success("取消成功");
            } else {
                return y9Result;
            }
        } catch (Exception e) {
            LOGGER.error("抄送", e);
        }
        return Y9Result.failure("取消失败");
    }
}
