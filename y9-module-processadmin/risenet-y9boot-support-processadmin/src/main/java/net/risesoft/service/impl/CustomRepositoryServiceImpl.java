package net.risesoft.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipInputStream;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.enums.DialectEnum;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomRepositoryService;
import net.risesoft.util.Y9SqlPaginationUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/30
 */
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service(value = "customRepositoryService")
public class CustomRepositoryServiceImpl implements CustomRepositoryService {

    private final RepositoryService repositoryService;

    private final RuntimeService runtimeService;

    private final ProcessEngineConfiguration processEngineConfiguration;

    @Override
    public Y9Result<Object> delete(String deploymentId) {
        repositoryService.deleteDeployment(deploymentId, true);
        return Y9Result.success();
    }

    @Override
    public Y9Result<Object> deploy(MultipartFile file) {
        try {
            String fileName = file.getOriginalFilename();
            InputStream fileInputStream = file.getInputStream();
            String extension = FilenameUtils.getExtension(fileName), zipString = "zip";
            if (zipString.equals(extension)) {
                ZipInputStream zip = new ZipInputStream(fileInputStream);
                repositoryService.createDeployment().addZipInputStream(zip).deploy();
            } else {
                repositoryService.createDeployment().addInputStream(fileName, fileInputStream).deploy();
            }
            return Y9Result.success();
        } catch (Exception e) {
            LOGGER.error("流程部署失败。", e);
            return Y9Result.failure("流程部署失败");
        }
    }

    @Override
    public ProcessDefinition getLatestProcessDefinitionByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey)
            .latestVersion().singleResult();
    }

    @Override
    public List<ProcessDefinition> getLatestProcessDefinitionList() {
        return repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc()
            .list();
    }

    @Override
    public ProcessDefinition getPreviousProcessDefinitionById(String processDefinitionId) {
        ProcessDefinition pd =
            repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        int version = pd.getVersion();
        String processDefinitionKey = pd.getKey();
        if (version > 1) {
            pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey)
                .processDefinitionVersion(--version).singleResult();
        }
        return pd;
    }

    @Override
    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionListByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey)
            .orderByProcessDefinitionVersion().desc().list();
    }

    @Override
    public InputStream getProcessInstance(String resourceType, String processInstanceId, String processDefinitionId) {
        InputStream resourceStream = null;
        ProcessDefinition processDefinition = null;
        if (StringUtils.isNotBlank(processInstanceId) || StringUtils.isNotBlank(processDefinitionId)) {
            if (StringUtils.isNotBlank(processInstanceId)) {
                ProcessInstance processInstance =
                    runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            } else if (StringUtils.isNotBlank(processDefinitionId)) {
                processDefinition = repositoryService.createProcessDefinitionQuery()
                    .processDefinitionId(processDefinitionId).singleResult();
            }
        }

        String image = "image", xml = "xml";
        if (processDefinition != null) {
            String resourceName = "";
            if (image.equals(resourceType)) {
                resourceName = processDefinition.getDiagramResourceName();
            } else if (xml.equals(resourceType)) {
                resourceName = processDefinition.getResourceName();
            }
            resourceStream = repositoryService.getResourceAsStream(processDefinition.getDeploymentId(), resourceName);
        }
        return resourceStream;
    }

    @Override
    public Y9Result<List<ProcessDefinitionModel>> list(String resourceId) {
        List<ProcessDefinitionModel> items = new ArrayList<>();
        Y9LoginUserHolder.setTenantId(Y9LoginUserHolder.getTenantId());
        String sql = "select RES.* from ACT_RE_PROCDEF RES WHERE 1=1";
        if (DialectEnum.MSSQL.getValue().equals(processEngineConfiguration.getDatabaseType())) {
            sql = "select top 100 percent RES.* from ACT_RE_PROCDEF RES WHERE 1=1";
        }
        sql +=
            " and RES.VERSION_ = (select max(VERSION_) from ACT_RE_PROCDEF where KEY_ = RES.KEY_ ) order by RES.KEY_ asc";
        try {
            sql = Y9SqlPaginationUtil.generatePagedSql(processEngineConfiguration.getDataSource(), sql, 0, 1000);
            List<ProcessDefinition> processDefinitionList =
                repositoryService.createNativeProcessDefinitionQuery().sql(sql).list();
            ProcessDefinitionModel processDefinitionModel;
            for (ProcessDefinition processDefinition : processDefinitionList) {
                processDefinitionModel = new ProcessDefinitionModel();
                String deploymentId = processDefinition.getDeploymentId();
                Deployment deployment =
                    repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
                processDefinitionModel.setId(processDefinition.getId());
                processDefinitionModel.setDeploymentId(processDefinition.getDeploymentId());
                processDefinitionModel.setName(processDefinition.getName());
                processDefinitionModel.setKey(processDefinition.getKey());
                processDefinitionModel.setVersion(processDefinition.getVersion());
                processDefinitionModel.setResourceName(processDefinition.getResourceName());
                processDefinitionModel.setDiagramResourceName(processDefinition.getDiagramResourceName());
                processDefinitionModel.setSuspended(processDefinition.isSuspended());
                processDefinitionModel
                    .setDeploymentTime(DateFormatUtils.format(deployment.getDeploymentTime(), "yyyy-MM-dd HH:mm:ss"));
                processDefinitionModel.setSortTime(deployment.getDeploymentTime().getTime());
                items.add(processDefinitionModel);
            }
            items.sort((o1, o2) -> {
                long startTime1 = o1.getSortTime();
                long startTime2 = o2.getSortTime();
                return Long.compare(startTime2, startTime1);
            });
            return Y9Result.success(items);
        } catch (Exception e) {
            return Y9Result.failure("操作异常");
        }
    }

    @Override
    public Y9Result<Object> switchSuspendOrActive(String state, String processDefinitionId) {
        if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
            repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
        } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
            repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
        }
        return Y9Result.success();
    }
}
