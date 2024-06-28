package net.risesoft.api;

import java.util.Date;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.ProcessCooperationModel;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.model.platform.Position;
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

    private final PositionApi positionApi;

    /**
     * 删除协作状态
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     */
    @Override
    public Y9Result<Boolean> deleteProcessInstance(String tenantId, String processInstanceId) {
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
     */
    @Override
    public Y9Page<ProcessCooperationModel> processInstanceList(String tenantId, String userId, String title, int page,
        int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.processInstanceList(userId, title, page, rows);
    }

    /**
     * 保存协作状态详情
     *
     * @param tenantId 租户id
     * @param model 状态详情
     */
    @Override
    public Y9Result<Boolean> saveProcessInstanceDetails(String tenantId,
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
     */
    @Override
    public Y9Result<Boolean> updateProcessInstanceDetails(String tenantId, String assigneeId, String processInstanceId,
        String taskId, String itembox, Date endTime) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, assigneeId).getData();
        Y9LoginUserHolder.setPosition(position);
        return Y9Result.success(
            processInstanceDetailsService.updateProcessInstanceDetails(processInstanceId, taskId, itembox, endTime));
    }
}
