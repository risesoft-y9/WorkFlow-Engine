package net.risesoft;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

import org.flowable.engine.RepositoryService;
import org.flowable.engine.repository.ProcessDefinition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.enums.platform.TenantTypeEnum;
import net.risesoft.model.platform.Tenant;
import net.risesoft.service.FlowableTenantInfoHolder;
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
public class OnApplicationReady implements ApplicationListener<ApplicationReadyEvent> {

    @Autowired
    private TenantApi tenantApi;

    @Autowired
    private RepositoryService repositoryService;

    private void createDeployment(String processDefinitionKey) {
        try {
            List<Tenant> tlist = tenantApi.listByTenantType(TenantTypeEnum.TENANT).getData();
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
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
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
