package net.risesoft.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.Y9PreFormItemBind;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.Y9PreFormItemBindRepository;
import net.risesoft.service.Y9PreFormItemBindService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9PreFormItemBindServiceImpl implements Y9PreFormItemBindService {

    private final Y9PreFormItemBindRepository y9PreFormItemBindRepository;

    @Override
    @Transactional(readOnly = false)
    public void copyBindInfo(String itemId, String newItemId) {
        try {
            Y9PreFormItemBind item = y9PreFormItemBindRepository.findByItemId(itemId);
            if (null != item) {
                Y9PreFormItemBind newItem = new Y9PreFormItemBind();
                newItem.setItemId(newItemId);
                newItem.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newItem.setFormId(item.getFormId());
                newItem.setFormName(item.getFormName());
                y9PreFormItemBindRepository.saveAndFlush(newItem);
            }
        } catch (Exception e) {
            LOGGER.error("复制前置表单项绑定信息失败", e);
        }
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> delete(String id) {
        try {
            y9PreFormItemBindRepository.deleteById(id);
            return Y9Result.successMsg("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("删除失败!");
        }
    }

    @Override
    public Y9PreFormItemBind findByItemId(String itemId) {
        return y9PreFormItemBindRepository.findByItemId(itemId);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> saveBindForm(String itemId, String formId, String formName) {
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
            return Y9Result.successMsg("保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return Y9Result.failure("保存失败");
        }
    }

    @Override
    @Transactional
    public void deleteBindInfo(String itemId) {
        try {
            y9PreFormItemBindRepository.deleteByItemId(itemId);
        } catch (Exception e) {
            LOGGER.error("删除前置表单项绑定信息失败", e);
        }
    }

}
