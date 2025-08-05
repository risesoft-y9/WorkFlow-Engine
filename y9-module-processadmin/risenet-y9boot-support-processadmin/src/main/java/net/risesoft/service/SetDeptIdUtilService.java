package net.risesoft.service;

import java.util.Map;

import org.flowable.task.service.delegate.DelegateTask;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
public interface SetDeptIdUtilService {

    /**
     * 保存委办局id,科室id
     *
     * @param taskEntity 任务
     * @param map 变量
     */
    void setDeptId(final DelegateTask taskEntity, final Map<String, Object> map);
}