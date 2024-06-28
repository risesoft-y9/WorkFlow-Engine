package net.risesoft.api;

import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.processadmin.BpmnModelApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricActivityService;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.BpmnAutoLayout;
import org.flowable.bpmn.converter.BpmnXMLConverter;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.ExclusiveGateway;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.GraphicInfo;
import org.flowable.bpmn.model.ParallelGateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.image.ProcessDiagramGenerator;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.flowable.ui.common.util.XmlUtil;
import org.flowable.ui.modeler.domain.AbstractModel;
import org.flowable.ui.modeler.domain.Model;
import org.flowable.ui.modeler.model.ModelRepresentation;
import org.flowable.ui.modeler.repository.ModelRepository;
import org.flowable.ui.modeler.serviceapi.ModelService;
import org.flowable.validation.ProcessValidator;
import org.flowable.validation.ProcessValidatorFactory;
import org.flowable.validation.ValidationError;
import org.flowable.variable.api.history.HistoricVariableInstance;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 流程图接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/bpmnModel")
public class BpmnModelApiImpl implements BpmnModelApi {

    private final CustomHistoricProcessService customHistoricProcessService;

    private final CustomHistoricTaskService customHistoricTaskService;

    private final CustomHistoricVariableService customHistoricVariableService;

    private final TaskService taskService;

    private final RuntimeService runtimeService;

    private final CustomHistoricActivityService customHistoricActivityService;

    private final RepositoryService repositoryService;

    private final PersonApi personManager;

    private final OfficeDoneInfoApi officeDoneInfoManager;

    private final ProcessParamApi processParamManager;

    private final ProcessTrackApi processTrackManager;

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    /**
     * 删除模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @Override
    @RequestMapping(value = "/deleteModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deleteModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        modelService.deleteModel(modelId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 根据Model部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<String>
     */
    @Override
    @RequestMapping(value = "/deployModel", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> deployModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Model modelData = modelService.getModel(modelId);
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().isEmpty()) {
            return Y9Result.failure("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
        return Y9Result.successMsg("部署成功");
    }

    /**
     * 生成流程图
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return byte[]
     * @throws Exception Exception
     */
    @Override
    @PostMapping(value = "/genProcessDiagram", produces = MediaType.APPLICATION_JSON_VALUE)
    public byte[] genProcessDiagram(@RequestParam String tenantId, @RequestParam String processInstanceId)
        throws Exception {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricProcessInstance pi = customHistoricProcessService.getById(processInstanceId);
        // 流程走完的不显示图
        if (pi == null) {
            return null;
        }
        InputStream in;
        ProcessEngine processEngine = Y9Context.getBean(ProcessEngine.class);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        ProcessEngineConfiguration engConf = processEngine.getProcessEngineConfiguration();
        ProcessDiagramGenerator diagramGenerator = engConf.getProcessDiagramGenerator();
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
            in = diagramGenerator.generateDiagram(bpmnModel, "png", activityIds, flows, engConf.getActivityFontName(),
                    engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), 1.0, false);
        } else {
            // 获取流程图
            in = diagramGenerator.generateDiagram(bpmnModel, "png", engConf.getActivityFontName(),
                    engConf.getLabelFontName(), engConf.getAnnotationFontName(), engConf.getClassLoader(), false);
        }

