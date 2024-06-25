package net.risesoft.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.impl.HistoricProcessInstanceQueryProperty;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.model.processadmin.ProcessInstanceModel;
import net.risesoft.service.CustomDoingService;
import net.risesoft.util.FlowableModelConvertUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customDoingService")
public class CustomDoingServiceImpl implements CustomDoingService {

    private final RuntimeService runtimeService;

    @Override
    public long getCountByUserId(String userId) {
        return runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
            .count();
    }

    @Override
    public long getCountByUserIdAndProcessDefinitionKey(String userId, String processDefinitionKey) {
        return runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
            .processDefinitionKey(processDefinitionKey).count();
    }

    @Override
    public long getCountByUserIdAndSystemName(String userId, String systemName) {
        return runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
            .processInstanceBusinessKey(systemName).count();
    }

    @Override
    public Map<String, Object> getListByUserId(String userId, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = this.getCountByUserId(userId);
        List<ProcessInstance> hpiList =
            runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
                .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
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
        long totalCount = this.getCountByUserIdAndProcessDefinitionKey(userId, processDefinitionKey);
        List<ProcessInstance> hpiList = runtimeService.createProcessInstanceQuery().involvedUser(userId).active()
            .variableNotExists(userId).processDefinitionKey(processDefinitionKey)
            .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
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
        long totalCount = this.getCountByUserIdAndSystemName(userId, systemName);
        List<ProcessInstance> hpiList = runtimeService.createProcessInstanceQuery().involvedUser(userId).active()
            .variableNotExists(userId).processInstanceBusinessKey(systemName)
            .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }

    @Override
    public Map<String, Object> searchListByUserId(String userId, String searchTerm, Integer page, Integer rows) {
        Map<String, Object> returnMap = new HashMap<>(16);
        long totalCount = runtimeService.createProcessInstanceQuery().involvedUser(userId).active()
            .variableNotExists(userId).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<ProcessInstance> hpiList = runtimeService.createProcessInstanceQuery().involvedUser(userId).active()
            .variableNotExists(userId).variableValueLike("searchTerm", "%" + searchTerm + "%")
            .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
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
        long totalCount = runtimeService.createProcessInstanceQuery().involvedUser(userId).active()
            .variableNotExists(userId).processDefinitionKey(processDefinitionKey)
            .variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<ProcessInstance> hpiList =
            runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
                .processDefinitionKey(processDefinitionKey).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
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
            runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
                .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%").count();
        List<ProcessInstance> hpiList =
            runtimeService.createProcessInstanceQuery().involvedUser(userId).active().variableNotExists(userId)
                .processInstanceBusinessKey(systemName).variableValueLike("searchTerm", "%" + searchTerm + "%")
                .orderBy(HistoricProcessInstanceQueryProperty.START_TIME).desc().listPage((page - 1) * rows, rows);
        List<ProcessInstanceModel> hpiModelList = FlowableModelConvertUtil.processInstanceList2ModelList(hpiList);
        returnMap.put("currpage", page);
        returnMap.put("totalpages", (totalCount + rows - 1) / rows);
        returnMap.put("total", totalCount);
        returnMap.put("rows", hpiModelList);
        return returnMap;
    }
}
