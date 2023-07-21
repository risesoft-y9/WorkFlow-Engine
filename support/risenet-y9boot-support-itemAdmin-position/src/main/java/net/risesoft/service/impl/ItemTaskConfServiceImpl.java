package net.risesoft.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemTaskConf;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.repository.jpa.ItemTaskConfRepository;
import net.risesoft.service.ItemTaskConfService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "taskConfService")
public class ItemTaskConfServiceImpl implements ItemTaskConfService {

    @Autowired
    private ItemTaskConfRepository taskConfRepository;

    @Autowired
    private RepositoryApi repositoryManager;

    @Autowired
    private ProcessDefinitionApi processDefinitionManager;

    @Override
    @Transactional(readOnly = false)
    public void copyTaskConf(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String proDefKey = processDefinitionId.split(":")[0];
        ProcessDefinitionModel latestpd = repositoryManager.getLatestProcessDefinitionByKey(tenantId, proDefKey);
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd = repositoryManager.getPreviousProcessDefinitionById(tenantId, latestpdId);
                previouspdId = previouspd.getId();
            }
        }
        if (latestpd.getVersion() > 1) {
            List<ItemTaskConf> confList = taskConfRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);
            List<Map<String, Object>> nodes = processDefinitionManager.getNodes(tenantId, latestpdId, false);
            for (Map<String, Object> map : nodes) {
                String currentTaskDefKey = (String)map.get("taskDefKey");
                ItemTaskConf currentConf = this.findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(itemId, latestpdId, currentTaskDefKey);
                if (null == currentConf) {
                    for (ItemTaskConf conf : confList) {
                        String previousTaskDefKey = conf.getTaskDefKey();
                        if (previousTaskDefKey.equals(currentTaskDefKey)) {
                            currentConf = new ItemTaskConf();
                            currentConf.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            currentConf.setItemId(itemId);
                            currentConf.setProcessDefinitionId(latestpdId);
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
                            currentConf.setProcessDefinitionId(latestpdId);
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
    @Transactional(readOnly = false)
    public void delete(String id) {
        taskConfRepository.deleteById(id);
    }

    @Override
    public ItemTaskConf findById(String id) {
        return taskConfRepository.findById(id).orElse(null);
    }

    @Override
    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId, String taskDefKey) {
        ItemTaskConf conf = null;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
            if (null == conf) {
                conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
            }
        }
        return conf;
    }

    @Override
    public ItemTaskConf findByItemIdAndProcessDefinitionIdAndTaskDefKey4Own(String itemId, String processDefinitionId, String taskDefKey) {
        ItemTaskConf conf = null;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        }
        return conf;
    }

    @Override
    public boolean getSponserStatus(String itemId, String processDefinitionId, String taskDefKey) {
        boolean sponserStatus = false;
        ItemTaskConf conf = null;
        if (StringUtils.isEmpty(taskDefKey)) {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKeyIsNull(itemId, processDefinitionId);
        } else {
            conf = taskConfRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId, taskDefKey);
        }
        if (null != conf) {
            sponserStatus = conf.getSponsor();
        }
        return sponserStatus;
    }

    @Override
    @Transactional(readOnly = false)
    public void save(ItemTaskConf t) {
        String id = t.getId();
        if (StringUtils.isNotBlank(id)) {
            ItemTaskConf olditc = taskConfRepository.findById(id).orElse(null);
            olditc.setSponsor(t.getSponsor());
            olditc.setSignOpinion(t.getSignOpinion());
            olditc.setSignTask(t.getSignTask());
            taskConfRepository.save(olditc);
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
            entity.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }

        entity.setProcessDefinitionId(processDefinitionId);
        entity.setTaskDefKey(taskDefKey);
        return entity;
    }
}
