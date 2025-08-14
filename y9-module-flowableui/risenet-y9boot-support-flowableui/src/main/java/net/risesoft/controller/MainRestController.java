package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.api.itemadmin.entrust.EntrustApi;
import net.risesoft.api.itemadmin.worklist.DraftApi;
import net.risesoft.api.itemadmin.worklist.ItemTodoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.api.platform.permission.PositionRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.platform.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;

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
@RequestMapping(value = "/vue/main", produces = MediaType.APPLICATION_JSON_VALUE)
public class MainRestController {

    private final ItemApi itemApi;

    private final HistoricProcessApi historicProcessApi;

    private final PositionRoleApi positionRoleApi;

    private final PositionApi positionApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessTodoApi processTodoApi;

    private final DraftApi draftApi;

    private final ChaoSongApi chaoSongApi;

    private final OfficeDoneInfoApi officeDoneInfoApi;

    private final ProcessParamApi processParamApi;

    private final EntrustApi entrustApi;

    private final Y9FlowableProperties y9FlowableProperties;

    private final ItemTodoApi itemTodoApi;

    /**
     * 获取所有事项信息
     *
     * @return Y9Result<List < ItemModel>>
     */
    @GetMapping(value = "/getAllItemList")
    public Y9Result<List<ItemModel>> getAllItemList() {
        return itemApi.getAllItemList(Y9LoginUserHolder.getTenantId());
    }

    /**
     * 根据事项id获取事项办件分类的统计数量
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
            ItemModel itemModel = itemApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = itemModel.getWorkflowGuid();
            if (itemModel.getId() != null) {
                map.put("processDefinitionKey", processDefinitionKey);
                draftCount = draftApi.getDraftCount(tenantId, positionId, itemId).getData();
                draftRecycleCount = draftApi.getDeleteDraftCount(tenantId, positionId, itemId).getData();
                Y9FlowableCountModel flowableCountModel =
                    processTodoApi.getCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey)
                        .getData();
                todoCount = flowableCountModel.getTodoCount();
                doingCount = flowableCountModel.getDoingCount();
                try {
                    doneCount = officeDoneInfoApi.countByUserId(tenantId, positionId, itemId).getData();
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
                    monitorDoing = officeDoneInfoApi.countDoingByItemId(tenantId, itemId).getData();
                    monitorDone = officeDoneInfoApi.countByItemId(tenantId, itemId).getData();
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
     * 根据系统名称获取事项统计数量
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
            draftCount = draftApi.countBySystemName(tenantId, positionId, systemName).getData();
            // draftRecycleCount = draftApi.getDeleteDraftCount(tenantId, positionId, systemName);
            Y9FlowableCountModel flowableCountModel =
                processTodoApi.getCountByUserIdAndSystemName(tenantId, positionId, systemName).getData();
            todoCount = flowableCountModel.getTodoCount();
            doingCount = flowableCountModel.getDoingCount();
            try {
                doneCount = officeDoneInfoApi.countByUserIdAndSystemName(tenantId, positionId, systemName).getData();
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
     * 获取事项详情信息（含监控管理员权限、租户管理员权限、重定向权限）
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItem")
    public Y9Result<Map<String, Object>> getItem(@RequestParam @NotBlank String itemId) {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("tenantManager", person.isGlobalManager());
        ItemModel itemModel = itemApi.getByItemId(tenantId, itemId).getData();
        map.put("itemModel", itemModel);
        map.put("tenantId", tenantId);
        boolean b = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9LoginUserHolder.getPositionId())
            .getData();
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put("monitorManage", b);
        boolean b1 =
            positionRoleApi
                .hasPublicRole(tenantId, y9FlowableProperties.getRepositionrManageRoleName(),
                    Y9LoginUserHolder.getPositionId())
                .getData();
        map.put("repositionrManage", b1);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 根据系统名称获取事项（含监控管理员权限、重定向权限、发文权限、收文权限）
     *
     * @param systemName 系统名称
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemBySystemName")
    public Y9Result<Map<String, Object>> getItemBySystemName(@RequestParam @NotBlank String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<ItemModel> itemList = itemApi.findAll(tenantId, systemName).getData();
        Map<String, Object> map = new HashMap<>(16);
        boolean b = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9LoginUserHolder.getPositionId())
            .getData();
        map.put("monitorManage", b);
        boolean b1 =
            positionRoleApi
                .hasPublicRole(tenantId, y9FlowableProperties.getRepositionrManageRoleName(),
                    Y9LoginUserHolder.getPositionId())
                .getData();
        map.put("repositionrManage", b1);
        boolean b2 = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getFawenManageRoleName(), Y9LoginUserHolder.getPositionId())
            .getData();
        map.put("fawenManage", b2);
        boolean b3 = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getShouwenManageRoleName(), Y9LoginUserHolder.getPositionId())
            .getData();
        map.put("shouwenManage", b3);
        map.put("itemList", itemList);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取个人所有件统计数量
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getMyCount")
    public Y9Result<Map<String, Object>> getMyCount() {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        int todoCount;
        long doingCount;
        int doneCount;
        try {
            todoCount = itemTodoApi.countByUserId(tenantId, positionId).getData();
            // 统计流程在办件
            Y9Page<OfficeDoneInfoModel> y9Page =
                officeDoneInfoApi.searchAllByUserId(tenantId, positionId, "", "", "", "todo", "", "", "", 1, 1);
            doingCount = y9Page.getTotal();
            // 统计历史办结件
            doneCount = officeDoneInfoApi.countByUserId(tenantId, positionId, "").getData();
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
     * 获取当前人所有的岗位信息和当前岗被委托的记录
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getPositionList")
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
            // 是否统计待办数量
            if (StringUtils.isNotBlank(count)) {
                // 单个事项获取待办数量
                if (StringUtils.isNotBlank(itemId)) {
                    ItemModel itemModel = itemApi.getByItemId(tenantId, itemId).getData();
                    todoCount = processTodoApi
                        .getTodoCountByUserIdAndProcessDefinitionKey(tenantId, p.getId(), itemModel.getWorkflowGuid())
                        .getData();
                    allCount = allCount + todoCount;
                } else if (StringUtils.isNotBlank(systemName)) {
                    // 单个事项获取待办数量
                    todoCount =
                        processTodoApi.getTodoCountByUserIdAndSystemName(tenantId, p.getId(), systemName).getData();
                    allCount = allCount + todoCount;
                } else {// 工作台获取所有待办数量
                    try {
                        todoCount = itemTodoApi.countByUserId(tenantId, p.getId()).getData();
                        allCount = allCount + todoCount;
                    } catch (Exception e) {
                        LOGGER.error("获取待办数量失败", e);
                    }
                }
            }
            map.put("todoCount", todoCount);
            resList.add(map);

            // 获取当前岗被委托记录
            List<EntrustModel> list1 = entrustApi.getMyEntrustList(tenantId, p.getId()).getData();
            for (EntrustModel model : list1) {
                if (model.getUsed().equals(1)) {// 使用中的委托，将委托岗位加入岗位列表
                    Map<String, Object> map1 = new HashMap<>(16);
                    String ownerId = model.getOwnerId();
                    OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, ownerId).getData();
                    if (orgUnit != null) {
                        map1.put("id", orgUnit.getId());
                        map1.put("name", orgUnit.getName());
                        long todoCount1 = 0;
                        if (StringUtils.isNotBlank(count)) {
                            // 是否统计待办数量
                            if (StringUtils.isNotBlank(itemId)) {
                                // 单个事项获取待办数量
                                ItemModel itemModel = itemApi.getByItemId(tenantId, itemId).getData();
                                todoCount1 =
                                    processTodoApi
                                        .getTodoCountByUserIdAndProcessDefinitionKey(tenantId, orgUnit.getId(),
                                            itemModel.getWorkflowGuid())
                                        .getData();
                                allCount = allCount + todoCount1;
                            } else if (StringUtils.isNotBlank(systemName)) {
                                // 单个事项获取待办数量
                                todoCount1 = processTodoApi
                                    .getTodoCountByUserIdAndSystemName(tenantId, orgUnit.getId(), systemName)
                                    .getData();
                                allCount = allCount + todoCount1;
                            } else {// 工作台获取所有待办数量
                                try {
                                    todoCount1 = itemTodoApi.countByUserId(tenantId, orgUnit.getId()).getData();
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
     * 获取阅件左侧菜单数量
     *
     * @return Y9Result<Map < String, Object>>
     */

