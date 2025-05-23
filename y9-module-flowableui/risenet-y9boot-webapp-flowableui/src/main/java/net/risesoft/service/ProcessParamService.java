package net.risesoft.service;

import net.risesoft.model.itemadmin.StartProcessResultModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 */
public interface ProcessParamService {

    /**
     * 保存流程自定义变量
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @param number 文件编号
     * @param level 级别
     * @param customItem 自定义事项
     * @return Y9Result<String>
     */
    Y9Result<String> saveOrUpdate(String itemId, String processSerialNumber, String processInstanceId,
        String documentTitle, String number, String level, Boolean customItem);

    /**
     * 保存流程自定义变量
     *
     * @param itemId 事项id
     * @param processSerialNumber 流程编号
     * @param processInstanceId 流程实例id
     * @param documentTitle 标题
     * @param number 文件编号
     * @param level 级别
     * @param customItem 自定义事项
     * @param theTaskKey 开始任务节点
     * @return Y9Result<String>
     */
    Y9Result<StartProcessResultModel> saveOrUpdate(String itemId, String processSerialNumber, String processInstanceId,
                                                   String documentTitle, String number, String level, Boolean customItem, String theTaskKey);

}
