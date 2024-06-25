package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.service.WorkflowProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/procDef")
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
    public List<Map<String, Object>> getBpmList(String processDefinitionId, Boolean isFilter) {
        return workflowProcessDefinitionService.getBpmList(processDefinitionId, isFilter);
    }

    /**
     * 获取任务节点信息和流程定义信息，包含流程启动节点start
     *
     * @param processDefinitionId 流程定义ID
     * @return List<Map<String, Object>>
     */
    @RequestMapping(value = "/getBpmListContainStart")
    public List<Map<String, Object>> getBpmListContainStart(String processDefinitionId) {
        return workflowProcessDefinitionService.getBpmListContainStart(processDefinitionId);
    }

    /**
     * 获取指定流程定义的所有流程定义Id
     *
     * @param processDefinitionKey 流程定义Key
     * @return List<String>
     */
    @RequestMapping(value = "/getProcDefIds", method = RequestMethod.GET)
    public List<String> getProcDefIds(@RequestParam(required = false) String processDefinitionKey) {
        return workflowProcessDefinitionService.getProcessDefinitionIds(processDefinitionKey);
    }

    /**
     * 获取指定流程定义的所有版本号
     *
     * @param processDefinitionKey 流程定义Key
     * @return List<Integer>
     */
    @RequestMapping(value = "/getProcDefVersions", method = RequestMethod.GET)
    public List<Integer> getProcDefVersions(@RequestParam(required = false) String processDefinitionKey) {
        return workflowProcessDefinitionService.getProcessDefinitionVersions(processDefinitionKey);
    }

    /**
     * 获取任务节点信息
     *
     * @param processDefinitionId 流程定义ID
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/getTaskList")
    public Map<String, Object> getTaskByProcDef(@RequestParam String processDefinitionId) {
        Map<String, Object> taskMap = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        List<FlowElement> activitieList =
            workflowProcessDefinitionService.getFilteredActivityImpls(processDefinitionId);
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMap = new HashMap<>(16);
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            list.add(tempMap);
        }
        taskMap.put("items", list);
        taskMap.put("success", true);
        return taskMap;
    }

    /**
     * 获取
     *
     * @param processDefinitionId 流程定义ID
     * @return Map<String, Object>
     */
    @RequestMapping(value = "/getTaskMap", method = RequestMethod.GET)
    public Map<String, Object> getTaskMap(@RequestParam(required = false) String processDefinitionId) {
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
        return taskMap;
    }

    /**
     * 查询流程定义，获取map，其key为流程定义对象ID，其value为流程定义对象名称
     * 
     * @return Map<String, String>
     */
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    public Map<String, String> runningShow() {
        // 获取流程定义对象ID和流程定义对象名称
        return workflowProcessDefinitionService.procDefIdMap();
    }
}
