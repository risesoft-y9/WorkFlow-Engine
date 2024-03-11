package net.risesoft.api;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.Execution;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.BpmnModelApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
import net.risesoft.service.CustomHistoricActivityService;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RestController
@RequestMapping(value = "/services/rest/bpmnModel")
public class BpmnModelApiImpl implements BpmnModelApi {

    @Autowired
    private CustomHistoricProcessService customHistoricProcessService;

    @Autowired
    private CustomHistoricTaskService customHistoricTaskService;

    @Autowired
    private CustomHistoricVariableService customHistoricVariableService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Autowired
    private CustomHistoricActivityService customHistoricActivityService;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private PersonApi personManager;

    @Autowired
    private OfficeDoneInfoApi officeDoneInfoManager;

    @Autowired
    private ProcessParamApi processParamManager;

    @Autowired
    private ProcessTrackApi processTrackManager;

    @Override
    @PostMapping(value = "/genProcessDiagram", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] genProcessDiagram(String tenantId, String processId) throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricProcessInstance pi = customHistoricProcessService.getById(processId);
        // 流程走完的不显示图
        if (pi == null) {
            return null;
        }
        InputStream in;
        ProcessEngine processEngine = Y9Context.getBean(ProcessEngine.class);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engconf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engconf.getProcessDiagramGenerator();
        if (pi.getEndTime() == null) {
            Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
            // 使用流程实例ID，查询正在执行的执行对象表，返回流程实例对象
            String instanceId = task.getProcessInstanceId();
            List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(instanceId).list();

            // 得到正在执行的Activity的Id
            List<String> activityIds = new ArrayList<>();
            List<String> flows = new ArrayList<>();
            for (Execution exe : executions) {
                List<String> ids = runtimeService.getActiveActivityIds(exe.getId());
                activityIds.addAll(ids);
            }
            // 获取流程图
            in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engconf.getActivityFontName(),
                engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), 1.0, false);
        } else {
            // 获取流程图
            in = diagramGenerator.generateDiagram(bpmnModel, "png", engconf.getActivityFontName(),
                engconf.getLabelFontName(), engconf.getAnnotationFontName(), engconf.getClassLoader(), false);
        }

        byte[] buf = new byte[1024];
        int legth = 0;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            while ((legth = in.read(buf)) != -1) {
                swapStream.write(buf, 0, legth);
            }
            byte[] in2b = swapStream.toByteArray();
            return in2b;
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    @Override
    @GetMapping(value = "/getBpmnModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getBpmnModel(String tenantId, String processInstanceId) throws Exception {
        Map<String, Object> map = new HashMap<String, Object>(16);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricProcessInstance pi = customHistoricProcessService.getById(processInstanceId);
        // 流程走完的不显示图
        if (pi == null) {
            map.put("success", false);
            return map;
        }
        String txtFlowPath = "";
        List<Map<String, Object>> nodeDataArray = new ArrayList<Map<String, Object>>();
        List<Map<String, Object>> linkDataArray = new ArrayList<Map<String, Object>>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        Map<String, GraphicInfo> infoMap = bpmnModel.getLocationMap();
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            Map<String, Object> nodeMap = new HashMap<String, Object>(16);
            if (flowElement instanceof StartEvent) {
                StartEvent startEvent = (StartEvent)flowElement;
                GraphicInfo graphicInfo = infoMap.get(startEvent.getId());
                txtFlowPath = startEvent.getId();
                nodeMap.put("key", startEvent.getId());
                nodeMap.put("text", "开始");
                nodeMap.put("figure", "Circle");
                nodeMap.put("fill", "#4fba4f");
                nodeMap.put("category", "Start");
                nodeMap.put("stepType", "1");
                nodeMap.put("loc", graphicInfo.getX() - 100 + " " + graphicInfo.getY());
                nodeDataArray.add(nodeMap);

                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
                // 获取开始节点输出路线
                list = startEvent.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement fe = tr.getTargetFlowElement();
                    if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        Map<String, Object> linkMap = new HashMap<String, Object>(16);
                        linkMap.put("from", startEvent.getId());
                        linkMap.put("to", u.getId());
                        linkDataArray.add(linkMap);
                    }
                }
            } else if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask)flowElement;
                GraphicInfo graphicInfo = infoMap.get(userTask.getId());
                nodeMap.put("key", userTask.getId());
                nodeMap.put("text", userTask.getName());
                nodeMap.put("remark", "111111111");
                nodeMap.put("loc", graphicInfo.getX() + " " + graphicInfo.getY());
                nodeDataArray.add(nodeMap);
                List<SequenceFlow> list = new ArrayList<SequenceFlow>();
                // 获取任务节点输出路线
                list = userTask.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement fe = tr.getTargetFlowElement();
                    if (fe instanceof ExclusiveGateway) {
                        // 目标节点时排他网关时，需要再次获取输出路线
                        List<SequenceFlow> outgoingFlows = new ArrayList<SequenceFlow>();
                        ExclusiveGateway gateway = (ExclusiveGateway)fe;
                        outgoingFlows = gateway.getOutgoingFlows();
                        for (SequenceFlow sf : outgoingFlows) {
                            FlowElement element = sf.getTargetFlowElement();
                            if (element instanceof UserTask) {
                                UserTask task = (UserTask)element;
                                Map<String, Object> linkMap = new HashMap<String, Object>(16);
                                linkMap.put("from", userTask.getId());
                                linkMap.put("to", task.getId());
                                linkDataArray.add(linkMap);
                            } else if (element instanceof EndEvent) {
                                EndEvent endEvent = (EndEvent)element;
                                Map<String, Object> linkMap = new HashMap<String, Object>(16);
                                linkMap.put("from", userTask.getId());
                                linkMap.put("to", endEvent.getId());
                                linkDataArray.add(linkMap);
                            } else if (element instanceof ParallelGateway) {
                                ParallelGateway parallelgateway = (ParallelGateway)element;
                                List<SequenceFlow> outgoingFlows1 = new ArrayList<SequenceFlow>();
                                outgoingFlows1 = parallelgateway.getOutgoingFlows();
                                for (SequenceFlow sf1 : outgoingFlows1) {
                                    FlowElement element1 = sf1.getTargetFlowElement();
                                    if (element1 instanceof UserTask) {
                                        UserTask task1 = (UserTask)element1;
                                        Map<String, Object> linkMap = new HashMap<String, Object>(16);
                                        linkMap.put("from", userTask.getId());
                                        linkMap.put("to", task1.getId());
                                        linkDataArray.add(linkMap);
                                    }
                                }
                            }
                        }
                    } else if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        Map<String, Object> linkMap = new HashMap<String, Object>(16);
                        linkMap.put("from", userTask.getId());
                        linkMap.put("to", u.getId());
                        linkDataArray.add(linkMap);
                    } else if (fe instanceof EndEvent) {
                        EndEvent endEvent = (EndEvent)fe;
                        Map<String, Object> linkMap = new HashMap<String, Object>(16);
                        linkMap.put("from", userTask.getId());
                        linkMap.put("to", endEvent.getId());
                        linkDataArray.add(linkMap);
                    } else if (fe instanceof ParallelGateway) {
                        ParallelGateway gateway = (ParallelGateway)fe;
                        List<SequenceFlow> outgoingFlows = new ArrayList<SequenceFlow>();
                        outgoingFlows = gateway.getOutgoingFlows();
                        for (SequenceFlow sf : outgoingFlows) {
                            FlowElement element = sf.getTargetFlowElement();
                            if (element instanceof UserTask) {
                                UserTask task = (UserTask)element;
                                Map<String, Object> linkMap = new HashMap<String, Object>(16);
                                linkMap.put("from", userTask.getId());
                                linkMap.put("to", task.getId());
                                linkDataArray.add(linkMap);
                            }
                        }
                    }
                }
            } else if (flowElement instanceof EndEvent) {
                EndEvent endEvent = (EndEvent)flowElement;
                GraphicInfo graphicInfo = infoMap.get(endEvent.getId());
                nodeMap.put("key", endEvent.getId());
                nodeMap.put("category", "End");
                nodeMap.put("text", "结束");
                nodeMap.put("figure", "Circle");
                nodeMap.put("fill", "#CE0620");
                nodeMap.put("stepType", "4");
                nodeMap.put("loc", graphicInfo.getX() + " " + graphicInfo.getY());
                nodeDataArray.add(nodeMap);
            }
        }

        List<HistoricTaskInstance> list = customHistoricTaskService.getByProcessInstanceId(processInstanceId, "");
        for (HistoricTaskInstance task : list) {
            txtFlowPath = Y9Util.genCustomStr(txtFlowPath, task.getTaskDefinitionKey());
        }
        map.put("nodeDataArray", nodeDataArray);
        map.put("linkDataArray", linkDataArray);
        map.put("txtFlowPath", txtFlowPath);
        map.put("isCompleted", pi.getEndTime() != null ? true : false);
        map.put("success", true);
        return map;
    }

    @Override
    @GetMapping(value = "/getFlowChart", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFlowChart(String tenantId, String processInstanceId) throws Exception {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<Map<String, Object>>();
        String activityId = "";
        String parentId = "";
        String year = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            HistoricProcessInstance hpi = customHistoricProcessService.getById(processInstanceId);
            if (hpi == null) {
                OfficeDoneInfoModel officeDoneInfo =
                    officeDoneInfoManager.findByProcessInstanceId(tenantId, processInstanceId);
                if (officeDoneInfo == null) {
                    ProcessParamModel processParam =
                        processParamManager.findByProcessInstanceId(tenantId, processInstanceId);
                    year = processParam.getCreateTime().substring(0, 4);
                } else {
                    year = officeDoneInfo.getStartTime().substring(0, 4);
                }
            }
            List<HistoricActivityInstance> list =
                customHistoricActivityService.getByProcessInstanceIdAndYear(processInstanceId, year);
            Collections.sort(list, new Comparator<HistoricActivityInstance>() {
                @Override
                public int compare(HistoricActivityInstance o1, HistoricActivityInstance o2) {
                    try {
                        if (o1.getEndTime() == null || o2.getEndTime() == null) {
                            return 0;
                        }
                        long endTime1 = o1.getEndTime().getTime();
                        long endTime2 = o2.getEndTime().getTime();
                        if (endTime1 > endTime2) {
                            return 1;
                        } else if (endTime1 == endTime2) {
                            return 0;
                        } else {
                            return -1;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return -1;
                }
            });
            int num = 0;
            for (int i = 0; i < list.size(); i++) {
                HistoricActivityInstance his = list.get(i);
                String id = his.getId();
                String taskId = his.getTaskId();
                String type = his.getActivityType();
                if (type.contains(SysVariables.STARTEVENT) || type.contains("Flow")
                    || type.contains(SysVariables.ENDEVENT)) {
                    continue;
                }
                if (type.contains(SysVariables.ENDEVENT)) {
                    num += 1;
                    String completer = (String)listMap.get(listMap.size() - 1).get("title");
                    if (completer.contains("主办")) {
                        completer = completer.substring(0, completer.length() - 4);
                    }
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    map.put("id", id);
                    map.put("name", "办结");
                    map.put("title", completer);
                    map.put("parentId", parentId);
                    map.put("className", "specialColor");
                    map.put("num", num);
                    map.put("endTime", his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    listMap.add(map);
                    continue;
                }
                if (type.contains(SysVariables.GATEWAY)) {
                    num += 1;
                    continue;
                }
                String userId = his.getAssignee();
                Person person = personManager.getPerson(tenantId, userId).getData();
                if ("".equals(activityId) || activityId.equals(his.getActivityId())) {
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    map.put("id", taskId);
                    map.put("name", his.getActivityName());
                    map.put("title", person != null ? person.getName() : "该用户不存在");
                    HistoricVariableInstance historicVariableInstance = customHistoricVariableService
                        .getByTaskIdAndVariableName(taskId, SysVariables.PARALLELSPONSOR, year);
                    if (historicVariableInstance != null) {
                        map.put("title", person != null ? person.getName() + "(主办)" : "该用户不存在");
                    }
                    map.put("parentId", parentId);
                    map.put("className", his.getEndTime() != null ? "serverColor" : "specialColor");
                    map.put("endTime", his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    map.put("num", num);
                    listMap.add(map);
                    activityId = his.getActivityId();
                    parentId = taskId;
                } else {
                    num += 1;
                    activityId = his.getActivityId();
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    map.put("id", taskId);
                    map.put("name", his.getActivityName());
                    map.put("title", person != null ? person.getName() : "该用户不存在");
                    HistoricVariableInstance historicVariableInstance = customHistoricVariableService
                        .getByTaskIdAndVariableName(taskId, SysVariables.PARALLELSPONSOR, year);
                    if (historicVariableInstance != null) {
                        map.put("title", person != null ? person.getName() + "(主办)" : "该用户不存在");
                    }
                    map.put("parentId", parentId);
                    map.put("className", his.getEndTime() != null ? "serverColor" : "specialColor");
                    map.put("endTime", his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    map.put("num", num);
                    listMap.add(map);
                }

                List<ProcessTrackModel> ptList = processTrackManager.findByTaskIdAsc(tenantId, userId, taskId);
                String parentId0 = taskId;
                for (int j = 0; j < ptList.size(); j++) {
                    num += 1;
                    ProcessTrackModel pt = ptList.get(j);
                    if (j != 0) {
                        parentId0 = pt.getId();
                    }
                    Map<String, Object> map = new HashMap<String, Object>(16);
                    map.put("id", pt.getId());
                    map.put("name", pt.getTaskDefName());
                    map.put("title", pt.getSenderName());
                    map.put("parentId", parentId0);
                    map.put("className", StringUtils.isNotBlank(pt.getEndTime()) ? "serverColor" : "specialColor");
                    map.put("endTime",
                        StringUtils.isNotBlank(pt.getEndTime()) ? sdf.parse(pt.getEndTime()).getTime() : 0);
                    map.put("num", num);
                    listMap.add(map);
                    if (j == ptList.size() - 1) {
                        parentId = parentId0;
                    }
                }
            }
            int oldnum = 0;
            int newnum = 0;
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Object> map = listMap.get(i);
                int currnum = (int)map.get("num");
                if (currnum == 0) {
                    parentId = (String)map.get("id");
                    map.put("parentId", "");
                }
                if (currnum != oldnum) {
                    map.put("parentId", parentId);
                    if (newnum == 0) {
                        newnum = currnum;
                    }
                    if (newnum != currnum) {
                        oldnum = newnum;
                        newnum = currnum;
                        parentId = (String)listMap.get(i - 1).get("id");
                        map.put("parentId", parentId);
                    }
                }
            }
            parentId = "0";
            List<Map<String, Object>> childrenMap = new ArrayList<Map<String, Object>>();
            for (int i = listMap.size() - 1; i >= 0; i--) {
                Map<String, Object> map = listMap.get(i);
                String id = (String)map.get("id");
                if (StringUtils.isNotBlank(parentId) && !parentId.equals(id)) {
                    parentId = (String)map.get("parentId");
                    childrenMap.add(map);
                } else {
                    map.put("children", childrenMap);
                    map.put("collapsed", false);
                    parentId = (String)map.get("parentId");
                    childrenMap = new ArrayList<Map<String, Object>>();
                    childrenMap.add(map);
                    if ("".equals(parentId)) {
                        resMap = map;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resMap;
    }
}
