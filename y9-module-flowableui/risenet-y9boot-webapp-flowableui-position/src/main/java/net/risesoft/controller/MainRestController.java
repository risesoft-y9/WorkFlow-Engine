package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.Draft4PositionApi;
import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 事项，统计相关
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/vue/mian")
public class MainRestController {

    private final Item4PositionApi item4PositionApi;

    private final HistoricProcessApi historicProcessApi;

    private final PositionRoleApi positionRoleApi;

    private final TaskApi taskApi;

    private final PositionApi positionApi;

    private final ProcessTodoApi processTodoApi;

    private final Draft4PositionApi draft4PositionApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final TodoTaskApi todotaskApi;

    private final ProcessParamApi processParamApi;

    private final Entrust4PositionApi entrust4PositionApi;

    /**
     * 获取所有事项
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/geAllItemList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> geAllItemList() {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemModel> list = item4PositionApi.getAllItemList(tenantId);
        boolean b = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", null, "监控管理员角色", Y9LoginUserHolder.getPositionId())
            .getData();
        map.put("monitorManage", b);
        map.put("itemList", list);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 根据事项id获取事项统计
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getCount4Item")
    public Y9Result<Map<String, Object>> getCount4Item(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        Map<String, Object> map = new HashMap<>(16);
        int draftCount = 0;
        long todoCount = 0;
        long doingCount = 0;
        long doneCount = 0;
        long draftRecycleCount = 0;

        long monitorDoing = 0;
        long monitorDone = 0;
        long recycleCount = 0;

        map.put("draftCount", draftCount);
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        map.put("doneCount", doneCount);
        map.put("draftRecycleCount", draftRecycleCount);

        map.put("monitorDoing", monitorDoing);
        map.put("monitorDone", monitorDone);
        map.put("monitorRecycle", recycleCount);
        try {
            ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = itemModel.getWorkflowGuid();
            if (itemModel.getId() != null) {
                map.put("processDefinitionKey", processDefinitionKey);
                draftCount = draft4PositionApi.getDraftCount(tenantId, positionId, itemId).getData();
                draftRecycleCount = draft4PositionApi.getDeleteDraftCount(tenantId, positionId, itemId).getData();
                Map<String, Object> countMap =
                    processTodoApi.getCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
                todoCount = countMap != null ? Long.parseLong(countMap.get("todoCount").toString()) : 0;
                doingCount = countMap != null ? Long.parseLong(countMap.get("doingCount").toString()) : 0;
                try {
                    doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, itemId);
                } catch (Exception e) {
                    LOGGER.error("获取事项统计失败", e);
                }
            }
            map.put("draftCount", draftCount);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            map.put("draftRecycleCount", draftRecycleCount);

            if (person.isGlobalManager()) {
                try {
                    monitorDoing = officeDoneInfo4PositionApi.countDoingByItemId(tenantId, itemId);
                    monitorDone = officeDoneInfo4PositionApi.countByItemId(tenantId, itemId);
                } catch (Exception e) {
                    LOGGER.error("获取事项统计失败", e);
                }
                // recycleCount = monitorManager.getRecycleCountByProcessDefinitionKey(tenantId,
                // processDefinitionKey);
                map.put("monitorDoing", monitorDoing);
                map.put("monitorDone", monitorDone);
                map.put("monitorRecycle", recycleCount);
            }
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项统计失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 根据系统名称获取事项统计
     *
     * @param systemName 系统名称
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getCount4SystemName")
    public Y9Result<Map<String, Object>> getCount4SystemName(@RequestParam @NotBlank String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        int draftCount = 0;
        long todoCount = 0;
        long doingCount = 0;
        long doneCount = 0;
        long draftRecycleCount = 0;

        long monitorDoing = 0;
        long monitorDone = 0;

        map.put("draftCount", draftCount);
        map.put("todoCount", todoCount);
        map.put("doingCount", doingCount);
        map.put("doneCount", doneCount);
        map.put("draftRecycleCount", draftRecycleCount);

        map.put("monitorDoing", monitorDoing);
        map.put("monitorDone", monitorDone);
        try {
            draftCount = draft4PositionApi.countBySystemName(tenantId, positionId, systemName).getData();
            // draftRecycleCount = draft4PositionApi.getDeleteDraftCount(tenantId, positionId, systemName);
            Map<String, Object> countMap =
                processTodoApi.getCountByUserIdAndSystemName(tenantId, positionId, systemName);
            todoCount = countMap != null ? Long.parseLong(countMap.get("todoCount").toString()) : 0;
            doingCount = countMap != null ? Long.parseLong(countMap.get("doingCount").toString()) : 0;
            try {
                doneCount = officeDoneInfo4PositionApi.countByPositionIdAndSystemName(tenantId, positionId, systemName);
            } catch (Exception e) {
                LOGGER.error("获取事项统计失败", e);
            }
            map.put("draftCount", draftCount);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            map.put("draftRecycleCount", draftRecycleCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项统计失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getItem", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getItem(@RequestParam @NotBlank String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("tenantManager", person.isGlobalManager());
        ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
        map.put("itemModel", itemModel);
        map.put("tenantId", tenantId);
        boolean b = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put("monitorManage", b);
        boolean b1 = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "重定向角色", Y9LoginUserHolder.getPositionId()).getData();
        map.put("repositionrManage", b1);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 根据系统名称获取事项
     *
     * @param systemName 系统名称
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getItemBySystemName", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getItemBySystemName(@RequestParam @NotBlank String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemModel> itemList = item4PositionApi.findAll(tenantId, systemName);
        Map<String, Object> map = new HashMap<>(16);
        boolean b = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
        map.put("monitorManage", b);
        boolean b1 = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "重定向角色", Y9LoginUserHolder.getPositionId()).getData();
        map.put("repositionrManage", b1);
        boolean b2 = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "发文角色", Y9LoginUserHolder.getPositionId()).getData();
        map.put("fawenManage", b2);
        boolean b3 = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "收文角色", Y9LoginUserHolder.getPositionId()).getData();
        map.put("shouwenManage", b3);
        map.put("itemList", itemList);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取个人所有件统计
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getMyCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getMyCount() {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        int todoCount;
        long doingCount;
        int doneCount;
        try {
            // 统计统一待办
            todoCount = todotaskApi.countByReceiverId(tenantId, positionId);
            // 统计流程在办件
            Map<String, Object> m = officeDoneInfo4PositionApi.searchAllByPositionId(tenantId, positionId, "", "", "",
                "todo", "", "", "", 1, 1);
            doingCount = Long.parseLong(m.get("total").toString());
            // 统计历史办结件
            doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, "");
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项统计失败", e);
        }
        return Y9Result.failure(50000, "获取失败");
    }

    /**
     * 获取个人所有岗位
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getPositionList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getPositionList(@RequestParam(required = false) String count,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> resMap = new HashMap<>(16);
        List<Map<String, Object>> resList = new ArrayList<>();
        List<Position> list = positionApi.listByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
        long allCount = 0;
        for (Position p : list) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", p.getId());
            map.put("name", p.getName());
            long todoCount = 0;
            if (StringUtils.isNotBlank(count)) {// 是否统计待办数量
                if (StringUtils.isNotBlank(itemId)) {// 单个事项获取待办数量
                    ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
                    todoCount = processTodoApi.getTodoCountByUserIdAndProcessDefinitionKey(tenantId, p.getId(),
                        itemModel.getWorkflowGuid());
                    allCount = allCount + todoCount;
                } else if (StringUtils.isNotBlank(systemName)) {// 单个事项获取待办数量
                    todoCount = processTodoApi.getTodoCountByUserIdAndSystemName(tenantId, p.getId(), systemName);
                    allCount = allCount + todoCount;
                } else {// 工作台获取所有待办数量
                    try {
                        todoCount = todotaskApi.countByReceiverId(tenantId, p.getId());
                        allCount = allCount + todoCount;
                    } catch (Exception e) {
                        LOGGER.error("获取待办数量失败", e);
                    }
                }
            }
            map.put("todoCount", todoCount);
            resList.add(map);

            // 获取当前岗被委托记录
            List<EntrustModel> list1 = entrust4PositionApi.getMyEntrustList(tenantId, p.getId()).getData();
            for (EntrustModel model : list1) {
                if (model.getUsed().equals(1)) {// 使用中的委托，将委托岗位加入岗位列表
                    Map<String, Object> map1 = new HashMap<>(16);
                    String positionId = model.getOwnerId();
                    Position position = positionApi.get(tenantId, positionId).getData();
                    if (position != null) {
                        map1.put("id", position.getId());
                        map1.put("name", position.getName());
                        long todoCount1 = 0;
                        if (StringUtils.isNotBlank(count)) {// 是否统计待办数量
                            if (StringUtils.isNotBlank(itemId)) {// 单个事项获取待办数量
                                ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
                                todoCount1 = processTodoApi.getTodoCountByUserIdAndProcessDefinitionKey(tenantId,
                                    position.getId(), itemModel.getWorkflowGuid());
                                allCount = allCount + todoCount1;
                            } else if (StringUtils.isNotBlank(systemName)) {// 单个事项获取待办数量
                                todoCount1 = processTodoApi.getTodoCountByUserIdAndSystemName(tenantId,
                                    position.getId(), systemName);
                                allCount = allCount + todoCount1;
                            } else {// 工作台获取所有待办数量
                                try {
                                    todoCount1 = todotaskApi.countByReceiverId(tenantId, position.getId());
                                    allCount = allCount + todoCount1;
                                } catch (Exception e) {
                                    LOGGER.error("获取待办数量失败", e);
                                }
                            }
                        }
                        map1.put("todoCount", todoCount1);
                        resList.add(map1);
                    }
                }
            }
        }
        resMap.put("allCount", allCount);
        resMap.put("positionList", resList);
        return Y9Result.success(resMap, "获取成功");
    }

    /**
     * 获取阅件左侧菜单数字
     *
     * @return Y9Result<Map < String, Object>>
     */

