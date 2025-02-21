package net.risesoft.service.impl;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.TaskRelated;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.TaskRelatedRepository;
import net.risesoft.service.TaskRelatedService;

/**
 * @author : qinman
 * @date : 2024-12-03
 **/
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class TaskRelatedServiceImpl implements TaskRelatedService {

    private final TaskRelatedRepository taskRelatedRepository;

    @Override
    @Transactional
    public void saveOrUpdate(TaskRelated taskRelated) {
        String id = taskRelated.getId();
        if (StringUtils.isNotBlank(id)) {
            TaskRelated old = taskRelatedRepository.findById(id).orElse(null);
            if (null != old) {
                old.setModifyTime(new Date());
                old.setMsgContent(taskRelated.getMsgContent());
                taskRelatedRepository.save(old);
                return;
            }
        }
        TaskRelated newTaskRelated = new TaskRelated();
        newTaskRelated.setId(Y9IdGenerator.genId());
        newTaskRelated.setInfoType(taskRelated.getInfoType());
        newTaskRelated.setMsgContent(taskRelated.getMsgContent());
        newTaskRelated.setTaskId(taskRelated.getTaskId());
        newTaskRelated.setProcessSerialNumber(taskRelated.getProcessSerialNumber());
        newTaskRelated.setProcessInstanceId(taskRelated.getProcessInstanceId());
        newTaskRelated.setExecutionId(taskRelated.getExecutionId());
        newTaskRelated.setSub(taskRelated.isSub());
        newTaskRelated.setSenderId(taskRelated.getSenderId());
        newTaskRelated.setSenderName(taskRelated.getSenderName());
        newTaskRelated.setCreateTime(new Date());
        newTaskRelated.setModifyTime(new Date());
        taskRelatedRepository.save(newTaskRelated);
    }

    @Override
    public List<TaskRelated> findByTaskId(String taskId) {
        return taskRelatedRepository.findByTaskIdOrderByCreateTimeAsc(taskId);
    }

    @Override
    public List<TaskRelated> findByProcessSerialNumber(String processSerialNumber) {
        return taskRelatedRepository.findByProcessSerialNumberOrderByCreateTimeDesc(processSerialNumber);
    }

    @Override
    public TaskRelated findByTaskIdAndInfoType(String taskId, String infoType) {
        return taskRelatedRepository.findByTaskIdAndInfoType(taskId, infoType);
    }
}
