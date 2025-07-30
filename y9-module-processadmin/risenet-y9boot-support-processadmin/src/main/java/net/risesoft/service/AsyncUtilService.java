package net.risesoft.service;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@EnableAsync
@Service(value = "asyncUtilService")
@RequiredArgsConstructor
public class AsyncUtilService {

    private final InterfaceMethodService interfaceMethodService;

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
     * @return
     */
    @Async
    public Future<Boolean> asynInterface(final String tenantId, final String orgUnitId,
        final String processSerialNumber, final String itemId, final InterfaceModel info,
        final String processInstanceId, final String processDefinitionId, final String taskId, final String taskKey,
        final Integer loopCounter) {
        Y9LoginUserHolder.setTenantId(tenantId);
        FlowableTenantInfoHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setOrgUnitId(orgUnitId);
        try {
            if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_GET)) {
                interfaceMethodService.getMethod(processSerialNumber, itemId, info, processInstanceId,
                    processDefinitionId, taskId, taskKey, loopCounter);

            } else if (info.getRequestType().equals(ItemInterfaceTypeEnum.METHOD_POST)) {
                interfaceMethodService.postMethod(processSerialNumber, itemId, info, processInstanceId,
                    processDefinitionId, taskId, taskKey, loopCounter);
            }
            return new AsyncResult<>(true);
        } catch (Exception e) {
            LOGGER.error("接口调用异常", e);
        }
        return new AsyncResult<>(false);
    }

}
