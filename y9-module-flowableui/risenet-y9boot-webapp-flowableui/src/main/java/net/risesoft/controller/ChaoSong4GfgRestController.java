package net.risesoft.controller;

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
import net.risesoft.model.itemadmin.DocumentCopyModel;
import net.risesoft.model.itemadmin.QueryParamModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

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

    /**
     * 批量删除传签件
     *
     * @param processSerialNumbers 传签件流程序列号
     * @return Y9Result<String>
     */
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
     * 传签记录
     *
     * @param processSerialNumber 流程序列号
     * @return Y9Result<List<DocumentCopyModel>>
     */
    @GetMapping(value = "/list4Sender")
    public Y9Result<List<DocumentCopyModel>> list4Sender(@RequestParam @NotBlank String processSerialNumber) {
        return documentCopyApi.findByProcessSerialNumberAndSenderId(Y9LoginUserHolder.getTenantId(),
            Y9LoginUserHolder.getPersonId(), Y9LoginUserHolder.getPositionId(), processSerialNumber);
    }

    /**
     * 传签件列表
     * 
     * @return Y9Page<DocumentCopyModel>
     */
    @GetMapping(value = "/list4Receive")
    public Y9Page<DocumentCopyModel> list4Receive(QueryParamModel queryParamModel) {
        return documentCopyApi.findByUserId(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(),
            Y9LoginUserHolder.getPositionId(), queryParamModel);
    }

    /**
     * 保存抄送信息
     *
     * @param processSerialNumber 流程编号
     * @param users 收件人
     * @param opinion 传签意见
     * @return Y9Result<Object>
     */
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
