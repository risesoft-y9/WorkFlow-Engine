package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ActRuDetailModel;
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
     * 根据流程实例id标记流程为办结
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/endByProcessInstanceId")
    Y9Result<Object> endByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据执行实例id标记流程为办结
     *
     * @param tenantId 租户id
     * @param executionId 执行实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/endByExecutionId")
    Y9Result<Object> endByExecutionId(@RequestParam("tenantId") String tenantId,
                                            @RequestParam("executionId") String executionId);

    /**
     * 根据流程编号标记流程为办结
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/endByProcessSerialNumber")
    Y9Result<Object> endByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程实例和状态查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessInstanceIdAndStatus")
    Y9Result<List<ActRuDetailModel>> findByProcessInstanceIdAndStatus(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId, @RequestParam("status") int status);

    /**
     * 根据流程编号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List < ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumber")
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程编号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param assignee 办理人id
     * @return {@code Y9Result<ActRuDetailModel>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumberAndAssignee")
    Y9Result<ActRuDetailModel> findByProcessSerialNumberAndAssignee(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("assignee") String assignee);

    /**
     * 根据流程编号查找正在办理的人员信息
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param status 0为待办，1位在办
     * @return {@code Y9Result<List<ActRuDetailModel>>} 通用请求返回对象 - data 是流转详细信息
     * @since 9.6.6
     */
    @GetMapping("/findByProcessSerialNumberAndStatus")
    Y9Result<List<ActRuDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("status") int status);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryByProcessInstanceId")
    Y9Result<Object> recoveryByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程序列号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/recoveryByProcessSerialNumber")
    Y9Result<Object> recoveryByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
                                                 @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程实例id删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeByProcessInstanceId")
    Y9Result<Object> removeByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 根据流程编号删除整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeByProcessSerialNumber")
    Y9Result<Object> removeByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 根据流程编号删除整个流程的办件详情(逻辑删除)
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteByProcessSerialNumber")
    Y9Result<Object> deleteByProcessSerialNumber(@RequestParam("tenantId") String tenantId,
                                                 @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 删除某个参与人的办件详情
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param assignee 办理人id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/removeByProcessSerialNumberAndAssignee")
    Y9Result<Object> removeByProcessSerialNumberAndAssignee(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("assignee") String assignee);

    /**
     * 保存或者更新
     *
     * @param tenantId 租户id
     * @param actRuDetailModel 详情对象
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveOrUpdate", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestBody ActRuDetailModel actRuDetailModel);

    /**
     * 恢复整个流程的办件详情
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/syncByProcessInstanceId")
    Y9Result<Object> syncByProcessInstanceId(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

}
