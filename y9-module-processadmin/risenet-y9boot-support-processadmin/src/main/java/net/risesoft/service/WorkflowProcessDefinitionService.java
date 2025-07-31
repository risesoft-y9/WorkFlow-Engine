package net.risesoft.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.processadmin.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@Service
@RequiredArgsConstructor
@DependsOn({"repositoryService"})
public class WorkflowProcessDefinitionService {

    private final RepositoryService repositoryService;

    private final CustomProcessDefinitionService customProcessDefinitionService;

    /**
     *
     * @param activities 流程定义中的任务节点List
     * @param activityId 任务节点Id（例如 outerflow）
     * @param propertiesNameList 指定要获取的属性的列表
     * @return
     */
    public Map<String, String> getActivityProperties(List<FlowElement> activities, String activityId,
        List<String> propertiesNameList) {
        Map<String, String> result = new HashMap<>(16);
        try {
            for (FlowElement activity : activities) {
                if (activityId.equals(activity.getId())) {
                    for (String s : propertiesNameList) {
                        UserTask userTask = (UserTask)activity;
                        Object obj = userTask.getBehavior();
                        if (obj instanceof SequentialMultiInstanceBehavior) {
                            result.put(s, SysVariables.SEQUENTIAL);
                        } else if (obj instanceof ParallelMultiInstanceBehavior) {
                            result.put(s, SysVariables.PARALLEL);
                        } else {
                            result.put(s, "");
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取某节点的指定属性
     *
     * @param processDefinitionId 流程定义ID
     * @param activityId 任务节点Id（例如 outerflow）
     * @param propertiesNameList 指定要获取的属性的列表
     * @return
     */
    public Map<String, String> getActivityProperties(String processDefinitionId, String activityId,
        List<String> propertiesNameList) {
        Map<String, String> result = new HashMap<>(16);
        try {
            List<FlowElement> list = customProcessDefinitionService.getFlowElements(processDefinitionId);
            return getActivityProperties(list, activityId, propertiesNameList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 根据processDefinition获取某一节点的属性
     *
     * @param processDefinitionId 流程定义ID
     * @param activityId 任务节点Id（例如 outerflow）
     * @param propertyName 指定要获取的属性的列表
     * @return
     */
    public String getActivityProperty(String processDefinitionId, String activityId, String propertyName) {
        Map<String, String> map = new HashMap<>(16);
        List<String> propertiesNameList = new ArrayList<>();
        propertiesNameList.add(propertyName);
        try {
            map = getActivityProperties(processDefinitionId, activityId, propertiesNameList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map.get(propertyName);
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param processDefinitionId
     * @param isFilter 是否过滤掉开始节点和结束节点
     * @return
     */
    public List<Map<String, Object>> getBpmList(String processDefinitionId, Boolean isFilter) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<FlowElement> feList = customProcessDefinitionService.getFlowElements(processDefinitionId);
        if (isFilter != null && isFilter) {
            feList.removeIf(fe -> fe instanceof Gateway || fe instanceof StartEvent || fe instanceof EndEvent);
        }
        for (FlowElement activity : feList) {
            Map<String, Object> tempMap = new LinkedHashMap<>();
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            list.add(tempMap);
        }
        Map<String, Object> tempMap = new LinkedHashMap<>();
        tempMap.put("taskDefKey", "");
        tempMap.put("taskDefName", "流程");
        list.add(0, tempMap);
        return list;
    }

    /**
     * 获取任务节点信息和流程定义信息,包含开始节点
     *
     * @param processDefinitionId
     * @return
     */
    public List<Map<String, Object>> getBpmListContainStart(String processDefinitionId) {
        List<Map<String, Object>> list = new ArrayList<>();
        List<FlowElement> feList = customProcessDefinitionService.getFlowElements(processDefinitionId);
        feList.removeIf(activity -> activity instanceof Gateway || activity instanceof EndEvent
            || activity instanceof SequenceFlow);
        feList.forEach(activity -> {
            Map<String, Object> tempMap = new LinkedHashMap<>();
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            list.add(tempMap);
        });
        Map<String, Object> tempMap = new LinkedHashMap<>();
        tempMap.put("taskDefKey", "");
        tempMap.put("taskDefName", "流程");
        list.add(0, tempMap);
        return list;
    }

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点，同时过滤掉filterList内包含的类型节点
     *
     * @param processDefinitionId
     * @return
     */
    public List<FlowElement> getFilteredActivityImpls(String processDefinitionId, List<String> filterList) {
        List<FlowElement> list = new ArrayList<>();
        try {
            list = customProcessDefinitionService.getFlowElements(processDefinitionId);
            list.removeIf(activity -> activity instanceof Gateway || filterList.contains(activity.getName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取指定processDefinitionKey的所有历史流程定义Id
     *
     * @param processDefinitionKey
     * @return
     */
    public List<String> getProcessDefinitionIds(String processDefinitionKey) {
        List<String> list = new ArrayList<>();
        List<ProcessDefinition> processDefinitionList = getProcessDefinitions(processDefinitionKey);
        for (ProcessDefinition pd : processDefinitionList) {
            list.add(pd.getId());
        }
        return list;
    }

    /**
     * 获取指定processDefinitionKey的所有历史流程定义
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    public List<ProcessDefinition> getProcessDefinitions(String processDefinitionKey) {
        List<ProcessDefinition> processDefinitionList = new ArrayList<>();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            ProcessDefinitionQuery processDefinitionQuery = repositoryService.createProcessDefinitionQuery()
                .processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().desc();
            processDefinitionList = processDefinitionQuery.list();
        }
        return processDefinitionList;
    }

    /**
     * 获取指定流程定义的所有版本号
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    public List<Integer> getProcessDefinitionVersions(String processDefinitionKey) {
        List<Integer> list = new ArrayList<>();
        List<ProcessDefinition> processDefinitionList = getProcessDefinitions(processDefinitionKey);
        for (ProcessDefinition pd : processDefinitionList) {
            list.add(pd.getVersion());
        }
        return list;
    }

    /**
     * Description: 获取流程定义Id
     *
     * @return
     */
    public Map<String, String> procDefIdMap() {
        Map<String, String> procDefMap = new HashMap<>(16);
        ProcessDefinitionQuery processDefinitionQuery =
            repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        for (ProcessDefinition pd : processDefinitionList) {
            procDefMap.put(pd.getId(), pd.getName());
        }
        return procDefMap;
    }
}
