package net.risesoft.service.config.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.TaskTimeConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.TaskTimeConfRepository;
import net.risesoft.service.config.TaskTimeConfService;
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
public class TaskTimeConfServiceImpl implements TaskTimeConfService {

    private final TaskTimeConfRepository taskTimeConfRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        List<TaskTimeConf> confList = taskTimeConfRepository.findByItemIdAndProcessDefinitionId(itemId, lastVersionPid);
        for (TaskTimeConf conf : confList) {
            TaskTimeConf newConf = new TaskTimeConf();
            newConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            newConf.setItemId(newItemId);
            newConf.setProcessDefinitionId(conf.getProcessDefinitionId());
            newConf.setTaskDefKey(conf.getTaskDefKey());
            newConf.setTimeoutInterrupt(conf.getTimeoutInterrupt());
            newConf.setLeastTime(conf.getLeastTime());
            taskTimeConfRepository.save(newConf);
        }
    }

    @Override
    @Transactional
    public void copyTaskConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String processDefinitionKey = processDefinitionId.split(":")[0];
        // 获取最新流程定义
        ProcessDefinitionModel latestProcessDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String latestProcessDefinitionId = latestProcessDefinition.getId();
        // 版本为1时无需复制配置
        if (latestProcessDefinition.getVersion() <= 1) {
            return;
        }
        // 获取前一版本流程定义ID
        String previousProcessDefinitionId = getPreviousProcessDefinitionId(tenantId, processDefinitionId,
            latestProcessDefinitionId, latestProcessDefinition);
        // 获取前一版本的时间配置列表
        List<TaskTimeConf> previousTimeConfigs =
            taskTimeConfRepository.findByItemIdAndProcessDefinitionId(itemId, previousProcessDefinitionId);
        // 获取最新流程定义的节点并复制时间配置
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestProcessDefinitionId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyTimeConfigForNode(itemId, latestProcessDefinitionId, currentTaskDefKey, previousTimeConfigs);
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
     * 为指定节点复制时间配置
     */
    private void copyTimeConfigForNode(String itemId, String latestProcessDefinitionId, String currentTaskDefKey,
        List<TaskTimeConf> previousTimeConfigs) {
        TaskTimeConf existingTimeConfig =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, latestProcessDefinitionId, currentTaskDefKey);
        for (TaskTimeConf previousTimeConfig : previousTimeConfigs) {
            String previousTaskDefKey = previousTimeConfig.getTaskDefKey();
            if (previousTaskDefKey.equals(currentTaskDefKey)) {
                if (null == existingTimeConfig) {
                    createNewTimeConfig(itemId, latestProcessDefinitionId, previousTimeConfig);
                } else {
                    updateExistingTimeConfig(existingTimeConfig, itemId, latestProcessDefinitionId, currentTaskDefKey,
                        previousTimeConfig);
                }
                break; // 找到匹配项后退出循环
            }
        }
    }

    /**
     * 创建新的时间配置
     */
    private void createNewTimeConfig(String itemId, String processDefinitionId, TaskTimeConf sourceTimeConfig) {
        TaskTimeConf newTimeConfig = new TaskTimeConf();
        newTimeConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newTimeConfig.setItemId(itemId);
        newTimeConfig.setProcessDefinitionId(processDefinitionId);
        newTimeConfig.setTimeoutInterrupt(sourceTimeConfig.getTimeoutInterrupt());
        newTimeConfig.setLeastTime(sourceTimeConfig.getLeastTime());
        taskTimeConfRepository.save(newTimeConfig);
    }

    /**
     * 更新现有的时间配置
     */
    private void updateExistingTimeConfig(TaskTimeConf existingTimeConfig, String itemId, String processDefinitionId,
        String taskDefKey, TaskTimeConf sourceTimeConfig) {
        existingTimeConfig.setItemId(itemId);
        existingTimeConfig.setProcessDefinitionId(processDefinitionId);
        existingTimeConfig.setTaskDefKey(taskDefKey);
        existingTimeConfig.setTimeoutInterrupt(sourceTimeConfig.getTimeoutInterrupt());
        existingTimeConfig.setLeastTime(sourceTimeConfig.getLeastTime());

        taskTimeConfRepository.save(existingTimeConfig);
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            taskTimeConfRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除配置失败", e);
        }
    }

    @Override
    public TaskTimeConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        return taskTimeConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
            taskDefKey);
    }

    @Override
    @Transactional
    public void save(TaskTimeConf t) {
        String id = t.getId();
        if (StringUtils.isNotBlank(id)) {
            TaskTimeConf existTaskTimeConf = taskTimeConfRepository.findById(id).orElse(null);
            assert existTaskTimeConf != null;
            existTaskTimeConf.setLeastTime(t.getLeastTime());
            existTaskTimeConf.setTimeoutInterrupt(t.getTimeoutInterrupt());
            taskTimeConfRepository.save(existTaskTimeConf);
            return;
        }
        TaskTimeConf taskTimeConf = new TaskTimeConf();
        taskTimeConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        taskTimeConf.setProcessDefinitionId(t.getProcessDefinitionId());
        taskTimeConf.setLeastTime(t.getLeastTime());
        taskTimeConf.setTimeoutInterrupt(t.getTimeoutInterrupt());
        taskTimeConf.setItemId(t.getItemId());
        if (StringUtils.isNotBlank(t.getTaskDefKey())) {
            taskTimeConf.setTaskDefKey(t.getTaskDefKey());
        }
        taskTimeConfRepository.save(taskTimeConf);
    }

}
