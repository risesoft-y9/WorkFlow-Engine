package net.risesoft.listener;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.ActRuDetailApi;
import net.risesoft.api.itemadmin.core.ItemApi;
import net.risesoft.api.itemadmin.core.ProcessParamApi;
import net.risesoft.consts.processadmin.SysVariables;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.core.ItemModel;
import net.risesoft.service.InterfaceUtilService;
import net.risesoft.service.Process4CompleteUtilService;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
public class TaskListener4ProcessCompleted extends AbstractFlowableEventListener {

    @Override
    public boolean isFailOnException() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType type = (FlowableEngineEventType)event.getType();
        switch (type) {
            case PROCESS_COMPLETED:
                FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl)event;
                ExecutionEntityImpl executionEntity = (ExecutionEntityImpl)entityEvent.getEntity();
                String tenantId = (String)executionEntity.getVariable(SysVariables.TENANT_ID);
                // 1、接口调用
                InterfaceUtilService interfaceUtilService = Y9Context.getBean(InterfaceUtilService.class);
                try {
                    interfaceUtilService.interfaceCallByProcess(executionEntity, executionEntity.getVariables(), "办结");
                } catch (Exception e) {
                    throw new RuntimeException("调用接口失败 TaskListener4ProcessCompleted_PROCESS_COMPLETED");
                }
                // 2、标记流转详情为办结状态
                Y9Context.getBean(ActRuDetailApi.class).endByProcessInstanceId(tenantId,
                    executionEntity.getProcessInstanceId());
                // 3、保存流程数据到ES，截转年度数据
                Process4CompleteUtilService process4CompleteUtilService =
                    Y9Context.getBean(Process4CompleteUtilService.class);
                process4CompleteUtilService.saveToEs(tenantId, "", Y9LoginUserHolder.getOrgUnitId(),
                    executionEntity.getProcessInstanceId(), Y9LoginUserHolder.getOrgUnit().getName());
                break;
            case PROCESS_STARTED:
                FlowableEntityEventImpl entityEventStart = (FlowableEntityEventImpl)event;
                ExecutionEntityImpl executionEntityStart = (ExecutionEntityImpl)entityEventStart.getEntity();
                // 1、接口调用
                InterfaceUtilService interfaceUtilService1 = Y9Context.getBean(InterfaceUtilService.class);
                try {
                    interfaceUtilService1.interfaceCallByProcess(executionEntityStart,
                        executionEntityStart.getVariables(), "启动");
                } catch (Exception e) {
                    throw new RuntimeException("调用接口失败 TaskListener4ProcessCompleted_PROCESS_STARTED");
                }
                // 2、子流程启动,初始化callActivity的流程参数信息
                ItemApi itemApi = Y9Context.getBean(ItemApi.class);
                String tenantIdTemp = (String)executionEntityStart.getVariable(SysVariables.TENANT_ID);
                ItemModel itemModel = itemApi
                    .findByProcessDefinitionKey(tenantIdTemp, executionEntityStart.getProcessDefinitionKey()).getData();
                if (StringUtils.isNotEmpty(itemModel.getType()) && "sub".equals(itemModel.getType())) {
                    String processSerialNumber =
                        (String)executionEntityStart.getVariable(SysVariables.PROCESS_SERIAL_NUMBER);
                    String subProcessSerialNumber = Y9IdGenerator.genId();
                    executionEntityStart.setVariable(SysVariables.PROCESS_SERIAL_NUMBER, subProcessSerialNumber);

                    ProcessParamApi processParamApi = Y9Context.getBean(ProcessParamApi.class);
                    processParamApi.initCallActivity(tenantIdTemp, processSerialNumber, subProcessSerialNumber,
                        executionEntityStart.getProcessInstanceId(), itemModel.getId(), itemModel.getName());
                }
                break;
            case HISTORIC_PROCESS_INSTANCE_ENDED:
                break;
            default:
                LOGGER.info("Event received:{} {}", event.getType(), System.nanoTime());
        }
    }

}
