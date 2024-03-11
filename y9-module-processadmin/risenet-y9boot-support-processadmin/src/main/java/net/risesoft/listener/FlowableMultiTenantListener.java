package net.risesoft.listener;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.init.TenantDataInitializer;
import net.risesoft.model.platform.Tenant;
import net.risesoft.service.FlowableTenantInfoHolder;
import net.risesoft.service.MultiTenantProcessEngineConfiguration;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
public class FlowableMultiTenantListener implements TenantDataInitializer {

    private MultiTenantProcessEngineConfiguration multiTenantProcessEngineConfiguration;

    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private TenantApi tenantApi;

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
    public void init(String tenantId) {
        Tenant tenant = tenantApi.getById(tenantId).getData();
        LOGGER.info("【{}】租用processAdmin 初始化表结构开始.........", tenant.getName());
        if (this.multiTenantProcessEngineConfiguration == null) {
            try {
                this.multiTenantProcessEngineConfiguration = Y9Context.getBean("processEngineConfiguration");
            } catch (Exception e) {
                LOGGER.error("multiTenantProcessEngineConfiguration==null，同步flowable租户数据源信息失败，退出。", e);
                return;
            }
        }
        this.multiTenantProcessEngineConfiguration.buildProcessEngine();
        LOGGER.info("【{}】租用processAdmin 初始化表结构结束.........", tenant.getName());
        LOGGER.info("【{}】租用processAdmin 初始化【自由办件】开始.........", tenant.getName());
        createDeployment("ziyouliucheng", tenantId);
        LOGGER.info("【{}】租用processAdmin 初始化【自由办件】结束.........", tenant.getName());
    }

}
