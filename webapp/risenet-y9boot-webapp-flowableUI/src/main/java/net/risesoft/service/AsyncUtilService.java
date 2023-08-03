package net.risesoft.service;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import net.risesoft.api.itemadmin.ChaoSongInfoApi;
import net.risesoft.api.itemadmin.OfficeFollowApi;
import net.risesoft.api.todo.TodoTaskApi;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2023/01/03
 */
@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

    @Autowired
    private TodoTaskApi todoTaskApi;

    @Autowired
    private ChaoSongInfoApi chaoSongInfoManager;

    @Autowired
    private OfficeFollowApi officeFollowManager;

    /**
     * 更新统一待办，抄送件标题
     *
     * @param tenantId
     * @param processInstanceId
     * @param documentTitle
     * @return
     */
    @Async
    public Future<Boolean> updateTitle(final String tenantId, final String processInstanceId,
        final String documentTitle) {
        try {
            chaoSongInfoManager.updateTitle(tenantId, processInstanceId, documentTitle);
            todoTaskApi.updateTitle(tenantId, processInstanceId, documentTitle);
            officeFollowManager.updateTitle(tenantId, processInstanceId, documentTitle);
            return new AsyncResult<>(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new AsyncResult<>(false);
    }

}
