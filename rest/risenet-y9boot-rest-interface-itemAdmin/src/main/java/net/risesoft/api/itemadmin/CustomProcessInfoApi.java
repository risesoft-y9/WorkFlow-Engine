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
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    CustomProcessInfoModel getCurrentTaskNextNode(String tenantId, String processSerialNumber);

    /**
     * 保存流程定制信息
     *
     * @param tenantId
     * @param itemId
     * @param processSerialNumber
     * @param taskList
     * @return
     */
    public boolean saveOrUpdate(String tenantId, String itemId, String processSerialNumber, List<Map<String, Object>> taskList);

    /**
     * 更新当前运行节点
     *
     * @param tenantId
     * @param processSerialNumber
     * @return
     */
    boolean updateCurrentTask(String tenantId, String processSerialNumber);

}
