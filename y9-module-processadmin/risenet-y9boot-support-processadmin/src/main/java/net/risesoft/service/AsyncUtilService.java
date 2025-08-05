package net.risesoft.service;

import java.util.concurrent.Future;

import net.risesoft.model.itemadmin.InterfaceModel;

public interface AsyncUtilService {

    /**
     * 异步调用接口
     *
     * @param tenantId 租户id
     * @param orgUnitId 组织id
     * @param processSerialNumber 流程编号
     * @param itemId 流程id
     * @param info 接口信息
     * @param processInstanceId 流程实例id
     * @param processDefinitionId 流程定义id
     * @param taskId 任务id
     * @param taskKey 任务key
     * @param loopCounter 循环次数
     * @return Future<Boolean>
     */
    Future<Boolean> asynInterface(final String tenantId, final String orgUnitId, final String processSerialNumber,
        final String itemId, final InterfaceModel info, final String processInstanceId,
        final String processDefinitionId, final String taskId, final String taskKey, final Integer loopCounter);
}
