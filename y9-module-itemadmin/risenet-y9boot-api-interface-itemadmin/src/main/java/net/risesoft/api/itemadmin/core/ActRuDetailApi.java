package net.risesoft.api.itemadmin.core;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.enums.ActRuDetailStatusEnum;
import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Result;

/**
 * 流转信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ActRuDetailApi {

    /**
     * 签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping(value = "/claim", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> claim(@RequestParam String taskId, @RequestParam String assignee);

    /**
     * 根据执行实例id标记流程为删除
     *
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByExecutionId")
    Y9Result<Object> deleteByExecutionId(@RequestParam String executionId);

    /**
     * 根据流程编号删除整个流程的办件详情(逻辑删除)
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessSerialNumber")
    Y9Result<Object> deleteByProcessSerialNumber(@RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程实例id标记流程为办结
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/endByProcessInstanceId")
    Y9Result<Object> endByProcessInstanceId(@RequestParam String processInstanceId);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessInstanceIdAndStatus")
    Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(@RequestParam String processInstanceId,
        @RequestParam ActRuDetailStatusEnum status);

    /**
     * 根据流程编号查找正在办理的人员信息
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 根据流程编号查找正在办理的人员信息
     *
     * @param processSerialNumber 流程编号
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List<ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumberAndStatus")
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam String processSerialNumber,
        @RequestParam ActRuDetailStatusEnum status);

    /**
     * 恢复会签流程的办件详情
     *
     * @param executionId 流程序列号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryByExecutionId")
    Y9Result<Object> recoveryByExecutionId(@RequestParam String executionId);

    /**
     * 恢复整个流程的办件详情
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryByProcessInstanceId")
    Y9Result<Object> recoveryByProcessInstanceId(@RequestParam String processInstanceId);

    /**
     * 恢复整个流程的办件详情
     *
     * @param processSerialNumber 流程序列号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryByProcessSerialNumber")
    Y9Result<Object> recoveryByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 撤销签收
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping(value = "/refuseClaim", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> refuseClaim(@RequestParam String taskId, @RequestParam String assignee);

    /**
     * 根据流程实例id删除整个流程的办件详情
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeByProcessInstanceId")
    Y9Result<Object> removeByProcessInstanceId(@RequestParam String processInstanceId);

    /**
     * 根据流程编号删除整个流程的办件详情
     *
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeByProcessSerialNumber")
    Y9Result<Object> removeByProcessSerialNumber(@RequestParam String processSerialNumber);

    /**
     * 保存或者更新
     *
     * @param actRuDetailModel 详情对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestBody ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param id 流程详情id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/setRead")
    Y9Result<Object> setRead(@RequestParam String id);

    /**
     * 恢复整个流程的办件详情
     *
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/syncByProcessInstanceId")
    Y9Result<Object> syncByProcessInstanceId(@RequestParam String processInstanceId);

    /**
     * 待办改为在办
     *
     * @param taskId 任务id
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/todo2doing", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> todo2doing(@RequestParam String taskId, @RequestParam String assignee);

    /**
     * 撤销签收
     *
     * @param taskId 任务id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @PostMapping(value = "/unClaim", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> unClaim(@RequestParam String taskId);

}
