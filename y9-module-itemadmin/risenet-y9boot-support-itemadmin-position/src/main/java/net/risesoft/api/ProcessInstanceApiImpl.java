package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ProcessInstanceApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;
import net.risesoft.model.platform.Position;
import net.risesoft.service.ProcessInstanceDetailsService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * 协作状态接口
 *
 * @author zhangchongjie
 * @date 2023/02/06
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/processInstance")
public class ProcessInstanceApiImpl implements ProcessInstanceApi {

    private final ProcessInstanceDetailsService processInstanceDetailsService;

    private final PositionApi positionApi;

    /**
     * 删除协作状态
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return
     */
    @Override
    @PostMapping(value = "/deleteProcessInstance", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean deleteProcessInstance(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.deleteProcessInstance(processInstanceId);
    }

    /**
     * 获取协作状态列表
     * @param tenantId 租户id
     * @param userId 人员id
     * @param title 标题或文号
     * @param page 页码
     * @param rows 条数
     * @return
     */
    @Override
    @GetMapping(value = "/processInstanceList", produces = MediaType.APPLICATION_JSON_VALUE)
    public Map<String, Object> processInstanceList(String tenantId, String userId, String title, int page, int rows) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.processInstanceList(userId, title, page, rows);
    }

    /**
     * 保存协作状态详情
     * @param tenantId 租户id
     * @param model 状态详情
     * @return
     */
    @Override
    @PostMapping(value = "/saveProcessInstanceDetails", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveProcessInstanceDetails(String tenantId, @RequestBody ProcessInstanceDetailsModel model) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return processInstanceDetailsService.save(model);
    }

    /**
     * 更新协作状态详情
     * @param tenantId 租户id
     * @param assigneeId 受让人id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param itembox 办件状态，todo（待办）,doing（在办）,done（办结）
     * @param endTime 结束时间
     * @return
     */
    @Override
    @PostMapping(value = "/updateProcessInstanceDetails", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean updateProcessInstanceDetails(String tenantId, String assigneeId, String processInstanceId,
        String taskId, String itembox, Date endTime) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, assigneeId).getData();
        Y9LoginUserHolder.setPosition(position);
        return processInstanceDetailsService.updateProcessInstanceDetails(processInstanceId, taskId, itembox, endTime);
    }
}
