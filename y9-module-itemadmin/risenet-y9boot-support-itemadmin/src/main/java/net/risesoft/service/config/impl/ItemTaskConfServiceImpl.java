package net.risesoft.service.config.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.ItemTaskConfRepository;
import net.risesoft.service.config.ItemTaskConfService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemTaskConfServiceImpl implements ItemTaskConfService {

    private final ItemTaskConfRepository taskConfRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        List<ItemTaskConf> confList = taskConfRepository.findByItemIdAndProcessDefinitionId(itemId, lastVersionPid);
        for (ItemTaskConf conf : confList) {
            ItemTaskConf newConf = new ItemTaskConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newConf.setItemId(newItemId);
            newConf.setProcessDefinitionId(conf.getProcessDefinitionId());
            newConf.setSignOpinion(conf.getSignOpinion());
            newConf.setSponsor(conf.getSponsor());
            newConf.setTaskDefKey(conf.getTaskDefKey());
            newConf.setTenantId(Y9LoginUserHolder.getTenantId());
            newConf.setSignTask(conf.getSignTask());
            taskConfRepository.save(newConf);
        }
    }

    @Override
    @Transactional
    public void copyTaskConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String processDefinitionKey = processDefinitionId.split(":")[0];
        // 获取最新和前一版本的流程定义
        ProcessDefinitionModel latestProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        if (latestProcessDefinition.getVersion() <= 1) {
            return; // 版本为1时无需复制配置
        }
        String previousProcessDefinitionId = getPreviousProcessDefinitionId(tenantId, processDefinitionId,
            latestProcessDefinitionId, latestProcessDefinition);
        // 获取前一版本的任务配置列表
        List<ItemTaskConf> previousTaskConfigs =
            taskConfRepository.findByItemIdAndProcessDefinitionId(itemId, previousProcessDefinitionId);
        // 获取最新流程定义的节点并复制任务配置
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyTaskConfigForNode(itemId, latestProcessDefinitionId, currentTaskDefKey, previousTaskConfigs, tenantId);
        }
    }

    /**
     * 获取前一版本流程定义ID
     */
    private String getPreviousProcessDefinitionId(String tenantId, String processDefinitionId,
        String latestProcessDefinitionId, ProcessDefinitionModel latestProcessDefinition) {
        String previousProcessDefinitionId = processDefinitionId;
        if (processDefinitionId.equals(latestProcessDefinitionId) && latestProcessDefinition.getVersion() > 1) {
            ProcessDefinitionModel previousProcessDefinition =
                repositoryApi.getPreviousProcessDefinitionById(tenantId, latestProcessDefinitionId).getData();
            previousProcessDefinitionId = previousProcessDefinition.getId();
        }
        return previousProcessDefinitionId;
    }

    /**
     * 为指定节点复制任务配置
     */
    private void copyTaskConfigForNode(String itemId, String latestProcessDefinitionId, String currentTaskDefKey,
        List<ItemTaskConf> previousTaskConfigs, String tenantId) {
        ItemTaskConf existingTaskConfig = this.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId,
            latestProcessDefinitionId, currentTaskDefKey);
        for (ItemTaskConf previousTaskConfig : previousTaskConfigs) {
            String previousTaskDefKey = previousTaskConfig.getTaskDefKey();
            if (previousTaskDefKey.equals(currentTaskDefKey)) {
                if (null == existingTaskConfig) {
                    createNewTaskConfig(itemId, latestProcessDefinitionId, currentTaskDefKey, previousTaskConfig,
                        tenantId);
                } else {
                    updateExistingTaskConfig(existingTaskConfig, itemId, latestProcessDefinitionId, currentTaskDefKey,
                        previousTaskConfig, tenantId);
                }
                break; // 找到匹配项后退出循环
            }
        }
    }

    /**
     * 创建新的任务配置
     */
    private void createNewTaskConfig(String itemId, String processDefinitionId, String taskDefKey,
        ItemTaskConf sourceTaskConfig, String tenantId) {
        ItemTaskConf newTaskConfig = new ItemTaskConf();
        newTaskConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newTaskConfig.setItemId(itemId);
        newTaskConfig.setProcessDefinitionId(processDefinitionId);
        newTaskConfig.setSignOpinion(sourceTaskConfig.getSignOpinion());
        newTaskConfig.setSponsor(sourceTaskConfig.getSponsor());
        newTaskConfig.setTaskDefKey(taskDefKey);
        newTaskConfig.setTenantId(tenantId);
        newTaskConfig.setSignTask(sourceTaskConfig.getSignTask());
        taskConfRepository.save(newTaskConfig);
    }

    /**
     * 更新现有的任务配置
     */
    private void updateExistingTaskConfig(ItemTaskConf existingTaskConfig, String itemId, String processDefinitionId,
        String taskDefKey, ItemTaskConf sourceTaskConfig, String tenantId) {
        existingTaskConfig.setItemId(itemId);
        existingTaskConfig.setProcessDefinitionId(processDefinitionId);
        existingTaskConfig.setSignOpinion(sourceTaskConfig.getSignOpinion());
        existingTaskConfig.setSponsor(sourceTaskConfig.getSponsor());
        existingTaskConfig.setTaskDefKey(taskDefKey);
        existingTaskConfig.setTenantId(tenantId);
        existingTaskConfig.setSignTask(sourceTaskConfig.getSignTask());
        taskConfRepository.save(existingTaskConfig);
    }

    @Override
    @Transactional
    public void delete(String id) {
        taskConfRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            taskConfRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除签收配置失败", e);
        }
    }

    @Override
    public ItemTaskConf findById(String id) {
        return taskConfRepository.findById(id).orElse(null);
    }

    @Override
    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        ItemTaskConf conf;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf =
                taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                taskDefKey);
            if (null == conf) {
                conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId,
                    processDefinitionId);
            }
        }
        return conf;
    }

    @Override
    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(String itemId, String processDefinitionId,
        String taskDefKey) {
        ItemTaskConf conf;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf =
                taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                taskDefKey);
        }
        return conf;
    }

    @Override
    public boolean getSponserStatus(String itemId, String processDefinitionId, String taskDefKey) {
        boolean status = false;
        ItemTaskConf conf;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf =
                taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
                taskDefKey);
        }
        if (null != conf) {
            status = conf.getSponsor();
        }
        return status;
    }

    @Override
    @Transactional
    public void save(ItemTaskConf t) {
        String id = t.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemTaskConf existingItemTaskConf = taskConfRepository.findById(id).orElse(null);
            assert existingItemTaskConf != null;
            existingItemTaskConf.setSponsor(t.getSponsor());
            existingItemTaskConf.setSignOpinion(t.getSignOpinion());
            existingItemTaskConf.setSignTask(t.getSignTask());
            taskConfRepository.save(existingItemTaskConf);
            return;
        }
        ItemTaskConf itemTaskConf = new ItemTaskConf();
        itemTaskConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        itemTaskConf.setTenantId(Y9LoginUserHolder.getTenantId());
        itemTaskConf.setProcessDefinitionId(t.getProcessDefinitionId());
        itemTaskConf.setSponsor(t.getSponsor());
        itemTaskConf.setSignOpinion(t.getSignOpinion());
        itemTaskConf.setSignTask(t.getSignTask());
        itemTaskConf.setItemId(t.getItemId());
        if (StringUtils.isNotBlank(t.getTaskDefKey())) {
            itemTaskConf.setTaskDefKey(t.getTaskDefKey());
        }
        taskConfRepository.save(itemTaskConf);
    }

    @Override
    public ItemTaskConf save(String id, String processDefinitionId, String taskDefKey) {
        ItemTaskConf entity = new ItemTaskConf();
        if (StringUtils.isBlank(id)) {
            entity.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        } else {
            entity.setId(id);
        }
        entity.setProcessDefinitionId(processDefinitionId);
        entity.setTaskDefKey(taskDefKey);
        return entity;
    }
}
