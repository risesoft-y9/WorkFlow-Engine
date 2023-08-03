package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomProcessDefinitionService;
import net.risesoft.util.SysVariables;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customProcessDefinitionService")
public class CustomProcessDefinitionServiceImpl implements CustomProcessDefinitionService {

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private CustomHistoricTaskService customHistoricTaskService;

    /**
     * 获取ActivityImpl的list
     *
     * @param procDefKey
     * @return
     */
    private List<FlowElement> getActivityImpls(BpmnModel bpmnModel) {
        List<FlowElement> list = new ArrayList<FlowElement>();
        try {
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            list = (List<FlowElement>)process.getFlowElements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Map<String, String>> getContainEndEvent4UserTask(String processDefinitionId) {
        List<Map<String, String>> userTaskList = new ArrayList<Map<String, String>>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list = new ArrayList<SequenceFlow>();
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
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
                                Map<String, String> map = new HashMap<String, String>(16);
                                map.put(SysVariables.TASKDEFKEY, event.getId());
                                map.put(SysVariables.TASKDEFNAME, event.getName());
                                userTaskList.add(map);
                                break;
                            }
                        }
                    } else if (element instanceof EndEvent) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        map.put(SysVariables.TASKDEFKEY, event.getId());
                        map.put(SysVariables.TASKDEFNAME, event.getName());
                        userTaskList.add(map);
                        break;
                    }
                }
            }
        }
        return userTaskList;
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
     * @param processDefinitionId
     * @return
     */
    private List<FlowElement> getFilteredActivityImpls(BpmnModel bpmnModel) {
        List<FlowElement> list = getActivityImpls(bpmnModel);
        List<FlowElement> resultList = new ArrayList<FlowElement>();
        if (list.size() > 0) {
            // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
            resultList.addAll(list);
        }
        Iterator<FlowElement> sListIterator = resultList.iterator();
        while (sListIterator.hasNext()) {
            FlowElement e = sListIterator.next();
            if (e instanceof Gateway) {
                sListIterator.remove();
            }
        }
        return resultList;
    }

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点
     *
     * @param processDefinitionId
     * @return
     */
    public List<FlowElement> getFilteredActivityImpls(String processDefinitionId) {
        List<FlowElement> list = new ArrayList<FlowElement>();
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            list = getFilteredActivityImpls(bpmnModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<Map<String, Object>> getNodes(String processDefinitionId, Boolean isContainStartNode) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<FlowElement> activitieList = new ArrayList<FlowElement>();
        if (!isContainStartNode) {
            List<FlowElement> list1 = new ArrayList<FlowElement>();
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            list1 = (List<FlowElement>)process.getFlowElements();
            if (list1.size() > 0) {
                // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
                activitieList.addAll(list1);
            }
            Iterator<FlowElement> sListIterator = activitieList.iterator();
            while (sListIterator.hasNext()) {
                FlowElement e = sListIterator.next();
                if (e instanceof Gateway || e instanceof StartEvent || e instanceof EndEvent
                    || e instanceof SequenceFlow) {
                    sListIterator.remove();
                }
            }
        } else {
            activitieList = getFilteredActivityImpls(processDefinitionId);
        }
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            try {
                if (activity instanceof UserTask) {
                    UserTask userTask = (UserTask)activity;
                    if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                        tempMap.put(SysVariables.MULTIINSTANCE, SysVariables.SEQUENTIAL);
                    } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                        tempMap.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                    } else {
                        tempMap.put(SysVariables.MULTIINSTANCE, SysVariables.COMMON);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            list.add(tempMap);
        }
        Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
        tempMap.put("taskDefKey", "");
        tempMap.put("taskDefName", "流程");
        list.add(0, tempMap);
        return list;
    }

    @Override
    public String getNodeType(String processDefinitionId, String taskDefKey) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SysVariables.COMMON;
    }

    @Override
    public Integer getOutPutNodeCount(String taskId) {
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

    @Override
    public List<Map<String, String>> getParallelGatewayList(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<Map<String, String>>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        boolean isGateway = false;
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
                if (flowElement instanceof UserTask) {
                    UserTask task = (UserTask)flowElement;
                    list = task.getOutgoingFlows();
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway task = (ExclusiveGateway)flowElement;
                    list = task.getOutgoingFlows();
                }
                if (!isGateway) {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement flowElementTemp = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && flowElementTemp instanceof ParallelGateway) {
                            map.put(SysVariables.TASKDEFKEY, flowElementTemp.getId());
                            map.put(SysVariables.TASKDEFNAME, flowElementTemp.getName());
                            targetNodes.add(map);
                        } else if (flowElementTemp instanceof ExclusiveGateway) {
                            /**
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
                        Map<String, String> map = new HashMap<String, String>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement flowElementTemp = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && (flowElementTemp instanceof ParallelGateway)) {
                            map.put(SysVariables.TASKDEFKEY, flowElementTemp.getId());
                            map.put(SysVariables.TASKDEFNAME, flowElementTemp.getName());
                            targetNodes.add(map);
                        }
                    }
                }
            }
        }
        return targetNodes;
    }

    /**
     * 根据taskId获取某个节点所有的输出线路
     *
     * @param taskId 任务Id
     * @return
     */
    public List<SequenceFlow> getPvmTransitions(String taskId) {
        boolean isGateway = false;
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        List<SequenceFlow> list = new ArrayList<SequenceFlow>();
        List<SequenceFlow> flowList = new ArrayList<SequenceFlow>();
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
                    for (SequenceFlow tr : list) {
                        flowList.add(tr);
                    }
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
    public List<Map<String, String>> getTargetNodes(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<Map<String, String>>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
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
                        if (tr.getTargetFlowElement() instanceof ParallelGateway) {
                            break;
                        }
                        /**
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
                        if (!(fe instanceof EndEvent)) {
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name) && "skip".equals(name)) {
                                // 忽略
                            } else {
                                map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                                if (StringUtils.isNotBlank(name)) {
                                    // 如果输出线上有名称，则使用线上的名称作为路由名称
                                    map.put(SysVariables.TASKDEFNAME, name);
                                } else {
                                    // 如果输出线上没有名称，则使用目标节点名称作为路由名称
                                    map.put(SysVariables.TASKDEFNAME, tr.getTargetFlowElement().getName());
                                }
                            }
                        }
                        targetNodes.add(map);
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && !(fe instanceof EndEvent)
                            && !(fe instanceof ParallelGateway)) {
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name) && "skip".equals(name)) {
                                // 忽略
                            } else {
                                map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                                if (StringUtils.isNotBlank(name)) {
                                    // 如果输出线上有名称，则使用线上的名称作为路由名称
                                    map.put(SysVariables.TASKDEFNAME, name);
                                } else {
                                    // 如果输出线上没有名称，则使用目标节点名称作为路由名称
                                    map.put(SysVariables.TASKDEFNAME, tr.getTargetFlowElement().getName());
                                }
                                if (fe instanceof CallActivity) {
                                    map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                } else if (fe instanceof SubProcess) {
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
                            }
                        }

                    }
                }
            }
        }
        return targetNodes;
    }

    @Override
    public List<Map<String, String>> getTargetNodes1(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<Map<String, String>>();
        List<String> nameListTemp = new ArrayList<String>();

        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
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
                        if (tr.getTargetFlowElement() instanceof ExclusiveGateway) {
                            taskDefKey = tr.getTargetFlowElement().getId();
                            // 当发现是Gateway类型时，需要从头再遍历一次activitiList，所以这里将i重置为-1，因为在for循环运行中会先加1
                            i = -1;
                            isGateway = true;
                            break;
                        } else {
                            Map<String, String> map = new HashMap<String, String>(16);
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name) && "skip".equals(name)) {
                                // 忽略
                            } else {
                                /**
                                 * 如果输出线上有名称，则使用线上的名称作为路由名称,否则就使用节点名称
                                 */
                                if (StringUtils.isBlank(name)) {
                                    name = tr.getTargetFlowElement().getName();
                                }
                            }
                            map.put(SysVariables.TASKDEFNAME, name);
                            map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                            if (nameListTemp.indexOf(name) < 0) {
                                targetNodes.add(map);
                                nameListTemp.add(name);
                            }
                        }
                    }
                } else {
                    for (SequenceFlow tr : list) {
                        Map<String, String> map = new HashMap<String, String>(16);
                        String conditionText = tr.getConditionExpression();
                        FlowElement fe = tr.getTargetFlowElement();
                        if (StringUtils.isNotBlank(conditionText) && !(fe instanceof EndEvent)) {
                            String name = tr.getName();
                            if (StringUtils.isNotBlank(name) && "skip".equals(name)) {
                                // 忽略
                            } else {
                                /**
                                 * 如果输出线上有名称，则使用线上的名称作为路由名称,否则就使用节点名称
                                 */
                                if (StringUtils.isBlank(name)) {
                                    name = tr.getTargetFlowElement().getName();
                                }

                                if (nameListTemp.indexOf(name) < 0) {
                                    map.put(SysVariables.TASKDEFKEY, tr.getTargetFlowElement().getId());
                                    map.put(SysVariables.TASKDEFNAME, name);
                                    if (!(fe instanceof EndEvent)) {
                                        if (fe instanceof CallActivity) {
                                            // 当节点为子流程CallActivity时，默认多人并行
                                            map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                        } else {
                                            UserTask userTask = (UserTask)fe;
                                            if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                                                map.put(SysVariables.MULTIINSTANCE, SysVariables.SEQUENTIAL);
                                            } else if (userTask
                                                .getBehavior() instanceof ParallelMultiInstanceBehavior) {
                                                map.put(SysVariables.MULTIINSTANCE, SysVariables.PARALLEL);
                                            }
                                        }
                                    }
                                    targetNodes.add(map);
                                    nameListTemp.add(name);
                                } else {
                                    UserTask userTask = (UserTask)fe;
                                    if (!(fe instanceof EndEvent)
                                        && userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
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
        }
        return targetNodes;
    }

    @Override
    public List<Map<String, String>> getTargetNodes4ParallelGateway(String processDefinitionId, String taskDefKey) {
        List<Map<String, String>> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
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
    public List<Map<String, String>> getTargetNodes4UserTask(String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode) {
        List<Map<String, String>> targetNodes = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (int i = 0; i < flowElements.size(); i++) {
            FlowElement flowElement = flowElements.get(i);
            if (taskDefKey.equals(flowElement.getId())) {
                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
                if (flowElement instanceof UserTask) {
                    UserTask task = (UserTask)flowElement;
                    list = task.getOutgoingFlows();
                }
                for (SequenceFlow tr : list) {
                    Map<String, String> map = new HashMap<String, String>(16);
                    FlowElement flowElementTemp = tr.getTargetFlowElement();
                    if (flowElementTemp instanceof UserTask) {
                        map.put(SysVariables.TASKDEFKEY, flowElementTemp.getId());
                        map.put(SysVariables.TASKDEFNAME, flowElementTemp.getName());
                        map.put(SysVariables.TYPE, SysVariables.USERTASK);
                        targetNodes.add(map);
                    } else if (flowElementTemp instanceof ExclusiveGateway) {
                        ExclusiveGateway task = (ExclusiveGateway)flowElementTemp;
                        List<SequenceFlow> list0 = task.getOutgoingFlows();
                        for (SequenceFlow tr0 : list0) {
                            Map<String, String> map0 = new HashMap<String, String>(16);
                            FlowElement flowElementTemp0 = tr0.getTargetFlowElement();
                            if (flowElementTemp0 instanceof UserTask) {
                                map0.put(SysVariables.TASKDEFKEY, flowElementTemp0.getId());
                                map0.put(SysVariables.TASKDEFNAME, flowElementTemp0.getName());
                                map0.put(SysVariables.TYPE, SysVariables.USERTASK);
                                targetNodes.add(map0);
                            } else if (flowElementTemp0 instanceof EndEvent) {
                                if (isContainEndNode) {
                                    if (StringUtils.isNotBlank(tr0.getName())) {
                                        map0.put(SysVariables.TASKDEFKEY, tr0.getId());
                                        map0.put(SysVariables.TASKDEFNAME, tr0.getName());
                                    } else if (StringUtils.isNotBlank(flowElementTemp0.getName())) {
                                        map0.put(SysVariables.TASKDEFKEY, flowElementTemp0.getId());
                                        map0.put(SysVariables.TASKDEFNAME, flowElementTemp0.getName());
                                    } else {
                                        map0.put(SysVariables.TASKDEFKEY, tr0.getId());
                                        map0.put(SysVariables.TASKDEFNAME, "办结");
                                    }
                                    map0.put(SysVariables.TYPE, SysVariables.ENDEVENT);
                                    targetNodes.add(map0);
                                }
                            }
                        }
                    } else if (flowElementTemp instanceof EndEvent) {
                        if (isContainEndNode) {
                            Map<String, String> map0 = new HashMap<String, String>(16);
                            if (StringUtils.isNotBlank(tr.getName())) {
                                map0.put(SysVariables.TASKDEFKEY, tr.getId());
                                map0.put(SysVariables.TASKDEFNAME, tr.getName());
                            } else if (StringUtils.isNotBlank(flowElementTemp.getName())) {
                                map0.put(SysVariables.TASKDEFKEY, flowElementTemp.getId());
                                map0.put(SysVariables.TASKDEFNAME, flowElementTemp.getName());
                            } else {
                                map0.put(SysVariables.TASKDEFKEY, tr.getId());
                                map0.put(SysVariables.TASKDEFNAME, "办结");
                            }
                            map0.put(SysVariables.TYPE, SysVariables.ENDEVENT);
                            targetNodes.add(map0);
                        }
                    }
                }
            }
        }
        return targetNodes;
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
