package net.risesoft.service.impl;

import java.util.List;

import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.service.CustomIdentityService;
import net.risesoft.service.FlowableTenantInfoHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@EnableAsync
@Transactional(readOnly = true)
@Service(value = "customIdentityService")
public class CustomIdentityServiceImpl implements CustomIdentityService {

    @Autowired
    private TaskService taskService;

    @Autowired
    private RuntimeService runtimeService;

    @Override
    @Transactional(readOnly = false)
    public void addParticipantUser(String tenantId, String userId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        runtimeService.addParticipantUser(processInstanceId, userId);
    }

    /**
     * 任务create的时候，进行此操作，如果不采用异步，并休眠0.2秒，删除的时候，流程参与人的数据估计还没有产生
     */
    @Async
    @Override
    @Transactional(readOnly = false)
    public void deleteParticipantUser(String tenantId, String userId, String processInstanceId) {
        FlowableTenantInfoHolder.setTenantId(tenantId);
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        runtimeService.deleteParticipantUser(processInstanceId, userId);
    }

    @Override
    public List<IdentityLink> getIdentityLinksForTask(String taskId) {
        return taskService.getIdentityLinksForTask(taskId);
    }
}
