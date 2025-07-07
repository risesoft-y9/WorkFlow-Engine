package net.risesoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.model.platform.Tenant;
import net.risesoft.y9.FlowableTenantInfoHolder;
import net.risesoft.y9.Y9Context;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author dingzhaojun
 * @author qinman
 * @author mengjuhua
 * @date 2022/2/10
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    private final TenantApi tenantApi;

    private final RepositoryService repositoryService;

    private void createDeployment(String processDefinitionKey) {
        try {
            List<Tenant> tlist = tenantApi.listAllTenants().getData();
            for (Tenant tenant : tlist) {
                Y9LoginUserHolder.setTenantId(tenant.getId());
                FlowableTenantInfoHolder.setTenantId(tenant.getId());
                try {
                    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
                    if (null == processDefinition) {
                        String xmlPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "processXml"
                            + File.separator + "ziyouliucheng.bpmn";
                        File file = new File(xmlPath);
                        InputStream fileInputStream = new FileInputStream(file);
                        repositoryService.createDeployment().addInputStream("ziyouliucheng.bpmn", fileInputStream)
                            .deploy();
                    }
                } catch (Exception e) {
                    LOGGER.error("租户：{}，创建流程定义失败", tenant.getName(), e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取租户列表失败", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("processAdmin ApplicationReady...");
        // createSystem("processAdmin");
        // createTenantSystem("processAdmin");
        // createDeployment("ziyouliucheng");
    }
}
