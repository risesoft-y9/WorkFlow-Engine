package net.risesoft.service.config.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemLinkRole;
import net.risesoft.entity.ItemNodeLinkBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.repository.jpa.ItemLinkRoleRepository;
import net.risesoft.repository.jpa.ItemNodeLinkBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemNodeLinkBindService;
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
public class ItemNodeLinkBindServiceImpl implements ItemNodeLinkBindService {

    private final ItemNodeLinkBindRepository itemNodeLinkBindRepository;

    private final ItemLinkRoleRepository itemLinkRoleRepository;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final RepositoryApi repositoryApi;

    private final ProcessDefinitionApi processDefinitionApi;

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        String tenantId = Y9LoginUserHolder.getTenantId();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
        String proDefKey = item.getWorkflowGuid();
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
        List<ItemNodeLinkBind> previouseibList =
            itemNodeLinkBindRepository.findByItemIdAndProcessDefinitionId(itemId, previouspdId);
        List<TargetModel> nodes = processDefinitionApi.getNodes(tenantId, latestpdId, false).getData();
        /*
         * 如果最新的流程定义存在当前任务节点，则查找当前事项的最新的流程定义的任务节点有没有绑定对应的链接，没有就保存
         */
        for (ItemNodeLinkBind bind : previouseibList) {
            String taskDefKey = bind.getTaskDefKey();
            String linkId = bind.getLinkId();
            if (StringUtils.isEmpty(taskDefKey)) {
                ItemNodeLinkBind old = itemNodeLinkBindRepository
                    .findByItemIdAndProcessDefinitionIdAndAndLinkIdAndTaskDefKey(itemId, latestpdId, linkId, "");
                if (null == old) {
                    ItemNodeLinkBind newBind = new ItemNodeLinkBind();
                    newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    newBind.setProcessDefinitionId(latestpdId);
                    newBind.setLinkId(linkId);
                    newBind.setItemId(itemId);
                    newBind.setCreateTime(sdf.format(new Date()));
                    newBind.setTaskDefKey("");
                    itemNodeLinkBindRepository.save(newBind);
                }
            } else {
                for (TargetModel targetModel : nodes) {
                    if (targetModel.getTaskDefKey().equals(taskDefKey)) {
                        ItemNodeLinkBind old =
                            itemNodeLinkBindRepository.findByItemIdAndProcessDefinitionIdAndAndLinkIdAndTaskDefKey(
                                itemId, latestpdId, linkId, taskDefKey);
                        if (null == old) {
                            ItemNodeLinkBind newBind = new ItemNodeLinkBind();
                            newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                            newBind.setProcessDefinitionId(latestpdId);
                            newBind.setLinkId(linkId);
                            newBind.setItemId(itemId);
                            newBind.setCreateTime(sdf.format(new Date()));
                            newBind.setTaskDefKey(taskDefKey);
                            itemNodeLinkBindRepository.save(newBind);
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String latestpdId) {
        try {
            List<ItemNodeLinkBind> previouseibList =
                itemNodeLinkBindRepository.findByItemIdAndProcessDefinitionId(itemId, latestpdId);
            for (ItemNodeLinkBind bind : previouseibList) {
                ItemNodeLinkBind newBind = new ItemNodeLinkBind();
                newBind.setItemId(newItemId);
                newBind.setLinkId(bind.getLinkId());
                newBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newBind.setCreateTime(bind.getCreateTime());
                newBind.setProcessDefinitionId(latestpdId);
                newBind.setTaskDefKey(bind.getTaskDefKey());
                itemNodeLinkBindRepository.save(newBind);
            }
        } catch (Exception e) {
            LOGGER.error("复制链接节点配置绑定关系失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            List<ItemNodeLinkBind> bindList = itemNodeLinkBindRepository.findByItemId(itemId);
            for (ItemNodeLinkBind bind : bindList) {
                itemLinkRoleRepository.deleteByItemLinkId(bind.getId());
                itemNodeLinkBindRepository.deleteById(bind.getId());
            }
        } catch (Exception e) {
            LOGGER.error("删除链接节点配置绑定关系失败", e);
        }
    }

    @Override
    public ItemNodeLinkBind listByItemIdAndProcessDefinitionIdAndTaskDefKey(String itemId, String processDefinitionId,
        String taskDefKey) {
        return itemNodeLinkBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId, processDefinitionId,
            taskDefKey);
    }

    @Override
    @Transactional
    public void removeBind(String bindId) {
        itemNodeLinkBindRepository.deleteById(bindId);
        itemLinkRoleRepository.deleteByItemLinkId(bindId);
    }

    @Override
    @Transactional
    public void removeRole(String[] ids) {
        for (String id : ids) {
            itemLinkRoleRepository.deleteById(id);
        }
    }

    @Override
    @Transactional
    public void saveBindRole(String itemLinkId, String roleIds) {
        String[] roleIdarr = roleIds.split(";");
        for (String roleId : roleIdarr) {
            ItemLinkRole info = itemLinkRoleRepository.findByItemLinkIdAndRoleId(itemLinkId, roleId);
            if (null == info) {
                info = new ItemLinkRole();
                info.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                info.setItemLinkId(itemLinkId);
                info.setRoleId(roleId);
                itemLinkRoleRepository.save(info);
            }
        }
    }

    @Override
    @Transactional
    public void saveItemNodeLinkBind(String itemId, String linkId, String processDefinitionId, String taskDefKey) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ItemNodeLinkBind item = itemNodeLinkBindRepository.findByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
            processDefinitionId, taskDefKey);
        if (item == null) {
            item = new ItemNodeLinkBind();
            item.setItemId(itemId);
            item.setLinkId(linkId);
            item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            item.setCreateTime(sdf.format(new Date()));
            item.setProcessDefinitionId(processDefinitionId);
            item.setTaskDefKey(taskDefKey);
            itemNodeLinkBindRepository.save(item);
            return;
        }
        item.setLinkId(linkId);
        item.setCreateTime(sdf.format(new Date()));
        itemNodeLinkBindRepository.save(item);
    }
}
