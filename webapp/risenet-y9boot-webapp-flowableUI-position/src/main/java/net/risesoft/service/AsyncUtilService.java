package net.risesoft.service;

import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import net.risesoft.api.itemadmin.position.ChaoSong4PositionApi;
import net.risesoft.api.itemadmin.position.OfficeFollow4PositionApi;
import net.risesoft.api.todo.TodoTaskApi;

@EnableAsync
@Service(value = "asyncUtilService")
public class AsyncUtilService {

	@Autowired
	private TodoTaskApi todoTaskManager;

	@Autowired
	private ChaoSong4PositionApi chaoSongInfoManager;

	@Autowired
	private OfficeFollow4PositionApi officeFollowManager;

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
			chaoSongInfoManager.updateTitle(tenantId, processInstanceId, documentTitle);
			todoTaskManager.updateTitle(tenantId, processInstanceId, documentTitle);
			officeFollowManager.updateTitle(tenantId, processInstanceId, documentTitle);
			return new AsyncResult<>(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new AsyncResult<>(false);
	}

}
