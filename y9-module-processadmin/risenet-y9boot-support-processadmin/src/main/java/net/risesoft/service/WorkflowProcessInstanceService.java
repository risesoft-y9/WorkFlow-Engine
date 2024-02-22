package net.risesoft.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.FlowNode;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.Process;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.util.MapUtil;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
public class WorkflowProcessInstanceService {

    @SuppressWarnings("unused")
    private static final String ENDEVENT = "endEvent";

    protected Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    protected RuntimeService runtimeService;

    @Autowired
    protected RepositoryService repositoryService;

    @Autowired
    protected TaskService taskService;

    @Autowired
    protected WorkflowTaskService workflowTaskService;

    /**
     * 根据流程实例ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public ProcessInstance findProcessInstance(String processInstanceId) {
        // 找到流程实例
        ProcessInstance processInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            try {
                processInstance =
                    runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                if (processInstance == null) {

                    throw new Exception("流程实例未找到!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processInstance;
    }

    /**
     * 根据任务ID获取对应的流程实例
     *
     * @param taskId 任务ID
     * @return
     * @throws Exception
     */
    public ProcessInstance findProcessInstanceByTaskId(String taskId) {
        ProcessInstance processInstance = null;
        if (StringUtils.isNotBlank(taskId)) {
            String processInstanceId = workflowTaskService.getTaskByTaskId(taskId).getProcessInstanceId();
            processInstance = findProcessInstance(processInstanceId);
        }
        return processInstance;
    }

    /**
     * 查找当前任务节点的目标节点中指定类型的特定节点
     *
     * @param taskId 任务Id
     * @param targetType 节点类型(例如endEvent)
     * @return
     */
    public Map<String, String> getCertainTaskTargets(String taskId, String targetType) {
        Map<String, String> map = new HashMap<String, String>(16);
        List<SequenceFlow> outTransitions = getPvmTransitions(taskId);
        for (SequenceFlow tr : outTransitions) {
            if (tr.getTargetFlowElement() instanceof EndEvent) {
                map.put(tr.getTargetFlowElement().getId(), tr.getTargetFlowElement().getName());
            }
        }
        return map;
    }

    /**
     * 查找当前任务节点的目标节点中指定类型的特定节点
     *
     * @param taskId 任务Id
     * @param targetType 节点类型(例如endEvent)
     * @return
     */
    public List<String> getCertainTaskTargetsId(String taskId, String targetType) {
        Map<String, String> targetTasks = getCertainTaskTargets(taskId, targetType);
        return MapUtil.getKey(targetTasks);
    }

