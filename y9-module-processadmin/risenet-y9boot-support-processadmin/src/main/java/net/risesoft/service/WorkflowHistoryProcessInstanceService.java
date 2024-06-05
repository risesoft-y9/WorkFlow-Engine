package net.risesoft.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Service
@RequiredArgsConstructor
@DependsOn({"runtimeService", "repositoryService", "historyService", "taskService"})
public class WorkflowHistoryProcessInstanceService {


    private final HistoryService historyService;

    /**
     * 根据流程实例ID查询历史流程定义对象{@link ProcessDefinition}
     *
     * @param processInstanceId 流程实例ID
     * @return 流程定义对象{@link ProcessDefinition}
     */
    public HistoricProcessInstance findOne(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        }
        return historicProcessInstance;
    }

    /**
     * 获取processInstanceId对应的所有层级的子流程实例Id
     *
     * @param processInstanceId 流程实例id
     * @return List<String>
     */
    public List<String> getHierarchySubProcessInstanceIds(String processInstanceId) {
        List<String> result = new ArrayList<>();
        List<HistoricProcessInstance> list = getHierarchySubProcessInstances(processInstanceId);
        if (list != null) {
            for (HistoricProcessInstance historicProcessInstance : list) {
                result.add(historicProcessInstance.getId());
            }
        }
        return result;
    }

    /**
     * 获取processInstanceId对应的所有层级的子流程
     *
     * @param processInstanceId 流程实例id
     */
    public List<HistoricProcessInstance> getHierarchySubProcessInstances(String processInstanceId) {
        List<HistoricProcessInstance> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            list = historyService.createHistoricProcessInstanceQuery().superProcessInstanceId(processInstanceId).list();
            if (list != null) {
                for (HistoricProcessInstance historicProcessInstance : list) {
                    String subProecssInstanceId = historicProcessInstance.getId();
                    list.addAll(getHierarchySubProcessInstances(subProecssInstanceId));
                }
            }
        }
        return list;
    }

    /**
     * 获取当前流程实例对应的所有层级的父流程实例
     *
     * @param processInstanceId 流程实例id
     * @return List<HistoricProcessInstance>
     */
    public List<HistoricProcessInstance> getHierarchySuperProcessInstance(String processInstanceId) {
        List<HistoricProcessInstance> list = new ArrayList<>();
        HistoricProcessInstance historicProcessInstance;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
            if (historicProcessInstance != null) {
                String superProcessInstanceId = historicProcessInstance.getId();
                list.addAll(getHierarchySuperProcessInstance(superProcessInstanceId));
            }
        }
        return list;
    }

    /**
     * 获取当前流程实例对应的所有层级的的父流程实例Id
     *
     * @param processInstanceId 流程实例id
     * @return List<String>
     */
    public List<String> getHierarchySuperProcessInstanceId(String processInstanceId) {
        List<String> result = new ArrayList<>();
        List<HistoricProcessInstance> list = getHierarchySuperProcessInstance(processInstanceId);
        if (list != null) {
            for (HistoricProcessInstance historicProcessInstance : list) {
                result.add(historicProcessInstance.getId());
            }
        }
        return result;
    }

    /**
     * 获取processInstanceId对应的子流程实例Id
     *
     * @param processInstanceId 流程实例id
     * @return List<String>
     */
    public List<String> getSubProcessInstanceIds(String processInstanceId) {
        List<String> result = new ArrayList<>();
        List<HistoricProcessInstance> list = getSubProcessInstances(processInstanceId);
        if (list != null) {
            for (HistoricProcessInstance historicProcessInstance : list) {
                result.add(historicProcessInstance.getId());
            }
        }
        return result;
    }

    /**
     * 获取processInstanceId对应的子流程
     *
     * @param processInstanceId 流程实例id
     */
    public List<HistoricProcessInstance> getSubProcessInstances(String processInstanceId) {
        List<HistoricProcessInstance> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processInstanceId)) {
            list = historyService.createHistoricProcessInstanceQuery().superProcessInstanceId(processInstanceId).list();
        }
        return list;
    }

    /**
     * 获取当前流程实例的父流程实例
     *
     * @param processInstanceId 流程实例id
     * @return HistoricProcessInstance
     */
    public HistoricProcessInstance getSuperProcessInstance(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = null;
        if (StringUtils.isNotBlank(processInstanceId)) {
            historicProcessInstance =
                    historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
        }
        return historicProcessInstance;
    }

    /**
     * 获取当前流程实例的父流程实例Id
     *
     * @param processInstanceId 流程实例id
     * @return String
     */
    public String getSuperProcessInstanceId(String processInstanceId) {
        HistoricProcessInstance historicProcessInstance = getSuperProcessInstance(processInstanceId);
        if (historicProcessInstance != null) {
            return historicProcessInstance.getSuperProcessInstanceId();
        }
        return "";
    }
}
