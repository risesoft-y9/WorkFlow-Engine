package net.risesoft.controller;

import java.text.SimpleDateFormat;
import java.util.*;

import javax.validation.constraints.NotBlank;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.RemindInstanceApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.TaskApi;
import net.risesoft.model.itemadmin.RemindInstanceModel;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.model.processadmin.TaskModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 消息提醒
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping(value = "/vue/remindInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class RemindInstanceRestController {

    private final RemindInstanceApi remindInstanceApi;

    private final TaskApi taskApi;

    private final HistoricProcessApi historicProcessApi;

    private final OrgUnitApi orgUnitApi;

    private final ProcessDefinitionApi processDefinitionApi;

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<Map<String, Object>> getBpmList(@RequestParam @NotBlank String processInstanceId) {
        List<TargetModel> list = new ArrayList<>();
        Map<String, Object> retMap = new HashMap<>(16);
        String tenantId = Y9LoginUserHolder.getTenantId();
        HistoricProcessInstanceModel his = historicProcessApi.getById(tenantId, processInstanceId).getData();
        List<TargetModel> list0 = processDefinitionApi.getNodes(tenantId, his.getProcessDefinitionId()).getData();
        RemindInstanceModel remindInstance = remindInstanceApi
            .getRemindInstance(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceId).getData();
        retMap.put("remindType", "");
        retMap.put("completeTaskKey", "");
        retMap.put("arriveTaskKey", "");
        if (remindInstance != null) {
            retMap.put("remindType", remindInstance.getRemindType());
            retMap.put("completeTaskKey", remindInstance.getCompleteTaskKey());
            retMap.put("arriveTaskKey", remindInstance.getArriveTaskKey());
        }
        for (TargetModel targetModel : list0) {
            String taskDefName = targetModel.getTaskDefName();
            if (!"流程".equals(taskDefName)) {
                list.add(targetModel);
            }
        }
        retMap.put("rows", list);
        // TODO
        return Y9Result.success(retMap, "获取成功");
    }

    private String longTime(Date startTime, Date endTime) {
        if (endTime == null) {
            return "";
        } else {
            long time = endTime.getTime() - startTime.getTime();
            time = time / 1000;
            int s = (int)(time % 60);
            int m = (int)(time / 60 % 60);
            int h = (int)(time / 3600 % 24);
            int d = (int)(time / 86400);
            return d + " 天  " + h + " 小时 " + m + " 分 " + s + " 秒 ";
        }
    }

    /**
     * 保存消息提醒数据
     *
     * @param processInstanceId 流程实例id
     * @param taskIds 任务ids 逗号隔开
     * @param process 是否流程办结提醒
     * @param arriveTaskKey 节点到达任务key
     * @param completeTaskKey 节点完成任务key
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveRemindInstance")
    public Y9Result<String> saveRemindInstance(@RequestParam @NotBlank String processInstanceId,
        @RequestParam(required = false) String taskIds, @RequestParam Boolean process,
        @RequestParam(required = false) String arriveTaskKey, @RequestParam(required = false) String completeTaskKey) {
        String tenantId = Y9LoginUserHolder.getTenantId(), userId = Y9LoginUserHolder.getPositionId();
        return remindInstanceApi.saveRemindInstance(tenantId, userId, processInstanceId, taskIds, process,
            arriveTaskKey, completeTaskKey);

    }

    /**
     * 获取未办理任务列表
     *
     * @param processInstanceId 流程实例id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "/taskList")
    public Y9Result<Map<String, Object>> taskList(@RequestParam @NotBlank String processInstanceId) {
        Map<String, Object> retMap = new HashMap<>(16);
        Y9Page<TaskModel> taskPage;
        String tenantId = Y9LoginUserHolder.getTenantId();
        try {
            taskPage = taskApi.findListByProcessInstanceId(tenantId, processInstanceId, 1, 500);
            List<TaskModel> list = taskPage.getRows();
            ObjectMapper objectMapper = new ObjectMapper();
            List<TaskModel> taskList = objectMapper.convertValue(list, new TypeReference<List<TaskModel>>() {});
            List<Map<String, Object>> items = new ArrayList<Map<String, Object>>();
            int serialNumber = 0;
            Map<String, Object> mapTemp = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currentTime = new Date();
            RemindInstanceModel remindInstance = remindInstanceApi
                .getRemindInstance(tenantId, Y9LoginUserHolder.getPositionId(), processInstanceId).getData();
            retMap.put("remindType", "");
            retMap.put("taskIds", "");
            if (remindInstance != null) {
                retMap.put("remindType", remindInstance.getRemindType());
                retMap.put("taskIds", remindInstance.getTaskId());
            }
            for (TaskModel task : taskList) {
                mapTemp = new HashMap<>(16);
                String taskId = task.getId();
                String taskName = task.getName();
                mapTemp.put("taskId", taskId);
                mapTemp.put("userName", StringUtils.isBlank(task.getAssignee()) ? ""
                    : orgUnitApi.getOrgUnit(tenantId, task.getAssignee()).getData().getName());
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
            LOGGER.error("获取未办理任务失败", e);
        }
        return Y9Result.failure("获取失败");
    }

}
