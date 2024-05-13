package net.risesoft.service.impl;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.Y9PreFormItemBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.Y9PreFormItemBindRepository;
import net.risesoft.service.SpmApproveItemService;
import net.risesoft.service.Y9PreFormItemBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Service(value = "y9PreFormItemBindService")
public class Y9PreFormItemBindServiceImpl implements Y9PreFormItemBindService {

    @Autowired
    private Y9PreFormItemBindRepository y9PreFormItemBindRepository;

    @Resource(name = "spmApproveItemService")
    private SpmApproveItemService spmApproveItemService;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, true);
        map.put("msg", "删除成功");
        try {
            y9PreFormItemBindRepository.deleteById(id);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Y9PreFormItemBind findByItemId(String itemId) {
        Y9PreFormItemBind item = y9PreFormItemBindRepository.findByItemId(itemId);
        return item;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveBindForm(String itemId, String formId, String formName) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put(UtilConsts.SUCCESS, false);
        map.put("msg", "保存失败");
        try {
            Y9PreFormItemBind item = y9PreFormItemBindRepository.findByItemId(itemId);
            if (item != null) {
                item.setFormId(formId);
                item.setFormName(formName);
            } else {
                item = new Y9PreFormItemBind();
                item.setItemId(itemId);
                item.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                item.setFormId(formId);
                item.setFormName(formName);
            }
            y9PreFormItemBindRepository.saveAndFlush(item);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

}
