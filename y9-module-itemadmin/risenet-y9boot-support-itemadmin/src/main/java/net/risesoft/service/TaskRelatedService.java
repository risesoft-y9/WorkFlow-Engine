package net.risesoft.service;

import java.util.List;

import net.risesoft.entity.TaskRelated;

/**
 * @author qinman
 */
public interface TaskRelatedService {

    void saveOrUpdate(TaskRelated taskRelated);
    List<TaskRelated> findByTaskId(String taskId);

    List<TaskRelated> findByProcessSerialNumber(String processSerialNumber);

    TaskRelated findByTaskIdAndInfoType(String taskId,String infoType);
}
