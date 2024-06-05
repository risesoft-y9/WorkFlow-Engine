package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.enums.DialectEnum;
import net.risesoft.enums.ItemProcessStateTypeEnum;
import net.risesoft.service.CustomRepositoryService;
import net.risesoft.util.Y9SqlPaginationUtil;
import net.risesoft.y9.Y9LoginUserHolder;
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

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipInputStream;

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
    public Map<String, Object> delete(String deploymentId) {
        Map<String, Object> retMap = new HashMap<>(16);
        retMap.put("success", false);
        retMap.put("msg", "流程级联删除失败。");
        try {
            repositoryService.deleteDeployment(deploymentId, true);
            retMap.put("success", true);
            retMap.put("msg", "流程级联删除成功。");
        } catch (Exception e) {
            LOGGER.error("流程级联删除失败。", e);
        }
        return retMap;
    }

    @Override
    public Map<String, Object> deploy(MultipartFile file) {
        HashMap<String, Object> retMap = new HashMap<>(16);
        retMap.put("success", false);
        retMap.put("msg", "流程部署失败。");
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
            retMap.put("success", true);
            retMap.put("msg", "流程部署成功。");
        } catch (Exception e) {
            LOGGER.error("流程部署失败。", e);
        }
        return retMap;
    }

    @Override
    public ProcessDefinition getLatestProcessDefinitionByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).latestVersion().singleResult();
    }

    @Override
    public List<ProcessDefinition> getLatestProcessDefinitionList() {
        return repositoryService.createProcessDefinitionQuery().latestVersion().orderByProcessDefinitionKey().asc().list();
    }

    @Override
    public ProcessDefinition getPreviousProcessDefinitionById(String processDefinitionId) {
        ProcessDefinition pd = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
        int version = pd.getVersion();
        String processDefinitionKey = pd.getKey();
        if (version > 1) {
            pd = repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).processDefinitionVersion(--version).singleResult();
        }
        return pd;
    }

    @Override
    public ProcessDefinition getProcessDefinitionById(String processDefinitionId) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
    }

    @Override
    public List<ProcessDefinition> getProcessDefinitionListByKey(String processDefinitionKey) {
        return repositoryService.createProcessDefinitionQuery().processDefinitionKey(processDefinitionKey).orderByProcessDefinitionVersion().desc().list();
    }

    @Override
    public InputStream getProcessInstance(String resourceType, String processInstanceId, String processDefinitionId) {
        InputStream resourceStream = null;
        ProcessDefinition processDefinition = null;
        if (StringUtils.isNotBlank(processInstanceId) || StringUtils.isNotBlank(processDefinitionId)) {
            if (StringUtils.isNotBlank(processInstanceId)) {
                ProcessInstance processInstance = runtimeService.createProcessInstanceQuery().processInstanceId(processInstanceId).singleResult();
                processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processInstance.getProcessDefinitionId()).singleResult();
            } else if (StringUtils.isNotBlank(processDefinitionId)) {
                processDefinition = repositoryService.createProcessDefinitionQuery().processDefinitionId(processDefinitionId).singleResult();
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
    public Map<String, Object> list(String resourceId) {
        Map<String, Object> retMap = new HashMap<>(16);
        List<Map<String, Object>> items = new ArrayList<>();
        try {
            Y9LoginUserHolder.setTenantId(Y9LoginUserHolder.getTenantId());
            // boolean tenantManager = userInfo.isGlobalManager();
            String sql = "select RES.* from ACT_RE_PROCDEF RES WHERE 1=1";
            if (DialectEnum.MSSQL.getValue().equals(processEngineConfiguration.getDatabaseType())) {
                sql = "select top 100 percent RES.* from ACT_RE_PROCDEF RES WHERE 1=1";
            }
            sql += " and RES.VERSION_ = (select max(VERSION_) from ACT_RE_PROCDEF where KEY_ = RES.KEY_ ) order by RES.KEY_ asc";
            sql = Y9SqlPaginationUtil.generatePagedSql(processEngineConfiguration.getDataSource(), sql, 0, 1000);
            List<ProcessDefinition> processDefinitionList = repositoryService.createNativeProcessDefinitionQuery().sql(sql).list();
            Map<String, Object> mapTemp;
            for (ProcessDefinition processDefinition : processDefinitionList) {
                mapTemp = new HashMap<>(16);
                String deploymentId = processDefinition.getDeploymentId();
                Deployment deployment = repositoryService.createDeploymentQuery().deploymentId(deploymentId).singleResult();
                mapTemp.put("id", processDefinition.getId());
                mapTemp.put("deploymentId", processDefinition.getDeploymentId());
                mapTemp.put("name", processDefinition.getName());
                mapTemp.put("key", processDefinition.getKey());
                mapTemp.put("version", processDefinition.getVersion());
                mapTemp.put("resourceName", processDefinition.getResourceName());
                mapTemp.put("diagramResourceName", processDefinition.getDiagramResourceName());
                mapTemp.put("suspended", processDefinition.isSuspended());
                mapTemp.put("deploymentTime", DateFormatUtils.format(deployment.getDeploymentTime(), "yyyy-MM-dd HH:mm:ss"));
                mapTemp.put("sortTime", deployment.getDeploymentTime().getTime());
                items.add(mapTemp);
            }
            items.sort((o1, o2) -> {
                long startTime1 = (long) o1.get("sortTime");
                long startTime2 = (long) o2.get("sortTime");
                return Long.compare(startTime2, startTime1);
            });
            retMap.put("success", true);
        } catch (Exception e) {
            retMap.put("success", false);
            LOGGER.error("获取流程列表失败。", e);
        }
        retMap.put("rows", items);
        return retMap;
    }

    @Override
    public Map<String, Object> switchSuspendOrActive(String state, String processDefinitionId) {
        Map<String, Object> retMap = new HashMap<>(16);
        retMap.put("success", false);
        retMap.put("msg", "操作异常。");
        try {
            if (ItemProcessStateTypeEnum.ACTIVE.getValue().equals(state)) {
                repositoryService.activateProcessDefinitionById(processDefinitionId, true, null);
                retMap.put("success", true);
                retMap.put("msg", "激活流程实例成功。");
            } else if (ItemProcessStateTypeEnum.SUSPEND.getValue().equals(state)) {
                repositoryService.suspendProcessDefinitionById(processDefinitionId, true, null);
                retMap.put("success", true);
                retMap.put("msg", "挂起流程实例成功。");
            }
        } catch (Exception e) {
            LOGGER.error("操作异常。", e);
        }
        return retMap;
    }
}
