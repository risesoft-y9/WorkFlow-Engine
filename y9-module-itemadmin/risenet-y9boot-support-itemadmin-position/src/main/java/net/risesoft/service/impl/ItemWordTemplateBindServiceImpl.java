package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ItemWordTemplateBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ItemWordTemplateBindRepository;
import net.risesoft.service.ItemWordTemplateBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    @Transactional
    public Map<String, Object> deleteBind(String id) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "删除失败");
        try {
            wordTemplateBindRepository.deleteById(id);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
        }
        return map;
    }

    @Override
    public ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
    }

    @Override
    public List<ItemWordTemplateBind> findByItemIdOrderByBindValueAsc(String ItemId) {
        return wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(ItemId);
    }

    @Override
    @Transactional
    public Map<String, Object> save(String itemId, String processDefinitionId, String templateId) {
        Map<String, Object> map = new HashMap<>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "正文模板绑定失败");
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
            map.put("bindId", bind.getId());
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "正文模板绑定成功");
        } catch (Exception e) {
            LOGGER.error("正文模板绑定失败", e);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> save(String itemId, String processDefinitionId, String[] templateId) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("msg", "正文模板绑定失败");
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

            map.put("success", true);
            map.put("msg", "正文模板绑定成功");
        } catch (Exception e) {
            LOGGER.error("正文模板绑定失败", e);
        }
        return map;
    }

    @Override
    @Transactional
    public Map<String, Object> saveTemplateValue(String id, String bindValue) {
        Map<String, Object> map = new HashMap<>();
        map.put("success", false);
        map.put("msg", "模板设置失败");
        try {
            List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(id);
            for (ItemWordTemplateBind itemWordTemplateBind : list) {
                if(bindValue.equals(itemWordTemplateBind.getBindValue())) {
                    map.put("success", false);
                    map.put("msg", "模板绑定值已经存在！请不要设置相同的值");
                    return map;
                }
            }
            ItemWordTemplateBind templateBind = wordTemplateBindRepository.findById(id).orElse(null);
            if (null != templateBind) {
                templateBind.setBindValue(bindValue);
                wordTemplateBindRepository.save(templateBind);
            }
            map.put("success", true);
            map.put("msg", "模板绑定值设置成功");
        } catch (Exception e) {
            LOGGER.error("模板绑定值设置失败", e);
        }
        return map;
    }

    @Override
    @Transactional
    public void updateBindStatus(String id, String itemId, String processDefinitionId) {
        try {
            ItemWordTemplateBind bind = wordTemplateBindRepository.findById(id).orElse(null);
            if (null != bind) {
                List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdOrderByBindValueAsc(itemId);
                for (ItemWordTemplateBind itemWordTemplateBind : list) {
                    if(itemWordTemplateBind.getId().equals(id)) {
                        itemWordTemplateBind.setBindStatus(1);
                        wordTemplateBindRepository.save(itemWordTemplateBind);
                    }else {
                        itemWordTemplateBind.setBindStatus(0);
                        wordTemplateBindRepository.save(itemWordTemplateBind);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("更新绑定状态失败", e);
        }
    }

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
    public void copyBindInfo(String itemId, String newItemId, String lastVersionPid) {
        try{
            List<ItemWordTemplateBind> list = wordTemplateBindRepository.findByItemIdAndProcessDefinitionIdOrderByBindStatus(itemId,lastVersionPid);
            if(null != list && !list.isEmpty()) {
                for (ItemWordTemplateBind templateBind : list) {
                    ItemWordTemplateBind bind = new ItemWordTemplateBind();
                    bind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                    bind.setItemId(newItemId);
                    bind.setProcessDefinitionId(lastVersionPid);
                    bind.setTemplateId(templateBind.getTemplateId());
                    bind.setBindStatus(templateBind.getBindStatus());
                    bind.setTenantId(Y9LoginUserHolder.getTenantId());
                    wordTemplateBindRepository.save(bind);
                }
            }
        } catch (Exception e) {
            LOGGER.error("复制正文模板绑定信息失败", e);
        }
    }
}
