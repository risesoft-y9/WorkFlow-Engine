package net.risesoft.controller.config;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.entity.OpinionFrameOneClickSet;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.OpinionFrameOneClickSetService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.config.ItemOpinionFrameBindService;
import net.risesoft.service.config.ItemOpinionFrameRoleService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@Validated
@Slf4j
@RequestMapping(value = "/vue/itemOpinionFrameBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemOpinionFrameBindRestController {

    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final ItemOpinionFrameBindService itemOpinionFrameBindService;

    private final ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    private final ProcessDefinitionApi processDefinitionManager;

    private final SpmApproveItemService spmApproveItemService;

    private final OpinionFrameOneClickSetService opinionFrameOneClickSetService;

    /**
     * 绑定意见框
     *
     * @param opinionFrameNameAndMarks 意见框标识与名称
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @return
     */
    @PostMapping(value = "/bindOpinionFrame")
    public Y9Result<String> bindOpinionFrame(@RequestParam String opinionFrameNameAndMarks, @RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemOpinionFrameBindService.save(opinionFrameNameAndMarks, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 改变是否必签意见
     *
     * @param id 绑定id
     * @param signOpinion 是否必签意见
     * @return
     */
    @RequestMapping("/changeSignOpinion")
    public Y9Result<String> changeSignOpinion(String id, boolean signOpinion) {
        itemOpinionFrameBindService.changeSignOpinion(id, signOpinion);
        return Y9Result.successMsg("操作成功");
    }

    /**
     * 复制上一版本意见框绑定
     *
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemOpinionFrameBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 删除一键设置及动作
     *
     * @param id
     * @return
     */
    @PostMapping(value = "/delOneClickSet")
    public Y9Result<String> delOneClickSet(String id) {
        try {
            opinionFrameOneClickSetService.delete(id);
        } catch (Exception e) {
            LOGGER.error("删除一键设置失败", e);
            Y9Result.failure("删除一键设置失败");
        }
        return Y9Result.successMsg("删除一键设置成功");
    }

    /**
     * 获取意见框与角色绑定的数据集合
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 任务节点key
     * @return
     */
    @GetMapping(value = "/getBindList")
    public Y9Result<List<ItemOpinionFrameBind>> getBindList(@RequestParam String itemId,
        @RequestParam String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOpinionFrameBind> oftrbList = itemOpinionFrameBindService
            .listByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(itemId, processDefinitionId, taskDefKey);
        return Y9Result.success(oftrbList, "获取成功");
    }

    @GetMapping(value = "/getBindListByMark")
    public Y9Result<List<Map<String, Object>>> getBindListByMark(@RequestParam String mark) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemOpinionFrameBind> oftrbList = itemOpinionFrameBindService.listByMark(mark);
        List<Map<String, Object>> bindList = new ArrayList<>();
        Map<String, Object> map;
        SpmApproveItem item;
        List<ItemOpinionFrameRole> roleList;
        for (ItemOpinionFrameBind bind : oftrbList) {
            map = new HashMap<>(16);
            map.put("id", bind.getId());

            item = spmApproveItemService.findById(bind.getItemId());
            map.put("itemName", null == item ? "事项不存在" : item.getName());
            map.put("processDefinitionId", bind.getProcessDefinitionId());
            roleList = itemOpinionFrameRoleService.listByItemOpinionFrameIdContainRoleName(bind.getId());
            String roleNames = "";
            for (ItemOpinionFrameRole role : roleList) {
                if (StringUtils.isEmpty(roleNames)) {
                    roleNames = role.getRoleName();
                } else {
                    roleNames += "、" + role.getRoleName();
                }
            }
            map.put("roleNames", StringUtils.isEmpty(roleNames) ? "未绑定角色（所有人都可以签写）" : roleNames);
            String taskDefName = "整个流程";
            if (StringUtils.isNotEmpty(bind.getTaskDefKey())) {
                List<TargetModel> list =
                    processDefinitionManager.getNodes(tenantId, bind.getProcessDefinitionId(), false).getData();
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
     * @param processDefinitionId 流程定义ID
     * @param itemId 事项id
     * @return
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<TargetModel>> getBpmList(@RequestParam String processDefinitionId,
        @RequestParam String itemId) {
        List<TargetModel> list;
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false).getData();
        for (TargetModel targetModel : list) {
            StringBuilder opinionFrameNames = new StringBuilder();
            List<ItemOpinionFrameBind> bindList =
                itemOpinionFrameBindService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                    targetModel.getTaskDefKey());
            for (ItemOpinionFrameBind bind : bindList) {
                if (StringUtils.isEmpty(opinionFrameNames)) {
                    opinionFrameNames.append(bind.getOpinionFrameName());
                } else {
                    opinionFrameNames.append("、" + bind.getOpinionFrameName());
                }
            }
            targetModel.setOpinionFrameNames(opinionFrameNames.toString());
        }
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取意见框绑定的一键设置列表
     *
     * @param bindId
     * @return
     */
    @GetMapping(value = "/getOneClickSetBindList")
    public Y9Result<List<OpinionFrameOneClickSet>>
        getOneClickSetBindList(@RequestParam(required = true) String bindId) {
        List<OpinionFrameOneClickSet> bindList = new ArrayList<>();
        try {
            bindList = opinionFrameOneClickSetService.findByBindId(bindId);
        } catch (Exception e) {
            LOGGER.error("获取意见框绑定的一键设置列表失败", e);
            Y9Result.failure("获取意见框绑定的一键设置列表失败");
        }
        return Y9Result.success(bindList, "获取意见框绑定的一键设置列表成功");
    }

    /**
     * 移除意见框
     *
     * @param ids 绑定ids
     * @return
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        itemOpinionFrameBindService.delete(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存修改
     *
     * @param id 绑定id
     * @param opinionFrameNameAndMarks 意见框标识与名称
     * @return
     */
    @PostMapping(value = "/saveModify")
    public Y9Result<String> saveModify(@RequestParam String id, @RequestParam String opinionFrameNameAndMarks) {
        ItemOpinionFrameBind opinionBind = itemOpinionFrameBindService.getById(id);
        String[] opinionFrameNameAndMark = opinionFrameNameAndMarks.split(":");
        String name = opinionFrameNameAndMark[0];
        String mark = opinionFrameNameAndMark[1];
        opinionBind.setOpinionFrameMark(mark);
        opinionBind.setOpinionFrameName(name);
        opinionBind.setModifyDate(sdf.format(new Date()));
        itemOpinionFrameBindService.save(opinionBind);
        return Y9Result.successMsg("修改成功");
    }

    /**
     * 保存一键设置数据
     *
     * @param opinionFrameOneClickSet
     * @return
     */
    @PostMapping(value = "/saveOneClickSet")
    public Y9Result<Map<String, Object>> saveOneClickSet1(@Validated OpinionFrameOneClickSet opinionFrameOneClickSet) {
        Map<String, Object> map = new HashMap<>();
        try {
            map = opinionFrameOneClickSetService.save(opinionFrameOneClickSet);
        } catch (Exception e) {
            LOGGER.error("一键设置失败", e);
            Y9Result.failure("一键设置失败");
        }
        return Y9Result.success(map, map.get("msg").toString());
    }
}
