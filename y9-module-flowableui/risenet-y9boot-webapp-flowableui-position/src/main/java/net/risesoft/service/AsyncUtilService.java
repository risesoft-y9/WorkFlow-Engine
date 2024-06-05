package net.risesoft.service;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.todo.TodoTaskApi;

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
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     * @return
     */
    @Async
    public Future<Boolean> updateTitle(final String tenantId, final String processInstanceId, final String documentTitle) {
        try {
            chaoSong4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollow4PositionApi.updateTitle(tenantId, processInstanceId, documentTitle);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

}
