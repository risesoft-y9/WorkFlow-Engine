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
     * @param sourceTenantId
     * @param targetTenantId
     * @param modelKey
     * @throws Exception
     */
    void copyModel(String sourceTenantId, String targetTenantId, String modelKey) throws Exception;
}
