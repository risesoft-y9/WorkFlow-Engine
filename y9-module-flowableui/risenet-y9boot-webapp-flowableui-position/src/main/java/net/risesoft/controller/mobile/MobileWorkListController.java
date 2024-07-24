package net.risesoft.controller.mobile;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.validation.constraints.NotBlank;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
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
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.itemadmin.HistoryProcessModel;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.Resource;
import net.risesoft.model.processadmin.Y9FlowableCountModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.DoingService;
import net.risesoft.service.DoneService;
import net.risesoft.service.TodoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 办件列表相关接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mobile/workList")
public class MobileWorkListController {

    private final TodoService todoService;
    private final DoingService doingService;
    private final DoneService doneService;
    private final ProcessTrack4PositionApi processTrack4PositionApi;
    private final ProcessTodoApi processTodoApi;
    private final Item4PositionApi item4PositionApi;
    private final OfficeDoneInfo4PositionApi officeDoneInfo4PositionApi;
    private final ResourceApi resourceApi;
    private final PositionResourceApi positionResourceApi;
    protected Logger log = LoggerFactory.getLogger(MobileWorkListController.class);

    /**
     * 获取在办件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/doingList")
    public void doingList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Y9Page<Map<String, Object>> doningPage =
                doingService.page4MobileByItemIdAndSearchTerm(itemId, title, page, rows);
            map.put("doingList", doningPage.getRows());
            map.put("currpage", page);
            map.put("totalpage", doningPage.getTotalPages());
            map.put("total", doningPage.getTotal());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取在办件列表失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 办结件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/doneList")
    public void doneList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Y9Page<Map<String, Object>> y9page =
                doneService.page4MobileByItemIdAndSearchTerm(itemId, title, page, rows);
            List<Map<String, Object>> doneList = y9page.getRows();
            map.put("doneList", doneList);
            map.put("currpage", page);
            map.put("totalpage", y9page.getTotalPages());
            map.put("total", y9page.getTotal());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取办结件列表失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * app办件计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAppCount")
    public void getAppCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Resource resource = resourceApi.getResource(tenantId).getData();
            List<Map<String, Object>> list = new ArrayList<>();
            if (null != resource && null != resource.getId()) {
                String resourceId = resource.getId();
                List<Resource> list0 = positionResourceApi
                    .listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
                String url;
                for (Resource r : list0) {
                    map = new HashMap<>(16);
                    url = r.getUrl();
                    if (StringUtils.isBlank(url)) {
                        continue;
                    }
                    if (!url.contains("itemId=")) {
                        continue;
                    }
                    String itemId = url.split("itemId=")[1];
                    ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
                    String processDefinitionKey = item.getWorkflowGuid();
                    long todoCount = processTodoApi
                        .getTodoCountByPositionIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey)
                        .getData();
                    Map<String, Object> m = new HashMap<>(16);
                    Y9Page<Map<String, Object>> resMap =
                        todoService.page4MobileByItemIdAndSearchTerm(item.getId(), "", 1, 1);
                    List<Map<String, Object>> todoList = resMap.getRows();
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
            Map<String, Object> m1 = this.getCalendar(tenantId, userId);
            if (m1.get("itemName") == null) {
                m1.put("todoCount", 0);
                m1.put("itemId", "日程");
                m1.put("itemName", "日程");
            }
            list.add(m1);

            /*
             * Map<String,Object> m2 = this.getMessage(tenantId, userId);
             * if(m2.get("itemName")==null) { m2.put("todoCount", 0); m2.put("itemId",
             * "消息快递"); m2.put("itemName", "消息快递"); } list.add(m2);
             */

            map.put("data", list);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取app办件计数失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 获取日程应用
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return Map
     */
    public Map<String, Object> getCalendar(String tenantId, String userId) {

        Map<String, Object> map = new HashMap<>(16);
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        HttpMethod method = new GetMethod();
        try {
            // 设置请求超时时间10s
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            // 设置读取数据超时时间10s
            client.getHttpConnectionManager().getParams().setSoTimeout(5000);
            String url = Y9Context.getProperty("y9.common.calendarBaseUrl") + "/mobile/calendar/getTodo";
            method.setPath(url);
            method.addRequestHeader("auth-tenantId", tenantId);
            method.addRequestHeader("auth-userId", userId);
            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                String msg = new String(method.getResponseBodyAsString().getBytes(StandardCharsets.UTF_8),
                    StandardCharsets.UTF_8);
                map = Y9JsonUtil.readHashMap(msg);
            }
        } catch (Exception e) {
            LOGGER.error("获取日程应用失败", e);
        }
        return map;
    }

    /**
     * 办件计数
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     */
    @RequestMapping(value = "/getCount")
    public void getTodoCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = item4PositionApi.getByItemId(tenantId, itemId).getData();
            String processDefinitionKey = item.getWorkflowGuid();
            Y9FlowableCountModel flowableCountModel = processTodoApi
                .getCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey).getData();
            int todoCount = (int)flowableCountModel.getTodoCount();
            int doingCount = (int)flowableCountModel.getDoingCount();
            int doneCount = officeDoneInfo4PositionApi.countByPositionId(tenantId, positionId, itemId).getData();
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取办件计数失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }

    /**
     * 历程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     */
    @RequestMapping(value = "/history")
    public void history(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String processInstanceId,
        HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            List<HistoryProcessModel> items =
                processTrack4PositionApi.processTrackList(tenantId, positionId, processInstanceId).getData();
            retMap.put("rows", items);
            retMap.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            LOGGER.error("历程失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(retMap));
    }

    /**
     * 待办件列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     */
    @RequestMapping(value = "/todoList")
    public void todoList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, @RequestParam @NotBlank String itemId,
        @RequestParam(required = false) String title, @RequestParam Integer page, @RequestParam Integer rows,
        HttpServletResponse response) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Y9Page<Map<String, Object>> m = todoService.page4MobileByItemIdAndSearchTerm(itemId, title, page, rows);
            List<Map<String, Object>> todoList = m.getRows();
            map.put("todoList", todoList);
            map.put("currpage", page);
            map.put("totalpage", m.getTotalPages());
            map.put("total", m.getTotal());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            LOGGER.error("获取待办件列表失败", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
    }
}
