package net.risesoft.api.processadmin;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessDataCopyApi {

    /**
     * 复制拷贝流程定义数据
     *
     * @param sourceTenantId 源租户Id
     * @param targetTenantId 目标租户Id
     * @param modelKey 流程定义key
     * @throws Exception 异常
     */
    void copyModel(String sourceTenantId, String targetTenantId, String modelKey) throws Exception;

}
