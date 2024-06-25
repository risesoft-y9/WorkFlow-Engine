package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.itemadmin.position.Attachment4PositionApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 草稿
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/draft", produces = MediaType.APPLICATION_JSON_VALUE)
public class DraftRestController {

    private final Draft4PositionApi draft4PositionApi;

    private final ItemViewConfApi itemViewConfApi;

    private final Attachment4PositionApi attachment4PositionApi;

    private final TransactionWordApi transactionWordApi;

    private final AssociatedFile4PositionApi associatedFile4PositionApi;

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
        return draft4PositionApi.deleteDraft(tenantId, ids);
    }

    /**
     * 获取草稿列表
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
        return draft4PositionApi.getDraftList(tenantId, positionId, page, rows, title, itemId, false);
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
        return draft4PositionApi.getDraftList(tenantId, Y9LoginUserHolder.getPositionId(), page, rows, title, itemId,
            true);
    }

    /**
     * 获取草稿列表视图配置
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/draftViewConf")
    public Y9Result<List<ItemViewConfModel>> draftViewConf(@RequestParam @NotBlank String itemId) {
        List<ItemViewConfModel> itemViewConfList = itemViewConfApi
            .findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, ItemBoxTypeEnum.DRAFT.getValue());
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 打开草稿,获取草稿信息
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param draftRecycle 草稿标识
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping("/openDraft")
    public Y9Result<Map<String, Object>> openDraft(@RequestParam @NotBlank String processSerialNumber,
        @RequestParam @NotBlank String itemId, @RequestParam(required = false) String draftRecycle) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> map = new HashMap<>(16);
        OpenDataModel model = null;
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(processSerialNumber)) {
            model = draft4PositionApi.openDraft4Position(tenantId, positionId, itemId, processSerialNumber, false)
                .getData();
        }
        String str = Y9JsonUtil.writeValueAsString(model);
        map = Y9JsonUtil.readHashMap(str);
        map.put("currentUser", Y9LoginUserHolder.getPosition().getName());
        map.put("draftRecycle", draftRecycle);
        map.put("tenantId", tenantId);
        map.put("userId", positionId);
        map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
        map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
        map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
        map.put("userName", person.getName());
        Integer fileNum = attachment4PositionApi.fileCounts(tenantId, processSerialNumber).getData();
        int docNum = 0;
        // 是否正文正常
        TransactionWordModel wordMap =
            transactionWordApi.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
        if (wordMap != null && wordMap.getId() != null) {
            docNum = 1;
        }
        int associatedFileNum = associatedFile4PositionApi.countAssociatedFile(tenantId, processSerialNumber).getData();
        map.put("associatedFileNum", associatedFileNum);
        map.put("docNum", docNum);
        map.put("fileNum", fileNum);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 还原草稿
     *
     * @param id 草稿id
     * @return Y9Result<Object>
     */
    @RequestMapping(value = "/reduction", method = RequestMethod.POST)
    public Y9Result<Object> reduction(@RequestParam @NotBlank String id) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draft4PositionApi.reduction(tenantId, id);
    }

    /**
     * 删除草稿
     *
     * @param ids 草稿ids，逗号隔开
     * @return Y9Result<Object>
     */
    @RequestMapping(value = "/removeDraft", method = RequestMethod.POST)
    public Y9Result<Object> removeDraft(@RequestParam @NotBlank String ids) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        return draft4PositionApi.removeDraft(tenantId, ids);
    }

    /**
     * 保存草稿
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processDefinitionKey 流程定义key
     * @param number 文件编号
     * @param level 紧急程度
     * @param title 标题
     * @return Y9Result<Object>
     */
    @RequestMapping(value = "/saveDraft", method = RequestMethod.POST)
    public Y9Result<Object> saveDraft(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String processSerialNumber, @RequestParam @NotBlank String processDefinitionKey,
        @RequestParam(required = false) String number, @RequestParam(required = false) String level,
        @RequestParam(required = false) String title) {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        if (StringUtils.isBlank(title)) {
            title = "未定义标题";
        }
        return draft4PositionApi.saveDraft(tenantId, positionId, itemId, processSerialNumber, processDefinitionKey,
            number, level, title);
    }

}
