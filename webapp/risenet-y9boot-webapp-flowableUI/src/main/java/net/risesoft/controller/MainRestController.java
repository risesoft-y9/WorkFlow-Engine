package net.risesoft.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.DraftApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.WorkOrderApi;
import net.risesoft.api.permission.PersonRoleApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.MonitorApi;
import net.risesoft.api.processadmin.ProcessTodoApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.api.todo.TodoTaskApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping(value = "/vue/mian")
public class MainRestController {

    @Autowired
    private ItemApi itemManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private MonitorApi monitorManager;

    @Autowired
    private PersonRoleApi personRoleApi;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private ProcessTodoApi todoManager;

    @Autowired
    private DraftApi draftManager;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private TodoTaskApi todoTaskApi;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private WorkOrderApi workOrderManager;

    @Value("${y9.app.flowable.systemWorkOrderKey}")
    private String systemWorkOrderKey;

    @Value("${y9.app.workOrder.processDefinitionKey}")
    private String processDefinitionKey;

    /**
     * 获取监控角色
     *
     * @param itemId
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getCount4Item")
    public Y9Result<Map<String, Object>> getCount4Item(String itemId, Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
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
            ItemModel itemModel = itemManager.getByItemId(Y9LoginUserHolder.getTenantId(), itemId);
            String processDefinitionKey = itemModel.getWorkflowGuid();
            if (itemModel != null && itemModel.getId() != null) {
                model.addAttribute("processDefinitionKey", processDefinitionKey);
                if (processDefinitionKey.equals(systemWorkOrderKey)) {
                    Map<String, Object> m = workOrderManager.getAdminCount();
                    map.put("wtodoCount", m.get("todoCount"));
                    map.put("wdoneCount", m.get("doneCount"));
                } else {
                    draftCount =
                        draftManager.getDraftCount(Y9LoginUserHolder.getTenantId(), userInfo.getPersonId(), itemId);
                    draftRecycleCount = draftManager.getDeleteDraftCount(Y9LoginUserHolder.getTenantId(),
                        userInfo.getPersonId(), itemId);
                }
                Map<String, Object> countMap = todoManager.getCountByUserIdAndProcessDefinitionKey(
                    Y9LoginUserHolder.getTenantId(), userInfo.getPersonId(), processDefinitionKey);
                todoCount = countMap != null ? Long.parseLong(countMap.get("todoCount").toString()) : 0;
                doingCount = countMap != null ? Long.parseLong(countMap.get("doingCount").toString()) : 0;
                try {
                    doneCount = officeDoneInfoManager.countByUserId(tenantId, userInfo.getPersonId(), itemId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            map.put("draftCount", draftCount);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            map.put("draftRecycleCount", draftRecycleCount);

            if (userInfo.isGlobalManager()) {
                try {
                    monitorDoing = officeDoneInfoManager.countDoingByItemId(tenantId, itemId);
                    monitorDone = officeDoneInfoManager.countByItemId(tenantId, itemId);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // recycleCount = monitorManager.getRecycleCountByProcessDefinitionKey(tenantId,
                // processDefinitionKey);
                map.put("monitorDoing", monitorDoing);
                map.put("monitorDone", monitorDone);
                map.put("monitorRecycle", recycleCount);
            }
            return Y9Result.success(map, "获取成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

    /**
     * 获取事项
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/getItem", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public Y9Result<Map<String, Object>> getItem(@RequestParam(required = true) String itemId) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("tenantManager", userInfo.isGlobalManager());
        ItemModel itemModel = itemManager.getByItemId(tenantId, itemId);
        map.put("itemModel", itemModel);
        map.put("tenantId", tenantId);
        map.put("dzxhTenantId", Y9Context.getProperty("y9.app.flowable.dzxhTenantId"));
        boolean b = personRoleApi.hasRole(tenantId, "Y9OrgHierarchyManagement", "", "监控管理员角色", userInfo.getPersonId());
        boolean deptManage = false;
        map.put("deptManage", deptManage);
        map.put("monitorManage", b);
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取监控角色
     *
     * @param itemId
     * @param model
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getMenu", method = RequestMethod.GET)
    public Map<String, Object> getMonitorAuthority(String itemId, Model model) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        boolean b = false;
        int draftCount = 0;
        long todoCount = 0;
        long doingCount = 0;
        long doneCount = 0;
        long draftRecycleCount = 0;
        long onLineCount = 0;
        long workOrderOnLineCount = 0;
        map.put("hasOnlineAccess", false);
        try {
            map.put("onLineCount", onLineCount);
            map.put("workOrderOnLineCount", workOrderOnLineCount);
            map.put("draftCount", draftCount);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            map.put("draftRecycleCount", draftRecycleCount);

            ItemModel itemModel = itemManager.getByItemId(Y9LoginUserHolder.getTenantId(), itemId);
            String processDefinitionKey = itemModel.getWorkflowGuid();
            if (itemModel != null && itemModel.getId() != null) {
                model.addAttribute("processDefinitionKey", processDefinitionKey);
                boolean hasOnlineAccess = personRoleApi.hasRole(tenantId, Y9Context.getSystemName(), "",
                    itemModel.getName() + "收件角色", userInfo.getPersonId());

                Map<String, Object> countMap = todoManager.getCountByUserIdAndProcessDefinitionKey(
                    Y9LoginUserHolder.getTenantId(), userInfo.getPersonId(), processDefinitionKey);
                draftCount =
                    draftManager.getDraftCount(Y9LoginUserHolder.getTenantId(), userInfo.getPersonId(), itemId);
                todoCount = countMap != null ? (long)countMap.get("todoCount") : 0;
                doingCount = countMap != null ? (long)countMap.get("doingCount") : 0;
                doneCount = countMap != null ? (long)countMap.get("doneCount") : 0;
                draftRecycleCount =
                    draftManager.getDeleteDraftCount(Y9LoginUserHolder.getTenantId(), userInfo.getPersonId(), itemId);
                if (itemModel.getWorkflowGuid().equals(processDefinitionKey)) {
                    map.put("itemType", "workOrder");
                }
                map.put("controlCount", 0);
                map.put("isOnline", itemModel.getIsOnline());
                map.put("hasOnlineAccess", hasOnlineAccess);
            }
            map.put("onLineCount", onLineCount);
            map.put("workOrderOnLineCount", workOrderOnLineCount);
            map.put("draftCount", draftCount);
            map.put("todoCount", todoCount);
            map.put("doingCount", doingCount);
            map.put("doneCount", doneCount);
            map.put("draftRecycleCount", draftRecycleCount);

            b = personRoleApi.hasRole(tenantId, "Y9OrgHierarchyManagement", "", "监控管理员角色", userInfo.getPersonId());
            if (b) {
                long monitorDoing = monitorManager.getDoingCountByProcessDefinitionKey(tenantId, processDefinitionKey);
                long monitorDone = monitorManager.getDoneCountByProcessDefinitionKey(tenantId, processDefinitionKey);
                long recycleCount =
                    monitorManager.getRecycleCountByProcessDefinitionKey(tenantId, processDefinitionKey);
                map.put("monitorDoing", monitorDoing);
                map.put("monitorDone", monitorDone);
                map.put("monitorRecycle", recycleCount);
            }
        } catch (

        Exception e) {
            e.printStackTrace();
        }
        map.put("hasRole", b);
        return map;
    }

    /**
     * 获取阅件左侧菜单数字
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getReadCount", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getReadCount() {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("notReadCount", chaoSongInfoManager.getTodoCountByUserId(tenantId, userId));
        map.put("hasReadCount", chaoSongInfoManager.getDoneCountByUserId(tenantId, userId));
        map.put("hasOpinionCount", chaoSongInfoManager.getDone4OpinionCountByUserId(tenantId, userId));
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 获取流程任务信息
     *
     * @param taskId 任务id
     * @param processInstanceId 流程实例id
     * @param type 类型
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getTaskOrProcessInfo", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getTaskOrProcessInfo(@RequestParam(required = false) String taskId,
        @RequestParam(required = false) String processInstanceId, @RequestParam(required = true) String type) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        String processSerialNumber = "";
        // FIXME
        String fromTodo = "fromTodo", fromCplane = "fromCplane", fromHistory = "fromHistory";
        if (type.equals(fromTodo)) {
            try {
                TaskModel taskModel = taskManager.findById(tenantId, taskId);
                if (taskModel == null || taskModel.getId() == null) {
                    todoTaskApi.deleteTodoTaskByTaskId(tenantId, taskId);
                    map.put("taskId", "");
                }
                processInstanceId = taskModel.getProcessInstanceId();
                ProcessParamModel processParamModel =
                    processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                processSerialNumber = processParamModel.getProcessSerialNumber();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (type.equals(fromCplane)) {
            taskId = "";
            HistoricProcessInstanceModel hisProcess = historicProcessManager.getById(tenantId, processInstanceId);
            ProcessParamModel processParamModel =
                processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
            processSerialNumber = processParamModel.getProcessSerialNumber();
            if (hisProcess == null || hisProcess.getId() == null) {

            } else {
                if (hisProcess.getEndTime() == null) {
                    List<TaskModel> list = taskManager.findByProcessInstanceId(tenantId, processInstanceId);
                    boolean isTodo = false;
                    if (list != null) {
                        for (TaskModel task : list) {
                            if ((task.getAssignee() != null && task.getAssignee().contains(userId))) {
                                taskId = task.getId();
                                isTodo = true;
                                break;
                            }
                        }
                        if (!isTodo) {
                            taskId = list.get(0).getId();
                        }
                    }
                    map.put("isTodo", isTodo);
                }
            }
            map.put("taskId", taskId);
        } else if (type.equals(fromHistory)) {
            HistoricProcessInstanceModel processModel = historicProcessManager.getById(tenantId, processInstanceId);
            if (processModel == null || processModel.getId() == null) {
                OfficeDoneInfoModel officeDoneInfoModel =
                    officeDoneInfoManager.findByProcessInstanceId(tenantId, processInstanceId);
                if (officeDoneInfoModel == null) {
                    processInstanceId = "";
                } else {
                    processSerialNumber = officeDoneInfoModel.getProcessSerialNumber();
                }
            }
        }
        map.put("processInstanceId", processInstanceId);
        map.put("processSerialNumber", processSerialNumber);
        return Y9Result.success(map, "获取成功");
    }

}
