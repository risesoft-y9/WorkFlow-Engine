package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkflowProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/procDef", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessDefinitionController {

    private final WorkflowProcessDefinitionService workflowProcessDefinitionService;

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId 流程定义ID
     * @param isFilter 是否过滤掉开始节点和结束节点
     * @return List<Map<String, Object>>
     */
    @RequestMapping(value = "/getBpmList")
    public Y9Result<List<Map<String, Object>>> getBpmList(String processDefinitionId, Boolean isFilter) {
        List<Map<String, Object>> bpmList = workflowProcessDefinitionService.getBpmList(processDefinitionId, isFilter);
        return Y9Result.success(bpmList);
    }

    /**
     * 获取任务节点信息和流程定义信息，包含流程启动节点start
     *
     * @param processDefinitionId 流程定义ID
     * @return List<Map<String, Object>>
     */
    @RequestMapping(value = "/getBpmListContainStart")
    public Y9Result<List<Map<String, Object>>> getBpmListContainStart(String processDefinitionId) {
        List<Map<String, Object>> bpmList =
            workflowProcessDefinitionService.getBpmListContainStart(processDefinitionId);
        return Y9Result.success(bpmList);
    }

    /**
     * 获取指定流程定义的所有流程定义Id
     *
     * @param processDefinitionKey 流程定义Key
     * @return List<String>
     */
    @GetMapping(value = "/getProcDefIds")
    public Y9Result<List<String>> getProcDefIds(@RequestParam(required = false) String processDefinitionKey) {
        List<String> definitionIds = workflowProcessDefinitionService.getProcessDefinitionIds(processDefinitionKey);
        return Y9Result.success(definitionIds);
    }

    /**
     * 获取指定流程定义的所有版本号
     *
     * @param processDefinitionKey 流程定义Key
     * @return List<Integer>
     */
    @GetMapping(value = "/getProcDefVersions")
    public Y9Result<List<Integer>> getProcDefVersions(@RequestParam(required = false) String processDefinitionKey) {
        List<Integer> versions = workflowProcessDefinitionService.getProcessDefinitionVersions(processDefinitionKey);
        return Y9Result.success(versions);
    }

    /**
     * 获取任务节点信息
     *
     * @param processDefinitionId 流程定义ID
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/getTaskList")
    public Y9Result<List<Map<String, Object>>> getTaskByProcDef(@RequestParam String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<FlowElement> activitieList =
            workflowProcessDefinitionService.getFilteredActivityImpls(processDefinitionId);
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMap = new HashMap<>(16);
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            list.add(tempMap);
        }
        // taskMap.put("items", list);
        return Y9Result.success(list);
    }

    /**
     * 获取
     *
     * @param processDefinitionId 流程定义ID
     * @return Map<String, Object>
     */
    @GetMapping(value = "/getTaskMap")
    public Y9Result<Map<String, Object>> getTaskMap(@RequestParam(required = false) String processDefinitionId) {
        Map<String, Object> taskMap = new LinkedHashMap<>();
        taskMap.put("", "--");
        if (StringUtils.isNotBlank(processDefinitionId)) {
            List<String> filterList = new ArrayList<>();
            filterList.add(SysVariables.STARTEVENT);
            List<FlowElement> list =
                workflowProcessDefinitionService.getFilteredActivityImpls(processDefinitionId, filterList);
            for (FlowElement activity : list) {
                taskMap.put(activity.getId(), activity.getName());
            }
        }
        return Y9Result.success(taskMap);
    }

    /**
     * 查询流程定义，获取map，其key为流程定义对象ID，其value为流程定义对象名称
     *
     * @return Map<String, String>
     */
    @GetMapping(value = "/map")
    public Y9Result<Map<String, String>> runningShow() {
        // 获取流程定义对象ID和流程定义对象名称
        Map<String, String> defIdMap = workflowProcessDefinitionService.procDefIdMap();
        return Y9Result.success(defIdMap);
    }
}
