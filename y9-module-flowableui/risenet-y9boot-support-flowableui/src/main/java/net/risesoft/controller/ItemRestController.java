package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.platform.permission.cache.PositionRoleApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.consts.FlowableUiConsts;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.model.itemadmin.ItemStartNodeRoleModel;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9FlowableHolder;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.configuration.app.flowble.Y9FlowableProperties;

/**
 * 发送，办结相关
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/item", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemRestController {

    private final ItemApi itemApi;
    private final DocumentApi documentApi;
    private final ChaoSongApi chaoSongApi;
    private final PositionRoleApi positionRoleApi;
    private final ProcessTodoApi processTodoApi;
    private final Y9FlowableProperties y9FlowableProperties;
    private final OfficeFollowApi officeFollowApi;

    /**
     * 获取所有开始节点
     *
     * @param itemId 事项id
     * @return Y9Result<List < ItemStartNodeRoleModel>>
     */
    @GetMapping(value = "/getAllStartTaskDefKey")
    public Y9Result<List<ItemStartNodeRoleModel>> getAllStartTaskDefKey(@RequestParam @NotBlank String itemId) {
        return documentApi.getAllStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId(),
            itemId);
    }

    /**
     * 获取事项列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemList")
    public Y9Result<Map<String, Object>> getItemList() {
        Map<String, Object> map = new HashMap<>(16);
        try {
            List<ItemListModel> listMap =
                itemApi.getItemList(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData();
            map.put("itemMap", listMap);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData());
            int followCount = officeFollowApi.getFollowCount().getData();
            map.put("followCount", followCount);
            // 公共角色
            boolean b =
                positionRoleApi
                    .hasPublicRole(Y9LoginUserHolder.getTenantId(), y9FlowableProperties.getMonitorManageRoleName(),
                        Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("monitorManage", b);
            boolean b1 =
                positionRoleApi
                    .hasRole(Y9LoginUserHolder.getTenantId(), Y9Context.getSystemName(), "",
                        y9FlowableProperties.getLeaveManageRoleName(), Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("leaveManage", b1);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项和事项对应的系统列表（含抄送未阅数量、监控管理权限、人事统计权限）
     *
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getItemSystemList")
    public Y9Result<Map<String, Object>> getItemSystemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<>(16);
        try {
            String positionId = Y9FlowableHolder.getPositionId();
            List<Map<String, Object>> list = new ArrayList<>();
            List<ItemModel> listMap = itemApi.getAllItem(Y9LoginUserHolder.getTenantId()).getData();
            for (ItemModel itemModel : listMap) {
                Map<String, Object> newmap = new HashMap<>(16);
                newmap.put(FlowableUiConsts.SYSTEMNAME_KEY, itemModel.getSystemName());
                newmap.put("systemCnName", itemModel.getSysLevel());
                if (!list.contains(newmap)) {
                    list.add(newmap);
                }
            }
            for (Map<String, Object> nmap : list) {
                long todoCount =
                    processTodoApi
                        .getTodoCountByUserIdAndSystemName(tenantId, positionId,
                            (String)nmap.get(FlowableUiConsts.SYSTEMNAME_KEY))
                        .getData();
                nmap.put("todoCount", todoCount);
                List<ItemModel> itemList = new ArrayList<>();
                for (ItemModel itemModel : listMap) {
                    if (nmap.get(FlowableUiConsts.SYSTEMNAME_KEY).equals(itemModel.getSystemName())) {
                        itemList.add(itemModel);
                    }
                }
                nmap.put("itemList", itemList);
            }

            map.put("systemList", list);
            map.put("notReadCount",
                chaoSongApi.getTodoCount(Y9LoginUserHolder.getTenantId(), Y9FlowableHolder.getPositionId()).getData());
            // 公共角色
            boolean b =
                positionRoleApi
                    .hasPublicRole(tenantId, y9FlowableProperties.getMonitorManageRoleName(),
                        Y9FlowableHolder.getPositionId())
                    .getData();
            map.put("monitorManage", b);

            boolean b1 = false;
            map.put("leaveManage", b1);

            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取事项系统列表失败", e);
        }
        return Y9Result.failure("获取失败");
    }
}