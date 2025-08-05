package net.risesoft.service.impl;

import java.util.concurrent.Future;

import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.model.itemadmin.InterfaceModel;
import net.risesoft.service.AsyncUtilService;
import net.risesoft.service.InterfaceMethodService;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9LoginUserHolder;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncUtilServiceImpl implements AsyncUtilService {

    private final InterfaceMethodService interfaceMethodService;

    @Async
    @Override
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
