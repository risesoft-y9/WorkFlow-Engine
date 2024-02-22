package net.risesoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.HistoryService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.service.CustomDoneService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customDoneService")
public class CustomDoneServiceImpl implements CustomDoneService {

    @Autowired
    private HistoryService historyService;

    @Override
    public long getCountByUserId(String userId) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted().finished().count();
    }

    @Override
    public long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted()
            .processDefinitionKey(processDefinitionKey).finished().count();
    }

    @Override
    public long getCountByUserIdAndSystemName(String userId, String systemName) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted()
            .processInstanceBusinessKey(systemName).finished().count();
    }

    @Override
    public Map<String, Object> getListByUserId(String userId, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);

        long totalCount = this.getCountByUserId(userId);
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .notDeleted().finished().orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);

        return returnMap;
    }

    @Override
    public Map<String, Object> getListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserId(userId);
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .notDeleted().processDefinitionKey(processDefinitionKey).finished().orderByProcessInstanceEndTime().desc()
            .listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);

        return returnMap;
    }

    @Override
    public Map<String, Object> getListByUserIdAndSystemName(String userId, String systemName, Integer page,
        Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserId(userId);
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .notDeleted().processInstanceBusinessKey(systemName).finished().orderByProcessInstanceEndTime().desc()
            .listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);

        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted()
            .finished().variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> hpiList = historyService.createHistoricProcessInstanceQuery().involvedUser(userId)
            .notDeleted().finished().variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey,
        String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted()
            .finished().processDefinitionKey(processDefinitionKey)
            .variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> hpiList =
            historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted().finished()
                .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserIdAndSystemName(String userId, String systemName, String searchTerm,
        Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount =
            historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted().finished()
                .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> hpiList =
            historyService.createHistoricProcessInstanceQuery().involvedUser(userId).notDeleted().finished()
                .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> hpiModelList =
            FlowableModelConvertUtil.historicProcessInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }
}
