package net.risesoft.service.impl;

import java.util.List;

import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.service.CustomMonitorService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customMonitorService")
public class CustomMonitorServiceImpl implements CustomMonitorService {

    private final HistoryService historyService;

    @Override
    public long getDoingCountByProcessDefinitionKey(String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey)
            .notDeleted().unfinished().count();
    }

    @Override
    public long getDoingCountBySystemName(String systemName) {
        return historyService.createHistoricProcessInstanceQuery().variableValueEquals("systemName", systemName)
            .notDeleted().unfinished().count();
    }

    @Override
    public long getDoneCountByProcessDefinitionKey(String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey)
            .notDeleted().finished().count();
    }

    @Override
    public long getDoneCountBySystemName(String systemName) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).notDeleted()
            .finished().count();
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> pageDoingListByProcessDefinitionKey(String processDefinitionKey,
        Integer page, Integer rows) {
        long totalCount = this.getDoingCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().notDeleted()
            .unfinished().processDefinitionKey(processDefinitionKey).orderByProcessInstanceStartTime().desc()
            .listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> pageDoingListBySystemName(String systemName, Integer page,
        Integer rows) {
        long totalCount = this.getDoingCountBySystemName(systemName);
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).notDeleted()
                .unfinished().orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> pageDoneListByProcessDefinitionKey(String processDefinitionKey,
        Integer page, Integer rows) {
        long totalCount = this.getDoneCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).notDeleted()
                .finished().orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> pageDoneListBySystemName(String systemName, Integer page,
        Integer rows) {
        long totalCount = this.getDoneCountBySystemName(systemName);
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).notDeleted()
                .finished().orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoingListByProcessDefinitionKey(String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().notDeleted().unfinished()
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list =
            historyService.createHistoricProcessInstanceQuery().notDeleted().unfinished()
                .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoingListBySystemName(String systemName, String searchTerm,
        Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().notDeleted().unfinished()
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().notDeleted()
            .unfinished().processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoneListByProcessDefinitionKey(String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().notDeleted().finished()
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().notDeleted().finished()
            .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }

    @Override
    public Y9Page<HistoricProcessInstanceModel> searchDoneListBySystemName(String systemName, String searchTerm,
        Integer page, Integer rows) {
        long totalCount = historyService.createHistoricProcessInstanceQuery().notDeleted().finished()
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().notDeleted().finished()
            .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        return Y9Page.success(page, (int)(totalCount + rows - 1) / rows, totalCount, modelList);
    }
}
