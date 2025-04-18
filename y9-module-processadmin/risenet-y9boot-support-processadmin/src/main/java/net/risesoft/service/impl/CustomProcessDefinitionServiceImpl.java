package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.CallActivity;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.SubProcess;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.TaskService;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.task.api.Task;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.GatewayModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
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

    @Override
    public List<FlowElement> getFlowElements(String processDefinitionId) {
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> list4Cache = (List<FlowElement>)process.getFlowElements();
        return new ArrayList<>(list4Cache);
    }

    @Override
    public Y9Result<TargetModel> getEndNode(String taskId) {
        List<SequenceFlow> outList = getSequenceFlow(taskId);
        Optional<SequenceFlow> first =
            outList.stream().filter(e -> e.getTargetFlowElement() instanceof EndEvent).findFirst();
        return first.map(sequenceFlow -> Y9Result.success(createTargetModel(sequenceFlow, "")))
            .orElseGet(() -> Y9Result.failure("获取流程结束节点失败"));
    }

    @Override
    public FlowElementModel getNode(String processDefinitionId, String flowElementId) {
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        Optional<FlowElement> first =
            feList.stream().filter(flowElement -> flowElementId.equals(flowElement.getId())).findFirst();
        boolean isSub = false;
        if (first.isEmpty()) {
            isSub = true;
            List<FlowElement> subFeList4All = new ArrayList<>();
            feList.stream().filter(flowElement -> flowElement instanceof SubProcess).forEach(flowElement -> {
                subFeList4All.addAll(((SubProcess)flowElement).getFlowElements());
            });
            first = subFeList4All.stream().filter(flowElement -> flowElementId.equals(flowElement.getId())).findFirst();
        }
        return first.isPresent() ? createFlowElementModel(first.get(), isSub) : null;
    }

    private FlowElementModel createFlowElementModel(FlowElement flowElement, boolean isSub) {
        FlowElementModel flowElementModel = new FlowElementModel();
        flowElementModel.setElementKey(flowElement.getId());
        flowElementModel.setElementName(flowElement.getName() + (isSub ? "[子]" : ""));
        Object obj = null;
        if (flowElement instanceof UserTask) {
            flowElementModel.setType(SysVariables.USERTASK);
            UserTask userTask = (UserTask)flowElement;
            obj = userTask.getBehavior();
        } else if (flowElement instanceof SubProcess) {
            flowElementModel.setElementName(flowElement.getName() + "【子】");
            flowElementModel.setType(SysVariables.SUBPROCESS);
            SubProcess subProcess = (SubProcess)flowElement;
            obj = subProcess.getBehavior();
        } else if (flowElement instanceof CallActivity) {
            flowElementModel.setElementName(flowElement.getName() + "【子】");
            flowElementModel.setType(SysVariables.CALLACTIVITY);
            CallActivity callActivity = (CallActivity)flowElement;
            obj = callActivity.getBehavior();
        }
        if (obj instanceof SequentialMultiInstanceBehavior) {
            flowElementModel.setMultiInstance(SysVariables.SEQUENTIAL);
        } else if (obj instanceof ParallelMultiInstanceBehavior) {
            flowElementModel.setMultiInstance(SysVariables.PARALLEL);
        } else {
            flowElementModel.setMultiInstance(SysVariables.COMMON);
        }
        return flowElementModel;
    }

    @Override
    public Integer getOutPutNodeCount(String taskId) {
        return (int)getSequenceFlow(taskId).stream().filter(e -> !(e.getTargetFlowElement() instanceof EndEvent))
            .count();
    }

    /**
     * 根据taskId获取节点所有的输出线路(输出线路的目标不为排他网关)
     *
     * @param taskId 任务Id
     * @return List<SequenceFlow>
     */
    public List<SequenceFlow> getSequenceFlow(String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        List<FlowElement> feList = this.getFlowElements(task.getProcessDefinitionId());
        List<FlowElement> listAll = new ArrayList<>(feList);
        for (int i = 0; i < listAll.size(); i++) {
            FlowElement flowElement = listAll.get(i);
            if (flowElement instanceof SubProcess) {
                listAll.addAll(((SubProcess)flowElement).getFlowElements());
            }
        }
        Optional<FlowElement> first =
            listAll.stream().filter(flowElement -> flowElement.getId().equals(task.getTaskDefinitionKey())).findFirst();
        if (first.isPresent()) {
            List<SequenceFlow> sequenceFlowList = new ArrayList<>();
            UserTask userTask = (UserTask)first.get();
            userTask.getOutgoingFlows().forEach(sequenceFlow -> {
                if (sequenceFlow.getTargetFlowElement() instanceof ExclusiveGateway) {
                    Optional<FlowElement> exclusiveGateway = listAll.stream()
                        .filter(flowElement -> flowElement.getId().equals(sequenceFlow.getTargetFlowElement().getId()))
                        .findFirst();
                    exclusiveGateway.ifPresent(flowElement -> sequenceFlowList
                        .addAll(((ExclusiveGateway)sequenceFlow.getTargetFlowElement()).getOutgoingFlows()));
                } else {
                    sequenceFlowList.add(sequenceFlow);
                }
            });
            return sequenceFlowList;
        }
        return List.of();
    }

    @Override
    public String getStartNodeKeyByProcessDefinitionId(String processDefinitionId) {
        List<FlowElement> flowElements = this.getFlowElements(processDefinitionId);
        Optional<FlowElement> first =
            flowElements.stream().filter(flowElement -> flowElement instanceof StartEvent).findFirst();
        return first.isPresent() ? first.get().getId() : "";
    }

    @Override
    public String getStartNodeKeyByProcessDefinitionKey(String processDefinitionKey) {
        String processDefinitionId = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey(processDefinitionKey).latestVersion().singleResult().getId();
        return this.getStartNodeKeyByProcessDefinitionId(processDefinitionId);
    }

    @Override
    public Y9Result<List<TargetModel>> getSubProcessChildNode(String processDefinitionId) {
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        List<TargetModel> targetNodeList = new ArrayList<>();
        feList.stream().filter(flowElement -> flowElement instanceof SubProcess)
            .forEach(flowElement -> ((SubProcess)flowElement).getFlowElements().stream()
                .filter(fe -> fe instanceof UserTask).forEach(fe -> {
                    TargetModel targetModel = new TargetModel();
                    targetModel.setTaskDefKey(fe.getId());
                    targetModel.setTaskDefName(fe.getName());
                    targetNodeList.add(targetModel);
                }));
        return Y9Result.success(targetNodeList);
    }

    @Override
    public TargetModel getSubProcessParentNode(String processDefinitionId, String taskDefKey) {
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        Optional<FlowElement> first =
            feList.stream()
                .filter(flowElement -> flowElement instanceof SubProcess && ((SubProcess)flowElement).getFlowElements()
                    .stream().anyMatch(subFe -> taskDefKey.equals(subFe.getId()) && subFe instanceof UserTask))
                .findFirst();
        return first.map(flowElement -> createTargetModel(flowElement, false)).orElse(null);
    }

    private TargetModel createTargetModel(SequenceFlow sequenceFlow, String processDefinitionId) {
        FlowElement fe = sequenceFlow.getTargetFlowElement();
        TargetModel targetModel = new TargetModel();
        targetModel.setTaskDefKey(fe.getId());
        targetModel.setProcessDefinitionId(processDefinitionId);
        targetModel.setRealTaskDefName(fe.getName());
        String name = sequenceFlow.getName();
        if (StringUtils.isNotBlank(name) && !"skip".equals(name)) {
            // 如果输出线上有名称且不为skip，则使用线上的名称作为路由名称
            targetModel.setTaskDefName(name);
        } else {
            // 如果输出线上没有名称，则使用目标节点名称作为路由名称
            targetModel.setTaskDefName(fe.getName());
        }
        if (fe instanceof UserTask) {
            targetModel.setType(SysVariables.USERTASK);
            UserTask userTask = (UserTask)fe;
            if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
            } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.PARALLEL);
            } else {
                targetModel.setMultiInstance(SysVariables.COMMON);
            }
        } else if (fe instanceof SubProcess) {
            targetModel.setType(SysVariables.SUBPROCESS);
            SubProcess subProcess = (SubProcess)fe;
            if (subProcess.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.PARALLEL);
            } else if (subProcess.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
            } else {
                targetModel.setMultiInstance(SysVariables.COMMON);
            }
        } else if (fe instanceof EndEvent) {
            targetModel.setType(SysVariables.ENDEVENT);
            if (StringUtils.isBlank(targetModel.getTaskDefName())) {
                targetModel.setTaskDefName("办结");
            }
        }
        return targetModel;
    }

    private TargetModel createTargetModel(FlowElement flowElement, boolean isSub) {
        TargetModel targetModel = new TargetModel();
        targetModel.setTaskDefKey(flowElement.getId());
        targetModel.setTaskDefName(flowElement.getName() + (isSub ? "[子]" : ""));
        if (flowElement instanceof UserTask) {
            targetModel.setType(SysVariables.USERTASK);
            UserTask userTask = ((UserTask)flowElement);
            if (userTask.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
            } else if (userTask.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.PARALLEL);
            } else {
                targetModel.setMultiInstance(SysVariables.COMMON);
            }
        } else if (flowElement instanceof SubProcess) {
            targetModel.setTaskDefName(flowElement.getName() + "【子】");
            targetModel.setType(SysVariables.SUBPROCESS);
            SubProcess subProcess = ((SubProcess)flowElement);
            if (subProcess.getBehavior() instanceof SequentialMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.SEQUENTIAL);
            } else if (subProcess.getBehavior() instanceof ParallelMultiInstanceBehavior) {
                targetModel.setMultiInstance(SysVariables.PARALLEL);
            } else {
                targetModel.setMultiInstance(SysVariables.COMMON);
            }
        } else if (flowElement instanceof EndEvent) {
            targetModel.setType(SysVariables.ENDEVENT);
        }
        return targetModel;
    }

    @Override
    public String getTaskDefKey4EndEvent(String processDefinitionId) {
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        Optional<FlowElement> first =
            feList.stream().filter(flowElement -> flowElement instanceof EndEvent).findFirst();
        return first.isPresent() ? first.get().getId() : "";
    }

    @Override
    public Boolean isCallActivity(String processDefinitionId, String taskDefKey) {
        return this.getFlowElements(processDefinitionId).stream()
            .anyMatch(flowElement -> taskDefKey.equals(flowElement.getId()) && flowElement instanceof CallActivity);
    }

    @Override
    public Boolean isContainEndEvent(String taskId) {
        return getSequenceFlow(taskId).stream().anyMatch(tr -> tr.getTargetFlowElement() instanceof EndEvent);
    }

    // ok
    @Override
    public Boolean isSubProcess(String processDefinitionId, String taskDefKey) {
        return this.getFlowElements(processDefinitionId).stream()
            .anyMatch(flowElement -> taskDefKey.equals(flowElement.getId()) && flowElement instanceof SubProcess);
    }

    @Override
    public Boolean isSubProcessChildNode(String processDefinitionId, String taskDefKey) {
        return this.getFlowElements(processDefinitionId).stream()
            .anyMatch(flowElement -> flowElement instanceof SubProcess && (((SubProcess)flowElement).getFlowElements()
                .stream().anyMatch(subFe -> taskDefKey.equals(subFe.getId()) && subFe instanceof UserTask)));
    }

    @Override
    public Y9Result<List<TargetModel>> listContainEndEvent4UserTask(String processDefinitionId) {
        List<TargetModel> userTaskList = new ArrayList<>();
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        feList.stream().filter(flowElement -> flowElement instanceof UserTask).forEach(flowElement -> {
            UserTask userTask = (UserTask)flowElement;
            for (SequenceFlow tr : userTask.getOutgoingFlows()) {
                FlowElement element = tr.getTargetFlowElement();
                if (element instanceof ExclusiveGateway) {
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway)element;
                    for (SequenceFlow sf : exclusiveGateway.getOutgoingFlows()) {
                        if (sf.getTargetFlowElement() instanceof EndEvent) {
                            userTaskList.add(createTargetModel(flowElement, false));
                            break;
                        }
                    }
                } else if (element instanceof EndEvent) {
                    userTaskList.add(createTargetModel(flowElement, false));
                    break;
                }
            }
        });
        return Y9Result.success(userTaskList);
    }

    @Override
    public Y9Result<List<FlowElementModel>> listUserTask(String processDefinitionId) {
        List<FlowElementModel> list = new ArrayList<>();
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        feList.stream().filter(flowElement -> flowElement instanceof UserTask)
            .forEach(flowElement -> list.add(createFlowElementModel(flowElement, false)));
        feList.stream().filter(flowElement -> flowElement instanceof SubProcess).forEach(subProcess -> {
            list.add(createFlowElementModel(subProcess, false));
            ((SubProcess)subProcess).getFlowElements().stream().filter(flowElement -> flowElement instanceof UserTask)
                .forEach(flowElement -> list.add(createFlowElementModel(flowElement, true)));
        });
        FlowElementModel feModel = new FlowElementModel();
        feModel.setElementKey("");
        feModel.setElementName("流程");
        feModel.setType("Process");
        list.add(0, feModel);
        return Y9Result.success(list);
    }

    @Override
    public Y9Result<List<TargetModel>> listNodesByProcessDefinitionId(String processDefinitionId) {
        List<TargetModel> list = new ArrayList<>();
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        feList.stream().filter(flowElement -> flowElement instanceof UserTask)
            .forEach(flowElement -> list.add(createTargetModel(flowElement, false)));
        feList.stream().filter(flowElement -> flowElement instanceof SubProcess).forEach(subProcess -> {
            list.add(createTargetModel(subProcess, false));
            ((SubProcess)subProcess).getFlowElements().stream().filter(flowElement -> flowElement instanceof UserTask)
                .forEach(flowElement -> list.add(createTargetModel(flowElement, true)));
        });
        TargetModel targetModel = new TargetModel();
        targetModel.setTaskDefKey("");
        targetModel.setTaskDefName("流程");
        list.add(0, targetModel);
        return Y9Result.success(list);
    }

    @Override
    public Y9Result<List<GatewayModel>> listParallelGateway(String processDefinitionId, String taskDefKey) {
        List<GatewayModel> targetNodes = new ArrayList<>();
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        Optional<FlowElement> userTaskOptional =
            feList.stream().filter(flowElement -> flowElement instanceof UserTask).findFirst();
        if (userTaskOptional.isPresent()) {
            UserTask userTask = (UserTask)userTaskOptional.get();
            List<SequenceFlow> outgoingFlows = userTask.getOutgoingFlows();
            outgoingFlows.forEach(tr -> {
                FlowElement flowElement = tr.getTargetFlowElement();
                if (flowElement instanceof ParallelGateway) {
                    if (StringUtils.isNoneBlank(tr.getConditionExpression())) {
                        GatewayModel gatewayModel = new GatewayModel();
                        gatewayModel.setTaskDefKey(flowElement.getId());
                        gatewayModel.setTaskDefName(flowElement.getName());
                        targetNodes.add(gatewayModel);
                    }
                } else if (flowElement instanceof ExclusiveGateway) {
                    ExclusiveGateway exclusiveGateway = (ExclusiveGateway)flowElement;
                    exclusiveGateway.getOutgoingFlows().stream()
                        .filter(outgoingFlow -> outgoingFlow.getTargetFlowElement() instanceof ParallelGateway)
                        .forEach(outgoingFlow -> {
                            GatewayModel gatewayModel = new GatewayModel();
                            gatewayModel.setTaskDefKey(outgoingFlow.getTargetFlowElement().getId());
                            gatewayModel.setTaskDefName(outgoingFlow.getTargetFlowElement().getName());
                            targetNodes.add(gatewayModel);
                        });
                }
            });
        }
        return Y9Result.success(targetNodes);
    }

    @Override
    public Y9Result<List<TargetModel>> listTargetNodes(String processDefinitionId, String taskDefKey) {
        return get(processDefinitionId, taskDefKey, false);
    }

    @Override
    public Y9Result<List<TargetModel>> listTargetNodes4UserTask(String processDefinitionId, String taskDefKey,
        Boolean isContainEndNode) {
        return get(processDefinitionId, taskDefKey, isContainEndNode);
    }

    private Y9Result<List<TargetModel>> get(String processDefinitionId, String taskDefKey, Boolean isContainEndNode) {
        List<TargetModel> targetNodes = new ArrayList<>();
        List<FlowElement> feList = this.getFlowElements(processDefinitionId);
        List<FlowElement> listAll = new ArrayList<>(feList);
        feList.stream().filter(flowElement -> flowElement instanceof SubProcess).forEach(subProcess -> {
            listAll.addAll(((SubProcess)subProcess).getFlowElements());
        });
        // 传入的节点
        Optional<FlowElement> first =
            listAll.stream().filter(flowElement -> flowElement.getId().equals(taskDefKey)).findFirst();
        if (first.isPresent()) {
            // 传入节点的输出路线
            List<SequenceFlow> outList = new ArrayList<>();
            FlowElement flowElement = first.get();
            if (flowElement instanceof StartEvent) {
                StartEvent task = (StartEvent)flowElement;
                outList = task.getOutgoingFlows();
            } else if (flowElement instanceof UserTask) {
                UserTask task = (UserTask)flowElement;
                outList = task.getOutgoingFlows();
            }
            outList.forEach(sequenceFlow -> {
                FlowElement fe = sequenceFlow.getTargetFlowElement();
                if (fe instanceof UserTask) {
                    // 如果为用户任务直接放入
                    targetNodes.add(createTargetModel(sequenceFlow, processDefinitionId));
                } else if (fe instanceof EndEvent) {
                    // 如果为办结节点
                    if (isContainEndNode) {
                        targetNodes.add(createTargetModel(sequenceFlow, processDefinitionId));
                    }
                } else if (fe instanceof ExclusiveGateway) {
                    // 如果为排他网关，则继续查找排他网关的输出路线
                    Optional<FlowElement> exclusiveGatewayOptional =
                        listAll.stream().filter(element -> element.getId().equals(fe.getId())).findFirst();
                    if (exclusiveGatewayOptional.isPresent()) {
                        ExclusiveGateway exclusiveGateway = (ExclusiveGateway)exclusiveGatewayOptional.get();
                        List<SequenceFlow> outgoingFlows = exclusiveGateway.getOutgoingFlows();
                        outgoingFlows
                            .removeIf(outgoingFlow -> outgoingFlow.getTargetFlowElement() instanceof ParallelGateway
                                || outgoingFlow.getTargetFlowElement() instanceof ExclusiveGateway);
                        if (!isContainEndNode) {
                            outgoingFlows
                                .removeIf(outgoingFlow -> outgoingFlow.getTargetFlowElement() instanceof EndEvent);
                        }
                        outgoingFlows.forEach(
                            outgoingFlow -> targetNodes.add(createTargetModel(outgoingFlow, processDefinitionId)));
                    }
                }
            });
        }
        return Y9Result.success(targetNodes);
    }
}
