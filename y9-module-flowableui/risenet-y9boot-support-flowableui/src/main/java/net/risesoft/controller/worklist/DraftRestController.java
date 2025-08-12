package net.risesoft.controller.worklist;

import java.util.List;
import java.util.Map;

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

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.view.ItemViewConfApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * 草稿箱
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/draft", produces = MediaType.APPLICATION_JSON_VALUE)
public class DraftRestController {

    private final DraftApi draftApi;

    private final ItemViewConfApi itemViewConfApi;

    private final AttachmentApi attachmentApi;

    private final TransactionWordApi transactionWordApi;

    private final AssociatedFileApi associatedFileApi;

    private final Y9Properties y9Config;

    /**
     * 彻底删除草稿
     *
     * @param ids 草稿ids，逗号隔开
     * @return Y9Result<String>
     */
    @PostMapping(value = "/deleteDraft")
    public Y9Result<Object> deleteDraft(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.deleteDraft(tenantId, ids);
    }

    /**
     * 获取草稿箱列表
     *
     * @param page 页码
     * @param rows 条数
     * @param itemId 事项id
     * @param title 搜索词
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/draftList")
    public Y9Page<Map<String, Object>> draftList(@RequestParam int page, @RequestParam int rows,
        @RequestParam @NotBlank String itemId, @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        return draftApi.getDraftList(tenantId, positionId, page, rows, title, itemId, false);
    }

    /**
     * 获取草稿回收站列表
     *
     * @param page 页码
     * @param rows 条数
     * @param itemId 事项id
     * @param title 搜索词
     * @return Y9Page<Map < String, Object>>
     */
    @GetMapping(value = "/draftRecycleList")
    public Y9Page<Map<String, Object>> draftRecycleList(@RequestParam int page, @RequestParam int rows,
        @RequestParam @NotBlank String itemId, @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.getDraftList(tenantId, Y9LoginUserHolder.getPositionId(), page, rows, title, itemId, true);
    }

    /**
     * 获取草稿列表视图配置信息
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/draftViewConf")
    public Y9Result<List<ItemViewConfModel>> draftViewConf(@RequestParam @NotBlank String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DRAFT.getValue())
            .getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 还原草稿
     *
     * @param id 草稿id
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/reduction")
    public Y9Result<Object> reduction(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.reduction(tenantId, id);
    }

    /**
     * 批量删除草稿
     *
     * @param ids 草稿ids，逗号隔开
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/removeDraft")
    public Y9Result<Object> removeDraft(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draftApi.removeDraft(tenantId, ids);
    }

    /**
     * 保存草稿信息
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 文件编号
     * @param level 紧急程度
     * @param title 标题
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveDraft")
    public Y9Result<Object> saveDraft(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam(required = false) String number, @RequestParam(required = false) String level,
        @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        if (StringUtils.isBlank(title)) {
            title = "未定义标题";
        }
        return draftApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey, number,
            level, title);
    }

}