        byte[] buf = new byte[1024];
        int length;
        try {
            ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
            while ((length = in.read(buf)) != -1) {
                swapStream.write(buf, 0, length);
            }
            return swapStream.toByteArray();
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * 获取流程图模型
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getBpmnModel", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getBpmnModel(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        Map<String, Object> map = new HashMap<>(16);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricProcessInstance pi = customHistoricProcessService.getById(processInstanceId);
        // 流程走完的不显示图
        if (pi == null) {
            map.put("success", false);
            return map;
        }
        String txtFlowPath = "";
        List<Map<String, Object>> nodeDataArray = new ArrayList<>();
        List<Map<String, Object>> linkDataArray = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        Map<String, GraphicInfo> infoMap = bpmnModel.getLocationMap();
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            Map<String, Object> nodeMap = new HashMap<>(16);
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
                // 获取开始节点输出路线
                List<SequenceFlow> list = startEvent.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement fe = tr.getTargetFlowElement();
                    if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        Map<String, Object> linkMap = new HashMap<>(16);
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
                List<SequenceFlow> list = userTask.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement fe = tr.getTargetFlowElement();
                    if (fe instanceof ExclusiveGateway) {
                        // 目标节点时排他网关时，需要再次获取输出路线
                        ExclusiveGateway gateway = (ExclusiveGateway)fe;
                        List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();
                        for (SequenceFlow sf : outgoingFlows) {
                            FlowElement element = sf.getTargetFlowElement();
                            if (element instanceof UserTask) {
                                UserTask task = (UserTask)element;
                                Map<String, Object> linkMap = new HashMap<>(16);
                                linkMap.put("from", userTask.getId());
                                linkMap.put("to", task.getId());
                                linkDataArray.add(linkMap);
                            } else if (element instanceof EndEvent) {
                                EndEvent endEvent = (EndEvent)element;
                                Map<String, Object> linkMap = new HashMap<>(16);
                                linkMap.put("from", userTask.getId());
                                linkMap.put("to", endEvent.getId());
                                linkDataArray.add(linkMap);
                            } else if (element instanceof ParallelGateway) {
                                ParallelGateway parallelgateway = (ParallelGateway)element;
                                List<SequenceFlow> outgoingFlows1 = parallelgateway.getOutgoingFlows();
                                for (SequenceFlow sf1 : outgoingFlows1) {
                                    FlowElement element1 = sf1.getTargetFlowElement();
                                    if (element1 instanceof UserTask) {
                                        UserTask task1 = (UserTask)element1;
                                        Map<String, Object> linkMap = new HashMap<>(16);
                                        linkMap.put("from", userTask.getId());
                                        linkMap.put("to", task1.getId());
                                        linkDataArray.add(linkMap);
                                    }
                                }
                            }
                        }
                    } else if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        Map<String, Object> linkMap = new HashMap<>(16);
                        linkMap.put("from", userTask.getId());
                        linkMap.put("to", u.getId());
                        linkDataArray.add(linkMap);
                    } else if (fe instanceof EndEvent) {
                        EndEvent endEvent = (EndEvent)fe;
                        Map<String, Object> linkMap = new HashMap<>(16);
                        linkMap.put("from", userTask.getId());
                        linkMap.put("to", endEvent.getId());
                        linkDataArray.add(linkMap);
                    } else if (fe instanceof ParallelGateway) {
                        ParallelGateway gateway = (ParallelGateway)fe;
                        List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();
                        for (SequenceFlow sf : outgoingFlows) {
                            FlowElement element = sf.getTargetFlowElement();
                            if (element instanceof UserTask) {
                                UserTask task = (UserTask)element;
                                Map<String, Object> linkMap = new HashMap<>(16);
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
        map.put("isCompleted", pi.getEndTime() != null);
        map.put("success", true);
        return map;
    }

    /**
     * 获取流程图数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return Map<String, Object>
     */
    @Override
    @GetMapping(value = "/getFlowChart", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> getFlowChart(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        Map<String, Object> resMap = new HashMap<>(16);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Map<String, Object>> listMap = new ArrayList<>();
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
            list.sort((o1, o2) -> {
                if (o1.getEndTime() == null || o2.getEndTime() == null) {
                    return 0;
                }
                long endTime1 = o1.getEndTime().getTime();
                long endTime2 = o2.getEndTime().getTime();
                return Long.compare(endTime1, endTime2);
            });
            int num = 0;
            for (HistoricActivityInstance his : list) {
                String id = his.getId();
                String taskId = his.getTaskId();
                String type = his.getActivityType();
                if (type.contains(SysVariables.STARTEVENT) || type.contains("Flow")) {
                    continue;
                }
                if (type.contains(SysVariables.ENDEVENT)) {
                    num += 1;
                    String completer = (String)listMap.get(listMap.size() - 1).get("title");
                    if (completer.contains("主办")) {
                        completer = completer.substring(0, completer.length() - 4);
                    }
                    Map<String, Object> map = new HashMap<>(16);
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
                Person person = personManager.get(tenantId, userId).getData();
                if ("".equals(activityId) || activityId.equals(his.getActivityId())) {
                    Map<String, Object> map = new HashMap<>(16);
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
                    Map<String, Object> map = new HashMap<>(16);
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

                List<ProcessTrackModel> ptList =
                    processTrackManager.findByTaskIdAsc(tenantId, userId, taskId).getData();
                String parentId0 = taskId;
                for (int j = 0; j < ptList.size(); j++) {
                    num += 1;
                    ProcessTrackModel pt = ptList.get(j);
                    if (j != 0) {
                        parentId0 = pt.getId();
                    }
                    Map<String, Object> map = new HashMap<>(16);
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
            int oldNum = 0;
            int newNum = 0;
            for (int i = 0; i < listMap.size(); i++) {
                Map<String, Object> map = listMap.get(i);
                int currNum = (int)map.get("num");
                if (currNum == 0) {
                    parentId = (String)map.get("id");
                    map.put("parentId", "");
                }
                if (currNum != oldNum) {
                    map.put("parentId", parentId);
                    if (newNum == 0) {
                        newNum = currNum;
                    }
                    if (newNum != currNum) {
                        oldNum = newNum;
                        newNum = currNum;
                        parentId = (String)listMap.get(i - 1).get("id");
                        map.put("parentId", parentId);
                    }
                }
            }
            parentId = "0";
            List<Map<String, Object>> childrenMap = new ArrayList<>();
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
                    childrenMap = new ArrayList<>();
                    childrenMap.add(map);
                    if ("".equals(parentId)) {
                        resMap = map;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取流程图数据失败", e);
        }
        return resMap;
    }

    /**
     * 获取模型列表
     *
     * @param tenantId 租户id
     * @return Y9Result<List<Map<String, Object>>>
     */
    @Override
    @RequestMapping(value = "/getModelList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> getModelList(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<Map<String, Object>> items = new ArrayList<>();
        List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Map<String, Object> mapTemp;
        for (AbstractModel model : list) {
            mapTemp = new HashMap<>(16);
            mapTemp.put("id", model.getId());
            mapTemp.put("key", model.getKey());
            mapTemp.put("name", model.getName());
            mapTemp.put("version", 0);
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(model.getKey())
                .latestVersion().singleResult();
            if (null != processDefinition) {
                mapTemp.put("version", processDefinition.getVersion());
            }
            mapTemp.put("createTime", sdf.format(model.getCreated()));
            mapTemp.put("lastUpdateTime", sdf.format(model.getLastUpdated()));
            items.add(mapTemp);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取流程设计模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return Y9Result<Map<String, Object>>
     */
    @Override
    @RequestMapping(value = "/getModelXml")
    public Y9Result<Map<String, Object>> getModelXml(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        byte[] bpmnBytes = null;
        Map<String, Object> map = new HashMap<>();
        try {
            Model model = modelService.getModel(modelId);
            map.put("key", model.getKey());
            map.put("name", model.getName());
            bpmnBytes = modelService.getBpmnXML(model);
        } catch (Exception e) {
            LOGGER.error("获取流程设计模型xml失败", e);
        }
        map.put("xml", bpmnBytes == null ? "" : new String(bpmnBytes, StandardCharsets.UTF_8));
        return Y9Result.success(map, "获取成功");
    }

    /**
     * 导入流程模型
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 模型文件
     * @return Map<String, Object>
     */
    @Override
    @RequestMapping(value = "/import")
    public Map<String, Object> importProcessModel(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam MultipartFile file) {
        Map<String, Object> map = new HashMap<>(16);
        map.put("success", false);
        map.put("msg", "导入失败");

        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            Person person = personManager.get(tenantId, userId).getData();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                map.put("msg", "导入失败：模板验证失败，原因: " + es);
                return map;
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                map.put("msg", "导入失败： 上传的文件中不存在流程的信息");
                return map;
            }
            if (bpmnModel.getLocationMap().isEmpty()) {
                BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                bpmnLayout.execute();
            }
            BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
            ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            String description = process.getDocumentation();
            // ModelRepresentation model = modelService.getModelRepresentation(modelId);
            // model.setKey(process.getId());
            // model.setName(name);
            // model.setDescription(description);
            // model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            // 查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(process.getId(), AbstractModel.MODEL_TYPE_BPMN);
            if (!models.isEmpty()) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(name);
            newModel.setKey(process.getId());
            newModel.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(person.getName());
            newModel.setDescription(description);
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(person.getName());
            newModel.setTenantId(tenantId);
            modelService.createModel(newModel, userId);
            map.put("success", true);
            map.put("msg", "导入成功");
            return map;
        } catch (Exception e) {
            LOGGER.error("导入流程模型失败", e);
        }
        return map;
    }

    /**
     * 保存模型xml
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param modelId 模型id
     * @param file 模型文件
     * @return Y9Result<String>
     */
    @Override
    @RequestMapping(value = "/saveModelXml")
    public Y9Result<String> saveModelXml(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String modelId, @RequestParam MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            Person person = personManager.get(tenantId, userId).getData();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuffer es = new StringBuffer();
                errors.forEach(ve -> es.append(ve.toString()).append("/n"));
                return Y9Result.failure("保存失败：模板验证失败，原因: " + es);
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("保存失败： 文件中不存在流程的信息");
            }
            if (bpmnModel.getLocationMap().isEmpty()) {
                BpmnAutoLayout bpmnLayout = new BpmnAutoLayout(bpmnModel);
                bpmnLayout.execute();
            }
            BpmnJsonConverter bpmnJsonConverter = new BpmnJsonConverter();
            ObjectNode modelNode = bpmnJsonConverter.convertToJson(bpmnModel);
            org.flowable.bpmn.model.Process process = bpmnModel.getMainProcess();
            String name = process.getId();
            if (StringUtils.isNotEmpty(process.getName())) {
                name = process.getName();
            }
            String description = process.getDocumentation();

            ModelRepresentation model = modelService.getModelRepresentation(modelId);
            model.setKey(process.getId());
            model.setName(name);
            model.setDescription(description);
            model.setModelType(AbstractModel.MODEL_TYPE_BPMN);
            // 查询是否已经存在流程模板
            Model newModel = new Model();
            List<Model> models = modelRepository.findByKeyAndType(model.getKey(), model.getModelType());
            if (!models.isEmpty()) {
                Model updateModel = models.get(0);
                newModel.setId(updateModel.getId());
            }
            newModel.setName(model.getName());
            newModel.setKey(model.getKey());
            newModel.setModelType(model.getModelType());
            newModel.setCreated(Calendar.getInstance().getTime());
            newModel.setCreatedBy(person.getName());
            newModel.setDescription(model.getDescription());
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(person.getName());
            newModel.setTenantId(tenantId);
            modelService.createModel(newModel, userId);
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存模型xml失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}
