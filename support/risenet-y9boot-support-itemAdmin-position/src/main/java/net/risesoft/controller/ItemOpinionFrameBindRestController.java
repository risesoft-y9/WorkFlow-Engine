package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.entity.ItemOpinionFrameBind;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOpinionFrameBindService;
import net.risesoft.service.ItemOpinionFrameRoleService;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/vue/itemOpinionFrameBind")
public class ItemOpinionFrameBindRestController {

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Autowired
    private ItemOpinionFrameBindService itemOpinionFrameBindService;

    @Autowired
    private ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Autowired
    private SpmApproveItemService spmApproveItemService;

    /**
     * 绑定意见框
     *
     * @param opinionFrameNameAndMarks 意见框标识与名称
     * @param itemId 事项id
     * @param processDefinitionId 流程定义key
     * @param taskDefKey 任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/bindOpinionFrame", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> bindOpinionFrame(@RequestParam(required = true) String opinionFrameNameAndMarks, @RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        itemOpinionFrameBindService.save(opinionFrameNameAndMarks, itemId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 改变是否必签意见
     *
     * @param id
     * @param signOpinion
     * @return
     */
    @RequestMapping("/changeSignOpinion")
    @ResponseBody
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
    @ResponseBody
    @RequestMapping(value = "/copyBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> copyBind(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId) {
        itemOpinionFrameBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取意见框与角色绑定的数据集合
     *
     * @param itemId
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<ItemOpinionFrameBind>> getBindList(@RequestParam(required = true) String itemId, @RequestParam(required = true) String processDefinitionId, @RequestParam(required = false) String taskDefKey) {
        List<ItemOpinionFrameBind> oftrbList = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKeyContainRole(itemId, processDefinitionId, taskDefKey);
        return Y9Result.success(oftrbList, "获取成功");
    }

    @RequestMapping(value = "/getBindListByMark", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<List<Map<String, Object>>> getBindListByMark(@RequestParam(required = true) String mark) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemOpinionFrameBind> oftrbList = itemOpinionFrameBindService.findByMark(mark);
        List<Map<String, Object>> bindList = new ArrayList<>();
        Map<String, Object> map = null;
        SpmApproveItem item = null;
        List<ItemOpinionFrameRole> roleList = new ArrayList<>();
        for (ItemOpinionFrameBind bind : oftrbList) {
            map = new HashMap<>(16);
            map.put("id", bind.getId());

            item = spmApproveItemService.findById(bind.getItemId());
            map.put("itemName", null == item ? "事项不存在" : item.getName());
            map.put("processDefinitionId", bind.getProcessDefinitionId());
            roleList = itemOpinionFrameRoleService.findByItemOpinionFrameIdContainRoleName(bind.getId());
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
                List<Map<String, Object>> list = processDefinitionManager.getNodes(tenantId, bind.getProcessDefinitionId(), false);
                for (Map<String, Object> mapTemp : list) {
                    if (mapTemp.get("taskDefKey").equals(bind.getTaskDefKey())) {
                        taskDefName = (String)mapTemp.get("taskDefName");
                    }
                }
            }
            map.put("taskDefKey", taskDefName + (StringUtils.isEmpty(bind.getTaskDefKey()) ? "" : "(" + bind.getTaskDefKey() + ")"));
            bindList.add(map);
        }
        return Y9Result.success(bindList, "获取成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionKey 流程定义key
     * @param itemId 事项id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam(required = true) String processDefinitionId, @RequestParam(required = true) String itemId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionManager.getNodes(tenantId, processDefinitionId, false);
        List<ItemOpinionFrameBind> bindList = new ArrayList<>();
        for (Map<String, Object> map : list) {
            String opinionFrameNames = "";
            bindList = itemOpinionFrameBindService.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, (String)map.get("taskDefKey"));
            for (ItemOpinionFrameBind bind : bindList) {
                if (StringUtils.isEmpty(opinionFrameNames)) {
                    opinionFrameNames = bind.getOpinionFrameName();
                } else {
                    opinionFrameNames += "、" + bind.getOpinionFrameName();
                }
            }
            map.put("opinionFrameNames", opinionFrameNames);
        }
        resMap.put("rows", list);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 移除意见框
     *
     * @param ids 绑定ids
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> remove(@RequestParam(required = true) String[] ids) {
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
    @RequestMapping(value = "/saveModify", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public Y9Result<String> saveModify(@RequestParam(required = true) String id, @RequestParam(required = true) String opinionFrameNameAndMarks) {
        ItemOpinionFrameBind opinionBind = itemOpinionFrameBindService.findOne(id);
        String[] opinionFrameNameAndMark = opinionFrameNameAndMarks.split(":");
        String name = opinionFrameNameAndMark[0];
        String mark = opinionFrameNameAndMark[1];
        opinionBind.setOpinionFrameMark(mark);
        opinionBind.setOpinionFrameName(name);
        opinionBind.setModifyDate(sdf.format(new Date()));
        itemOpinionFrameBindService.save(opinionBind);
        return Y9Result.successMsg("修改成功");
    }
}
