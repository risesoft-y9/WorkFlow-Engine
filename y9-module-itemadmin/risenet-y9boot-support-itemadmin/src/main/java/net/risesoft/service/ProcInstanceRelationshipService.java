package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.HistoricProcessApi;
import net.risesoft.entity.ProcInstanceRelationship;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.repository.jpa.ProcInstanceRelationshipRepository;
import net.risesoft.util.ListUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcInstanceRelationshipService {

    private final ProcInstanceRelationshipRepository procInstanceRelationshipRepository;

    private final HistoricProcessApi historicProcessApi;

    /**
     * 查询procInstanceId为给定参数的所有数据，即查询procInstanceId的所有子流程
     *
     * @param procInstanceId 流程实例Id
     * @return
     */
    public List<ProcInstanceRelationship> findByParentProcInstanceId(String procInstanceId) {
        List<ProcInstanceRelationship> list = new ArrayList<>();
        if (StringUtils.isNotBlank(procInstanceId)) {
            list = procInstanceRelationshipRepository.findByParentProcInstanceId(procInstanceId);
        }
        return list;
    }

    /**
     * 查询processDefinitionKey为给定参数的所有数据
     *
     * @param processDefinitionKey 父流程实例Key
     * @return
     */
    public List<ProcInstanceRelationship> findByProcDefKey(String processDefinitionKey) {
        List<ProcInstanceRelationship> list = new ArrayList<>();
        if (StringUtils.isNotBlank(processDefinitionKey)) {
            list = procInstanceRelationshipRepository.findByProcDefKey(processDefinitionKey);
        }
        return list;
    }

    /**
     * 根据流程实例Id查找对应的实体类
     *
     * @param procInstanceId 流程实例Id
     * @return
     */
    public ProcInstanceRelationship findOne(String procInstanceId) {
        return procInstanceRelationshipRepository.findById(procInstanceId).orElse(null);
    }

    /**
     * 查询processInstanceId相关的所有的流程实例Id，不管是父流程还是子流程，包括两种流程调用流程的方式，当前只向上和向下查询一层
     * 结果包含processInstanceId，且processInstanceId在第一个 因此返回结果至少有processInstanceId
     *
     * @param processInstanceId
     */
    public List<String> getAllRelateProcessInstanceIds(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> results = new ArrayList<>();
        // 根据当前流程实例查找其子流程实例Id
        List<String> subProcessInstanceIds = getSubProcessInstanceIds(processInstanceId);
        if (subProcessInstanceIds != null && !subProcessInstanceIds.isEmpty()) {
            results.addAll(getSubProcessInstanceIds(processInstanceId));
        }
        String parentProcessInstanceId = getParentProcInstanceId(processInstanceId);
        if (StringUtils.isNotBlank(parentProcessInstanceId)) {
            results.add(parentProcessInstanceId);
            subProcessInstanceIds = getSubProcessInstanceIds(parentProcessInstanceId);
            if (subProcessInstanceIds != null && !subProcessInstanceIds.isEmpty()) {
                results.addAll(subProcessInstanceIds);
            }
        }
        // 下面是通过call activity方法调用子流程后，获取相关流程实例Id的方法，当前只适用于向上和向下查找一层
        List<HistoricProcessInstanceModel> hpiModel =
            historicProcessApi.getBySuperProcessInstanceId(tenantId, processInstanceId).getData();
        for (HistoricProcessInstanceModel hpi : hpiModel) {
            subProcessInstanceIds.add(hpi.getId());
        }
        if (subProcessInstanceIds != null && !subProcessInstanceIds.isEmpty()) {
            results.addAll(subProcessInstanceIds);
        }
        String superProcessInstanceId =
            historicProcessApi.getSuperProcessInstanceById(tenantId, processInstanceId).getData().getId();
        if (StringUtils.isNotBlank(superProcessInstanceId)) {
            results.add(superProcessInstanceId);
            List<HistoricProcessInstanceModel> hpiModelTemp =
                historicProcessApi.getBySuperProcessInstanceId(tenantId, processInstanceId).getData();
            for (HistoricProcessInstanceModel hpi : hpiModelTemp) {
                subProcessInstanceIds.add(hpi.getId());
            }
            if (subProcessInstanceIds != null && !subProcessInstanceIds.isEmpty()) {
                results.addAll(subProcessInstanceIds);
            }
        }
        ListUtil.removeDuplicateWithOrder(results);
        ListUtil.traversalDel(results, processInstanceId);
        results.add(0, processInstanceId);
        return results;
    }

    /**
     * 查询processInstanceId所有的子流程实例Id，包括两种流程调用流程的方式，当前只查询一层
     *
     * @param processInstanceId
     */
    public List<String> getAllSubProcessInstanceIds(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> results = new ArrayList<>();
        results.addAll(getSubProcessInstanceIds(processInstanceId));
        List<HistoricProcessInstanceModel> hpiModel =
            historicProcessApi.getBySuperProcessInstanceId(tenantId, processInstanceId).getData();
        List<String> list = new ArrayList<>();
        for (HistoricProcessInstanceModel hpi : hpiModel) {
            list.add(hpi.getId());
        }
        results.addAll(list);
        return results;
    }

    /**
     * 查询processInstanceId所有的父流程实例Id，包括两种流程调用流程的方式，当前只查询一层
     *
     * @param processInstanceId
     */
    public List<String> getAllSuperProcessInstanceIds(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        List<String> results = new ArrayList<>();
        results.add(getParentProcInstanceId(processInstanceId));
        results.add(historicProcessApi.getSuperProcessInstanceById(tenantId, processInstanceId).getData().getId());
        return results;
    }

    /**
     * 获取procInstanceId的父流程实例Id
     *
     * @param procInstanceId 子流程实例Id
     * @return
     */
    public String getParentProcInstanceId(String procInstanceId) {
        ProcInstanceRelationship entity = findOne(procInstanceId);
        if (entity != null) {
            return entity.getParentProcInstanceId();
        }
        return "";
    }

    /**
     * 根据流程实例Key获取所有子流程实例Id，目前只用于通过普通usertask节点的监听器调用子流程的情况
     *
     * @param processDefinitionKey 流程实例Key
     * @return
     */
    public List<String> getProcInstanceIdsByProcDefKey(String processDefinitionKey) {
        List<String> result = new ArrayList<>();
        List<ProcInstanceRelationship> list = findByProcDefKey(processDefinitionKey);
        for (ProcInstanceRelationship entity : list) {
            result.add(entity.getProcInstanceId());
        }
        return result;
    }

    /**
     * 获取子流程实例为processInstanceId的个数
     *
     * @param processInstanceId
     * @return
     */
    public int getSubProcessInstanceIdCount(String processInstanceId) {
        if (StringUtils.isNotBlank(processInstanceId)) {
            return procInstanceRelationshipRepository.getSubProcessInstanceIdCount(processInstanceId);
        }
        return 0;
    }

    /**
     * 获取procInstanceId的子流程实例Id
     *
     * @param processInstanceId 流程实例Id
     * @return
     */
    public List<String> getSubProcessInstanceIds(String processInstanceId) {
        List<String> list = new ArrayList<>();
        List<ProcInstanceRelationship> entityList = findByParentProcInstanceId(processInstanceId);
        for (ProcInstanceRelationship entity : entityList) {
            list.add(entity.getProcInstanceId());
        }
        return list;
    }

    /**
     * 当前流程实例是否是子流程实例
     *
     * @param processInstanceId
     * @return ，如果是子流程实例则返回true，否则返回false
     */
    public boolean isSubProcessInstance(String processInstanceId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        int count = 0;
        count = getSubProcessInstanceIdCount(processInstanceId);
        if (count > 0) {
            return true;
        } else {
            List<HistoricProcessInstanceModel> hpiModelTemp =
                historicProcessApi.getBySuperProcessInstanceId(tenantId, processInstanceId).getData();
            count = hpiModelTemp.size();
            return count > 0;
        }
    }

    /**
     * 保存
     *
     * @param procInstanceId 子流程实例Id
     * @param parentProcInstanceId 父流程实例Id
     */
    public void save(String procInstanceId, String parentProcInstanceId, String procDefinitionKey) {
        ProcInstanceRelationship entity = setCommon(procInstanceId, parentProcInstanceId, procDefinitionKey);
        try {
            procInstanceRelationshipRepository.save(entity);
        } catch (Exception e) {
            LOGGER.error("保存流程关系异常：", e);
        }
    }

    /**
     * 设置常用值
     *
     * @param procInstanceId
     * @param parentProcInstanceId
     * @param procDefinitionKey
     * @return
     */
    public ProcInstanceRelationship setCommon(String procInstanceId, String parentProcInstanceId,
        String procDefinitionKey) {
        ProcInstanceRelationship entity = new ProcInstanceRelationship();
        entity.setProcInstanceId(procInstanceId);
        entity.setParentProcInstanceId(parentProcInstanceId);
        entity.setProcDefinitionKey(procDefinitionKey);
        return entity;
    }
}
