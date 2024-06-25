package net.risesoft.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemInterfaceTaskBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.repository.jpa.ItemInterfaceTaskBindRepository;
import net.risesoft.service.ItemInterfaceTaskBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ItemInterfaceTaskBindServiceImpl implements ItemInterfaceTaskBindService {

    private final ItemInterfaceTaskBindRepository itemInterfaceTaskBindRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void copyBind(String itemId, String interfaceId, String processDefinitionId) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String tenantId = Y9LoginUserHolder.getTenantId();
        String proDefKey = processDefinitionId.split(":")[0];
        ProcessDefinitionModel latestpd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey);
        String latestpdId = latestpd.getId();
        String previouspdId = processDefinitionId;
        if (processDefinitionId.equals(latestpdId)) {
            if (latestpd.getVersion() > 1) {
                ProcessDefinitionModel previouspd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestpdId);
                previouspdId = previouspd.getId();
            }
        }
        List<Map<String, Object>> nodes = processDefinitionApi.getFlowElement(tenantId, latestpdId, false);
        for (Map<String, Object> map : nodes) {
            String currentTaskDefKey = (String)map.get("elementKey");
            // 当前/上一版本配置
            ItemInterfaceTaskBind bind =
                itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(
                    currentTaskDefKey, itemId, previouspdId, interfaceId);
            if (bind != null) {
                // 最新版本配置
                ItemInterfaceTaskBind oldbind =
                    itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(
                        currentTaskDefKey, itemId, latestpdId, interfaceId);
                if (null == oldbind) {
                    ItemInterfaceTaskBind newbind = new ItemInterfaceTaskBind();
                    newbind.setItemId(itemId);
                    newbind.setInterfaceId(interfaceId);
                    newbind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newbind.setCreateTime(sdf.format(new Date()));
                    newbind.setExecuteCondition(bind.getExecuteCondition());
                    newbind.setProcessDefinitionId(latestpdId);
                    newbind.setTaskDefKey(currentTaskDefKey);
                    itemInterfaceTaskBindRepository.save(newbind);
                }
            }
        }

    }

    @Override
    @Transactional
    public void saveBind(String itemId, String interfaceId, String processDefinitionId, String elementKey,
        String condition) {
        ItemInterfaceTaskBind bind =
            itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(elementKey,
                itemId, processDefinitionId, interfaceId);
        if (bind != null) {
            if (StringUtils.isBlank(condition)) {// 没有执行条件，则删除绑定
                itemInterfaceTaskBindRepository.delete(bind);
            } else {
                bind.setExecuteCondition(condition);
                itemInterfaceTaskBindRepository.save(bind);
            }
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            bind = new ItemInterfaceTaskBind();
            bind.setItemId(itemId);
            bind.setInterfaceId(interfaceId);
            bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            bind.setCreateTime(sdf.format(new Date()));
            bind.setExecuteCondition(condition);
            bind.setProcessDefinitionId(processDefinitionId);
            bind.setTaskDefKey(elementKey);
            itemInterfaceTaskBindRepository.save(bind);
        }
    }
}
