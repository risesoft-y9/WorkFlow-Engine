package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.AssociatedFileApi;
import net.risesoft.api.itemadmin.AttachmentApi;
import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.api.itemadmin.TransactionWordApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.ItemBoxTypeEnum;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.model.itemadmin.TransactionWordModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.Y9Properties;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/draft")
public class DraftRestController {

    @Autowired
    private DraftApi draftManager;

    @Autowired
    private ItemViewConfApi itemViewConfManager;

    @Autowired
    private AttachmentApi attachmentManager;

    @Autowired
    private TransactionWordApi transactionWordManager;

    @Autowired
    private AssociatedFileApi associatedFileManager;

    @Autowired
    private Y9Properties y9Config;

    /**
     * 彻底删除草稿
     *
     * @param ids 草稿ids，逗号隔开
     * @return
     */
    @RequestMapping(value = "/deleteDraft", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> deleteDraft(@RequestParam(required = true) String ids) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = draftManager.deleteDraft(tenantId, userId, ids);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
    }

    /**
     * 获取草稿列表
     *
     * @param page 页码
     * @param rows 条数
     * @param itemId 事项id
     * @param title 搜索词
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/draftList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> draftList(@RequestParam(required = true) int page,
        @RequestParam(required = true) int rows, @RequestParam(required = true) String itemId,
        @RequestParam(required = false) String title) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = draftManager.getDraftList(tenantId, userId, page, rows, title, itemId, false);
        List<Map<String, Object>> draftList = (List<Map<String, Object>>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()),
            Integer.parseInt(map.get("total").toString()), draftList, "获取列表成功");
    }

    /**
     * 获取草稿回收站列表
     *
     * @param page 页码
     * @param rows 条数
     * @param itemId 事项id
     * @param title 搜索词
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/draftRecycleList", method = RequestMethod.GET, produces = "application/json")
    public Y9Page<Map<String, Object>> draftRecycleList(@RequestParam(required = true) int page,
        @RequestParam(required = true) int rows, @RequestParam(required = true) String itemId,
        @RequestParam(required = false) String title) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = draftManager.getDraftList(tenantId, userId, page, rows, title, itemId, true);
        List<Map<String, Object>> draftList = (List<Map<String, Object>>)map.get("rows");
        return Y9Page.success(page, Integer.parseInt(map.get("totalpage").toString()),
            Integer.parseInt(map.get("total").toString()), draftList, "获取列表成功");
    }

    /**
     * 获取草稿列表视图配置
     *
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/draftViewConf", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemViewConfModel>> draftViewConf(@RequestParam(required = true) String itemId) {
        List<ItemViewConfModel> itemViewConfList = new ArrayList<>();
        itemViewConfList = itemViewConfManager.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId,
            ItemBoxTypeEnum.DRAFT.getValue());
        return Y9Result.success(itemViewConfList, "获取成功");
    }

    /**
     * 打开草稿,获取草稿信息
     *
     * @param processSerialNumber 流程编号
     * @param itemId 事项id
     * @param draftRecycle 草稿标识
     * @return
     */
    @RequestMapping("/openDraft")
    public Y9Result<Map<String, Object>> openDraft(@RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String itemId, @RequestParam(required = false) String draftRecycle) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        if (StringUtils.isNotBlank(itemId) && StringUtils.isNotBlank(processSerialNumber)) {
            map = draftManager.openDraft(tenantId, userId, itemId, processSerialNumber, false);
        }
        map.put("currentUser", userInfo.getName());
        map.put("draftRecycle", draftRecycle);
        map.put("tenantId", tenantId);
        map.put("userId", userId);
        map.put("itemAdminBaseURL", y9Config.getCommon().getItemAdminBaseUrl());
        map.put("jodconverterURL", y9Config.getCommon().getJodconverterBaseUrl());
        map.put("flowableUIBaseURL", y9Config.getCommon().getFlowableBaseUrl());
        map.put("userName", Y9LoginUserHolder.getUserInfo().getName());
        Integer fileNum = attachmentManager.fileCounts(tenantId, userId, processSerialNumber);
        int docNum = 0;
        // 是否正文正常
        TransactionWordModel wordMap =
            transactionWordManager.findWordByProcessSerialNumber(tenantId, processSerialNumber).getData();
        if (wordMap != null && wordMap.getId() != null) {
            docNum = 1;
        }
        int associatedFileNum = associatedFileManager.countAssociatedFile(tenantId, processSerialNumber);
        map.put("associatedFileNum", associatedFileNum);
        map.put("docNum", docNum);
        map.put("fileNum", fileNum);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 还原草稿
     *
     * @param id 草稿id
     * @return
     */
    @RequestMapping(value = "/reduction", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> reduction(@RequestParam(required = true) String id) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = draftManager.reduction(tenantId, userId, id);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("还原成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("还原失败");
    }

    /**
     * 删除草稿
     *
     * @param ids 草稿ids，逗号隔开
     * @return
     */
    @RequestMapping(value = "/removeDraft", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> removeDraft(@RequestParam(required = true) String ids) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        try {
            Map<String, Object> map = draftManager.removeDraft(tenantId, userId, ids);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("删除成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("删除失败");
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
     * @return
     */
    @RequestMapping(value = "/saveDraft", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveDraft(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processSerialNumber,
        @RequestParam(required = true) String processDefinitionKey, @RequestParam(required = false) String number,
        @RequestParam(required = false) String level, @RequestParam(required = false) String title) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        if (StringUtils.isBlank(title)) {
            title = "未定义标题";
        }
        try {
            Map<String, Object> map = draftManager.saveDraft(tenantId, userId, itemId, processSerialNumber,
                processDefinitionKey, number, level, title);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

}
