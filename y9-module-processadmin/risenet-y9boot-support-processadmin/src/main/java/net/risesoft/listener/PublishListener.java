package net.risesoft.listener;

import lombok.extern.slf4j.Slf4j;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.transaction.annotation.Transactional;

/**
 * 提取
 *
 * @author qinman
 * @date 2024/03/19
 */
@Slf4j
public class PublishListener implements ExecutionListener {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(DelegateExecution execution) {
        try {
            System.out.printf("==========processSerialNumber" + execution.getVariable("processSerialNumber"));
            System.out.printf("==========processInstanceId" + execution.getProcessInstanceId());
        } catch (Exception e) {
            throw new RuntimeException("调用发布接口失败");
        }
    }
}
