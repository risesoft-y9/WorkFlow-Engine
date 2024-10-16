package net.risesoft.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ChaoSongApi;
import net.risesoft.api.itemadmin.ItemTodoTaskApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

    private final ItemTodoTaskApi todoTaskApi;

    private final ChaoSongApi chaoSongApi;

    private final OfficeFollowApi officeFollowApi;

    /**
     * 更新统一待办，抄送件标题
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     */
    @Async
    public void updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSongApi.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

}
