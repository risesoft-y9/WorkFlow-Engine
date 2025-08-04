package net.risesoft.service.impl;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.ProcInstanceRelationship;
import net.risesoft.repository.jpa.ProcInstanceRelationshipRepository;
import net.risesoft.service.ProcInstanceRelationshipService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class ProcInstanceRelationshipServiceImpl implements ProcInstanceRelationshipService {

    private final ProcInstanceRelationshipRepository procInstanceRelationshipRepository;

    @Override
    public ProcInstanceRelationship findOne(String procInstanceId) {
        return procInstanceRelationshipRepository.findById(procInstanceId).orElse(null);
    }

    @Override
    public String getParentProcInstanceId(String procInstanceId) {
        ProcInstanceRelationship entity = findOne(procInstanceId);
        if (entity != null) {
            return entity.getParentProcInstanceId();
        }
        return "";
    }

    @Override
    public void save(String procInstanceId, String parentProcInstanceId, String procDefinitionKey) {
        ProcInstanceRelationship entity = setCommon(procInstanceId, parentProcInstanceId, procDefinitionKey);
        try {
            procInstanceRelationshipRepository.save(entity);
        } catch (Exception e) {
            LOGGER.error("保存流程关系异常：", e);
        }
    }

    @Override
    public ProcInstanceRelationship setCommon(String procInstanceId, String parentProcInstanceId,
        String procDefinitionKey) {
        ProcInstanceRelationship entity = new ProcInstanceRelationship();
        entity.setProcInstanceId(procInstanceId);
        entity.setParentProcInstanceId(parentProcInstanceId);
        entity.setProcDefinitionKey(procDefinitionKey);
        return entity;
    }
}
