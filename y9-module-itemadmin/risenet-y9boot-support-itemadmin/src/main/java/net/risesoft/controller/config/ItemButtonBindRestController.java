package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.button.CommonButton;
import net.risesoft.entity.button.ItemButtonBind;
import net.risesoft.entity.button.SendButton;
import net.risesoft.enums.ItemButtonTypeEnum;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CommonButtonService;
import net.risesoft.service.ItemService;
import net.risesoft.service.SendButtonService;
import net.risesoft.service.config.ItemButtonBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 绑定按钮管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemButtonBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemButtonBindRestController {

    private final ItemButtonBindService itemButtonBindService;

    private final CommonButtonService commonButtonService;

    private final SendButtonService sendButtonService;

    private final ProcessDefinitionApi processDefinitionApi;

    private final ItemService itemService;

    /**
     * 复制按钮配置
     *
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
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
    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemButtonBind>> getBindList(@RequestParam String itemId,
        @RequestParam ItemButtonTypeEnum buttonType, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        List<ItemButtonBind> list =
            itemButtonBindService.listContainRole(itemId, buttonType, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    @GetMapping(value = "/getBindListByButtonId")
    public Y9Result<List<Map<String, Object>>> getBindListByButtonId(@RequestParam String buttonId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemButtonBind> ibbList = itemButtonBindService.listByButtonId(buttonId);
        List<Map<String, Object>> bindList = new ArrayList<>();
        Map<String, Object> map;
        Item item;
        for (ItemButtonBind bind : ibbList) {
            map = new HashMap<>(16);
            map.put("id", bind.getId());
            map.put("processDefinitionId", bind.getProcessDefinitionId());
            map.put("roleNames", bind.getRoleNames());

            item = itemService.findById(bind.getItemId());
            map.put("itemName", null == item ? "事项不存在" : item.getName());

            String taskDefName = "整个流程";
            if (StringUtils.isNotEmpty(bind.getTaskDefKey())) {
                List<TargetModel> list =
                    processDefinitionApi.getNodes(tenantId, bind.getProcessDefinitionId()).getData();
                for (TargetModel targetModel : list) {
                    if (targetModel.getTaskDefKey().equals(bind.getTaskDefKey())) {
                        taskDefName = targetModel.getTaskDefName();
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
     * @param processDefinitionId 流程定义id
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String itemId,
        @RequestParam String processDefinitionId) {
        List<TargetModel> list;
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionApi.getNodes(tenantId, processDefinitionId).getData();
        List<ItemButtonBind> cbList, sbList;
        for (TargetModel targetModel : list) {
            String commonButtonNames = "";
            String sendButtonNames = "";
            cbList = itemButtonBindService.listContainRole(itemId, ItemButtonTypeEnum.COMMON, processDefinitionId,
                targetModel.getTaskDefKey());
            sbList = itemButtonBindService.listContainRole(itemId, ItemButtonTypeEnum.SEND, processDefinitionId,
                targetModel.getTaskDefKey());
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
            targetModel.setCommonButtonNames(commonButtonNames);
            targetModel.setSendButtonNames(sendButtonNames);
        }
        return Y9Result.success(list, "获取成功");
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
    @GetMapping(value = "/getButtonList")
    public Y9Result<Map<String, Object>> getButtonList(@RequestParam String itemId,
        @RequestParam ItemButtonTypeEnum buttonType, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        Map<String, Object> map = new HashMap<>(16);
        List<ItemButtonBind> buttonItemBindList =
            itemButtonBindService.listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(itemId, buttonType,
                processDefinitionId, taskDefKey);
        if (ItemButtonTypeEnum.COMMON == buttonType) {
            List<CommonButton> cbList = commonButtonService.listAll();
            List<CommonButton> cbListTemp = new ArrayList<>();
            if (buttonItemBindList.isEmpty()) {
                cbListTemp = cbList;
            } else {
                for (CommonButton cb : cbList) {
                    boolean isBind = false;
                    for (ItemButtonBind bib : buttonItemBindList) {
                        if (bib.getButtonId().equals(cb.getId())) {
                            isBind = true;
                            break;
                        }
                    }
                    if (!isBind) {
                        cbListTemp.add(cb);
                    }
                }
            }
            map.put("rows", cbListTemp);
        } else {
            List<SendButton> sbList = sendButtonService.listAll();
            List<SendButton> sbListTemp = new ArrayList<>();
            if (buttonItemBindList.isEmpty()) {
                sbListTemp = sbList;
            } else {
                for (SendButton sb : sbList) {
                    boolean isBind = false;
                    for (ItemButtonBind bib : buttonItemBindList) {
                        if (bib.getButtonId().equals(sb.getId())) {
                            isBind = true;
                            break;
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
    @GetMapping(value = "/getButtonOrderList")
    public Y9Result<List<ItemButtonBind>> getButtonOrderList(@RequestParam String itemId,
        @RequestParam ItemButtonTypeEnum buttonType, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey) {
        List<ItemButtonBind> list = itemButtonBindService.listByItemIdAndButtonTypeAndProcessDefinitionIdAndTaskDefKey(
            itemId, buttonType, processDefinitionId, taskDefKey);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 删除按钮绑定
     *
     * @param ids 绑定ids
     * @return
     */
    @PostMapping(value = "/removeBind")
    public Y9Result<String> removeBind(@RequestParam String[] ids) {
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
    @PostMapping(value = "/saveBindButton")
    public Y9Result<String> saveBindButton(@RequestParam String buttonId, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam ItemButtonTypeEnum buttonType,
        @RequestParam(required = false) String taskDefKey) {
        itemButtonBindService.bindButton(itemId, buttonId, processDefinitionId, taskDefKey, buttonType);
        return Y9Result.successMsg("绑定成功");
    }

    /**
     * 保存按钮排序
     *
     * @param idAndTabIndexs 排序id
     * @return
     */
    @PostMapping(value = "/saveOrder")
    public Y9Result<String> saveOrder(@RequestParam String[] idAndTabIndexs) {
        itemButtonBindService.saveOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }
}
