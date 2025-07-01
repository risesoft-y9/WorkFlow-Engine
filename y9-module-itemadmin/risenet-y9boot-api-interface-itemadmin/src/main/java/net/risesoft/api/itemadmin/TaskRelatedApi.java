package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 */
public interface TaskRelatedApi {

    /**
     * 保存或者更新
     *
     * @param tenantId 租户id
     * @param taskRelatedModel 详情对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody TaskRelatedModel taskRelatedModel);

    /**
     * 根据任务id查找任务相关信息
     *
     * @param tenantId 租户id
     * @param taskId 任务id
     * @return {@code Y9Result<List<TaskRelatedModel>>} 通用请求返回对象 - data 是任务相关信息
     * @since 9.6.6
     */
    @GetMapping("/findByTaskId")
    Y9Result<List<TaskRelatedModel>> findByTaskId(@RequestParam("tenantId") String tenantId,
        @RequestParam("taskId") String taskId);

    /**
     * 根据任务id查找任务相关信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return {@code Y9Result<List<TaskRelatedModel>>} 通用请求返回对象 - data 是任务相关信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<TaskRelatedModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);
}
