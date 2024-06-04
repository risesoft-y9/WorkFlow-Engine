package net.risesoft.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.util.SysVariables;
import net.risesoft.util.WorkflowUtils;
import net.risesoft.y9.Y9Context;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.EndEvent;
import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.Gateway;
import org.flowable.bpmn.model.SequenceFlow;
import org.flowable.bpmn.model.StartEvent;
import org.flowable.bpmn.model.UserTask;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.impl.RepositoryServiceImpl;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ProcessDefinitionEntity;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.repository.ProcessDefinitionQuery;
import org.springframework.context.annotation.DependsOn;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

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

    private final  RepositoryService repositoryService;

    private final  WorkflowHistoryProcessInstanceService workflowHistoryProcessInstanceService;

    /**
     * 重新部署所有流程定义
     *
     * @throws Exception
     */
    public void deployAllFromClasspath() throws Exception {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        File file = null;

        String classpathResourceUrl = "classpath:/application.yml";
        Resource resource = resourceLoader.getResource(classpathResourceUrl);
        if (resource.exists()) {
            file = resource.getFile();
            file = file.getParentFile();
            file = new File(file, "deployments");
        } else {
            LOGGER.error("类路径下不存在application.yaml，不应该啊！");

            String root = Y9Context.getWebRootRealPath();
            file = new File(root);
            file = new File(file, "WEB-INF");
            file = new File(file, "classes");
            file = new File(file, "deployments");
        }

        if (!file.exists()) {
            file.mkdirs();
        }

        Collection<File> files = FileUtils.listFiles(file, new String[] {"bar"}, false);
        for (File f : files) {
            deploySingleProcess(f.getName());
        }
    }

    /**
     * 部署单个流程定义
     *
     * @param processKey 流程定义Key
     * @throws IOException 找不到zip文件时
     */
    private void deploySingleProcess(String processKey) throws IOException {
        ResourceLoader resourceLoader = new DefaultResourceLoader();
        String classpathResourceUrl = "classpath:/deployments/" + processKey + ".bar";
        LOGGER.debug("read workflow from: {}", classpathResourceUrl);
        Resource resource = resourceLoader.getResource(classpathResourceUrl);
        InputStream inputStream = resource.getInputStream();
        if (inputStream == null) {
            LOGGER.warn("ignore deploy workflow module: {}", classpathResourceUrl);
        } else {
            LOGGER.debug("finded workflow module: {}, deploy it!", classpathResourceUrl);
            ZipInputStream zis = new ZipInputStream(inputStream);
            Deployment deployment = repositoryService.createDeployment().addZipInputStream(zis).deploy();

            // export diagram
            List<ProcessDefinition> list =
                repositoryService.createProcessDefinitionQuery().deploymentId(deployment.getId()).list();
            for (ProcessDefinition processDefinition : list) {
                WorkflowUtils.exportDiagramToFile(repositoryService, processDefinition);
            }
        }
    }

    /**
     * 根据processDefinitionId(例如:luohufawen:10:2494)获取流程定义
     *
     * @param processDefinitionId 流程定义Id(例如:luohufawen:10:2494)
     * @return
     * @throws Exception
     */
    public ProcessDefinitionEntity findProcessDefinition(String processDefinitionId) {
        ProcessDefinitionEntity processDefinition = null;
        if (StringUtils.isNotBlank(processDefinitionId)) {
            try {
                // 取得流程定义
                processDefinition = (ProcessDefinitionEntity)((RepositoryServiceImpl)repositoryService)
                    .getDeployedProcessDefinition(processDefinitionId);
                if (processDefinition == null) {
                    throw new Exception("流程定义未找到!");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return processDefinition;
    }

    /**
     * 根据流程实例ID查询流程定义对象{@link ProcessDefinition}
     *
     * @param processInstanceId 流程实例ID
     * @return 流程定义对象{@link ProcessDefinition}
     */
    public ProcessDefinitionEntity findProcessDefinitionByPid(String processInstanceId) {
        ProcessDefinitionEntity processDefinition = null;
        HistoricProcessInstance historicProcessInstance =
            workflowHistoryProcessInstanceService.findOne(processInstanceId);
        if (historicProcessInstance != null) {
            String processDefinitionId = historicProcessInstance.getProcessDefinitionId();
            if (StringUtils.isNotBlank(processDefinitionId)) {
                processDefinition = findProcessDefinition(processDefinitionId);
            }
        }
        return processDefinition;
    }

    /**
     * 获取ActivityImpl的list
     * @param bpmnModel
     * @return
     */
    public List<FlowElement> getActivityImpls(BpmnModel bpmnModel) {
        List<FlowElement> list = new ArrayList<FlowElement>();
        try {
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            list = (List<FlowElement>)process.getFlowElements();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 获取ActivityImpl的list
     * @param processDefinitionId 流程定义Id
     * @return
     */
    public List<FlowElement> getActivityImpls(String processDefinitionId) {
        List<FlowElement> list = new ArrayList<FlowElement>();
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            list = getActivityImpls(bpmnModel);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     *
     * @param activities 流程定义中的任务节点List
     * @param activityId 任务节点Id（例如 outerflow）
     * @param propertiesNameList 指定要获取的属性的列表
     * @return
     */
    public Map<String, String> getActivityProperties(List<FlowElement> activities, String activityId,
        List<String> propertiesNameList) {
        Map<String, String> result = new HashMap<String, String>(16);
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
     * 获取某一节点的指定属性
     *
     * @param processDefinitionId 流程定义ID
     * @param activityId 任务节点Id（例如 outerflow）
     * @param propertiesNameList 指定要获取的属性的列表
     * @return
     */
    public Map<String, String> getActivityProperties(String processDefinitionId, String activityId,
        List<String> propertiesNameList) {
        Map<String, String> result = new HashMap<String, String>(16);
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            List<FlowElement> list = (List<FlowElement>)process.getFlowElements();
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
        Map<String, String> map = new HashMap<String, String>(16);
        List<String> propertiesNameList = new ArrayList<String>();
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
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<FlowElement> activitieList = new ArrayList<FlowElement>();
        if (isFilter != null && isFilter) {
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
            list.add(tempMap);
        }
        Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
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
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        List<FlowElement> activitieList = new ArrayList<FlowElement>();
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
            if (e instanceof Gateway || e instanceof EndEvent || e instanceof SequenceFlow) {
                sListIterator.remove();
            }
        }
        for (FlowElement activity : activitieList) {
            Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
            tempMap.put("taskDefKey", activity.getId());
            tempMap.put("taskDefName", activity.getName());
            list.add(tempMap);
        }
        Map<String, Object> tempMap = new LinkedHashMap<String, Object>();
        tempMap.put("taskDefKey", "");
        tempMap.put("taskDefName", "流程");
        list.add(0, tempMap);
        return list;
    }

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点
     * @param bpmnModel
     * @return
     */
    public List<FlowElement> getFilteredActivityImpls(BpmnModel bpmnModel) {
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

    /**
     * 获取过滤过的ActivityImpl的list，过滤掉GateWay类型节点，同时过滤掉filterList内包含的类型节点
     *
     * @param processDefinitionId
     * @return
     */
    public List<FlowElement> getFilteredActivityImpls(String processDefinitionId, List<String> filterList) {
        List<FlowElement> resultList = new ArrayList<FlowElement>();
        List<FlowElement> list = new ArrayList<FlowElement>();
        try {
            BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
            org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
            list = (List<FlowElement>)process.getFlowElements();
            if (list.size() > 0) {
                // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
                resultList.addAll(list);
            }
            Iterator<FlowElement> sListIterator = resultList.iterator();
            while (sListIterator.hasNext()) {
                FlowElement e = sListIterator.next();
                if (e instanceof Gateway || filterList.contains(e.getName())) {
                    sListIterator.remove();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultList;
    }

    /**
     * 查询最新的流程定义
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    public ProcessDefinition getLatestProcessDefinition(String processDefinitionKey) {
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey)
                .latestVersion().singleResult();
        }
        return null;
    }

    /**
     * 查询最新的流程定义对应的Id
     *
     * @param processDefinitionKey 流程定义Key，例如luohubanwen
     * @return
     */
    public String getLatestProcessDefinitionId(String processDefinitionKey) {
        String latestProcessDefinitionId = "";
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            ProcessDefinition pd = getLatestProcessDefinition(processDefinitionKey);
            if (pd != null) {
                latestProcessDefinitionId = pd.getId();
            }
        }
        return latestProcessDefinitionId;
    }

    /**
     * 判断当前节点是并行还是串行,得到当前节点的multiInstance
     *
     * @param processDefinitionId 流程定义ID
     * @param activityId 任务节点Id（例如 outerflow）
     * @throws Exception
     * @return PARALLEL表示并行，SEQUENTIAL表示串行
     */
    public String getMultiinstanceType(String processDefinitionId, String activityId) throws Exception {
        String multiInstance = getActivityProperty(processDefinitionId, activityId, SysVariables.MULTIINSTANCE);
        if (StringUtils.isNotBlank(multiInstance)) {
            if (multiInstance.equals(SysVariables.PARALLEL)) {
                return SysVariables.PARALLEL;
            }
            if (multiInstance.equals(SysVariables.SEQUENTIAL)) {
                return SysVariables.SEQUENTIAL;
            }
        }
        return "";
    }

    /**
     * 根据processDefinitionId获取节点类型nodeType对应的节点名称
     *
     * @param processDefinitionId 流程定义Id
     * @param nodeType 节点类型，例如userTask、startEvent、endEvent等
     * @return
     */
    public List<String> getNodeName(String processDefinitionId, String nodeType) {
        List<String> result = new ArrayList<String>();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {

            flowElement.getId();
        }
        return result;
    }

    /**
     * 获取指定processDefinitionKey的所有历史流程定义Id
     *
     * @param processDefinitionKey
     * @return
     */
    public List<String> getProcessDefinitionIds(String processDefinitionKey) {
        List<String> list = new ArrayList<String>();
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
        List<ProcessDefinition> processDefinitionList = new ArrayList<ProcessDefinition>();
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
        List<Integer> list = new ArrayList<Integer>();
        List<ProcessDefinition> processDefinitionList = getProcessDefinitions(processDefinitionKey);
        for (ProcessDefinition pd : processDefinitionList) {
            list.add(pd.getVersion());
        }
        return list;
    }

    /**
     * 根据流程定义processDefinitionKey获取当前流程的启动节点Id
     *
     * @param processDefinitionKey
     * @return
     */
    public String getStartActivityImplByProcessDefinitionKey(String processDefinitionKey) {
        String processDefinitionId = getLatestProcessDefinitionId(processDefinitionKey);
        BpmnModel bpmnModel = repositoryService.getBpmnModel(processDefinitionId);
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        Collection<FlowElement> flowElements = process.getFlowElements();
        for (FlowElement flowElement : flowElements) {
            if (flowElement instanceof StartEvent) {
                return flowElement.getId();
            }
        }
        return null;
    }

    /**
     * 获取开始节点的taskDefinitionKey
     *
     * @param processDefinitionId 流程定义Id，例如luohubanwen:1:8
     * @return
     */
    public String getStartTaskDefinitionKey(String processDefinitionId) {
        String result = "";
        List<FlowElement> resultList = new ArrayList<FlowElement>();
        List<FlowElement> list = getActivityImpls(processDefinitionId);
        if (list.size() > 0) {
            // 这里需要复制一次，因为processDefinition是在内存中的，如果直接对list删除，将会影响processDefinition中的数据
            resultList.addAll(list);
        }
        Iterator<FlowElement> sListIterator = resultList.iterator();
        while (sListIterator.hasNext()) {
            FlowElement e = sListIterator.next();
            if (e instanceof StartEvent) {
                result = e.getId();
                break;
            }
        }
        return result;
    }

    /**
     * Description: 获取流程定义Id
     * 
     * @return
     */
    public Map<String, String> procDefIdMap() {
        Map<String, String> procDefMap = new HashMap<String, String>(16);
        ProcessDefinitionQuery processDefinitionQuery =
            repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc();
        List<ProcessDefinition> processDefinitionList = processDefinitionQuery.list();
        for (ProcessDefinition pd : processDefinitionList) {
            procDefMap.put(pd.getId(), pd.getName());
        }
        return procDefMap;
    }

    /**
     * 重新部署流程定义
     *
     * @param processKeys 流程定义KEY
     * @throws Exception
     */
    public void redeploy(String... processKeys) throws Exception {
        if (ArrayUtils.isNotEmpty(processKeys)) {
            for (String processKey : processKeys) {
                deploySingleProcess(processKey);
            }
        }
    }
}
