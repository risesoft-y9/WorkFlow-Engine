package net.risesoft.service.form.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.service.form.Y9FormFieldService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service(value = "y9FormElementService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormFieldServiceImpl implements Y9FormFieldService {

    @Autowired
    private Y9FormFieldRepository y9FormFieldRepository;

    @Override
    public List<Y9FormField> findByFormId(String formId) {
        return y9FormFieldRepository.findByFormId(formId);
    }

    @Override
    public Y9FormField findById(String id) {
        return y9FormFieldRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9FormField> findByTableName(String tableName) {
        return y9FormFieldRepository.findByTableName(tableName);
    }

    @Override
    public List<Y9FormField> findByTableNameAndFormId(String tableName, String formId) {
        return y9FormFieldRepository.findByFormIdAndTableName(tableName, formId);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrUpdate(Y9FormField formField) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            String id = formField.getId();
            if (StringUtils.isNotEmpty(id)) {
                Y9FormField oldField = this.findById(id);
                if (oldField != null) {
                    oldField.setFieldCnName(formField.getFieldCnName());
                    oldField.setFieldName(formField.getFieldName());
                    oldField.setFormId(formField.getFormId());
                    oldField.setTableId(formField.getTableId());
                    oldField.setTableName(formField.getTableName());
                    y9FormFieldRepository.save(oldField);
                } else {
                    y9FormFieldRepository.save(formField);
                }
            } else {
                Y9FormField newField = new Y9FormField();
                newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newField.setFieldCnName(formField.getFieldCnName());
                newField.setFieldName(formField.getFieldName());
                newField.setFormId(formField.getFormId());
                newField.setTableId(formField.getTableId());
                newField.setTableName(formField.getTableName());
                y9FormFieldRepository.save(newField);
            }
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Page<Y9FormField> findByFormId(String formId, Integer page, Integer rows) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows);
        return y9FormFieldRepository.findByFormId(formId, pageable);
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> deleteFormFieldBind(String id) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            y9FormFieldRepository.deleteById(id);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "删除表单绑定字段成功");
        } catch (Exception e) {
            e.printStackTrace();
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除表单绑定字段失败");
        }
        return map;
    }
}
