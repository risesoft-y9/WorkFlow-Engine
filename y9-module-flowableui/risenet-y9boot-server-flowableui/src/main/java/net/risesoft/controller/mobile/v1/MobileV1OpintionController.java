package net.risesoft.controller.mobile.v1;

import java.util.List;

import jakarta.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CommonSentencesApi;
import net.risesoft.api.itemadmin.OpinionApi;
import net.risesoft.model.itemadmin.CommonSentencesModel;
import net.risesoft.model.itemadmin.OpinionListModel;
import net.risesoft.model.itemadmin.OpinionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 意见相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/opintion")
public class MobileV1OpintionController {

    private final CommonSentencesApi commonSentencesApi;

    private final OpinionApi opinionApi;

    /**
     * 保存意见
     *
     * @param formJsonData 意见json内容
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/comment/save")
    public Y9Result<String> addComment(@RequestParam @NotBlank String formJsonData) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            String userId = Y9LoginUserHolder.getPersonId();
            OpinionModel opinionModel = Y9JsonUtil.readValue(formJsonData, OpinionModel.class);
            if (opinionModel != null) {
                opinionModel.setTenantId(tenantId + ":mobile");
            }
            opinionApi.saveOrUpdate(tenantId, userId, positionId, opinionModel);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存意见失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 是否已填写意见
     *
     * @param taskId 任务id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Boolean>
     */
    @RequestMapping(value = "/comment/checkSignOpinion")
    public Y9Result<Boolean> checkSignOpinion(@RequestParam(required = false) String taskId,
        @RequestParam @NotBlank String processSerialNumber) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String userId = Y9LoginUserHolder.getPersonId();
            boolean b = opinionApi.checkSignOpinion(tenantId, userId, processSerialNumber, taskId).getData();
            return Y9Result.success(b, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取意见失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 删除意见
     *
     * @param id 意见id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/comment/delete")
    public Y9Result<String> deleteComment(@RequestParam @NotBlank String id) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            opinionApi.delete(tenantId, id);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除意见失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取意见
     *
     * @param processSerialNumber 流程编号
     * @param taskId 任务id
     * @param itembox 办件状态，待办：todo,在办：doing,办结：done
     * @param opinionFrameMark 意见框标识
     * @param itemId 事项id
     * @param taskDefinitionKey 任务key
     * @param activitiUser 当前任务受让人
     * @return Y9Result<List < OpinionListModel>>
     */
    @RequestMapping(value = "/personCommentList")
    public Y9Result<List<OpinionListModel>> personCommentList(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam(required = false) String taskId, @RequestParam(required = false) String itembox,
        @RequestParam @NotBlank String opinionFrameMark, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String taskDefinitionKey, @RequestParam(required = false) String activitiUser,
        @RequestParam(required = false) String orderByUser) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return opinionApi.personCommentList(tenantId, userId, processSerialNumber, taskId, itembox, opinionFrameMark,
            itemId, taskDefinitionKey, activitiUser, orderByUser);
    }

    /**
     * 获取个人常用语
     *
     * @return Y9Result<List < CommonSentencesModel>>
     */
    @RequestMapping(value = "/personalSetup")
    public Y9Result<List<CommonSentencesModel>> personalSetup() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return commonSentencesApi.listSentencesService(tenantId, userId);
    }

    /**
     * 删除常用语
     *
     * @param id 常用语id
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/removeCommonSentences")
    public Y9Result<String> removeCommonSentences(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            commonSentencesApi.delete(tenantId, id);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除常用语失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 保存常用语
     *
     * @param content 内容
     * @param id 常用语id,新增id为空
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/saveCommonSentences")
    public Y9Result<String> saveCommonSentences(@RequestParam String content,
        @RequestParam(required = false) String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        try {
            commonSentencesApi.save(tenantId, userId, id, content);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存常用语失败", e);
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 获取个人常用语
     *
     * @return Y9Result<List < CommonSentencesModel>>
     */
    @RequestMapping(value = "/systemSetup")
    public Y9Result<List<CommonSentencesModel>> systemSetup() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return commonSentencesApi.listSentencesService(tenantId, userId);
    }
}
