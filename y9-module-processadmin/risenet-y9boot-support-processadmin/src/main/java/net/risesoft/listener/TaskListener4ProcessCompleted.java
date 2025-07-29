package net.risesoft.listener;

import org.apache.commons.lang3.StringUtils;
import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.api.itemadmin.ProcessParamApi;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.service.InterfaceUtilService;
import net.risesoft.util.SysVariables;
import net.risesoft.y9.Y9Context;

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
                String tenantId = (String)executionEntity.getVariable(SysVariables.TENANTID);
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
                String tenantIdTemp = (String)executionEntityStart.getVariable(SysVariables.TENANTID);
                ItemModel itemModel = itemApi
                    .findByProcessDefinitionKey(tenantIdTemp, executionEntityStart.getProcessDefinitionKey()).getData();
                if (StringUtils.isNotEmpty(itemModel.getType()) && "sub".equals(itemModel.getType())) {
                    String processSerialNumber =
                        (String)executionEntityStart.getVariable(SysVariables.PROCESSSERIALNUMBER);
                    String subProcessSerialNumber = Y9IdGenerator.genId();
                    executionEntityStart.setVariable(SysVariables.PROCESSSERIALNUMBER, subProcessSerialNumber);

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
