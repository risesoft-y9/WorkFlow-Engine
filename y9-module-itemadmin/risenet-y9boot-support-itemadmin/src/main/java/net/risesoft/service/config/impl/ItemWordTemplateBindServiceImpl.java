package net.risesoft.service.config.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.entity.SpmApproveItem;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemWordTemplateBindRepository;
import net.risesoft.repository.jpa.SpmApproveItemRepository;
import net.risesoft.service.config.ItemWordTemplateBindService;
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
public class ItemWordTemplateBindServiceImpl implements ItemWordTemplateBindService {

    private final ItemWordTemplateBindRepository wordTemplateBindRepository;

    private final SpmApproveItemRepository spmApproveItemRepository;

    private final RepositoryApi repositoryApi;

    @Override
    @Transactional
    public void clearBindStatus(String itemId, String processDefinitionId) {
        List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(itemId);
        for (ItemWordTemplateBind itemWordTemplateBind : list) {
            itemWordTemplateBind.setBindStatus(0);
            wordTemplateBindRepository.save(itemWordTemplateBind);
        }
    }

    @Override
    @Transactional
    public void copyBind(String itemId, String processDefinitionId) {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            SpmApproveItem item = spmApproveItemRepository.findById(itemId).orElse(null);
            String proDefKey = item.getWorkflowGuid();
            ProcessDefinitionModel latestpd =
                repositoryApi.getLatestProcessDefinitionByKey(tenantId, proDefKey).getData();
            String latestpdId = latestpd.getId();
            String previouspdId = processDefinitionId;
            if (processDefinitionId.equals(latestpdId)) {
                if (latestpd.getVersion() > 1) {
                    ProcessDefinitionModel previouspd =
                        repositoryApi.getPreviousProcessDefinitionById(tenantId, latestpdId).getData();
                    previouspdId = previouspd.getId();
                }
            }
            List<ItemWordTemplateBind> previousList =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionIdOrderByBindStatus(itemId, previouspdId);
            if (null != previousList && !previousList.isEmpty()) {
                for (ItemWordTemplateBind templateBind : previousList) {
                    ItemWordTemplateBind oldBind =
                        wordTemplateBindRepository.findByItemIdAndProcessDefinitionIdAndTemplateId(itemId, previouspdId,
                            templateBind.getTemplateId());
                    if (null == oldBind) {
                        ItemWordTemplateBind bind = new ItemWordTemplateBind();
                        bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        bind.setItemId(itemId);
                        bind.setProcessDefinitionId(latestpdId);
                        bind.setTemplateId(templateBind.getTemplateId());
                        bind.setBindStatus(templateBind.getBindStatus());
                        bind.setBindValue(templateBind.getBindValue());
                        bind.setTenantId(Y9LoginUserHolder.getTenantId());
                        wordTemplateBindRepository.save(bind);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制正文模板绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        try {
            List<ItemWordTemplateBind> list =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionIdOrderByBindStatus(itemId, lastVersionPid);
            if (null != list && !list.isEmpty()) {
                for (ItemWordTemplateBind templateBind : list) {
                    ItemWordTemplateBind bind = new ItemWordTemplateBind();
                    bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    bind.setItemId(newItemId);
                    bind.setProcessDefinitionId(lastVersionPid);
                    bind.setTemplateId(templateBind.getTemplateId());
                    bind.setBindStatus(templateBind.getBindStatus());
                    bind.setBindValue(templateBind.getBindValue());
                    bind.setTenantId(Y9LoginUserHolder.getTenantId());
                    wordTemplateBindRepository.save(bind);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制正文模板绑定信息失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBind(String id) {
        try {
            wordTemplateBindRepository.deleteById(id);
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            wordTemplateBindRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除正文模板绑定信息失败", e);
        }
    }

    @Override
    public ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
    }

    @Override
    public List<ItemWordTemplateBind> listByItemIdOrderByBindValueAsc(String itemId) {
        return wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(itemId);
    }

    @Override
    @Transactional
    public Y9Result<String> save(String itemId, String processDefinitionId, String templateId) {
        try {
            ItemWordTemplateBind bind =
                wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
            if (null != bind) {
                bind.setTemplateId(templateId);
                wordTemplateBindRepository.save(bind);
            } else {
                bind = new ItemWordTemplateBind();
                bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                bind.setItemId(itemId);
                bind.setProcessDefinitionId(processDefinitionId);
                bind.setTemplateId(templateId);
                bind.setTenantId(Y9LoginUserHolder.getTenantId());
                wordTemplateBindRepository.save(bind);
            }
            return Y9Result.success(bind.getId(), "正文模板绑定成功");
        } catch (Exception e) {
            LOGGER.error("正文模板绑定失败", e);
            return Y9Result.failure("正文模板绑定失败");
        }

    }

    @Override
    @Transactional
    public Y9Result<String> save(String itemId, String processDefinitionId, String[] templateId) {
        try {
            for (String id : templateId) {
                ItemWordTemplateBind bind = wordTemplateBindRepository.findByItemIdAndTemplateId(itemId, id);
                if (null != bind) {
                    bind.setTemplateId(id);
                    wordTemplateBindRepository.save(bind);
                } else {
                    bind = new ItemWordTemplateBind();
                    bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    bind.setItemId(itemId);
                    bind.setProcessDefinitionId(processDefinitionId);
                    bind.setTemplateId(id);
                    bind.setTenantId(Y9LoginUserHolder.getTenantId());
                    wordTemplateBindRepository.save(bind);
                }
            }

            return Y9Result.successMsg("正文模板绑定成功");
        } catch (Exception e) {
            LOGGER.error("正文模板绑定失败", e);
            return Y9Result.failure("正文模板绑定失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> saveTemplateValue(String id, String itemId, String bindValue) {
        try {
            List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(itemId);
            for (ItemWordTemplateBind itemWordTemplateBind : list) {
                if (bindValue.equals(itemWordTemplateBind.getBindValue())) {
                    return Y9Result.failure("模板绑定值已经存在！请不要设置相同的值");
                }
            }
            ItemWordTemplateBind templateBind = wordTemplateBindRepository.findById(id).orElse(null);
            if (null != templateBind) {
                templateBind.setBindValue(bindValue);
                wordTemplateBindRepository.save(templateBind);
            }
            return Y9Result.successMsg("模板绑定值设置成功");
        } catch (Exception e) {
            LOGGER.error("模板绑定值设置失败", e);
            return Y9Result.failure("模板绑定值设置失败");
        }
    }

    @Override
    @Transactional
    public void updateBindStatus(String id, String itemId, String processDefinitionId) {
        try {
            ItemWordTemplateBind bind = wordTemplateBindRepository.findById(id).orElse(null);
            if (null != bind) {
                List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(itemId);
                for (ItemWordTemplateBind itemWordTemplateBind : list) {
                    if (itemWordTemplateBind.getId().equals(id)) {
                        itemWordTemplateBind.setBindStatus(1);
                        wordTemplateBindRepository.save(itemWordTemplateBind);
                    } else {
                        itemWordTemplateBind.setBindStatus(0);
                        wordTemplateBindRepository.save(itemWordTemplateBind);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
        }
    }
}
