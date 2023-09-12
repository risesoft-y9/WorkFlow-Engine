package net.risesoft.controller.mobile;

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

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.permission.PersonResourceApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.resource.ResourceApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.enums.AuthorityEnum;
import net.risesoft.model.Person;
import net.risesoft.model.itemadmin.ItemModel;
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
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping("/mobile/workList")
public class MobileWorkListController {

    protected Logger log = LoggerFactory.getLogger(MobileWorkListController.class);

    @Autowired
    private TodoService todoService;

    @Autowired
    private DoingService doingService;

    @Autowired
    private DoneService doneService;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessTrackApi processTrackManager;

    @Autowired
    private ProcessTodoApi processTodoManager;

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private ResourceApi resourceApi;

    @Autowired
    private PersonResourceApi personResourceApi;

    @Autowired
    private PersonRoleApi personRoleApi;

    /**
     * 获取在办件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/doingList")
    public void doingList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.getPerson(tenantId, userId));
            Map<String, Object> m = doingService.list(itemId, title, page, rows);
            List<Map<String, Object>> doingList = (List<Map<String, Object>>)m.get("rows");
            map.put("doingList", doingList);
            map.put("currpage", page);
            map.put("totalpage", m.get("totalpages"));
            map.put("total", m.get("total"));
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机在办件列表异常：");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 办结件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @RequestMapping(value = "/doneList")
    public void doneList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map.put(UtilConsts.SUCCESS, true);
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.getPerson(tenantId, userId));
            Y9Page<Map<String, Object>> y9page = doneService.list(itemId, title, page, rows);
            List<Map<String, Object>> doneList = y9page.getRows();
            map.put("doneList", doneList);
            map.put("currpage", page);
            map.put("totalpage", y9page.getTotalPages());
            map.put("total", y9page.getTotal());
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("办结件列表异常：");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * app办件计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/getAppCount")
    public void getAppCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            net.risesoft.model.Resource resource = resourceApi.getResource(tenantId);
            List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
            if (null != resource && null != resource.getId()) {
                String resourceId = resource.getId();
                List<net.risesoft.model.Resource> list0 =
                    personResourceApi.listSubResources(tenantId, userId, AuthorityEnum.BROWSE.getValue(), resourceId);
                String url = "";
                for (net.risesoft.model.Resource r : list0) {
                    map = new HashMap<String, Object>(16);
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
                    long todoCount = processTodoManager.getTodoCountByUserIdAndProcessDefinitionKey(
                        Y9LoginUserHolder.getTenantId(), person.getId(), processDefinitionKey);
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
                String riseTenantId = Y9Context.getProperty("y9.app.flowable.tenantId");
                if (riseTenantId.equals(Y9LoginUserHolder.getTenantId())) {
                    boolean workOrder = personRoleApi.hasRole(tenantId, "itemAdmin", "", "系统工单角色", userId);
                    // 拥有系统工单角色,才在我的工作中显示系统工单事项
                    if (workOrder) {
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
                            long todoCount = processTodoManager.getTodoCountByUserIdAndProcessDefinitionKey(
                                Y9LoginUserHolder.getTenantId(), person.getId(), processDefinitionKey);
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
                }
            }
            Map<String, Object> m1 = this.getCalendar(tenantId, userId);
            boolean b = m1.get("itemName") == null;
            if (b) {
                m1.put("todoCount", 0);
                m1.put("itemId", "日程");
                m1.put("itemName", "日程");
            }
            list.add(m1);
            map.put("data", list);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 获取日程应用
     *
     * @param tenantId
     * @param userId
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
     * @param itemId 事项id
     * @param response
     */
    @RequestMapping(value = "/getCount")
    public void getTodoCount(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-userId") String userId, @RequestParam String itemId, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Person person = personApi.getPerson(tenantId, userId);
            Y9LoginUserHolder.setPerson(person);
            ItemModel item = itemManager.getByItemId(tenantId, itemId);
            String processDefinitionKey = item.getWorkflowGuid();
            Map<String, Object> countMap = processTodoManager.getCountByUserIdAndProcessDefinitionKey(
                Y9LoginUserHolder.getTenantId(), person.getId(), processDefinitionKey);
            int todoCount = countMap != null ? (int)countMap.get("todoCount") : 0;
            int doingCount = countMap != null ? (int)countMap.get("doingCount") : 0;
            // int doneCount = countMap != null ? (int) countMap.get("doneCount") : 0;
            int doneCount = officeDoneInfoManager.countByUserId(tenantId, person.getId(), itemId);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }

    /**
     * 历程
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @param response
     */
    @RequestMapping(value = "/history")
    public void history(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String processInstanceId, HttpServletResponse response) {
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(personApi.getPerson(tenantId, userId));
        try {
            retMap = processTrackManager.processTrackList(tenantId, userId, processInstanceId);
            retMap.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            retMap.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(retMap));
        return;
    }

    /**
     * 待办件列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param itemId 事项id
     * @param title 搜索标题
     * @param page 页码
     * @param rows 行数
     * @param response
     */
    @SuppressWarnings("unchecked")
    @RequestMapping(value = "/todoList")
    public void todoList(@RequestHeader("auth-tenantId") String tenantId, @RequestHeader("auth-userId") String userId,
        @RequestParam String itemId, @RequestParam(required = false) String title, @RequestParam Integer page,
        @RequestParam Integer rows, HttpServletResponse response) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            Y9LoginUserHolder.setPerson(personApi.getPerson(tenantId, userId));
            Map<String, Object> m = todoService.list(itemId, title, page, rows);
            List<Map<String, Object>> todoList = (List<Map<String, Object>>)m.get("rows");
            map.put("todoList", todoList);
            map.put("currpage", page);
            map.put("totalpage", m.get("totalpages"));
            map.put("total", m.get("total"));
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            log.error("手机端待办件列表异常");
            e.printStackTrace();
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(map));
        return;
    }
}
