package y9.client.rest.itemadmin;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.api.itemadmin.TaskVariableApi;
import net.risesoft.model.itemadmin.TaskVariableModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@FeignClient(contextId = "TaskVariableApiClient", name = "itemAdmin", url = "${y9.common.itemAdminBaseUrl}", path = "/services/rest/taskVariable")
public interface TaskVariableApiClient extends TaskVariableApi {

    /**
     * 根据任务id,变量名获取变量值
     *
     * @param tenantId
     * @param taskId
     * @param keyName
     * @return
     */
    @Override
    @GetMapping("/findByTaskIdAndKeyName")
    TaskVariableModel findByTaskIdAndKeyName(@RequestParam("tenantId") String tenantId, @RequestParam("taskId") String taskId, @RequestParam("keyName") String keyName);
}
