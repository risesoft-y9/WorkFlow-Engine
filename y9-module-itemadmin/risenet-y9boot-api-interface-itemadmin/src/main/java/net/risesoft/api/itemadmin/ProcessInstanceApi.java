package net.risesoft.api.itemadmin;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;

/**
 * 协作状态
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
public interface ProcessInstanceApi {

    /**
     * 删除协作状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     */
    @PostMapping("/deleteProcessInstance")
    Y9Result<Boolean> deleteProcessInstance(@RequestParam("tenantId") String tenantId,
        @RequestParam("processInstanceId") String processInstanceId);

    /**
     * 获取协作状态列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param title 标题或文号
     * @param page 页码
     * @param rows 条数
     * @return{@code Y9Page<ProcessCooperationModel>} 通用请求返回对象 -rows 协作状态信息
     */
    @GetMapping("/processInstanceList")
    Y9Page<ProcessCooperationModel> processInstanceList(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("title") String title, @RequestParam("page") int page,
        @RequestParam("rows") int rows);

    /**
     * 保存协作状态详情
     *
     * @param tenantId 租户id
     * @param model 状态详情
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     */
    @PostMapping(value = "/saveProcessInstanceDetails", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Boolean> saveProcessInstanceDetails(@RequestParam("tenantId") String tenantId,
        @RequestBody ProcessInstanceDetailsModel model);

    /**
     * 更新协作状态详情
     *
     * @param tenantId 租户id
     * @param assigneeId 受让人id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param itembox 状态
     * @param itembox 办件状态，todo（待办）,doing（在办）,done（办结）
     * @param endTime 结束时间
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     */
    @PostMapping("/updateProcessInstanceDetails")
    Y9Result<Boolean> updateProcessInstanceDetails(@RequestParam("tenantId") String tenantId,
        @RequestParam("assigneeId") String assigneeId, @RequestParam("processInstanceId") String processInstanceId,
        @RequestParam("taskId") String taskId, @RequestParam("itembox") String itembox,
        @RequestParam("endTime") Date endTime);
}
