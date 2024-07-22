package net.risesoft.listener;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

/**
 * 提取
 *
 * @author qinman
 * @date 2024/03/19
 */
@Slf4j
public class ExtractListener implements ExecutionListener {

    /**
     *
     */
    private static final long serialVersionUID = -7803204966451312248L;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void notify(DelegateExecution execution) {
        try {
            LOGGER.info("==========processSerialNumber" + execution.getVariable("processSerialNumber"));
            LOGGER.info("==========processInstanceId" + execution.getProcessInstanceId());
        } catch (Exception e) {
            throw new RuntimeException("调用提取接口失败");
        }
    }
}
