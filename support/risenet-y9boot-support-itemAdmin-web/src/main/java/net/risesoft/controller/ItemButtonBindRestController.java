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

import net.risesoft.entity.CommonButton;
import net.risesoft.entity.ItemButtonBind;
import net.risesoft.entity.SendButton;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.ItemButtonBindService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.ProcessDefinitionApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/itemButtonBind")
public class ItemButtonBindRestController {

    @Autowired
    private ItemButtonBindService itemButtonBindService;

    @Autowired
    private CommonButtonService commonButtonService;

    @Autowired
    private SendButtonService sendButtonService;

    @Autowired
    private ProcessDefinitionApiClient processDefinitionManager;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    /**
     * 复制按钮配置
     *
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId) {
        itemButtonBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取按钮绑定列表
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemButtonBind>> getBindList(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) Integer buttonType, @RequestParam(required = true) String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        List<ItemButtonBind> list =
            itemButtonBindService.findListContainRole(itemId, buttonType, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    @ResponseBody
    @RequestMapping(value = "/getBindListByButtonId", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getBindListByButtonId(@RequestParam(required = true) String buttonId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemButtonBind> ibbList = itemButtonBindService.findListByButtonId(buttonId);
        List<Map<String, Object>> bindList = new ArrayList<>();
        Map<String, Object> map = null;
        SpmApproveItem item = null;
        for (ItemButtonBind bind : ibbList) {
            map = new HashMap<>(16);
            map.put("id", bind.getId());
            map.put("processDefinitionId", bind.getProcessDefinitionId());
            map.put("roleNames", bind.getRoleNames());

            item = spmApproveItemService.findById(bind.getItemId());
            map.put("itemName", null == item ? "事项不存在" : item.getName());

            String taskDefName = "整个流程";
            if (StringUtils.isNotEmpty(bind.getTaskDefKey())) {
                List<Map<String, Object>> list =
                    processDefinitionManager.getNodes(tenantId, bind.getProcessDefinitionId(), false);
                for (Map<String, Object> mapTemp : list) {
                    if (mapTemp.get("taskDefKey").equals(bind.getTaskDefKey())) {
                        taskDefName = (String)mapTemp.get("taskDefName");
                    }
                }
            }
            map.put("taskDefKey",
                taskDefName + (StringUtils.isEmpty(bind.getTaskDefKey()) ? "" : "(" + bind.getTaskDefKey() + ")"));
            bindList.add(map);
        }
        return Y9Result.success(bindList, "获取成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionKey 流程定义key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        List<ItemButtonBind> cbList = new ArrayList<>();
        List<ItemButtonBind> sbList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String commonButtonNames = "";
            String sendButtonNames = "";
            cbList = itemButtonBindService.findListContainRole(itemId, ItemButtonTypeEnum.COMMON.getValue(),
                processDefinitionId, (String)map.get("taskDefKey"));
            sbList = itemButtonBindService.findListContainRole(itemId, ItemButtonTypeEnum.SEND.getValue(),
                processDefinitionId, (String)map.get("taskDefKey"));
            for (ItemButtonBind cb : cbList) {
                if (StringUtils.isEmpty(commonButtonNames)) {
                    commonButtonNames = cb.getButtonName();
                } else {
                    commonButtonNames += "、" + cb.getButtonName();
                }
            }
            for (ItemButtonBind sb : sbList) {
                if (StringUtils.isEmpty(sendButtonNames)) {
                    sendButtonNames = sb.getButtonName();
                } else {
                    sendButtonNames += "、" + sb.getButtonName();
                }
            }
            map.put("commonButtonNames", commonButtonNames);
            map.put("sendButtonNames", sendButtonNames);
        }
        resMap.put("rows", list);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取按钮列表
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getButtonList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getButtonList(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) Integer buttonType, @RequestParam(required = true) String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        List<ItemButtonBind> buttonItemBindList =
            itemButtonBindService.findList(itemId, buttonType, processDefinitionId, taskDefKey);
        if (1 == buttonType) {
            List<CommonButton> cbList = commonButtonService.findAll();
            List<CommonButton> cbListTemp = new ArrayList<>();
            if (buttonItemBindList.isEmpty()) {
                cbListTemp = cbList;
            } else {
                for (CommonButton cb : cbList) {
                    Boolean isBind = false;
                    for (ItemButtonBind bib : buttonItemBindList) {
                        if (bib.getButtonId().equals(cb.getId())) {
                            isBind = true;
                            continue;
                        }
                    }
                    if (!isBind) {
                        cbListTemp.add(cb);
                    }
                }
            }
            map.put("rows", cbListTemp);
        } else {
            List<SendButton> sbList = sendButtonService.findAll();
            List<SendButton> sbListTemp = new ArrayList<>();
            if (buttonItemBindList.isEmpty()) {
                sbListTemp = sbList;
            } else {
                for (SendButton sb : sbList) {
                    Boolean isBind = false;
                    for (ItemButtonBind bib : buttonItemBindList) {
                        if (bib.getButtonId().equals(sb.getId())) {
                            isBind = true;
                            continue;
                        }
                    }
                    if (!isBind) {
                        sbListTemp.add(sb);
                    }
                }
            }
            map.put("rows", sbListTemp);
        }
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取按钮排序列表
     *
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getButtonOrderList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemButtonBind>> getButtonOrderList(@RequestParam(required = true) String itemId,
        @RequestParam(required = true) Integer buttonType, @RequestParam(required = true) String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        List<ItemButtonBind> list = itemButtonBindService.findList(itemId, buttonType, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除按钮绑定
     *
     * @param buttonItemBindIds 绑定ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/removeBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeBind(@RequestParam(required = true) String[] ids) {
        itemButtonBindService.removeButtonItemBinds(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存绑定按钮
     *
     * @param buttonId 按钮id
     * @param itemId 事项id
     * @param buttonType 按钮类型
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveBindButton", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBindButton(@RequestParam(required = true) String buttonId,
        @RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId,
        @RequestParam(required = true) Integer buttonType, @RequestParam(required = false) String taskDefKey) {
        itemButtonBindService.bindButton(itemId, buttonId, processDefinitionId, taskDefKey, buttonType);
        return Y9Result.successMsg("绑定成功");
    }

    /**
     * 保存按钮排序
     *
     * @param idAndTabIndexs 排序id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrder", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrder(@RequestParam(required = true) String[] idAndTabIndexs) {
        itemButtonBindService.saveOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}
