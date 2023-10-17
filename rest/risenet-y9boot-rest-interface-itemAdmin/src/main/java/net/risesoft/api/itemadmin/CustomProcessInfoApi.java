package net.risesoft.api.itemadmin;

import java.util.List;
import java.util.Map;

import net.risesoft.model.itemadmin.CustomProcessInfoModel;

/**
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
     * @return CustomProcessInfoModel
     */
    CustomProcessInfoModel getCurrentTaskNextNode(String tenantId, String processSerialNumber);

    /**
     * 保存流程定制信息
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param taskList 任务ids
     * @return boolean
     */
    boolean saveOrUpdate(String tenantId, String itemId, String processSerialNumber, List<Map<String, Object>> taskList);

    /**
     * 更新当前运行节点
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return boolean
     */
    boolean updateCurrentTask(String tenantId, String processSerialNumber);

}
