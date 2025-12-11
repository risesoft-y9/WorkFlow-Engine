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
import net.risesoft.model.platform.tenant.Tenant;
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

    /**
     * 启动时，部署流程定义，仅在只有一个租户（默认生成的租户）时需要部署， 因为启动的时候还不能监听到租户租用系统的消息
     */
    private void createDeployment() {
        try {
            List<Tenant> tenantList = tenantApi.listAllTenants().getData();
            if (1 == tenantList.size()) {
                String tenantId = tenantList.get(0).getId();
                String tenantName = tenantList.get(0).getName();
                Y9LoginUserHolder.setTenantId(tenantId);
                FlowableTenantInfoHolder.setTenantId(tenantId);
                try {
                    ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
                        .processDefinitionKey("ziyouliucheng")
                        .latestVersion()
                        .singleResult();
                    if (null == processDefinition) {
                        String xmlPath = Y9Context.getWebRootRealPath() + "static" + File.separator + "processXml"
                            + File.separator + "ziyouliucheng.bpmn";
                        File file = new File(xmlPath);
                        InputStream fileInputStream = new FileInputStream(file);
                        repositoryService.createDeployment()
                            .addInputStream("ziyouliucheng.bpmn", fileInputStream)
                            .deploy();
                    }
                } catch (Exception e) {
                    LOGGER.error("租户：{}，创建流程定义失败", tenantName, e);
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取租户列表失败", e);
        }
    }

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        LOGGER.info("processAdmin ApplicationReady...");
        createDeployment();
    }
}