package net.risesoft.listener;

import org.flowable.common.engine.api.delegate.event.AbstractFlowableEventListener;
import org.flowable.common.engine.api.delegate.event.FlowableEngineEventType;
import org.flowable.common.engine.api.delegate.event.FlowableEvent;
import org.flowable.engine.delegate.event.impl.FlowableEntityEventImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ActRuDetailApi;
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
        return false;
    }

    @Override
    public void onEvent(FlowableEvent event) {
        FlowableEngineEventType type = (FlowableEngineEventType)event.getType();
        switch (type) {
            case PROCESS_COMPLETED:
                FlowableEntityEventImpl entityEvent = (FlowableEntityEventImpl)event;
                ExecutionEntityImpl executionEntity = (ExecutionEntityImpl)entityEvent.getEntity();
                String tenantId = (String)executionEntity.getVariable(SysVariables.TENANTID);
                Y9Context.getBean(ActRuDetailApi.class).endByProcessInstanceId(tenantId, executionEntity.getProcessInstanceId());
                break;
            case HISTORIC_PROCESS_INSTANCE_ENDED:
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                LOGGER.info("Event received:{} {}", event.getType(), System.nanoTime());
        }
    }

}
