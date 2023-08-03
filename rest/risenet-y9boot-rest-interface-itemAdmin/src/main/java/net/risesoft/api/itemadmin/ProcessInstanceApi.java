package net.risesoft.api.itemadmin;

import java.util.Date;
import java.util.Map;

import net.risesoft.model.itemadmin.ProcessInstanceDetailsModel;

/**
 * 协作状态接口
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
     * @return
     */
    boolean deleteProcessInstance(String tenantId, String processInstanceId);

    /**
     * 获取协作状态列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param title 标题或文号
     * @param page 页码
     * @param rows 条数
     * @return
     */
    Map<String, Object> processInstanceList(String tenantId, String userId, String title, int page, int rows);

    /**
     * 保存协作状态详情
     *
     * @param tenantId 租户id
     * @param model 状态详情
     * @return
     */
    boolean saveProcessInstanceDetails(String tenantId, ProcessInstanceDetailsModel model);

    /**
     * 更新协作状态详情
     *
     * @param tenantId 租户id
     * @param assigneeId 受让人id
     * @param processInstanceId 流程实例id
     * @param taskId 任务id
     * @param itembox 状态
     * @param endTime 结束时间
     * @return
     */
    boolean updateProcessInstanceDetails(String tenantId, String assigneeId, String processInstanceId, String taskId,
        String itembox, Date endTime);
}
