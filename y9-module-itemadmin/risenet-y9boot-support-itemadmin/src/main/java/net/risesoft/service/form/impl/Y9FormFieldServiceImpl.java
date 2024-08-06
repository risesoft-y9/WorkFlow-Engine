package net.risesoft.service.form.impl;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.form.Y9FormField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.service.form.Y9FormFieldService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9FormFieldServiceImpl implements Y9FormFieldService {

    private final Y9FormFieldRepository y9FormFieldRepository;

    @Override
    @Transactional(readOnly = false)
    public Y9Result<String> deleteFormFieldBind(String id) {
        try {
            y9FormFieldRepository.deleteById(id);
            return Y9Result.successMsg("删除表单绑定字段成功");
        } catch (Exception e) {
            LOGGER.error("删除表单绑定字段失败", e);
            return Y9Result.failure("删除表单绑定字段失败");
        }
    }

    @Override
    public Y9FormField findById(String id) {
        return y9FormFieldRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9FormField> listByFormId(String formId) {
        return y9FormFieldRepository.findByFormId(formId);
    }

    @Override
    public List<Y9FormField> listByTableName(String tableName) {
        return y9FormFieldRepository.findByTableName(tableName);
    }

    @Override
    public List<Y9FormField> listByTableNameAndFormId(String tableName, String formId) {
        return y9FormFieldRepository.findByFormIdAndTableName(tableName, formId);
    }

    @Override
    public Page<Y9FormField> pageByFormId(String formId, Integer page, Integer rows) {
        PageRequest pageable = PageRequest.of(page > 0 ? page - 1 : 0, rows);
        return y9FormFieldRepository.findByFormId(formId, pageable);
    }

    @Override
    @Transactional
    public Y9Result<Y9FormField> saveOrUpdate(Y9FormField formField) {
        try {
            String id = formField.getId();
            Y9FormField newField = new Y9FormField();
            if (StringUtils.isNotEmpty(id)) {
                Y9FormField oldField = this.findById(id);
                if (oldField != null) {
                    oldField.setFieldCnName(formField.getFieldCnName());
                    oldField.setFieldName(formField.getFieldName());
                    oldField.setFormId(formField.getFormId());
                    oldField.setTableId(formField.getTableId());
                    oldField.setTableName(formField.getTableName());
                    newField = y9FormFieldRepository.save(oldField);
                } else {
                    newField = y9FormFieldRepository.save(formField);
                }
            } else {
                newField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                newField.setFieldCnName(formField.getFieldCnName());
                newField.setFieldName(formField.getFieldName());
                newField.setFormId(formField.getFormId());
                newField.setTableId(formField.getTableId());
                newField.setTableName(formField.getTableName());
                newField = y9FormFieldRepository.save(newField);
            }
            return Y9Result.success(newField, "保存成功");
        } catch (Exception e) {
            LOGGER.error("保存表单字段失败", e);
            return Y9Result.failure("保存表单字段失败");
        }
    }
}
