package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.ExtranetEformItemBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.ExtranetEformItemBindRepository;
import net.risesoft.service.ExtranetEformItemBindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "extranetEformItemBindService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class ExtranetEformItemBindServiceImpl implements ExtranetEformItemBindService {

    @Autowired
    private ExtranetEformItemBindRepository extranetEformItemBindRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isNotBlank(id)) {
                extranetEformItemBindRepository.deleteById(id);
            }
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public List<ExtranetEformItemBind> getList(String itemId) {
        List<ExtranetEformItemBind> list = new ArrayList<ExtranetEformItemBind>();
        try {
            list = extranetEformItemBindRepository.findByItemId(itemId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> save(String itemId, String formId, String formName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            ExtranetEformItemBind extranetEformItemBind = new ExtranetEformItemBind();
            extranetEformItemBind.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            extranetEformItemBind.setFormId(formId);
            extranetEformItemBind.setFormName(formName);
            extranetEformItemBind.setFormUrl("/engine/TemplatePreview.pfm?temp_Id=" + formId);
            extranetEformItemBind.setItemId(itemId);
            extranetEformItemBind.setItemName("");
            Integer tabIndex = extranetEformItemBindRepository.getMaxTabIndex(itemId);
            extranetEformItemBind.setTabIndex(tabIndex == null ? 1 : tabIndex + 1);
            extranetEformItemBind.setTenantId(Y9LoginUserHolder.getTenantId());
            extranetEformItemBindRepository.save(extranetEformItemBind);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        return map;
    }

}
