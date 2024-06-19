package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.platform.permission.PositionResourceApi;
import net.risesoft.api.platform.resource.ResourceApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.Resource;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DoingService;
import net.risesoft.service.DoneService;
import net.risesoft.service.TodoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 办件列表相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/workList")
public class MobileV1WorkListController {

    private final TodoService todoService;

    private final DoingService doingService;

    private final DoneService doneService;

    private final ProcessTrack4PositionApi processTrack4PositionApi;

    private final ProcessTodoApi processTodoApi;

    private final Item4PositionApi item4PositionApi;

    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;

    private final ResourceApi resourceApi;

    private final PositionResourceApi positionResourceApi;

    /**
     * 获取在办件列表
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/doingList")
    public Y9Page<Map<String, Object>> doingList(@RequestParam @NotBlank String itemId, @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows) {
        try {
            Map<String, Object> retMap = doingService.list(itemId, title, page, rows);
            List<Map<String, Object>> doingList = (List<Map<String, Object>>)retMap.get("rows");
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), doingList, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("获取在办件列表异常：");
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * 办结件列表
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    @RequestMapping(value = "/doneList")
    public Y9Page<Map<String, Object>> doneList(@RequestParam @NotBlank String itemId, @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows) {
        try {
            return doneService.list(itemId, title, page, rows);
        } catch (Exception e) {
            LOGGER.error("办结件列表异常：", e);
        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }

    /**
     * app办件计数
     *
     * @return Y9Result<List < Map < String, Object>>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAppCount")
    public Y9Result<List<Map<String, Object>>> getAppCount() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            Resource resource = resourceApi.getResource(tenantId).getData();
            List<Map<String, Object>> list = new ArrayList<>();
            if (null != resource && null != resource.getId()) {
                String resourceId = resource.getId();
                List<Resource> list0 = positionResourceApi.listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
                String url;
                for (Resource r : list0) {
                    url = r.getUrl();
                    if (StringUtils.isBlank(url)) {
                        continue;
                    }
                    if (!url.contains("itemId=")) {
                        continue;
                    }
                    String itemId = url.split("itemId=")[1];
                    ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
                    String processDefinitionKey = item.getWorkflowGuid();
                    long todoCount = processTodoApi.getTodoCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
                    Map<String, Object> m = new HashMap<>(16);
                    Map<String, Object> resMap = todoService.list(item.getId(), "", 1, 1);
                    List<Map<String, Object>> todoList = (List<Map<String, Object>>)resMap.get("rows");
                    if (todoList != null && !todoList.isEmpty()) {
                        Map<String, Object> todo = todoList.get(0);
                        m.put("title", todo.get(SysVariables.DOCUMENTTITLE));
                        m.put("time", todo.get("taskCreateTime"));
                    }
                    m.put("todoCount", todoCount);
                    m.put("itemId", item.getId());
                    m.put("itemName", item.getName());
                    list.add(m);
                }
            }
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取办件计数异常：", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 办件计数
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @RequestMapping(value = "/getCount")
    public Y9Result<Map<String, Object>> getTodoCount(@RequestParam @NotBlank String itemId) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            Map<String, Object> countMap = processTodoApi.getCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
            int todoCount = countMap != null ? (int)countMap.get("todoCount") : 0;
            int doingCount = countMap != null ? (int)countMap.get("doingCount") : 0;
            // int doneCount = countMap != null ? (int) countMap.get("doneCount") : 0;
            int doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, itemId);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            LOGGER.error("获取办件计数异常：", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 历程
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<List < Map < String, Object>>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/history")
    public Y9Result<List<Map<String, Object>>> history(@RequestParam @NotBlank String processInstanceId) {
        Map<String, Object> retMap;
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            retMap = processTrack4PositionApi.processTrackList(tenantId, positionId, processInstanceId);
            if ((boolean)retMap.get("success")) {
                return Y9Result.success((List<Map<String, Object>>)retMap.get("rows"), "获取成功");
            }
        } catch (Exception e) {
            LOGGER.error("获取历程异常：", e);
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 待办件列表
     *
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @return Y9Page<Map < String, Object>>
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows) {
        try {
            Map<String, Object> retMap = todoService.list(itemId, title, page, rows);
            List<Map<String, Object>> todoList = (List<Map<String, Object>>)retMap.get("rows");
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), todoList, "获取列表成功");
        } catch (Exception e) {
            LOGGER.error("手机端待办件列表异常", e);

        }
        return Y9Page.success(page, 0, 0, new ArrayList<>(), "获取列表失败");
    }
}