    @GetMapping(value = "/getReadCount")
    public Y9Result<Map<String, Object>> getReadCount() {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9LoginUserHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("notReadCount", chaoSongApi.getTodoCount(tenantId, positionId).getData());
        map.put("hasReadCount", chaoSongApi.getDoneCount(tenantId, positionId).getData());
        map.put("hasOpinionCount", chaoSongApi.getDone4OpinionCountByUserId(tenantId, positionId).getData());
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取当前人的角色权限
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getRole")
    public Y9Result<Map<String, Object>> getRole() {
        UserInfo person = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        map.put("tenantManager", person.isGlobalManager());
        boolean b = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9LoginUserHolder.getPositionId())
            .getData();
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
    @GetMapping(value = "/getTaskOrProcessInfo")
    public Y9Result<Map<String, Object>> getTaskOrProcessInfo(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processInstanceId, @RequestParam @NotBlank String type) {
        Map<String, Object> map = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        String processSerialNumber = "";
        try {
            if (type.equals("fromHistory")) {
                HistoricProcessInstanceModel processModel =
                    historicProcessApi.getById(tenantId, processInstanceId).getData();
                if (processModel == null || processModel.getId() == null) {
                    OfficeDoneInfoModel officeDoneInfoModel =
                        officeDoneInfoApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    if (officeDoneInfoModel == null) {
                        processInstanceId = "";
                    } else {
                        processSerialNumber = officeDoneInfoModel.getProcessSerialNumber();
                    }
                }
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                ItemModel itemModel = itemApi.getByItemId(tenantId, processParamModel.getItemId()).getData();
                map.put("itemModel", itemModel);
            }
        } catch (Exception e) {
            LOGGER.error("获取待办任务信息失败", e);
            return Y9Result.failure("获取失败");
        }
        map.put("processInstanceId", processInstanceId);
        map.put("processSerialNumber", processSerialNumber);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取当前人有权限的事项列表
     *
     * @return Y9Result<List < Map < String, Object>>>
     */
    @GetMapping(value = "/getMyItemList")
    public Y9Result<List<ItemListModel>> getMyItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String userId = Y9LoginUserHolder.getPersonId();
        return itemApi.getMyItemList(tenantId, userId);
    }
}
