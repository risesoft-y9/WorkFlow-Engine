package net.risesoft.service.config.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.BackTaskConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.BackTaskConfRepository;
import net.risesoft.service.config.ItemBackTaskConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemBackTaskConfServiceImpl implements ItemBackTaskConfService {

    private final BackTaskConfRepository backTaskConfRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

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
        List<BackTaskConf> previousTaskConfigs =
            backTaskConfRepository.findByItemIdAndProcessDefinitionId(itemId, previousProcessDefinitionId);
        // 获取最新流程定义的节点并复制任务配置
        List<TargetModel> nodes = processDefinitionApi.getNodes(latestProcessDefinitionId).getData();
        for (TargetModel targetModel : nodes) {
            String currentTaskDefKey = targetModel.getTaskDefKey();
            copyTaskConfigForNode(itemId, latestProcessDefinitionId, currentTaskDefKey, previousTaskConfigs, tenantId);
        }
    }

    /**
     * 为指定节点复制任务配置
     */
    private void copyTaskConfigForNode(String itemId, String latestProcessDefinitionId, String currentTaskDefKey,
        List<BackTaskConf> previousTaskConfigs, String tenantId) {
        BackTaskConf existingTaskConfig =
            this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, latestProcessDefinitionId, currentTaskDefKey);
        for (BackTaskConf previousTaskConfig : previousTaskConfigs) {
            String previousTaskDefKey = previousTaskConfig.getTaskDefKey();
            if (previousTaskDefKey.equals(currentTaskDefKey)) {
                if (null == existingTaskConfig) {
                    createNewTaskConfig(itemId, latestProcessDefinitionId, currentTaskDefKey, previousTaskConfig,
                        tenantId);
                } else {
                    updateExistingTaskConfig(existingTaskConfig, itemId, latestProcessDefinitionId, currentTaskDefKey,
                        previousTaskConfig);
                }
                break; // 找到匹配项后退出循环
            }
        }
    }

    /**
     * 创建新的任务配置
     */
    private void createNewTaskConfig(String itemId, String processDefinitionId, String taskDefKey,
        BackTaskConf sourceTaskConfig, String tenantId) {
        BackTaskConf newTaskConfig = new BackTaskConf();
        newTaskConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newTaskConfig.setItemId(itemId);
        newTaskConfig.setProcessDefinitionId(processDefinitionId);
        newTaskConfig.setTaskDefKey(taskDefKey);
        newTaskConfig.setBackTaskDefKey(sourceTaskConfig.getBackTaskDefKey());
        backTaskConfRepository.save(newTaskConfig);
    }

    @Override
    public BackTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        BackTaskConf conf = backTaskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        return conf;
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

    @Override
    public List<BackTaskConf> listByTaskDefKey(String itemId, String processDefinitionId, String taskDefKey) {
        BackTaskConf conf = backTaskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        List<BackTaskConf> reslist = new ArrayList<>();
        if (conf != null && StringUtils.isNotBlank(conf.getBackTaskDefKey())) {
            String backTaskDefKey = conf.getBackTaskDefKey();
            String[] taskDefKeys = backTaskDefKey.split(",");
            List<TargetModel> list = processDefinitionApi.getNodes(processDefinitionId).getData();
            for (String key : taskDefKeys) {
                String finalKey = key;
                TargetModel target =
                    list.stream().filter(t -> t.getTaskDefKey().equals(finalKey)).findFirst().orElse(null);
                String targetTaskDefName = target != null ? target.getTaskDefName() : "节点不存在";
                BackTaskConf backTaskConf = new BackTaskConf();
                backTaskConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                backTaskConf.setItemId(itemId);
                backTaskConf.setProcessDefinitionId(processDefinitionId);
                backTaskConf.setTaskDefKey(key);
                backTaskConf.setTaskDefName(targetTaskDefName);
                reslist.add(backTaskConf);
            }
        }
        return reslist;
    }

    @Override
    @Transactional
    public void removeBind(String itemId, String processDefinitionId, String taskDefKey, String[] removeTaskKey) {
        BackTaskConf conf = backTaskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        if (conf != null && StringUtils.isNotBlank(conf.getBackTaskDefKey())) {
            String backTaskDefKey = conf.getBackTaskDefKey();
            List<String> taskDefKeys = Arrays.stream(backTaskDefKey.split(",")).collect(Collectors.toList());
            List<String> newTaskDefKeys = new ArrayList<>();
            List<String> removeTaskDefKeys = new ArrayList<>();
            removeTaskDefKeys = Arrays.stream(removeTaskKey).collect(Collectors.toList());
            for (String key : taskDefKeys) {
                if (!removeTaskDefKeys.contains(key)) {
                    newTaskDefKeys.add(key);
                }
            }
            conf.setBackTaskDefKey(Y9Util.join(newTaskDefKeys, ","));
            backTaskConfRepository.save(conf);
        }
    }

    @Override
    @Transactional
    public void saveBindTask(String itemId, String processDefinitionId, String taskDefKey, String[] bindTaskDefKey) {
        BackTaskConf conf = backTaskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        if (conf == null) {
            conf = new BackTaskConf();
            conf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            conf.setItemId(itemId);
            conf.setProcessDefinitionId(processDefinitionId);
            conf.setTaskDefKey(taskDefKey);
            conf.setBackTaskDefKey(Y9Util.join(bindTaskDefKey, ","));
            backTaskConfRepository.save(conf);
        } else {
            String backTaskDefKey = conf.getBackTaskDefKey();
            if (backTaskDefKey != null) {
                List<String> taskDefKeys = Arrays.stream(backTaskDefKey.split(",")).collect(Collectors.toList());
                for (String key : bindTaskDefKey) {
                    if (!taskDefKeys.contains(key)) {
                        taskDefKeys.add(key);
                    }
                }
                conf.setBackTaskDefKey(Y9Util.join(taskDefKeys, ","));
                backTaskConfRepository.save(conf);
            }
        }
    }

    /**
     * 更新现有的任务配置
     */
    private void updateExistingTaskConfig(BackTaskConf existingTaskConfig, String itemId, String processDefinitionId,
        String taskDefKey, BackTaskConf sourceTaskConfig) {
        existingTaskConfig.setItemId(itemId);
        existingTaskConfig.setProcessDefinitionId(processDefinitionId);
        existingTaskConfig.setTaskDefKey(taskDefKey);
        existingTaskConfig.setBackTaskDefKey(sourceTaskConfig.getBackTaskDefKey());
        backTaskConfRepository.save(existingTaskConfig);
    }
}
