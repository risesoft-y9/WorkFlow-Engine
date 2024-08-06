package net.risesoft.api;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 协作状态接口
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processInstance", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProcessInstanceApiImpl implements ProcessInstanceApi {

    private final ProcessInstanceDetailsService processInstanceDetailsService;

    /**
     * 删除协作状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> deleteProcessInstance(@RequestParam String tenantId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(processInstanceDetailsService.deleteProcessInstance(processInstanceId));
    }

    /**
     * 获取协作状态列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param title 标题或文号
     * @param page 页码
     * @param rows 条数
     * @return{@code Y9Page<ProcessCooperationModel>} 通用请求返回对象 -rows 协作状态信息
     * @since 9.6.6
     */
    @Override
    public Y9Page<ProcessCooperationModel> processInstanceList(@RequestParam String tenantId,
        @RequestParam String userId, String title, @RequestParam int page, @RequestParam int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.pageByUserIdAndTitle(userId, title, page, rows);
    }

    /**
     * 保存协作状态详情
     *
     * @param tenantId 租户id
     * @param model 状态详情
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> saveProcessInstanceDetails(@RequestParam String tenantId,
        @RequestBody ProcessInstanceDetailsModel model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(processInstanceDetailsService.save(model));
    }

    /**
     * 更新协作状态详情
     *
     * @param tenantId 租户id
     * @param assigneeId 受让人id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办）,doing（在办）,done（办结）
     * @param endTime 结束时间
     * @return{@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> updateProcessInstanceDetails(@RequestParam String tenantId,
        @RequestParam String assigneeId, @RequestParam String processInstanceId, @RequestParam String taskId,
        @RequestParam String itembox, @RequestParam Date endTime) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(assigneeId);
        return Y9Result.success(
            processInstanceDetailsService.updateProcessInstanceDetails(processInstanceId, taskId, itembox, endTime));
    }
}
