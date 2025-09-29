package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.CustomProcessInfo;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface CustomProcessInfoService {

    /**
     * 获取当前运行任务的下一个节点
     *
     * @param processSerialNumber 流程序列号
     * @return CustomProcessInfo
     */
    CustomProcessInfo getCurrentTaskNextNode(String processSerialNumber);

    /**
     * 保存流程定制信息
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程序列号
     * @param taskList 节点列表
     * @return boolean
     */
    boolean saveOrUpdate(String itemId, String processSerialNumber, List<Map<String, Object>> taskList);

    /**
     * 更新当前运行节点
     *
     * @param processSerialNumber 流程程序列号
     */
    void updateCurrentTask(String processSerialNumber);

}
