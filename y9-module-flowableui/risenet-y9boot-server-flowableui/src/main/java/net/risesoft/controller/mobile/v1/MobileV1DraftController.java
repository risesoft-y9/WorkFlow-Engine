package net.risesoft.controller.mobile.v1;

import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 草稿相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/draft")
public class MobileV1DraftController {

    private final DraftApi draftApi;

    /**
     * 彻底删除草稿
     *
     * @param ids 草稿ids,“,”分隔
     * @return Y9Result<String>
     */
    @RequestMapping(value = "/delDraft")
    public Y9Result<String> delDraft(@RequestParam @NotBlank String ids) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            Y9Result<Object> y9Result = draftApi.deleteDraft(tenantId, ids);
            if (y9Result.isSuccess()) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            LOGGER.error("删除草稿失败", e);
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 回收站计数
     *
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @RequestMapping(value = "/getDeleteDraftCount")
    public Y9Result<Integer> getDeleteDraftCount(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return draftApi.getDeleteDraftCount(tenantId, positionId, itemId);
    }

    /**
     * 草稿列表
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param delFlag 是否删除 true为回收站列表，false为草稿列表
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/getDraft")
    public Y9Page<Map<String, Object>> getDraft(@RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam(required = false) boolean delFlag,
        @RequestParam @NotBlank Integer page, @RequestParam @NotBlank Integer rows) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return draftApi.getDraftList(tenantId, positionId, page, rows, title, itemId, delFlag);
    }

    /**
     * 草稿箱计数
     *
     * @param itemId 事项id
     * @return Y9Result<Integer>
     */
    @RequestMapping(value = "/getDraftCount")
    public Y9Result<Integer> getDraftCount(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return draftApi.getDraftCount(tenantId, positionId, itemId);
    }

    /**
     * 还原草稿
     *
     * @param id 草稿id
     * @return Y9Result<Object>
     */
    @RequestMapping(value = "/reduction")
    public Y9Result<Object> reduction(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.reduction(tenantId, id);
    }

    /**
     * 删除草稿
     *
     * @param ids 草稿ids,“,”分隔
     * @return Y9Result<Object>
     */
    @RequestMapping(value = "/removeDraft")
    public Y9Result<Object> removeDraft(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.removeDraft(tenantId, ids);
    }
}
