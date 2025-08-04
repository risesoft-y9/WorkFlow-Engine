package net.risesoft.service;

import org.springframework.scheduling.annotation.Async;

import net.risesoft.entity.ProcessParam;
import net.risesoft.model.platform.OrgUnit;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface Process4SearchService {

    /**
     * 启动流程保存流程信息
     *
     * @param tenantId
     * @param processParam
     * @param orgUnit
     */
    void saveToDataCenter(final String tenantId, final ProcessParam processParam, final OrgUnit orgUnit);

    /**
     * 发送修改办件流程信息
     *
     * @param tenantId
     * @param processParam
     * @return
     */
    @Async
    void saveToDataCenter1(final String tenantId, final String taskId, final ProcessParam processParam);
}
