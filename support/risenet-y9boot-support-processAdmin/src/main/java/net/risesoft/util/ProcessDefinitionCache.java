package net.risesoft.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.impl.RepositoryServiceImpl;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.ProcessDefinition;

import net.risesoft.y9.Y9Context;

/**
 * 流程定义缓存
 * 
 * @author henryyan
 */
/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public class ProcessDefinitionCache {

    private static Map<String, ProcessDefinition> map = new HashMap<String, ProcessDefinition>();

    private static Map<String, List<FlowElement>> activities = new HashMap<String, List<FlowElement>>();

    private static Map<String, FlowElement> singleActivity = new HashMap<String, FlowElement>();

    private static RepositoryService repositoryService = Y9Context.getBean(RepositoryService.class);

    public static ProcessDefinition get(String processDefinitionId) {
        ProcessDefinition processDefinition = map.get(processDefinitionId);
        if (processDefinition == null) {
            processDefinition = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService).getDeployedProcessDefinition(processDefinitionId);
            if (processDefinition != null) {
                put(processDefinitionId, processDefinition);
            }
        }
        return processDefinition;
    }

    public static FlowElement getActivity(String processDefinitionId, String activityId) {
        ProcessDefinition processDefinition = get(processDefinitionId);
        if (processDefinition != null) {
            FlowElement activityImpl = singleActivity.get(processDefinitionId + "_" + activityId);
            if (activityImpl != null) {
                return activityImpl;
            }
        }
        return null;
    }

    public static String getActivityName(String processDefinitionId, String activityId) {
        FlowElement activity = getActivity(processDefinitionId, activityId);
        if (activity != null) {
            return activity.getName();
        }
        return null;
    }

    public static void put(String processDefinitionId, ProcessDefinition processDefinition) {
        map.put(processDefinitionId, processDefinition);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        activities.put(processDefinitionId, flowElements);
        for (FlowElement activityImpl : flowElements) {
            singleActivity.put(processDefinitionId + "_" + activityImpl.getId(), activityImpl);
        }
    }

    public static void setRepositoryService(RepositoryService repositoryService) {
        ProcessDefinitionCache.repositoryService = repositoryService;
    }

}
