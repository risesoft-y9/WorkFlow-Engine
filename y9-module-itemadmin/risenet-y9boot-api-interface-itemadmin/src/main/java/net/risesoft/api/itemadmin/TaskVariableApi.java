package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TaskVariableModel;
import net.risesoft.pojo.Y9Result;

/**
 * 任务变量管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface TaskVariableApi {

    /**
     * 根据任务id和keyName获取任务变量
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @param keyName keyName
     * @return {@code Y9Result<TaskVariableModel>} 通用请求返回对象 - data 是变量值
     * @since 9.6.6
     */
    @GetMapping(value = "/findByTaskIdAndKeyName")
    Y9Result<TaskVariableModel> findByTaskIdAndKeyName(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId, @RequestParam("keyName") String keyName);
}
