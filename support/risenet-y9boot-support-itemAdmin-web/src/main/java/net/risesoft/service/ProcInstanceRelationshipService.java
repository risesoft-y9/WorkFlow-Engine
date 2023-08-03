package net.risesoft.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import net.risesoft.entity.ProcInstanceRelationship;
import net.risesoft.model.processadmin.HistoricProcessInstanceModel;
import net.risesoft.repository.jpa.ProcInstanceRelationshipRepository;
import net.risesoft.util.ListUtil;
import net.risesoft.y9.Y9LoginUserHolder;

import y9.client.rest.processadmin.HistoricProcessApiClient;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service
public class ProcInstanceRelationshipService {

    private static final Logger logger = LoggerFactory.getLogger(ProcInstanceRelationshipService.class);

    @Autowired
    private ProcInstanceRelationshipRepository procInstanceRelationshipRepository;

    @Autowired
    private HistoricProcessApiClient historicProcessManager;

    /**
     * 查询procInstanceId为给定参数的所有数据，即查询procInstanceId的所有子流程
     * 
     * @param parentProcInstanceId 父流程实例Id
     * @return
     */
    public List<ProcInstanceRelationship> findByParentProcInstanceId(String procInstanceId) {
        List<ProcInstanceRelationship> list = new ArrayList<ProcInstanceRelationship>();
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
        List<ProcInstanceRelationship> list = new ArrayList<ProcInstanceRelationship>();
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
        List<String> results = new ArrayList<String>();
        // 根据当前流程实例查找其子流程实例Id
        List<String> subProcessInstanceIds = getSubProcessInstanceIds(processInstanceId);
        if (subProcessInstanceIds != null && subProcessInstanceIds.size() > 0) {
            results.addAll(getSubProcessInstanceIds(processInstanceId));
        }
        String parentProcessInstanceId = getParentProcInstanceId(processInstanceId);
        // 如果存在父流程实例id，首先添加其父流程实例Id，同时获取父流程实例的所有子流程实例Id
        if (StringUtils.isNotBlank(parentProcessInstanceId)) {
            results.add(parentProcessInstanceId);
            subProcessInstanceIds = getSubProcessInstanceIds(parentProcessInstanceId);
            if (subProcessInstanceIds != null && subProcessInstanceIds.size() > 0) {
                results.addAll(subProcessInstanceIds);
            }
        }
        // 下面是通过call activity方法调用子流程后，获取相关流程实例Id的方法，当前只适用于向上和向下查找一层
        List<HistoricProcessInstanceModel> hpiModel =
            historicProcessManager.getBySuperProcessInstanceId(tenantId, processInstanceId);
        for (HistoricProcessInstanceModel hpi : hpiModel) {
            subProcessInstanceIds.add(hpi.getId());
        }
        if (subProcessInstanceIds != null && subProcessInstanceIds.size() > 0) {
            results.addAll(subProcessInstanceIds);
        }
        String superProcessInstanceId =
            historicProcessManager.getSuperProcessInstanceById(tenantId, processInstanceId).getId();
        if (StringUtils.isNotBlank(superProcessInstanceId)) {
            results.add(superProcessInstanceId);
            List<HistoricProcessInstanceModel> hpiModelTemp =
                historicProcessManager.getBySuperProcessInstanceId(tenantId, processInstanceId);
            for (HistoricProcessInstanceModel hpi : hpiModelTemp) {
                subProcessInstanceIds.add(hpi.getId());
            }
            if (subProcessInstanceIds != null && subProcessInstanceIds.size() > 0) {
                results.addAll(subProcessInstanceIds);
            }
        }
        // 去除重复的元素
        ListUtil.removeDuplicateWithOrder(results);
        // 删除processInstanceId
        ListUtil.traversalDel(results, processInstanceId);
        // 将processInstanceId放在第一个
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
        List<String> results = new ArrayList<String>();
        results.addAll(getSubProcessInstanceIds(processInstanceId));
        List<HistoricProcessInstanceModel> hpiModel =
            historicProcessManager.getBySuperProcessInstanceId(tenantId, processInstanceId);
        List<String> list = new ArrayList<String>();
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
        List<String> results = new ArrayList<String>();
        results.add(getParentProcInstanceId(processInstanceId));
        results.add(historicProcessManager.getSuperProcessInstanceById(tenantId, processInstanceId).getId());
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
        List<String> result = new ArrayList<String>();
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
        List<String> list = new ArrayList<String>();
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
            List<String> list = new ArrayList<String>();
            List<HistoricProcessInstanceModel> hpiModelTemp =
                historicProcessManager.getBySuperProcessInstanceId(tenantId, processInstanceId);
            for (HistoricProcessInstanceModel hpi : hpiModelTemp) {
                list.add(hpi.getId());
            }

            if (list != null) {
                count = list.size();
                if (count > 0) {
                    return true;
                }
            }
        }
        return false;
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
            logger.error("保存流程关系异常：", e);
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
