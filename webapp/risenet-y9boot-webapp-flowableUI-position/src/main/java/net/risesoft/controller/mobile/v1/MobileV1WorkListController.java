package net.risesoft.controller.mobile.v1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeDoneInfo4PositionApi;
import net.risesoft.api.itemadmin.position.ProcessTrack4PositionApi;
import net.risesoft.api.permission.PositionResourceApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.resource.ResourceApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.platform.AuthorityEnum;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.platform.Resource;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DoingService;
import net.risesoft.service.DoneService;
import net.risesoft.service.TodoService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 办件列表相关接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@RestController
@RequestMapping("/mobile/v1/workList")
public class MobileV1WorkListController {

    protected Logger log = LoggerFactory.getLogger(MobileV1WorkListController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private DoingService doingService;

    @Autowired
    private DoneService doneService;

    @Autowired
    private ProcessTrack4PositionApi processTrackManager;

    @Autowired
    private ProcessTodoApi processTodoManager;

    @Autowired
    private Item4PositionApi itemManager;

    @Autowired
    private OfficeDoneInfo4PositionApi officeDoneInfoManager;

    @Autowired
    private ResourceApi resourceManager;

    @Autowired
    private PositionResourceApi positionResourceApi;

    /**
     * 获取在办件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/doingList")
    public Y9Page<Map<String, Object>> doingList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Map<String, Object> retMap = doingService.list(itemId, title, page, rows);
            List<Map<String, Object>> doingList = (List<Map<String, Object>>)retMap.get("rows");
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), doingList, "获取列表成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * 办结件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/doneList")
    public Y9Page<Map<String, Object>> doneList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            return doneService.list(itemId, title, page, rows);
        } catch (Exception e) {
            log.error("办结件列表异常：");
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }

    /**
     * app办件计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAppCount")
    public Y9Result<List<Map<String, Object>>> getAppCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Resource resource = resourceManager.getResource(tenantId).getData();
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            if (null != resource && null != resource.getId()) {
                String resourceId = resource.getId();
                List<Resource> list0 = positionResourceApi.listSubResources(tenantId, positionId, AuthorityEnum.BROWSE, resourceId).getData();
                String url = "";
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
                    ItemModel item = itemManager.getByItemId(tenantId, itemId);
                    String processDefinitionKey = item.getWorkflowGuid();
                    long todoCount = processTodoManager.getTodoCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
                    Map<String, Object> m = new HashMap<String, Object>(16);
                    Map<String, Object> resMap = todoService.list(item.getId(), "", 1, 1);
                    List<Map<String, Object>> todoList = (List<Map<String, Object>>)resMap.get("rows");
                    if (todoList != null && todoList.size() > 0) {
                        Map<String, Object> todo = todoList.get(0);
                        m.put("title", todo.get(SysVariables.DOCUMENTTITLE));
                        m.put("time", todo.get("taskCreateTime"));
                    }
                    m.put("todoCount", todoCount);
                    m.put("itemId", item.getId());
                    m.put("itemName", item.getName());
                    list.add(m);
                }
                // 系统工单为大有生租户专用,不创建应用,不生成资源,避免其他租户可租用,大有生租户添加系统工单
                /*String riseTenantId = Y9Context.getProperty("y9.app.flowable.tenantId");
                if (riseTenantId.equals(Y9LoginUserHolder.getTenantId())) {
                    boolean workOrder = positionRoleApi.hasRole(tenantId, "itemAdmin", "", "系统工单角色", positionId).getData();
                    if (workOrder) {// 拥有系统工单角色,才在我的工作中显示系统工单事项
                        map = new HashMap<String, Object>(16);
                        String workOrderItemId = Y9Context.getProperty("y9.app.flowable.workOrderItemId");
                        ItemModel item = itemManager.getByItemId(tenantId, workOrderItemId);
                        map.put("id", workOrderItemId);
                        map.put("name", item.getName());
                        map.put("url", workOrderItemId);
                        map.put("iconData", "");
                        map.put("todoCount", 0);
                        if (item != null && item.getId() != null) {
                            String processDefinitionKey = item.getWorkflowGuid();
                            long todoCount = processTodoManager.getTodoCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
                            Map<String, Object> m = new HashMap<String, Object>(16);
                            Map<String, Object> resMap = todoService.list(item.getId(), "", 1, 1);
                            List<Map<String, Object>> todoList = (List<Map<String, Object>>)resMap.get("rows");
                            if (todoList != null && todoList.size() > 0) {
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
                }*/
            }
            return Y9Result.success(list, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取日程应用
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @return
     */
    public Map<String, Object> getCalendar(String tenantId, String userId) {

        Map<String, Object> map = new HashMap<String, Object>(16);
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
                String msg = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                map = Y9JsonUtil.readHashMap(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 获取消息快递
     *
     * @param tenantId
     * @param userId
     * @return
     */
    public Map<String, Object> getMessage(String tenantId, String userId) {
        // 获取日程应用
        Map<String, Object> map = new HashMap<String, Object>(16);
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.BUFFER_WARN_TRIGGER_LIMIT, 1024 * 1024 * 10);
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        HttpMethod method = new GetMethod();
        try {
            // 设置请求超时时间10s
            client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
            // 设置读取数据超时时间10s
            client.getHttpConnectionManager().getParams().setSoTimeout(5000);
            String url = Y9Context.getProperty("y9.common.messageBaseUrl") + "/mobile/messageDelivery/getNotReadNum";
            method.setPath(url);
            method.addRequestHeader("auth-tenantId", tenantId);
            method.addRequestHeader("auth-userId", userId);
            int code = client.executeMethod(method);
            if (code == HttpStatus.SC_OK) {
                String msg = new String(method.getResponseBodyAsString().getBytes("UTF-8"), "UTF-8");
                map = Y9JsonUtil.readHashMap(msg);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 办件计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getCount")
    public Y9Result<Map<String, Object>> getTodoCount(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            Map<String, Object> countMap = processTodoManager.getCountByUserIdAndProcessDefinitionKey(tenantId, positionId, processDefinitionKey);
            int todoCount = countMap != null ? (int)countMap.get("todoCount") : 0;
            int doingCount = countMap != null ? (int)countMap.get("doingCount") : 0;
            // int doneCount = countMap != null ? (int) countMap.get("doneCount") : 0;
            int doneCount = officeDoneInfoManager.countByPositionId(tenantId, positionId, itemId);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 历程
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/history")
    public Y9Result<List<Map<String, Object>>> history(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        try {
            retMap = processTrackManager.processTrackList(tenantId, positionId, processInstanceId);
            if ((boolean)retMap.get("success")) {
                return Y9Result.success((List<Map<String, Object>>)retMap.get("rows"), "获取成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 待办件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/todoList")
    public Y9Page<Map<String, Object>> todoList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId, @RequestHeader("auth-positionId") String positionId, @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPositionId(positionId);
            Map<String, Object> retMap = todoService.list(itemId, title, page, rows);
            List<Map<String, Object>> todoList = (List<Map<String, Object>>)retMap.get("rows");
            return Y9Page.success(page, Integer.parseInt(retMap.get("totalpages").toString()), Integer.parseInt(retMap.get("total").toString()), todoList, "获取列表成功");
        } catch (Exception e) {
            log.error("手机端待办件列表异常");
            e.printStackTrace();
        }
        return Y9Page.success(page, 0, 0, new ArrayList<Map<String, Object>>(), "获取列表失败");
    }
}
