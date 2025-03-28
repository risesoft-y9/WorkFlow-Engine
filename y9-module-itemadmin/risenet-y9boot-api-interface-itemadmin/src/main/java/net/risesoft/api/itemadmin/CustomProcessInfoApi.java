package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.CustomProcessInfoModel;
import net.risesoft.pojo.Y9Result;

/**
 * 定制流程接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface CustomProcessInfoApi {

    /**
     * 获取当前运行任务的下一个节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<CustomProcessInfoModel>} 通用请求返回对象 - data 是自定义流程信息
     * @since 9.6.6
     */
    @GetMapping("/getCurrentTaskNextNode")
    Y9Result<CustomProcessInfoModel> getCurrentTaskNextNode(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存流程定制信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param taskList 任务列表
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("taskList") List<Map<String, Object>> taskList);

    /**
     * 更新当前运行节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/updateCurrentTask")
    Y9Result<Object> updateCurrentTask(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber);

}
