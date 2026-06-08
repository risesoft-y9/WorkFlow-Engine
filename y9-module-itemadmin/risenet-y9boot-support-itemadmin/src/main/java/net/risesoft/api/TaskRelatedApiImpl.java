package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.TaskRelatedApi;
import net.risesoft.entity.TaskRelated;
import net.risesoft.model.itemadmin.TaskRelatedModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.TaskRelatedService;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 任务变量接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/taskRelated", produces = MediaType.APPLICATION_JSON_VALUE)
public class TaskRelatedApiImpl implements TaskRelatedApi {

    private final TaskRelatedService taskRelatedService;

    /**
     * 根据任务id获取任务相关信息
     *
     * @param processSerialNumber 流程序列号
     * @return {@code Y9Result<List<TaskRelatedModel>>} 通用请求返回对象 - data 是任务相关信息
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<TaskRelatedModel>> findByProcessSerialNumber(@RequestParam String processSerialNumber) {
        List<TaskRelated> list = taskRelatedService.findByProcessSerialNumber(processSerialNumber);
        List<TaskRelatedModel> modelList = new ArrayList<>();
        TaskRelatedModel taskRelatedModel;
        for (TaskRelated taskRelated : list) {
            taskRelatedModel = new TaskRelatedModel();
            Y9BeanUtil.copyProperties(taskRelated, taskRelatedModel);
            modelList.add(taskRelatedModel);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据任务id获取任务相关信息
     *
     * @param taskId 任务id
     * @return {@code Y9Result<List<TaskRelatedModel>>} 通用请求返回对象 - data 是任务相关信息
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<TaskRelatedModel>> findByTaskId(@RequestParam String taskId) {
        List<TaskRelated> list = taskRelatedService.findByTaskId(taskId);
        List<TaskRelatedModel> modelList = new ArrayList<>();
        TaskRelatedModel taskRelatedModel;
        for (TaskRelated taskRelated : list) {
            taskRelatedModel = new TaskRelatedModel();
            Y9BeanUtil.copyProperties(taskRelated, taskRelatedModel);
            modelList.add(taskRelatedModel);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存任务变量
     *
     * @param taskRelatedModel 任务变量信息
     * @return {@code Y9Result<Object>} 通用请求返回对象 - data 是任务变量信息
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> saveOrUpdate(TaskRelatedModel taskRelatedModel) {
        TaskRelated taskRelated = new TaskRelated();
        Y9BeanUtil.copyProperties(taskRelatedModel, taskRelated);
        taskRelatedService.saveOrUpdate(taskRelated);
        return Y9Result.success();
    }
}
