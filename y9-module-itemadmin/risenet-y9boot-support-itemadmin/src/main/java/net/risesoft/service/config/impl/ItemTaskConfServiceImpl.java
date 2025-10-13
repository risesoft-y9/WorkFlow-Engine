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
        try {
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
        } catch (Exception e) {
            LOGGER.error("复制签收配置失败", e);
        }
    }

    @Override
    @Transactional
    public void copyTaskConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String proDefKey = processDefinitionId.split(":")[0];
        ProcessDefinitionModel latest = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestId = latest.getId();
        String previousId = processDefinitionId;
        if (processDefinitionId.equals(latestId)) {
            if (latest.getVersion() > 1) {
                ProcessDefinitionModel previous =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestId).getData();
                previousId = previous.getId();
            }
        }
        if (latest.getVersion() > 1) {
            List<ItemTaskConf> confList = taskConfRepository.findByItemIdAndProcessDefinitionId(itemId, previousId);
            List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestId).getData();
            for (TargetModel targetModel : nodes) {
                String currentTaskDefKey = targetModel.getTaskDefKey();
                ItemTaskConf currentConf =
                    this.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId, latestId, currentTaskDefKey);
                if (null == currentConf) {
                    for (ItemTaskConf conf : confList) {
                        String previousTaskDefKey = conf.getTaskDefKey();
                        if (previousTaskDefKey.equals(currentTaskDefKey)) {
                            currentConf = new ItemTaskConf();
                            currentConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            currentConf.setItemId(itemId);
                            currentConf.setProcessDefinitionId(latestId);
                            currentConf.setSignOpinion(conf.getSignOpinion());
                            currentConf.setSponsor(conf.getSponsor());
                            currentConf.setTaskDefKey(currentTaskDefKey);
                            currentConf.setTenantId(tenantId);
                            currentConf.setSignTask(conf.getSignTask());
                            taskConfRepository.save(currentConf);
                        }
                    }
                } else {
                    for (ItemTaskConf conf : confList) {
                        String previousTaskDefKey = conf.getTaskDefKey();
                        if (previousTaskDefKey.equals(currentTaskDefKey)) {
                            currentConf.setItemId(itemId);
                            currentConf.setProcessDefinitionId(latestId);
                            currentConf.setSignOpinion(conf.getSignOpinion());
                            currentConf.setSponsor(conf.getSponsor());
                            currentConf.setTaskDefKey(currentTaskDefKey);
                            currentConf.setTenantId(tenantId);
                            currentConf.setSignTask(conf.getSignTask());
                            taskConfRepository.save(currentConf);
                        }
                    }
                }
            }
        }
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
            ItemTaskConf oldItemTaskConf = taskConfRepository.findById(id).orElse(null);
            assert oldItemTaskConf != null;
            oldItemTaskConf.setSponsor(t.getSponsor());
            oldItemTaskConf.setSignOpinion(t.getSignOpinion());
            oldItemTaskConf.setSignTask(t.getSignTask());
            taskConfRepository.save(oldItemTaskConf);
            return;
        }
        ItemTaskConf newitc = new ItemTaskConf();
        newitc.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newitc.setTenantId(Y9LoginUserHolder.getTenantId());
        newitc.setProcessDefinitionId(t.getProcessDefinitionId());
        newitc.setSponsor(t.getSponsor());
        newitc.setSignOpinion(t.getSignOpinion());
        newitc.setSignTask(t.getSignTask());
        newitc.setItemId(t.getItemId());
        if (StringUtils.isNotBlank(t.getTaskDefKey())) {
            newitc.setTaskDefKey(t.getTaskDefKey());
        }
        taskConfRepository.save(newitc);
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
