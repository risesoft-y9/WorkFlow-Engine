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
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.model.itemadmin.core.ProcessParamModel;
import net.risesoft.model.platform.org.OrgUnit;
import net.risesoft.model.platform.org.Position;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9FlowableHolder;
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

    private static final String DRAFT_COUNT_KEY = "draftCount";
    private static final String TODO_COUNT_KEY = "todoCount";
    private static final String DOING_COUNT_KEY = "doingCount";
    private static final String DONE_COUNT_KEY = "doneCount";
    private static final String DRAFT_RECYCLE_COUNT_KEY = "draftRecycleCount";
    private static final String MONITOR_DOING_KEY = "monitorDoing";
    private static final String MONITOR_DONE_KEY = "monitorDone";
    private static final String MONITOR_RECYCLE_KEY = "monitorRecycle";
    private static final String MONITOR_MANAGE_KEY = "monitorManage";
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
    @GetMapping(value = "/getCount4Item")
    public Y9Result<Map<String, Object>> getCount4Item(@RequestParam @NotBlank String itemId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
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

        map.put(DRAFT_COUNT_KEY, draftCount);
        map.put(TODO_COUNT_KEY, todoCount);
        map.put(DOING_COUNT_KEY, doingCount);
        map.put(DONE_COUNT_KEY, doneCount);
        map.put(DRAFT_RECYCLE_COUNT_KEY, draftRecycleCount);

        map.put(MONITOR_DOING_KEY, monitorDoing);
        map.put(MONITOR_DONE_KEY, monitorDone);
        map.put(MONITOR_RECYCLE_KEY, recycleCount);
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
                    LOGGER.error("获取事项办结件数量统计失败", e);
                }
            }
            map.put(DRAFT_COUNT_KEY, draftCount);
            map.put(TODO_COUNT_KEY, todoCount);
            map.put(DOING_COUNT_KEY, doingCount);
            map.put(DONE_COUNT_KEY, doneCount);
            map.put(DRAFT_RECYCLE_COUNT_KEY, draftRecycleCount);

            if (person.isGlobalManager()) {
                try {
                    monitorDoing = officeDoneInfoApi.countDoingByItemId(tenantId, itemId).getData();
                    monitorDone = officeDoneInfoApi.countByItemId(tenantId, itemId).getData();
                } catch (Exception e) {
                    LOGGER.error("获取事项监控在办和办结件数量统计失败", e);
                }
                map.put(MONITOR_DOING_KEY, monitorDoing);
                map.put(MONITOR_DONE_KEY, monitorDone);
                map.put(MONITOR_RECYCLE_KEY, recycleCount);
            }
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项办件分类数量统计失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 根据系统名称获取事项统计数量
     *
     * @param systemName 系统名称
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getCount4SystemName")
    public Y9Result<Map<String, Object>> getCount4SystemName(@RequestParam @NotBlank String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9FlowableHolder.getPositionId();
        Map<String, Object> map = new HashMap<>(16);
        int draftCount = 0;
        long todoCount = 0;
        long doingCount = 0;
        long doneCount = 0;
        long draftRecycleCount = 0;

        long monitorDoing = 0;
        long monitorDone = 0;

        map.put(DRAFT_COUNT_KEY, draftCount);
        map.put(TODO_COUNT_KEY, todoCount);
        map.put(DOING_COUNT_KEY, doingCount);
        map.put(DONE_COUNT_KEY, doneCount);
        map.put(DRAFT_RECYCLE_COUNT_KEY, draftRecycleCount);

        map.put(MONITOR_DOING_KEY, monitorDoing);
        map.put(MONITOR_DONE_KEY, monitorDone);
        try {
            draftCount = draftApi.countBySystemName(tenantId, positionId, systemName).getData();
            draftRecycleCount = draftApi.getDeleteDraftCount(tenantId, positionId, systemName).getData();
            Y9FlowableCountModel flowableCountModel =
                processTodoApi.getCountByUserIdAndSystemName(tenantId, positionId, systemName).getData();
            todoCount = flowableCountModel.getTodoCount();
            doingCount = flowableCountModel.getDoingCount();
            try {
                doneCount = officeDoneInfoApi.countByUserIdAndSystemName(tenantId, positionId, systemName).getData();
            } catch (Exception e) {
                LOGGER.error("获取事项流程办结件统计失败", e);
            }
            map.put(DRAFT_COUNT_KEY, draftCount);
            map.put(TODO_COUNT_KEY, todoCount);
            map.put(DOING_COUNT_KEY, doingCount);
            map.put(DONE_COUNT_KEY, doneCount);
            map.put(DRAFT_RECYCLE_COUNT_KEY, draftRecycleCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("根据系统名称获取事项办件分类数量统计失败", e);
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
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9FlowableHolder.getPositionId())
            .getData();
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put(MONITOR_MANAGE_KEY, b);
        boolean b1 =
            positionRoleApi
                .hasPublicRole(tenantId, y9FlowableProperties.getRepositionManagerRoleName(),
                    Y9FlowableHolder.getPositionId())
                .getData();
        map.put("repositionManager", b1);
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
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9FlowableHolder.getPositionId())
            .getData();
        map.put(MONITOR_MANAGE_KEY, b);
        boolean b1 =
            positionRoleApi
                .hasPublicRole(tenantId, y9FlowableProperties.getRepositionManagerRoleName(),
                    Y9FlowableHolder.getPositionId())
                .getData();
        map.put("repositionManager", b1);
        boolean b2 = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getFaWenManageRoleName(), Y9FlowableHolder.getPositionId())
            .getData();
        map.put("faWenManage", b2);
        boolean b3 = positionRoleApi
            .hasPublicRole(tenantId, y9FlowableProperties.getShouWenManageRoleName(), Y9FlowableHolder.getPositionId())
            .getData();
        map.put("shouWenManage", b3);
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
        String positionId = Y9FlowableHolder.getPositionId();
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
            map.put(TODO_COUNT_KEY, todoCount);
            map.put(DOING_COUNT_KEY, doingCount);
            map.put(DONE_COUNT_KEY, doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项流程与当前人相关的办件数量统计失败", e);
        }
        return Y9Result.failure(50000, "获取失败");
    }

    /**
     * 获取当前人所有的岗位信息和当前岗被委托的记录
     *
     * @return Y9Result<Map<String, Object>>
     */
    @GetMapping(value = "/getPositionList")
    public Y9Result<Map<String, Object>> getPositionList(@RequestParam(required = false) String count,
        @RequestParam(required = false) String itemId, @RequestParam(required = false) String systemName) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> resMap = new HashMap<>(16);
        List<Map<String, Object>> resList = new ArrayList<>();
        long allCount = 0;
        try {
            List<Position> positions = positionApi.listByPersonId(tenantId, Y9LoginUserHolder.getPersonId()).getData();
            for (Position position : positions) {
                // 处理当前岗位
                PositionInfo positionInfo = buildPositionInfo(position, tenantId, count, itemId, systemName);
                resList.add(positionInfo.positionMap);
                allCount += positionInfo.todoCount;
                // 处理委托岗位
                List<EntrustModel> entrustList = entrustApi.getMyEntrustList(tenantId, position.getId()).getData();
                for (EntrustModel model : entrustList) {
                    if (model.getUsed().equals(1)) { // 使用中的委托
                        PositionInfo entrustInfo = buildEntrustPositionInfo(model, tenantId, count, itemId, systemName);
                        if (entrustInfo != null) {
                            resList.add(entrustInfo.positionMap);
                            allCount += entrustInfo.todoCount;
                        }
                    }
                }
            }
            resMap.put("allCount", allCount);
            resMap.put("positionList", resList);
            return Y9Result.success(resMap, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取岗位列表失败", e);
            return Y9Result.failure("获取失败");
        }
    }

    /**
     * 构建岗位信息
     */
    private PositionInfo buildPositionInfo(Position position, String tenantId, String count, String itemId,
        String systemName) {
        Map<String, Object> positionMap = new HashMap<>(16);
        positionMap.put("id", position.getId());
        positionMap.put("name", position.getName());

        long todoCount = 0;
        if (StringUtils.isNotBlank(count)) {
            todoCount = getTodoCount(tenantId, position.getId(), itemId, systemName);
        }

        positionMap.put(TODO_COUNT_KEY, todoCount);
        return new PositionInfo(positionMap, todoCount);
    }

    /**
     * 构建委托岗位信息
     */
    private PositionInfo buildEntrustPositionInfo(EntrustModel model, String tenantId, String count, String itemId,
        String systemName) {
        try {
            String ownerId = model.getOwnerId();
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, ownerId).getData();
            if (orgUnit != null) {
                Map<String, Object> positionMap = new HashMap<>(16);
                positionMap.put("id", orgUnit.getId());
                positionMap.put("name", orgUnit.getName());
                long todoCount = 0;
                if (StringUtils.isNotBlank(count)) {
                    todoCount = getTodoCount(tenantId, orgUnit.getId(), itemId, systemName);
                }
                positionMap.put(TODO_COUNT_KEY, todoCount);
                return new PositionInfo(positionMap, todoCount);
            }
        } catch (Exception e) {
            LOGGER.warn("获取委托岗位信息失败: ownerId={}", model.getOwnerId(), e);
        }
        return null;
    }

    /**
     * 获取待办数量
     */
    private long getTodoCount(String tenantId, String userId, String itemId, String systemName) {
        try {
            if (StringUtils.isNotBlank(itemId)) {
                // 单个事项获取待办数量
                ItemModel itemModel = itemApi.getByItemId(tenantId, itemId).getData();
                return processTodoApi
                    .getTodoCountByUserIdAndProcessDefinitionKey(tenantId, userId, itemModel.getWorkflowGuid())
                    .getData();
            } else if (StringUtils.isNotBlank(systemName)) {
                // 按系统名称获取待办数量
                return processTodoApi.getTodoCountByUserIdAndSystemName(tenantId, userId, systemName).getData();
            } else {
                // 工作台获取所有待办数量
                return itemTodoApi.countByUserId(tenantId, userId).getData();
            }
        } catch (Exception e) {
            LOGGER.error("获取待办数量失败: userId={}, itemId={}, systemName={}", userId, itemId, systemName, e);
            return 0;
        }
    }

    /**
     * 获取阅件左侧菜单数量
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getReadCount")
    public Y9Result<Map<String, Object>> getReadCount() {
        String tenantId = Y9LoginUserHolder.getTenantId(), positionId = Y9FlowableHolder.getPositionId();
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
            .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(), Y9FlowableHolder.getPositionId())
            .getData();
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put(MONITOR_MANAGE_KEY, b);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取流程任务信息
     *
     * @param processInstanceId 流程实例id
     * @param type 类型
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getTaskOrProcessInfo")
    public Y9Result<Map<String, Object>> getTaskOrProcessInfo(@RequestParam(required = false) String processInstanceId,
        @RequestParam @NotBlank String type) {
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
                    }
                }
                ProcessParamModel processParamModel =
                    processParamApi.findByProcessInstanceId(tenantId, processInstanceId).getData();
                processSerialNumber = processParamModel.getProcessSerialNumber();
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

    /**
     * 岗位信息封装类
     */
    private static class PositionInfo {
        final Map<String, Object> positionMap;
        final long todoCount;

        PositionInfo(Map<String, Object> positionMap, long todoCount) {
            this.positionMap = positionMap;
            this.todoCount = todoCount;
        }
    }
}
