package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.runtime.Execution;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customProcessDefinitionService")
public class CustomProcessDefinitionServiceImpl implements CustomProcessDefinitionService {

    private final RepositoryService repositoryService;

    private final TaskService taskService;

    private final RuntimeService runtimeService;

    private final CustomHistoricTaskService customHistoricTaskService;

    /**
     * 获取ActivityImpl的list
     *
     * @param bpmnModel bpmnModel
     * @return List<FlowElement>
     */
    private List<FlowElement> getActivityImpls(BpmnModel bpmnModel) {
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        return (List<FlowElement>)process.getFlowElements();
    }

    @Override
    public Y9Result<List<TargetModel>> getContainEndEvent4UserTask(String processDefinitionId) {
        List<TargetModel> userTaskList = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list;
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof UserTask) {
                UserTask event = (UserTask)flowElement;
                list = event.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement element = tr.getTargetFlowElement();
                    if (element instanceof ExclusiveGateway) {
                        ExclusiveGateway event1 = (ExclusiveGateway)element;
                        List<SequenceFlow> list1 = event1.getOutgoingFlows();
                        for (SequenceFlow tr1 : list1) {
                            FlowElement element1 = tr1.getTargetFlowElement();
                            if (element1 instanceof EndEvent) {
                                TargetModel targetModel = new TargetModel();
                                targetModel.setTaskDefKey(event.getId());
                                targetModel.setTaskDefName(event.getName());
                                userTaskList.add(targetModel);
                                break;
                            }
                        }
                    } else if (element instanceof EndEvent) {
                        TargetModel targetModel = new TargetModel();
                        targetModel.setTaskDefKey(event.getId());
                        targetModel.setTaskDefName(event.getName());
                        userTaskList.add(targetModel);
                        break;
                    }
                }
            }
        }
        return Y9Result.success(userTaskList);
    }

    @Override
    public String getEndNodeKeyByTaskId(String taskId) {
        List<SequenceFlow> outTransitions = getPvmTransitions(taskId);
        for (SequenceFlow tr : outTransitions) {
            if (tr.getTargetFlowElement() instanceof EndEvent) {
                return tr.getTargetFlowElement().getId();
            }
        }
        return "";
    }

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点
     *
     * @param bpmnModel bpmnModel
     * @return List<FlowElement>
     */
    private List<FlowElement> getFilteredActivityImpls(BpmnModel bpmnModel) {
        List<FlowElement> list = getActivityImpls(bpmnModel);
        List<FlowElement> resultList = new ArrayList<>();
        if (!list.isEmpty()) {
            // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
            resultList.addAll(list);
        }
        resultList.removeIf(e -> e instanceof Gateway);
        return resultList;
    }

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点
     *
     * @param processDefinitionId 流程定义ID
     * @return List<FlowElement>
     */
    public List<FlowElement> getFilteredActivityImpls(String processDefinitionId) {
        return getFilteredActivityImpls(repositoryService.getBpmnModel(processDefinitionId));
    }

    @Override
    public Y9Result<List<FlowElementModel>> getFlowElement(String processDefinitionId, Boolean isContainStartNode) {
        List<FlowElementModel> list = new ArrayList<>();
        List<FlowElement> activitieList = new ArrayList<>();
        if (!isContainStartNode) {
            List<FlowElement> listTemp;
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            listTemp = (List<FlowElement>)process.getFlowElements();
            if (!listTemp.isEmpty()) {
                // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
                activitieList.addAll(listTemp);
            }
            activitieList.removeIf(e -> e instanceof Gateway || e instanceof StartEvent || e instanceof EndEvent);
        } else {
            activitieList = getFilteredActivityImpls(processDefinitionId);
        }
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMapA = new LinkedHashMap<>();
            FlowElementModel feModel = new FlowElementModel();
            feModel.setElementKey(activity.getId());
            feModel.setElementName(activity.getName());
            if (activity instanceof UserTask) {
                feModel.setType("UserTask");
            } else if (activity instanceof SequenceFlow) {
                feModel.setType("SequenceFlow");
            }
            if (activity instanceof UserTask) {
                UserTask userTask = (UserTask)activity;
                if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                    feModel.setMultiInstance(SysVariables.SEQUENTIAL);
                } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                    feModel.setMultiInstance(SysVariables.PARALLEL);
                } else {
                    feModel.setMultiInstance(SysVariables.COMMON);
                }
            }
            if (activity.getName() != null && !activity.getName().isEmpty() && activity.getId() != null
                && !activity.getId().isEmpty()) {
                list.add(feModel);
            }
        }
        FlowElementModel feModel = new FlowElementModel();
        feModel.setElementKey("");
        feModel.setElementName("流程");
        feModel.setType("Process");
        list.add(0, feModel);
        return Y9Result.success(list);
    }

    @Override
    public Y9Result<List<TargetModel>> getNodes(String processDefinitionId, Boolean isContainStartNode) {
        List<TargetModel> list = new ArrayList<>();
        List<FlowElement> activitieList = new ArrayList<>();
        if (!isContainStartNode) {
            List<FlowElement> listTemp;
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            listTemp = (List<FlowElement>)process.getFlowElements();
            if (!listTemp.isEmpty()) {
                // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
                activitieList.addAll(listTemp);
            }
            activitieList.removeIf(e -> e instanceof Gateway || e instanceof StartEvent || e instanceof EndEvent
                || e instanceof SequenceFlow);
        } else {
            activitieList = getFilteredActivityImpls(processDefinitionId);
        }
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMapA = new LinkedHashMap<>();
            TargetModel targetModel = new TargetModel();
            targetModel.setTaskDefKey(activity.getId());
            targetModel.setTaskDefName(activity.getName());
            if (activity instanceof UserTask) {
                UserTask userTask = (UserTask)activity;
                if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                    targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
                } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                    targetModel.setMultiInstance(SysVariables.PARALLEL);
                } else {
                    targetModel.setMultiInstance(SysVariables.COMMON);
                }
            }
            list.add(targetModel);
        }
        TargetModel targetModel = new TargetModel();
        targetModel.setTaskDefKey("");
        targetModel.setTaskDefName("流程");
        list.add(0, targetModel);
        return Y9Result.success(list);
    }

    @Override
    public String getNodeType(String processDefinitionId, String taskDefKey) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> list = (List<FlowElement>)process.getFlowElements();
        for (FlowElement activity : list) {
            if (taskDefKey.equals(activity.getId())) {
                if (activity instanceof UserTask) {
                    UserTask userTask = (UserTask)activity;
                    Object obj = userTask.getBehavior();
                    if (obj instanceof SequentialMultiInstanceBehavior) {
                        return SysVariables.SEQUENTIAL;
                    } else if (obj instanceof ParallelMultiInstanceBehavior) {
                        return SysVariables.PARALLEL;
                    } else {
                        return SysVariables.COMMON;
                    }
                } else if (activity instanceof SubProcess) {
                    return SysVariables.SUBPROCESS;
                } else if (activity instanceof CallActivity) {
                    return SysVariables.CALLACTIVITY;
                }
            }
        }
        return SysVariables.COMMON;
    }

    @Override
    public Integer getOutPutNodeCount(String taskId) {
        List<SequenceFlow> outTransitions = getPvmTransitions(taskId);
        List<SequenceFlow> list = new ArrayList<>();
        for (SequenceFlow tr : outTransitions) {
            if (tr.getTargetFlowElement() instanceof EndEvent) {
                continue;
            }
            list.add(tr);
        }
        return list.size();
    }

    @Override
    public Y9Result<List<GatewayModel>> getParallelGatewayList(String processDefinitionId, String taskDefKey) {
        List<GatewayModel> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        boolean isGateway = false;
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<>();
                if (flowElement instanceof UserTask) {
                    UserTask task = (UserTask)flowElement;
                    list = task.getOutgoingFlows();
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway task = (ExclusiveGateway)flowElement;
                    list = task.getOutgoingFlows();
                }
                if (!isGateway) {
                    for (SequenceFlow tr : list) {
                        GatewayModel gatewayModel = new GatewayModel();
                        String conditionText = tr.getConditionExpression();
                        FlowElement flowElementTemp = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && flowElementTemp instanceof ParallelGateway) {
                            gatewayModel.setTaskDefKey(flowElementTemp.getId());
                            gatewayModel.setTaskDefName(flowElementTemp.getName());
                            targetNodes.add(gatewayModel);
                        } else if (flowElementTemp instanceof ExclusiveGateway) {
                            /*
                             * 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                             */
                            taskDefKey = flowElementTemp.getId();
                            i = -1;
                            isGateway = true;
                            break;
                        } else {
                            break;
                        }
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        GatewayModel gatewayModel = new GatewayModel();
                        String conditionText = tr.getConditionExpression();
                        FlowElement flowElementTemp = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && (flowElementTemp instanceof ParallelGateway)) {
                            gatewayModel.setTaskDefKey(flowElementTemp.getId());
                            gatewayModel.setTaskDefName(flowElementTemp.getName());
                            targetNodes.add(gatewayModel);
                        }
                    }
                }
            }
        }
        return Y9Result.success(targetNodes);
    }

    /**
     * 根据taskId获取某个节点所有的输出线路
     *
     * @param taskId 任务Id
     * @return List<SequenceFlow>
     */
    public List<SequenceFlow> getPvmTransitions(String taskId) {
        boolean isGateway = false;
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list = new ArrayList<>();
        List<SequenceFlow> flowList = new ArrayList<>();
        Execution execution = runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        String activitiId = execution.getActivityId();
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            if (flowElement instanceof SubProcess) {
                Collection<FlowElement> flowElements2 = ((SubProcess)flowElement).getFlowElements();
                for (FlowElement subFlowElement : flowElements2) {
                    if (!flowElements.contains(subFlowElement)) {
                        flowElements.add(subFlowElement);
                    }
                }
            }
        }
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
                    flowList.addAll(list);
                    list = flowList;
                }
            }
        }
        return list;
    }

    @Override
    public String getStartNodeKeyByProcessDefinitionId(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return flowElement.getId();
            }
        }
        return "";
    }

    @Override
    public String getStartNodeKeyByProcessDefinitionKey(String processDefinitionKey) {
        String processDefinitionId = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey).latestVersion().singleResult().getId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return flowElement.getId();
            }
        }
        return "";
    }

    @Override
    public Y9Result<List<TargetModel>> getTargetNodes(String processDefinitionId, String taskDefKey) {
        List<TargetModel> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        boolean isGateway = false;
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            // 如果是任务节点
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<>();
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
                        TargetModel targetModel = new TargetModel();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (tr.getTargetFlowElement() instanceof ParallelGateway) {
                            break;
                        }
                        /*
                         * 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                         */
                        if (tr.getTargetFlowElement() instanceof ExclusiveGateway) {
                            taskDefKey = tr.getTargetFlowElement().getId();
                            i = -1;
                            isGateway = true;
                            break;
                        }
                        if (fe instanceof EndEvent) {
                            break;
                        }
                        targetModel.setTaskDefKey(tr.getTargetFlowElement().getId());
                        String name = tr.getName();
                        if (StringUtils.isNotBlank(name) && !"skip".equals(name)) {
                            // 如果输出线上有名称且不为skip，则使用线上的名称作为路由名称
                            targetModel.setTaskDefName(name);
                        } else {
                            // 如果输出线上没有名称，则使用目标节点名称作为路由名称
                            targetModel.setTaskDefName(tr.getTargetFlowElement().getName());
                        }
                        targetModel.setRealTaskDefName(tr.getTargetFlowElement().getName());
                        targetNodes.add(targetModel);
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        TargetModel targetModel = new TargetModel();
                        String conditionText = tr.getConditionExpression();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && !(fe instanceof EndEvent)
                            && !(fe instanceof ParallelGateway)) {
                            targetModel.setTaskDefKey(tr.getTargetFlowElement().getId());
                            targetModel.setConditionExpression(tr.getConditionExpression());
                            targetModel.setRealTaskDefName(tr.getTargetFlowElement().getName());
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name) && !"skip".equals(name)) {
                                // 如果输出线上有名称且不为skip，则使用线上的名称作为路由名称
                                targetModel.setTaskDefName(name);
                            } else {
                                // 如果输出线上没有名称，则使用目标节点名称作为路由名称
                                targetModel.setRealTaskDefName(tr.getTargetFlowElement().getName());
                            }
                            if (fe instanceof SubProcess) {
                                targetModel.setMultiInstance(SysVariables.PARALLEL);
                            } else {
                                UserTask userTask = (UserTask)fe;
                                if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                                    targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
                                } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                                    targetModel.setMultiInstance(SysVariables.PARALLEL);
                                }
                            }
                            targetNodes.add(targetModel);
                        }

                    }
                }
            }
        }
        return Y9Result.success(targetNodes);
    }

    @Override
    public List<Map<String, String>> getTargetNodes1(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<>();
        List<String> nameListTemp = new ArrayList<>();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        boolean isGateway = false;
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            // 如果是任务节点
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<>();
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
                        if (tr.getTargetFlowElement() instanceof ExclusiveGateway) {
                            taskDefKey = tr.getTargetFlowElement().getId();
                            // 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                            i = -1;
                            isGateway = true;
                            break;
                        } else {
                            Map<String, String> map = new HashMap<>(16);
                            String name = tr.getName();
                            /*
                             * 如果输出线上有名称，则使用线上的名称作为路由名称,否则就使用节点名称
                             */
                            if (StringUtils.isBlank(name) && !"skip".equals(name)) {
                                name = tr.getTargetFlowElement().getName();
                            } else {
                                map.put(SysVariables.TASKDEFNAME, name);
                            }
                            map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                            if (!nameListTemp.contains(name)) {
                                targetNodes.add(map);
                                nameListTemp.add(name);
                            }
                        }
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && !(fe instanceof EndEvent)) {
                            String name = tr.getName();
                            /*
                             * 如果输出线上有名称，则使用线上的名称作为路由名称,否则就使用节点名称
                             */
                            if (StringUtils.isBlank(name) && !"skip".equals(name)) {
                                name = tr.getTargetFlowElement().getName();
                            }
                            if (!nameListTemp.contains(name)) {
                                map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                                map.put(SysVariables.TASKDEFNAME, name);
                                if (fe instanceof CallActivity) {
                                    // 当节点为子流程CallActivity时，默认多人并行
                                    map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                } else {
                                    UserTask userTask = (UserTask)fe;
                                    if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                                        map.put(SysVariables.MULTIINSTANCE, SysVariables.SEQUENTIAL);
                                    } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                                        map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                    }
                                }
                                targetNodes.add(map);
                                nameListTemp.add(name);
                            } else {
                                UserTask userTask = (UserTask)fe;
                                if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                                    for (int j = 0; j < targetNodes.size(); j++) {
                                        // 当节点名称相同时，默认选择并行节点
                                        if (targetNodes.get(j).get("taskDefName").equals(name)) {
                                            targetNodes.remove(j);
                                            map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                                            map.put(SysVariables.TASKDEFNAME, name);
                                            map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                            targetNodes.add(map);
                                        }
                                    }
                                }
                            }
                        }

                    }
                }
            }
        }
        return targetNodes;
    }

    @Override
    public List<Map<String, String>> getTargetNodes4ParallelGateway(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<>();
                if (flowElement instanceof ParallelGateway) {
                    ParallelGateway task = (ParallelGateway)flowElement;
                    list = task.getOutgoingFlows();
                }
                for (SequenceFlow tr : list) {
                    Map<String, String> map = new HashMap<>(16);
                    FlowElement flowElementTemp = tr.getTargetFlowElement();
                    if (flowElementTemp instanceof UserTask) {
                        map.put(SysVariables.TASKDEFKEY, flowElementTemp.getId());
                        map.put(SysVariables.TASKDEFNAME, flowElementTemp.getName());
                        targetNodes.add(map);
                    }
                }
            }
        }
        return targetNodes;
    }

    @Override
    public Y9Result<List<TargetModel>> getTargetNodes4UserTask(String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode) {
        List<TargetModel> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<>();
                if (flowElement instanceof UserTask) {
                    UserTask task = (UserTask)flowElement;
                    list = task.getOutgoingFlows();
                }
                for (SequenceFlow tr : list) {
                    TargetModel targetModel = new TargetModel();
                    FlowElement flowElementTemp = tr.getTargetFlowElement();
                    if (flowElementTemp instanceof UserTask) {
                        targetModel.setTaskDefKey(flowElementTemp.getId());
                        targetModel.setTaskDefName(flowElementTemp.getName());
                        targetModel.setType(SysVariables.USERTASK);
                        targetNodes.add(targetModel);
                    } else if (flowElementTemp instanceof ExclusiveGateway) {
                        ExclusiveGateway task = (ExclusiveGateway)flowElementTemp;
                        List<SequenceFlow> list0 = task.getOutgoingFlows();
                        for (SequenceFlow tr0 : list0) {
                            TargetModel targetModel0 = new TargetModel();
                            FlowElement flowElementTemp0 = tr0.getTargetFlowElement();
                            if (flowElementTemp0 instanceof UserTask) {
                                targetModel0.setTaskDefKey(flowElementTemp0.getId());
                                targetModel0.setTaskDefName(flowElementTemp0.getName());
                                targetModel0.setType(SysVariables.USERTASK);
                                targetNodes.add(targetModel0);
                            } else if (flowElementTemp0 instanceof EndEvent) {
                                if (isContainEndNode) {
                                    if (StringUtils.isNotBlank(tr0.getName())) {
                                        targetModel0.setTaskDefKey(tr0.getId());
                                        targetModel0.setTaskDefName(tr0.getName());
                                    } else if (StringUtils.isNotBlank(flowElementTemp0.getName())) {
                                        targetModel0.setTaskDefKey(flowElementTemp0.getId());
                                        targetModel0.setTaskDefName(flowElementTemp0.getName());
                                    } else {
                                        targetModel0.setTaskDefKey(tr0.getId());
                                        targetModel0.setTaskDefName("办结");
                                    }
                                    targetModel0.setType(SysVariables.ENDEVENT);
                                    targetNodes.add(targetModel0);
                                }
                            }
                        }
                    } else if (flowElementTemp instanceof EndEvent) {
                        if (isContainEndNode) {
                            TargetModel targetModel0 = new TargetModel();
                            if (StringUtils.isNotBlank(tr.getName())) {
                                targetModel0.setTaskDefKey(tr.getId());
                                targetModel0.setTaskDefName(tr.getName());
                            } else if (StringUtils.isNotBlank(flowElementTemp.getName())) {
                                targetModel0.setTaskDefKey(flowElementTemp.getId());
                                targetModel0.setTaskDefName(flowElementTemp.getName());
                            } else {
                                targetModel0.setTaskDefKey(tr.getId());
                                targetModel0.setTaskDefName("办结");
                            }
                            targetModel0.setType(SysVariables.ENDEVENT);
                            targetNodes.add(targetModel0);
                        }
                    }
                }
            }
        }
        return Y9Result.success(targetNodes);
    }

    @Override
    public String getTaskDefKey4EndEvent(String taskId) {
        String taskDefKey4EndEvent = "";
        HistoricTaskInstance hti = customHistoricTaskService.getById(taskId);
        String processDefinitionId = hti.getProcessDefinitionId();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof EndEvent) {
                taskDefKey4EndEvent = flowElement.getId();
                break;
            }
        }
        return taskDefKey4EndEvent;
    }

    @Override
    public Boolean isCallActivity(String processDefinitionId, String taskDefKey) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            String id = flowElement.getId();
            if (taskDefKey.equals(id)) {
                if (flowElement instanceof CallActivity) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public Boolean isContainNodeType(String taskId, String nodeType) {
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
