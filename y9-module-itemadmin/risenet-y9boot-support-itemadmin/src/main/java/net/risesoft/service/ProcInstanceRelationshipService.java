package net.risesoft.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ProcInstanceRelationship;
import net.risesoft.repository.jpa.ProcInstanceRelationshipRepository;

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
