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
        try {
            List<TaskTimeConf> confList =
                taskTimeConfRepository.findByItemIdAndProcessDefinitionId(itemId, lastVersionPid);
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
        } catch (Exception e) {
            LOGGER.error("复制签收配置失败", e);
        }
    }

    @Override
    @Transactional
    public void copyTaskConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String proDefKey = processDefinitionId.split(":")[0];
        ProcessDefinitionModel latestpd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                previouspdId = previouspd.getId();
            }
        }
        if (latestpd.getVersion() > 1) {
            List<TaskTimeConf> confList =
                taskTimeConfRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);
            List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId, false).getData();
            for (TargetModel targetModel : nodes) {
                String currentTaskDefKey = targetModel.getTaskDefKey();
                TaskTimeConf currentConf =
                    this.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, latestpdId, currentTaskDefKey);
                if (null == currentConf) {
                    for (TaskTimeConf conf : confList) {
                        String previousTaskDefKey = conf.getTaskDefKey();
                        if (previousTaskDefKey.equals(currentTaskDefKey)) {
                            currentConf = new TaskTimeConf();
                            currentConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            currentConf.setItemId(itemId);
                            currentConf.setProcessDefinitionId(latestpdId);
                            currentConf.setTimeoutInterrupt(conf.getTimeoutInterrupt());
                            currentConf.setLeastTime(conf.getLeastTime());
                            taskTimeConfRepository.save(currentConf);
                        }
                    }
                } else {
                    for (TaskTimeConf conf : confList) {
                        String previousTaskDefKey = conf.getTaskDefKey();
                        if (previousTaskDefKey.equals(currentTaskDefKey)) {
                            currentConf.setItemId(itemId);
                            currentConf.setProcessDefinitionId(latestpdId);
                            currentConf.setTaskDefKey(currentTaskDefKey);
                            currentConf.setTimeoutInterrupt(conf.getTimeoutInterrupt());
                            currentConf.setLeastTime(conf.getLeastTime());
                            taskTimeConfRepository.save(currentConf);
                        }
                    }
                }
            }
        }
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
            TaskTimeConf olditc = taskTimeConfRepository.findById(id).orElse(null);
            assert olditc != null;
            olditc.setLeastTime(t.getLeastTime());
            olditc.setTimeoutInterrupt(t.getTimeoutInterrupt());
            taskTimeConfRepository.save(olditc);
            return;
        }
        TaskTimeConf newitc = new TaskTimeConf();
        newitc.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newitc.setProcessDefinitionId(t.getProcessDefinitionId());
        newitc.setLeastTime(t.getLeastTime());
        newitc.setTimeoutInterrupt(t.getTimeoutInterrupt());
        newitc.setItemId(t.getItemId());
        if (StringUtils.isNotBlank(t.getTaskDefKey())) {
            newitc.setTaskDefKey(t.getTaskDefKey());
        }
        taskTimeConfRepository.save(newitc);
    }

}
