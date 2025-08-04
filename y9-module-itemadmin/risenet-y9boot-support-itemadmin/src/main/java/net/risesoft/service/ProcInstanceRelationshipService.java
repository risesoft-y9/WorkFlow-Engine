package net.risesoft.service;

import net.risesoft.entity.ProcInstanceRelationship;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ProcInstanceRelationshipService {

    /**
     * 根据流程实例Id查找对应的实体类
     *
     * @param procInstanceId 流程实例Id
     * @return
     */
    ProcInstanceRelationship findOne(String procInstanceId);

    /**
     * 获取procInstanceId的父流程实例Id
     *
     * @param procInstanceId 子流程实例Id
     * @return
     */
    String getParentProcInstanceId(String procInstanceId);

    /**
     * 保存
     *
     * @param procInstanceId 子流程实例Id
     * @param parentProcInstanceId 父流程实例Id
     */
    void save(String procInstanceId, String parentProcInstanceId, String procDefinitionKey);

    /**
     * 设置常用值
     *
     * @param procInstanceId
     * @param parentProcInstanceId
     * @param procDefinitionKey
     * @return
     */
    ProcInstanceRelationship setCommon(String procInstanceId, String parentProcInstanceId, String procDefinitionKey);
}