    /**
     * 获取当前任务节点的目标节点
     *
     * @param processDefinitionId
     * @param taskDefKey
     * @return
     */
    public List<Map<String, String>> getCurrentTaskTargets(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetTasks = new ArrayList<Map<String, String>>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        boolean isGateway = false;
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            // 如果是任务节点
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
                if (flowElement instanceof StartEvent) {
                    StartEvent task = (StartEvent)flowElement;
                    list = task.getOutgoingFlows();
                } else if (flowElement instanceof UserTask) {
                    UserTask task = (UserTask)flowElement;
                    list = task.getOutgoingFlows();
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway task = (ExclusiveGateway)flowElement;
                    list = task.getOutgoingFlows();
                }
                if (!isGateway) {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        FlowElement fe = tr.getTargetFlowElement();
                        if (!(fe instanceof EndEvent)) {
                            map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name)) {
                                // 如果输出线上有名称，则使用线上的名称作为路由名称
                                map.put(SysVariables.TASKDEFNAME, name);
                            } else {
                                // 如果输出线上没有名称，则使用目标节点名称作为路由名称
                                map.put(SysVariables.TASKDEFNAME, tr.getTargetFlowElement().getName());
                            }
                        }
                        if (tr.getTargetFlowElement() instanceof ExclusiveGateway) {
                            taskDefKey = tr.getTargetFlowElement().getId();
                            // 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                            i = -1;
                            isGateway = true;
                            break;
                        }
                        targetTasks.add(map);
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && !(fe instanceof EndEvent)) {
                            map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                            map.put(SysVariables.TASKDEFNAME, tr.getName());
                            UserTask userTask = (UserTask)fe;
                            if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                                map.put(SysVariables.MULTIINSTANCE, SysVariables.SEQUENTIAL);
                            } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                                map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                            }
                            targetTasks.add(map);
                        }

                    }
                }
            }
        }
        return targetTasks;
    }

    /**
     * 根据流程定义Key（例如luohubanwen）获取流程定义id（例如luohubanwen:1:23145）
     *
     * @param processDefinitionKey
     * @return
     */
    public String getProcessDefinitionId(String processDefinitionKey) {
        ProcessDefinitionQuery processDefinitionQuery =
            repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        String processDefinitionId = "";
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            if (processDefinitionKey.equals(processDefinition.getKey())) {
                processDefinitionId = processDefinition.getId();
            }
        }
        return processDefinitionId;
    }

    /**
     * 根据流程定义key查找对应的所有流程实例Id
     *
     * @param processDefinitionKey 流程定义key
     * @return
     */
    public List<String> getProcessInstanceIds(String processDefinitionKey) {
        List<String> result = new ArrayList<String>();
        List<ProcessInstance> processInstanceList = getProcessInstances(processDefinitionKey);
        if (processInstanceList.size() > 0) {
            for (ProcessInstance entity : processInstanceList) {
                result.add(entity.getProcessInstanceId());
            }
        }
        return result;
    }

    /**
     * 根据流程定义key查找对应的所有流程实例
     *
     * @param processDefinitionKey 流程定义key
     * @return
     */
    public List<ProcessInstance> getProcessInstances(String processDefinitionKey) {
        List<ProcessInstance> list = new ArrayList<ProcessInstance>();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            list = runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey).list();
        }
        return list;
    }

    /**
     * 获取activitiId的默认输出线路
     *
     * @param processDefinitionId 流程定义Id
     * @param activitiId 某个节点Id，例如outflow
     * @return
     */
    /*
     * public List<PvmTransition> getDefultPvmTransitions(String
     * processDefinitionId, String taskDefKey) { boolean isGateway = false;//
     * 记录当前节点是否是Gateway类型节点 List<PvmTransition> outTransitions = new
     * ArrayList<PvmTransition>(); ProcessDefinitionEntity def =
     * (ProcessDefinitionEntity) ((RepositoryServiceImpl)
     * repositoryService).getDeployedProcessDefinition(processDefinitionId);
     * List<ActivityImpl> activitiList = def.getActivities(); // List<ActivityImpl>
     * activitiList = def.getInitialActivityStack(); for (int i = 0; i <
     * activitiList.size(); i++) { ActivityImpl activityImpl = activitiList.get(i);
     * String id = activityImpl.getId(); if (taskDefKey.equals(id)) { outTransitions
     * = activityImpl.getOutgoingTransitions();// 获取从某个节点出来的所有线路 if (!isGateway) {
     * for (PvmTransition tr : outTransitions) { PvmActivity ac =
     * tr.getDestination(); // 获取线路的终点节点 //
     * 根据activiti流程图的特点，如果流程图中当前任务节点有多条目标节点，那么要使用Gateway类型节点，
     * 然后从Gateway类型节点向外流向多个目标节点 if
     * (ac.getProperty("type").toString().endsWith("Gateway")) { taskDefKey =
     * ac.getId(); i = -1;//
     * 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1 isGateway =
     * true; break; } } } else { List<PvmTransition> tempList = new
     * ArrayList<PvmTransition>(); for (PvmTransition pt : outTransitions) {//
     * 获取默认路由 String conditionText = (String) pt.getProperty("conditionText"); if
     * (StringUtils.isBlank(conditionText)) { tempList.add(pt); } } outTransitions =
     * tempList; } } } return outTransitions; }
     */

    /**
     * 根据taskId获取某个节点所有的输出线路
     *
     * @param taskId 任务Id
     * @return
     */
    public List<SequenceFlow> getPvmTransitions(String taskId) {
        boolean isGateway = false;
        Task task = workflowTaskService.getTaskByTaskId(taskId);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list = new ArrayList<SequenceFlow>();
        List<SequenceFlow> flowList = new ArrayList<SequenceFlow>();
        String activitiId = workflowTaskService.getActivitiIdByTask(task);
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            String id = flowElement.getId();
            if (activitiId.equals(id)) {
                if (flowElement instanceof StartEvent) {
                    StartEvent event = (StartEvent)flowElement;
                    list = event.getOutgoingFlows();
                } else if (flowElement instanceof UserTask) {
                    UserTask event = (UserTask)flowElement;
                    list = event.getOutgoingFlows();
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway event = (ExclusiveGateway)flowElement;
                    list = event.getOutgoingFlows();
                }
                if (!isGateway) {
                    for (SequenceFlow tr : list) {
                        FlowElement fe = tr.getTargetFlowElement();
                        if (fe instanceof ExclusiveGateway) {
                            activitiId = fe.getId();
                            // 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                            i = -1;
                            isGateway = true;
                            break;
                        }
                        flowList.add(tr);
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        flowList.add(tr);
                    }
                    list = flowList;
                }
            }
        }
        return list;
    }

    /**
     * 获取activitiId的所有输出线路
     *
     * @param processDefinitionId 流程定义Id
     * @param taskDefKey 某个节点Id，例如outflow
     * @return
     */
    public List<SequenceFlow> getPvmTransitions(String processDefinitionId, String taskDefKey) {
        boolean isGateway = false;
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list = new ArrayList<SequenceFlow>();
        List<SequenceFlow> flowList = new ArrayList<SequenceFlow>();
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            String id = flowElement.getId();
            if (taskDefKey.equals(id)) {
                if (flowElement instanceof StartEvent) {
                    StartEvent event = (StartEvent)flowElement;
                    list = event.getOutgoingFlows();
                } else if (flowElement instanceof UserTask) {
                    UserTask event = (UserTask)flowElement;
                    list = event.getOutgoingFlows();
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway event = (ExclusiveGateway)flowElement;
                    list = event.getOutgoingFlows();
                }
                if (!isGateway) {
                    for (SequenceFlow tr : list) {
                        FlowElement fe = tr.getTargetFlowElement();
                        // 根据activiti流程图的特点，如果流程图中当前任务节点有多条目标节点，那么要使用Gateway类型节点，然后从Gateway类型节点向外流向多个目标节点
                        if (fe instanceof ExclusiveGateway) {
                            taskDefKey = fe.getId();
                            // 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                            i = -1;
                            isGateway = true;
                            break;
                        }
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        String conditionText = tr.getConditionExpression();
                        if (StringUtils.isNotBlank(conditionText)) {
                            flowList.add(tr);
                        }
                    }
                    list = flowList;
                }
            }
        }
        return list;
    }

    /**
     * 根据taskId获取某个节点除去end节点和默认路由节点的所有的输出线路的个数
     *
     * @param taskId 任务Id
     * @return
     */
    public int getPvmTransitionsCount(String taskId) {
        int count = 0;
        List<SequenceFlow> outTransitions = getPvmTransitions(taskId);
        List<SequenceFlow> list = new ArrayList<SequenceFlow>();
        for (SequenceFlow tr : outTransitions) {
            if (tr.getTargetFlowElement() instanceof EndEvent) {
                continue;
            }
            list.add(tr);
        }
        if (list != null) {
            count = list.size();
        }
        return count;
    }

    /**
     * 获取上一任务发送人
     *
     * @param taskId 任务Id
     * @return
     */
    public String getSenderUser(String taskId) {
        String senderId = "";
        if (StringUtils.isNotBlank(taskId)) {
            senderId = (String)taskService.getVariable(taskId, SysVariables.TASKSENDERID);
        }
        return senderId;
    }

    public List<Map<String, String>> getStartTaskTargets(String processDefinitionKey) {
        List<Map<String, String>> targetTasks = new ArrayList<Map<String, String>>();
        ProcessDefinitionQuery processDefinitionQuery =
            repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        String processDefinitionId = "";
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        for (ProcessDefinition processDefinition : processDefinitionList) {
            if (processDefinitionKey.equals(processDefinition.getKey())) {
                processDefinitionId = processDefinition.getId();
            }
        }
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        Process process = bpmnModel.getMainProcess();
        Collection<FlowElement> flowElements = process.getFlowElements();

        List<StartEvent> startEvents = process.findFlowElementsOfType(StartEvent.class);
        FlowElement sourceNode = startEvents.get(0);
        for (FlowElement flowElement : flowElements) {
            if (flowElement.getId().equals(sourceNode.getId())) {
                List<SequenceFlow> sequenceFlows = ((FlowNode)sourceNode).getOutgoingFlows();
                for (SequenceFlow sequenceFlow : sequenceFlows) {
                    FlowElement targetFlowElement = process.getFlowElement(sequenceFlow.getTargetRef());
                    if (targetFlowElement instanceof Gateway) {
                        sourceNode = targetFlowElement;
                    } else {
                        org.flowable.bpmn.model.Task targetTask = (org.flowable.bpmn.model.Task)targetFlowElement;
                        Map<String, String> map = new HashMap<String, String>(16);
                        map.put("taskName", targetTask.getName());
                        map.put("taskId", targetTask.getId());
                        targetTasks.add(map);
                    }
                }
            }
        }
        return targetTasks;
    }

    /**
     * 查找当前任务节点的目标节点中是否包含某一类型的特定节点
     *
     * @param taskId 任务Id
     * @param type 节点类型(例如endEvent)
     * @return
     */
    public boolean isContainTaskTarget(String taskId, String type) {
        boolean result = false;
        List<SequenceFlow> flowElements = getPvmTransitions(taskId);
        for (SequenceFlow tr : flowElements) {
            FlowElement flowElement = tr.getTargetFlowElement();
            if (flowElement instanceof EndEvent) {
                result = true;
            }
        }
        return result;
    }

    /**
     * 查找当前任务节点的目标节点中是否包含某些类型的特定节点
     *
     * @param taskId 任务Id
     * @return
     */
    public boolean isContainTaskTargets(String taskId) {
        boolean result = false;
        List<SequenceFlow> flowElements = getPvmTransitions(taskId);
        for (SequenceFlow tr : flowElements) {
            FlowElement flowElement = tr.getTargetFlowElement();
            if (flowElement instanceof EndEvent) {
                result = true;
            }
        }
        return result;
    }

}
