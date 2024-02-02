package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FlowElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.service.WorkflowProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@Controller
@RequestMapping(value = "/procDef")
public class ProcessDefinitionController {

    @Autowired
    private WorkflowProcessDefinitionService workflowProcessDefinitionService;

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId
     * @param isFilter 是否过滤掉开始节点和结束节点
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmList")
    public List<Map<String, Object>> getBpmList(String processDefinitionId, Boolean isFilter) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = workflowProcessDefinitionService.getBpmList(processDefinitionId, isFilter);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取任务节点信息和流程定义信息，包含流程启动节点start
     *
     * @param processDefinitionId
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getBpmListContainStart")
    public List<Map<String, Object>> getBpmListContainStart(String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<>();
        try {
            list = workflowProcessDefinitionService.getBpmListContainStart(processDefinitionId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取指定流程定义的所有流程定义Id
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    @RequestMapping(value = "/getProcDefIds", method = RequestMethod.GET)
    @ResponseBody
    public List<String> getProcDefIds(@RequestParam(required = false) String processDefinitionKey) {
        return workflowProcessDefinitionService.getProcessDefinitionIds(processDefinitionKey);
    }

    /**
     * 获取指定流程定义的所有版本号
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    @RequestMapping(value = "/getProcDefVersions", method = RequestMethod.GET)
    @ResponseBody
    public List<Integer> getProcDefVersions(@RequestParam(required = false) String processDefinitionKey) {
        return workflowProcessDefinitionService.getProcessDefinitionVersions(processDefinitionKey);
    }

    /**
     * 获取任务节点信息
     *
     * @param processDefinitionId
     * @return
     */
    @RequestMapping(value = "/getTaskList")
    @ResponseBody
    public Map<String, Object> getTaskByProcDef(@RequestParam String processDefinitionId) {
        Map<String, Object> taskMap = new HashMap<>(16);
        List<Map<String, Object>> list = new ArrayList<>();
        List<String> filterList = new ArrayList<>();
        filterList.add(SysVariables.STARTEVENT);
        List<FlowElement> activitieList =
            workflowProcessDefinitionService.getFilteredActivityImpls(processDefinitionId);
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMap = new HashMap<String, Object>(16);
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
     * @param processDefinitionId
     * @return
     */
    @RequestMapping(value = "/getTaskMap", method = RequestMethod.GET)
    @ResponseBody
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
     * @param model
     * @return
     */
    @RequestMapping(value = "/map", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, String> runningShow(Model model) {
        // 获取流程定义对象ID和流程定义对象名称
        return workflowProcessDefinitionService.procDefIdMap();
    }
}
