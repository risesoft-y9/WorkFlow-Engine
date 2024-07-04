package net.risesoft.service.impl;

import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.CustomRecycleService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service(value = "customRecycleService")
public class CustomRecycleServiceImpl implements CustomRecycleService {

    private final HistoryService historyService;

    @Override
    public long getRecycleCountByProcessDefinitionKey(String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).deleted()
            .count();
    }

    @Override
    public long getRecycleCountBySystemName(String systemName) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).deleted()
            .count();
    }

    @Override
    public long getRecycleCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .processDefinitionKey(processDefinitionKey).deleted().count();
    }

    @Override
    public long getRecycleCountByUserIdAndSystemName(String userId, String systemName) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .processInstanceBusinessKey(systemName).deleted().count();
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> getRecycleListByProcessDefinitionKey(String processDefinitionKey,
        Integer page, Integer rows) {
        long totalCount = this.getRecycleCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).deleted()
                .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> getRecycleListBySystemName(String systemName, Integer page,
        Integer rows) {
        long totalCount = this.getRecycleCountBySystemName(systemName);
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).deleted()
                .orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndProcessDefinitionKey(String userId,
        String processDefinitionKey, Integer page, Integer rows) {
        long totalCount = this.getRecycleCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .processDefinitionKey(processDefinitionKey).deleted().orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> getRecycleListByUserIdAndSystemName(String userId, String systemName,
        Integer page, Integer rows) {
        long totalCount = this.getRecycleCountBySystemName(systemName);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .processInstanceBusinessKey(systemName).deleted().orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchRecycleListByProcessDefinitionKey(String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted()
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted()
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchRecycleListBySystemName(String systemName, String searchTerm,
        Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted()
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted()
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndProcessDefinitionKey(String userId,
        String processDefinitionKey, String searchTerm, Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId)
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId)
                .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchRecycleListByUserIdAndSystemName(String userId, String systemName,
        String searchTerm, Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId)
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId)
                .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }
}
