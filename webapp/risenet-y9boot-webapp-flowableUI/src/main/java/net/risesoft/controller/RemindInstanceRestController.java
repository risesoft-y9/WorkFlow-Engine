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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.org.PersonApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.model.user.UserInfo;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequestMapping(value = "/vue/remindInstance")
public class RemindInstanceRestController {

    @Autowired
    private RemindInstanceApi remindInstanceManager;

    @Autowired
    private TaskApi taskManager;

    @Autowired
    private HistoricProcessApi historicProcessManager;

    @Autowired
    private PersonApi personApi;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam(required = true) String processInstanceId) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        Map<String, Object> retMap = new HashMap<String, Object>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        HistoricProcessInstanceModel his = historicProcessManager.getById(tenantId, processInstanceId);
        List<Map<String, Object>> list0 =
            processDefinitionManager.getNodes(tenantId, his.getProcessDefinitionId(), false);
        RemindInstanceModel remindInstance =
            remindInstanceManager.getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
        retMap.put("remindType", "");
        retMap.put("completeTaskKey", "");
        retMap.put("arriveTaskKey", "");
        if (remindInstance != null) {
            retMap.put("remindType", remindInstance.getRemindType());
            retMap.put("completeTaskKey", remindInstance.getCompleteTaskKey());
            retMap.put("arriveTaskKey", remindInstance.getArriveTaskKey());
        }
        for (Map<String, Object> map : list0) {
            String taskDefName = (String)map.get("taskDefName");
            if (!"流程".equals(taskDefName)) {
                list.add(map);
            }
        }
        retMap.put("rows", list);
        return Y9Result.success(retMap, "获取成功");
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            Date d1 = endTime;
            Date d2 = startTime;
            long time = d1.getTime() - d2.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            String str = d + " 天  " + h + " 小时 " + m + " 分 " + s + " 秒 ";
            return str;
        }
    }

    /**
     * 保存消息提醒
     *
     * @param processInstanceId 流程实例id
     * @param taskIds 任务ids 逗号隔开
     * @param process 是否流程办结提醒
     * @param arriveTaskKey 节点到达任务key
     * @param completeTaskKey 节点完成任务key
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveRemindInstance", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveRemindInstance(@RequestParam(required = true) String processInstanceId,
        @RequestParam(required = false) String taskIds, @RequestParam(required = true) Boolean process,
        @RequestParam(required = false) String arriveTaskKey, @RequestParam(required = false) String completeTaskKey) {
        UserInfo userInfo = Y9LoginUserHolder.getUserInfo();
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = userInfo.getPersonId();
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            map = remindInstanceManager.saveRemindInstance(tenantId, userId, processInstanceId, taskIds, process,
                arriveTaskKey, completeTaskKey);
            if ((Boolean)map.get(UtilConsts.SUCCESS)) {
                return Y9Result.successMsg("保存成功");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("保存失败");
    }

    /**
     * 获取未办理任务
     *
     * @param processInstanceId 流程实例id
     * @return
     */
    @SuppressWarnings("unchecked")
    @ResponseBody
    @RequestMapping(value = "/taskList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<Map<String, Object>> taskList(@RequestParam(required = true) String processInstanceId) {
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            retMap = taskManager.findListByProcessInstanceId(tenantId, processInstanceId, 1, 500);
            List<TaskModel> list = (List<TaskModel>)retMap.get("rows");
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taskList = objectMapper.convertValue(list, new TypeReference<List<TaskModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = 0;
            Map<String, Object> mapTemp = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime = new Date();
            RemindInstanceModel remindInstance =
                remindInstanceManager.getRemindInstance(tenantId, Y9LoginUserHolder.getPersonId(), processInstanceId);
            retMap.put("remindType", "");
            retMap.put("taskIds", "");
            if (remindInstance != null) {
                retMap.put("remindType", remindInstance.getRemindType());
                retMap.put("taskIds", remindInstance.getTaskId());
            }
            for (TaskModel task : taskList) {
                mapTemp = new HashMap<String, Object>(16);
                String taskId = task.getId();
                String taskName = task.getName();
                mapTemp.put("taskId", taskId);
                mapTemp.put("userName", StringUtils.isBlank(task.getAssignee()) ? ""
                    : personApi.getPerson(tenantId, task.getAssignee()).getData().getName());
                mapTemp.put("taskName", taskName);
                mapTemp.put("createTime", sdf.format(task.getCreateTime()));
                mapTemp.put("duration", longTime(task.getCreateTime(), currentTime));
                mapTemp.put("serialNumber", serialNumber + 1);
                serialNumber += 1;
                items.add(mapTemp);
            }
            retMap.put("rows", items);
            return Y9Result.success(retMap, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取失败");
    }

}