    @RequestMapping(value = "/getReadCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getReadCount() {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("notReadCount", chaoSong4PositionApi.getTodoCount(tenantId, positionId).getData());
        map.put("hasReadCount", chaoSong4PositionApi.getDoneCount(tenantId, positionId).getData());
        map.put("hasOpinionCount", chaoSong4PositionApi.getDone4OpinionCountByUserId(tenantId, positionId).getData());
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取角色权限
     *
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getRole", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getRole() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("tenantManager", person.isGlobalManager());
        boolean b = positionRoleApi
            .hasRole(tenantId, "Y9OrgHierarchyManagement", "", "监控管理员角色", Y9LoginUserHolder.getPositionId()).getData();
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put("monitorManage", b);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取流程任务信息
     *
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param type 类型
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getTaskOrProcessInfo", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTaskOrProcessInfo(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processInstanceId, @RequestParam @NotBlank String type) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String processSerialNumber = "";
        try {
            switch (type) {
                case "fromTodo":
                    try {
                        TaskModel taskModel = taskApi.findById(tenantId, taskId);
                        if (taskModel == null || taskModel.getId() == null) {
                            try {
                                todotaskApi.deleteTodoTaskByTaskId(tenantId, taskId);
                            } catch (Exception e) {
                                LOGGER.error("删除待办任务失败", e);
                            }
                            map.put("taskId", "");
                        }
                        if (taskModel != null) {
                            processInstanceId = taskModel.getProcessInstanceId();
                        }
                        ProcessParamModel processParamModel =
                            processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                        String itemId = processParamModel.getItemId();
                        ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
                        map.put("itemModel", itemModel);
                        processSerialNumber = processParamModel.getProcessSerialNumber();
                    } catch (Exception e) {
                        LOGGER.error("获取待办任务信息失败", e);
                    }
                    break;
                case "fromCplane": {
                    taskId = "";// 等于空为办结件

                    HistoricProcessInstanceModel hisProcess = historicProcessApi.getById(tenantId, processInstanceId);
                    ProcessParamModel processParamModel =
                        processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                    processSerialNumber = processParamModel.getProcessSerialNumber();
                    String itemId = processParamModel.getItemId();
                    ItemModel itemModel = item4PositionApi.getByItemId(tenantId, itemId);
                    map.put("itemModel", itemModel);
                    if (hisProcess == null || hisProcess.getId() == null) {// 办结件
                        // todotaskApi.deleteTodoTaskByTaskId(tenantId, taskId);
                        // model.addAttribute("type", "");
                    } else {
                        if (hisProcess.getEndTime() == null) {// 协作状态未办结
                            List<TaskModel> list = taskApi.findByProcessInstanceId(tenantId, processInstanceId);
                            boolean isTodo = false;
                            if (list != null) {
                                for (TaskModel task : list) {
                                    if ((task.getAssignee() != null
                                        && task.getAssignee().contains(Y9LoginUserHolder.getPositionId()))) {// 待办件
                                        taskId = task.getId();
                                        isTodo = true;
                                        break;
                                    }
                                }
                                if (!isTodo) {// 在办件
                                    taskId = list.get(0).getId();
                                }
                            }
                            map.put("isTodo", isTodo);
                        }
                    }
                    map.put("taskId", taskId);
                    break;
                }
                case "fromHistory": {
                    HistoricProcessInstanceModel processModel = historicProcessApi.getById(tenantId, processInstanceId);
                    if (processModel == null || processModel.getId() == null) {
                        OfficeDoneInfoModel officeDoneInfoModel =
                            officeDoneInfo4PositionApi.findByProcessInstanceId(tenantId, processInstanceId);
                        if (officeDoneInfoModel == null) {
                            processInstanceId = "";
                        } else {
                            processSerialNumber = officeDoneInfoModel.getProcessSerialNumber();
                        }
                    }
                    ProcessParamModel processParamModel =
                        processParamApi.findByProcessInstanceId(tenantId, processInstanceId);
                    ItemModel itemModel = item4PositionApi.getByItemId(tenantId, processParamModel.getItemId());
                    map.put("itemModel", itemModel);
                    break;
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取待办任务信息失败", e);
            return Y9Result.failure("获取失败");
        }
        map.put("processInstanceId", processInstanceId);
        map.put("processSerialNumber", processSerialNumber);
        return Y9Result.success(map, "获取成功");
    }

}
