package net.risesoft.service;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface ProcessModelService {

    /**
     * 把源租户的model_key对应的设计模型复制到目标租户并部署
     *
     * @param sourceTenantId 源租户id
     * @param targetTenantId 目标租户id
     * @param modelKey 模型key
     */
    void copyModel(String sourceTenantId, String targetTenantId, String modelKey);
}
