package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.interfaceinfo.ItemInterfaceTaskBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.FlowElementModel;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.repository.interfaceinfo.ItemInterfaceTaskBindRepository;
import net.risesoft.service.config.ItemInterfaceTaskBindService;
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
        ProcessDefinitionModel latestPd = repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
        String latestPdId = latestPd.getId();
        String previousPdId = processDefinitionId;
        if (processDefinitionId.equals(latestPdId)) {
            if (latestPd.getVersion() > 1) {
                ProcessDefinitionModel previousPd =
                    repositoryApi.getPreviousProcessDefinitionById(tenantId, latestPdId).getData();
                previousPdId = previousPd.getId();
            }
        }
        List<FlowElementModel> nodes = processDefinitionApi.listUserTask(tenantId, latestPdId).getData();
        for (FlowElementModel feModel : nodes) {
            String currentTaskDefKey = feModel.getElementKey();
            // 当前上一个版本配置
            ItemInterfaceTaskBind bind =
                itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(
                    currentTaskDefKey, itemId, previousPdId, interfaceId);
            if (bind != null) {
                // 最新版本配置
                ItemInterfaceTaskBind oldBind =
                    itemInterfaceTaskBindRepository.findByTaskDefKeyAndItemIdAndProcessDefinitionIdAndInterfaceId(
                        currentTaskDefKey, itemId, latestPdId, interfaceId);
                if (null == oldBind) {
                    ItemInterfaceTaskBind newBind = new ItemInterfaceTaskBind();
                    newBind.setItemId(itemId);
                    newBind.setInterfaceId(interfaceId);
                    newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newBind.setCreateTime(sdf.format(new Date()));
                    newBind.setExecuteCondition(bind.getExecuteCondition());
                    newBind.setProcessDefinitionId(latestPdId);
                    newBind.setTaskDefKey(currentTaskDefKey);
                    itemInterfaceTaskBindRepository.save(newBind);
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
            if (StringUtils.isBlank(condition)) {
                // 没有执行条件，则删除绑定
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
