package net.risesoft.service;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.todo.TodoTaskApi;

@Slf4j
@RequiredArgsConstructor
@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

    private final TodoTaskApi todoTaskApi;

    private final ChaoSong4PositionApi chaoSong4PositionApi;

    private final OfficeFollow4PositionApi officeFollow4PositionApi;

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
            chaoSong4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollow4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
        } catch (Exception e) {
            LOGGER.error("更新统一待办，抄送件标题", e);
        }
    }

}
