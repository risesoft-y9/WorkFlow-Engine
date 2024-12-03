package net.risesoft.service;

import net.risesoft.entity.TaskRelated;

import java.util.List;

/**
 * @author qinman
 */
public interface TaskRelatedService {

    void saveOrUpdate(TaskRelated taskRelated);
    List<TaskRelated> findByTaskId(String taskId);
}
