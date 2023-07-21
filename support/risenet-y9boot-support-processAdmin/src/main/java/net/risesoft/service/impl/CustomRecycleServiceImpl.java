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
import net.risesoft.service.CustomRecycleService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Transactional(readOnly = true)
@Service(value = "customRecycleService")
public class CustomRecycleServiceImpl implements CustomRecycleService {

    @Autowired
    private HistoryService historyService;

    @Override
    public long getRecycleCountByProcessDefinitionKey(String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).deleted().count();
    }

    @Override
    public long getRecycleCountBySystemName(String systemName) {
        return historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).deleted().count();
    }

    @Override
    public long getRecycleCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).processDefinitionKey(processDefinitionKey).deleted().count();
    }

    @Override
    public long getRecycleCountByUserIdAndSystemName(String userId, String systemName) {
        return historyService.createHistoricProcessInstanceQuery().involvedUser(userId).processInstanceBusinessKey(systemName).deleted().count();
    }

    @Override
    public Map<String, Object> getRecycleListByProcessDefinitionKey(String processDefinitionKey, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = this.getRecycleCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processDefinitionKey(processDefinitionKey).deleted().orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getRecycleListBySystemName(String systemName, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = this.getRecycleCountBySystemName(systemName);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().processInstanceBusinessKey(systemName).deleted().orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getRecycleListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = this.getRecycleCountByProcessDefinitionKey(processDefinitionKey);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().involvedUser(userId).processDefinitionKey(processDefinitionKey).deleted().orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> getRecycleListByUserIdAndSystemName(String userId, String systemName, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = this.getRecycleCountBySystemName(systemName);
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().involvedUser(userId).processInstanceBusinessKey(systemName).deleted().orderByProcessInstanceStartTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchRecycleListByProcessDefinitionKey(String processDefinitionKey, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted().processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchRecycleListBySystemName(String systemName, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted().processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchRecycleListByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId).processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId).processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%").orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchRecycleListByUserIdAndSystemName(String userId, String systemName, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<String, Object>(16);
        long totalCount = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId).processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<HistoricProcessInstance> list = historyService.createHistoricProcessInstanceQuery().deleted().involvedUser(userId).processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").orderByProcessInstanceEndTime().desc().listPage((page - 1) * rows, rows);
        List<HistoricProcessInstanceModel> modelList = FlowableModelConvertUtil.historicProcessInstanceList2ModelList(list);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", modelList);
        return returnMap;
    }
}
