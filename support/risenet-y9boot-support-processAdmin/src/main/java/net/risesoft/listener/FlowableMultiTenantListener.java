package net.risesoft.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.model.platform.TenantSystem;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.MultiTenantProcessEngineConfiguration;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.pubsub.constant.Y9CommonEventConst;
import net.risesoft.y9.pubsub.event.Y9EventCommon;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
public class FlowableMultiTenantListener implements ApplicationListener<Y9EventCommon> {

    private MultiTenantProcessEngineConfiguration multiTenantProcessEngineConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    private void createDeployment(String processDefinitionKey, String tenantId) {
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            FlowableTenantInfoHolder.setTenantId(tenantId);
            try {
                ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
                if (null == processDefinition) {
                    String xmlPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "processXml"
                        + File.separator + "ziyouliucheng.bpmn";
                    File file = new File(xmlPath);
                    InputStream fileInputStream = new FileInputStream(file);
                    repositoryService.createDeployment().addInputStream("ziyouliucheng.bpmn", fileInputStream).deploy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onApplicationEvent(Y9EventCommon event) {
        String eventType = event.getEventType();
        String target = event.getTarget();
        TenantSystem tenantSystem = (TenantSystem)event.getEventObject();
        if (Y9CommonEventConst.TENANT_SYSTEM_REGISTERED.equals(eventType) && Y9Context.getSystemName().equals(target)) {
            LOGGER.info("租户:{} 租用processAdmin 初始化数据.........", tenantSystem.getTenantId());
            if (this.multiTenantProcessEngineConfiguration == null) {
                try {
                    this.multiTenantProcessEngineConfiguration = Y9Context.getBean("processEngineConfiguration");
                } catch (Exception e) {
                    LOGGER.error("multiTenantProcessEngineConfiguration==null，同步flowable租户数据源信息失败，退出。", e);
                    return;
                }
            }
            this.multiTenantProcessEngineConfiguration.buildProcessEngine();
            createDeployment("ziyouliucheng", tenantSystem.getTenantId());
            LOGGER.info(Y9Context.getSystemName() + ", 同步租户数据源信息, 成功！");
        }
    }

}
