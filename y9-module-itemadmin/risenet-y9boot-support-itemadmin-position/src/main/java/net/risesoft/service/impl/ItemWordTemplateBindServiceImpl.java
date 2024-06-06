package net.risesoft.service.impl;

import lombok.RequiredArgsConstructor;
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
import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
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
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public ItemWordTemplateBind findByItemIdAndProcessDefinitionId(String itemId, String processDefinitionId) {
        return wordTemplateBindRepository.findByItemIdAndProcessDefinitionId(itemId, processDefinitionId);
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
            e.printStackTrace();
        }
        return map;
    }
}
