package net.risesoft.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.apache.commons.io.IOUtils;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.OfficeDoneInfoApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.api.itemadmin.ProcessTrackApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.processadmin.BpmnModelApi;
import net.risesoft.model.itemadmin.OfficeDoneInfoModel;
import net.risesoft.model.itemadmin.ProcessParamModel;
import net.risesoft.model.itemadmin.ProcessTrackModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.model.processadmin.FlowNodeModel;
import net.risesoft.model.processadmin.FlowableBpmnModel;
import net.risesoft.model.processadmin.LinkNodeModel;
import net.risesoft.model.processadmin.Y9BpmnModel;
import net.risesoft.model.processadmin.Y9FlowChartModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomHistoricActivityService;
import net.risesoft.service.CustomHistoricProcessService;
import net.risesoft.service.CustomHistoricTaskService;
import net.risesoft.service.CustomHistoricVariableService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

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

    private final OrgUnitApi orgUnitApi;

    private final OfficeDoneInfoApi officeDoneInfoManager;

    private final ProcessParamApi processParamManager;

    private final ProcessTrackApi processTrackManager;

    private final ModelService modelService;

    private final ModelRepository modelRepository;

    /**
     * 删除流程图模型
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Boolean>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        modelService.deleteModel(modelId);
        return Y9Result.success();
    }

    /**
     * 根据流程图模型部署流程
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deployModel(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Model modelData = modelService.getModel(modelId);
        BpmnModel model = modelService.getBpmnModel(modelData);
        if (model.getProcesses().isEmpty()) {
            return Y9Result.failure("数据模型不符要求，请至少设计一条主线流程。");
        }
        byte[] bpmnBytes = new BpmnXMLConverter().convertToXML(model);
        String processName = modelData.getName() + ".bpmn20.xml";
        repositoryService.createDeployment().name(modelData.getName()).addBytes(processName, bpmnBytes).deploy();
        return Y9Result.success();
    }

    /**
     * 获取流程图字节数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 流程图
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> genProcessDiagram(@RequestParam String tenantId, @RequestParam String processInstanceId) {
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
        byte[] buf;
        try {
            buf = IOUtils.toByteArray(in);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return Y9Result.success(Base64.getEncoder().encodeToString(buf));
    }

    /**
     * 获取流程图模型数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Y9BpmnModel>} 通用请求返回对象 - data 流程图模型
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9BpmnModel> getBpmnModel(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        HistoricProcessInstance pi = customHistoricProcessService.getById(processInstanceId);
        // 流程走完的不显示图
        if (pi == null) {
            return Y9Result.failure("流程已办结");
        }
        String txtFlowPath = "";
        List<FlowNodeModel> nodeDataArray = new ArrayList<>();
        List<LinkNodeModel> linkDataArray = new ArrayList<>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(pi.getProcessDefinitionId());
        Map<String, GraphicInfo> infoMap = bpmnModel.getLocationMap();
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        List<FlowElement> flowElements = (List<FlowElement>)process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                StartEvent startEvent = (StartEvent)flowElement;
                GraphicInfo graphicInfo = infoMap.get(startEvent.getId());
                txtFlowPath = startEvent.getId();
                nodeDataArray.add(new FlowNodeModel(startEvent.getId(), "Start", "开始", "Circle", "#4fba4f", "1",
                    graphicInfo.getX() - 100 + " " + graphicInfo.getY(), ""));
                // 获取开始节点输出路线
                List<SequenceFlow> list = startEvent.getOutgoingFlows();
                for (SequenceFlow tr : list) {
                    FlowElement fe = tr.getTargetFlowElement();
                    if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        linkDataArray.add(new LinkNodeModel(startEvent.getId(), u.getId()));
                    }
                }
            } else if (flowElement instanceof UserTask) {
                UserTask userTask = (UserTask)flowElement;
                GraphicInfo graphicInfo = infoMap.get(userTask.getId());
                nodeDataArray.add(new FlowNodeModel(userTask.getId(), "", userTask.getName(), "", "", "",
                    graphicInfo.getX() + " " + graphicInfo.getY(), "111111111"));
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
                                linkDataArray.add(new LinkNodeModel(userTask.getId(), task.getId()));
                            } else if (element instanceof EndEvent) {
                                EndEvent endEvent = (EndEvent)element;
                                linkDataArray.add(new LinkNodeModel(userTask.getId(), endEvent.getId()));
                            } else if (element instanceof ParallelGateway) {
                                ParallelGateway parallelgateway = (ParallelGateway)element;
                                List<SequenceFlow> outgoingFlows1 = parallelgateway.getOutgoingFlows();
                                for (SequenceFlow sf1 : outgoingFlows1) {
                                    FlowElement element1 = sf1.getTargetFlowElement();
                                    if (element1 instanceof UserTask) {
                                        UserTask task1 = (UserTask)element1;
                                        linkDataArray.add(new LinkNodeModel(userTask.getId(), task1.getId()));
                                    }
                                }
                            }
                        }
                    } else if ((fe instanceof UserTask)) {
                        UserTask u = (UserTask)fe;
                        linkDataArray.add(new LinkNodeModel(userTask.getId(), u.getId()));
                    } else if (fe instanceof EndEvent) {
                        EndEvent endEvent = (EndEvent)fe;
                        linkDataArray.add(new LinkNodeModel(userTask.getId(), endEvent.getId()));
                    } else if (fe instanceof ParallelGateway) {
                        ParallelGateway gateway = (ParallelGateway)fe;
                        List<SequenceFlow> outgoingFlows = gateway.getOutgoingFlows();
                        for (SequenceFlow sf : outgoingFlows) {
                            FlowElement element = sf.getTargetFlowElement();
                            if (element instanceof UserTask) {
                                UserTask task = (UserTask)element;
                                linkDataArray.add(new LinkNodeModel(userTask.getId(), task.getId()));
                            }
                        }
                    }
                }
            } else if (flowElement instanceof EndEvent) {
                EndEvent endEvent = (EndEvent)flowElement;
                GraphicInfo graphicInfo = infoMap.get(endEvent.getId());
                nodeDataArray.add(new FlowNodeModel(endEvent.getId(), "End", "结束", "Circle", "#CE0620", "4",
                    graphicInfo.getX() + " " + graphicInfo.getY(), ""));
            }
        }

        List<HistoricTaskInstance> list = customHistoricTaskService.listByProcessInstanceId(processInstanceId, "");
        for (HistoricTaskInstance task : list) {
            txtFlowPath = Y9Util.genCustomStr(txtFlowPath, task.getTaskDefinitionKey());
        }
        return Y9Result.success(new Y9BpmnModel(nodeDataArray, linkDataArray, txtFlowPath, pi.getEndTime() != null));
    }

    /**
     * 获取流程图数据
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Y9FlowChartModel>} 通用请求返回对象 - data 流程图数据
     * @since 9.6.6
     */
    @Override
    public Y9Result<Y9FlowChartModel> getFlowChart(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9FlowChartModel flowChartModel = new Y9FlowChartModel();
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setTenantId(tenantId);
        List<Y9FlowChartModel> listMap = new ArrayList<>();
        String activityId = "";
        String parentId = "";
        String year = "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            HistoricProcessInstance hpi = customHistoricProcessService.getById(processInstanceId);
            if (hpi == null) {
                OfficeDoneInfoModel officeDoneInfo =
                    officeDoneInfoManager.findByProcessInstanceId(tenantId, processInstanceId).getData();
                if (officeDoneInfo == null) {
                    ProcessParamModel processParam =
                        processParamManager.findByProcessInstanceId(tenantId, processInstanceId).getData();
                    year = processParam.getCreateTime().substring(0, 4);
                } else {
                    year = officeDoneInfo.getStartTime().substring(0, 4);
                }
            }
            List<HistoricActivityInstance> list =
                customHistoricActivityService.listByProcessInstanceIdAndYear(processInstanceId, year);
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
                    String completer = listMap.get(listMap.size() - 1).getTitle();
                    if (completer.contains("主办")) {
                        completer = completer.substring(0, completer.length() - 4);
                    }
                    Y9FlowChartModel flowChart = new Y9FlowChartModel();
                    flowChart.setId(id);
                    flowChart.setName("办结");
                    flowChart.setTitle(completer);
                    flowChart.setParentId(parentId);
                    flowChart.setClassName("specialColor");
                    flowChart.setNum(num);
                    flowChart.setEndTime(his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    listMap.add(flowChart);
                    continue;
                }
                if (type.contains(SysVariables.GATEWAY)) {
                    num += 1;
                    continue;
                }
                String userId = his.getAssignee();
                OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
                if ("".equals(activityId) || activityId.equals(his.getActivityId())) {

                    HistoricVariableInstance historicVariableInstance = customHistoricVariableService
                        .getByTaskIdAndVariableName(taskId, SysVariables.PARALLELSPONSOR, year);
                    Y9FlowChartModel flowChart = new Y9FlowChartModel();
                    flowChart.setId(taskId);
                    flowChart.setName(his.getActivityName());
                    flowChart.setTitle(orgUnit != null ? orgUnit.getName() : "该用户不存在");
                    if (historicVariableInstance != null) {
                        flowChart.setTitle(orgUnit != null ? orgUnit.getName() + "(主办)" : "该用户不存在");
                    }
                    flowChart.setParentId(parentId);
                    flowChart.setClassName(his.getEndTime() != null ? "serverColor" : "specialColor");
                    flowChart.setNum(num);
                    flowChart.setEndTime(his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    listMap.add(flowChart);
                    activityId = his.getActivityId();
                    parentId = taskId;
                } else {
                    num += 1;
                    activityId = his.getActivityId();
                    HistoricVariableInstance historicVariableInstance = customHistoricVariableService
                        .getByTaskIdAndVariableName(taskId, SysVariables.PARALLELSPONSOR, year);
                    Y9FlowChartModel flowChart = new Y9FlowChartModel();
                    flowChart.setId(taskId);
                    flowChart.setName(his.getActivityName());
                    flowChart.setTitle(orgUnit != null ? orgUnit.getName() : "该用户不存在");
                    if (historicVariableInstance != null) {
                        flowChart.setTitle(orgUnit != null ? orgUnit.getName() + "(主办)" : "该用户不存在");
                    }
                    flowChart.setParentId(parentId);
                    flowChart.setClassName(his.getEndTime() != null ? "serverColor" : "specialColor");
                    flowChart.setNum(num);
                    flowChart.setEndTime(his.getEndTime() != null ? his.getEndTime().getTime() : 0);
                    listMap.add(flowChart);
                }

                List<ProcessTrackModel> ptList = processTrackManager.findByTaskIdAsc(tenantId, taskId).getData();
                String parentId0 = taskId;
                for (int j = 0; j < ptList.size(); j++) {
                    num += 1;
                    ProcessTrackModel pt = ptList.get(j);
                    if (j != 0) {
                        parentId0 = pt.getId();
                    }
                    Y9FlowChartModel flowChart = new Y9FlowChartModel();
                    flowChart.setId(pt.getId());
                    flowChart.setName(pt.getTaskDefName());
                    flowChart.setTitle(pt.getSenderName());
                    flowChart.setParentId(parentId0);
                    flowChart.setClassName(StringUtils.isNotBlank(pt.getEndTime()) ? "serverColor" : "specialColor");
                    flowChart.setNum(num);
                    flowChart
                        .setEndTime(StringUtils.isNotBlank(pt.getEndTime()) ? sdf.parse(pt.getEndTime()).getTime() : 0);
                    listMap.add(flowChart);
                    if (j == ptList.size() - 1) {
                        parentId = parentId0;
                    }
                }
            }
            int oldNum = 0;
            int newNum = 0;
            for (int i = 0; i < listMap.size(); i++) {
                Y9FlowChartModel y9FlowChartModel = listMap.get(i);
                int currNum = y9FlowChartModel.getNum();
                if (currNum == 0) {
                    parentId = y9FlowChartModel.getId();
                    y9FlowChartModel.setParentId("");
                }
                if (currNum != oldNum) {
                    y9FlowChartModel.setParentId(parentId);
                    if (newNum == 0) {
                        newNum = currNum;
                    }
                    if (newNum != currNum) {
                        oldNum = newNum;
                        newNum = currNum;
                        parentId = listMap.get(i - 1).getId();
                        y9FlowChartModel.setParentId(parentId);
                    }
                }
            }
            parentId = "0";
            List<Y9FlowChartModel> childrenMap = new ArrayList<>();
            for (int i = listMap.size() - 1; i >= 0; i--) {
                Y9FlowChartModel y9FlowChartModel = listMap.get(i);
                String id = y9FlowChartModel.getId();
                if (StringUtils.isNotBlank(parentId) && !parentId.equals(id)) {
                    parentId = y9FlowChartModel.getParentId();
                    childrenMap.add(y9FlowChartModel);
                } else {
                    y9FlowChartModel.setChildren(childrenMap);
                    y9FlowChartModel.setCollapsed(false);
                    parentId = y9FlowChartModel.getParentId();
                    childrenMap = new ArrayList<>();
                    childrenMap.add(y9FlowChartModel);
                    if ("".equals(parentId)) {
                        flowChartModel = y9FlowChartModel;
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取流程图数据失败", e);
        }
        return Y9Result.success(flowChartModel);
    }

    /**
     * 获取流程模型列表
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<List<FlowableBpmnModel>>} 通用请求返回对象 - data 流模型列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<FlowableBpmnModel>> getModelList(@RequestParam String tenantId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        List<FlowableBpmnModel> items = new ArrayList<>();
        List<AbstractModel> list = modelService.getModelsByModelType(Model.MODEL_TYPE_BPMN);
        ProcessDefinition processDefinition;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        FlowableBpmnModel flowableBpmnModel;
        for (AbstractModel model : list) {
            flowableBpmnModel = new FlowableBpmnModel();
            flowableBpmnModel.setId(model.getId());
            flowableBpmnModel.setKey(model.getKey());
            flowableBpmnModel.setName(model.getName());
            flowableBpmnModel.setVersion(0);
            processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionKey(model.getKey())
                .latestVersion().singleResult();
            if (null != processDefinition) {
                flowableBpmnModel.setVersion(processDefinition.getVersion());
            }

            flowableBpmnModel.setCreateTime(sdf.format(model.getCreated()));
            flowableBpmnModel.setLastUpdateTime(sdf.format(model.getLastUpdated()));
            items.add(flowableBpmnModel);
        }
        return Y9Result.success(items, "获取成功");
    }

    /**
     * 获取流程设计模型xml
     *
     * @param tenantId 租户id
     * @param modelId 模型id
     * @return {@code Y9Result<FlowableBpmnModel>} 通用请求返回对象 - data 流程设计模型xml
     * @since 9.6.6
     */
    @Override
    public Y9Result<FlowableBpmnModel> getModelXml(@RequestParam String tenantId, @RequestParam String modelId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        FlowableBpmnModel flowableBpmnModel = new FlowableBpmnModel();
        try {
            Model model = modelService.getModel(modelId);
            flowableBpmnModel.setKey(model.getKey());
            flowableBpmnModel.setName(model.getName());
            byte[] bpmnBytes = modelService.getBpmnXML(model);
            if (null != bpmnBytes) {
                flowableBpmnModel.setXml(new String(bpmnBytes, StandardCharsets.UTF_8));
            }
        } catch (Exception e) {
            LOGGER.error("获取流程设计模型xml失败", e);
        }
        return Y9Result.success(flowableBpmnModel);
    }

    /**
     * 导入流程模型文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param file 模型文件
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> importProcessModel(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
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
                return Y9Result.failure("导入失败：模板验证失败，原因: " + es);
            }
            if (bpmnModel.getProcesses().isEmpty()) {
                return Y9Result.failure("导入失败： 上传的文件中不存在流程的信息");
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
            newModel.setCreatedBy(orgUnit.getName());
            newModel.setDescription(description);
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(orgUnit.getName());
            newModel.setTenantId(tenantId);
            modelService.createModel(newModel, userId);
            return Y9Result.success();
        } catch (Exception e) {
            LOGGER.error("导入流程模型失败", e);
        }
        return Y9Result.failure("导入失败");
    }

    /**
     * 保存模型xml
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param modelId 模型id
     * @param file 模型文件
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveModelXml(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String modelId, @RequestParam MultipartFile file) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
            XMLInputFactory xif = XmlUtil.createSafeXmlInputFactory();
            InputStreamReader xmlIn = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8);
            XMLStreamReader xtr = xif.createXMLStreamReader(xmlIn);

            BpmnXMLConverter bpmnXmlConverter = new BpmnXMLConverter();
            BpmnModel bpmnModel = bpmnXmlConverter.convertToBpmnModel(xtr);
            // 模板验证
            ProcessValidator validator = new ProcessValidatorFactory().createDefaultProcessValidator();
            List<ValidationError> errors = validator.validate(bpmnModel);
            if (!errors.isEmpty()) {
                StringBuilder es = new StringBuilder();
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
            newModel.setCreatedBy(orgUnit.getName());
            newModel.setDescription(model.getDescription());
            newModel.setModelEditorJson(modelNode.toString());
            newModel.setLastUpdated(Calendar.getInstance().getTime());
            newModel.setLastUpdatedBy(orgUnit.getName());
            newModel.setTenantId(tenantId);
            modelService.createModel(newModel, userId);
            return Y9Result.success();
        } catch (Exception e) {
            LOGGER.error("保存模型xml失败", e);
        }
        return Y9Result.failure("保存失败");
    }
}
