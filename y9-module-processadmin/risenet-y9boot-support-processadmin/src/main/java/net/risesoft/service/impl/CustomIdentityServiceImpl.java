package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.flowable.engine.TaskService;
import org.flowable.identitylink.api.IdentityLink;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.service.CustomIdentityService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customIdentityService")
public class CustomIdentityServiceImpl implements CustomIdentityService {

    private final TaskService taskService;

    @Override
    public List<IdentityLink> listIdentityLinksForTaskByTaskId(String taskId) {
        try {
            return taskService.getIdentityLinksForTask(taskId);
        } catch (Exception e) {
            LOGGER.error("getIdentityLinksForTask发生异常", e);
        }
        return new ArrayList<>();
    }
}
