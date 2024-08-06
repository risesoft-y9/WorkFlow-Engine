package net.risesoft.service.form.impl;

import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9TableFieldService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableFieldServiceImpl implements Y9TableFieldService {

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final Y9TableRepository y9TableRepository;

    private final TableManagerService tableManagerService;

    @Override
    @Transactional(readOnly = false)
    public void delete(String id) {
        y9TableFieldRepository.deleteById(id);
    }

    @Override
    public Y9TableField findById(String id) {
        return y9TableFieldRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9TableField> listByTableId(String tableId) {
        List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
        Map<String, Object> map = tableManagerService.getExistTableFields(tableId);
        for (Y9TableField field : list) {
            /*
             * 指定表中是否存在该字段，0为否，1为是
             */
            field.setState(0);
            if (map != null && !map.isEmpty()) {
                if (map.get(field.getFieldName().toLowerCase()) != null) {
                    field.setState(1);
                }
            }
        }
        return list;
    }

    @Override
    public List<Y9TableField> listByTableIdAndState(String tableId, Integer state) {
        return y9TableFieldRepository.findByTableIdAndStateOrderByDisplayOrderAsc(tableId, state);
    }

    @Override
    public List<Y9TableField> listByTableIdOrderByDisplay(String tableId) {
        return y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
    }

    @Override
    public List<Y9TableField> listSystemFieldByTableId(String tableId) {
        return y9TableFieldRepository.findByTableIdAndIsSystemFieldOrderByDisplayOrderAsc(tableId, 1);
    }

    @Override
    @Transactional(readOnly = false)
    public Y9TableField saveOrUpdate(Y9TableField field) {
        if (StringUtils.isBlank(field.getId())) {
            field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        }
        if (field.getDisplayOrder() == null) {
            Integer order = y9TableFieldRepository.getMaxDisplayOrder(field.getTableId());
            field.setDisplayOrder(order == null ? 1 : order + 1);
        }
        Y9Table y9Table = y9TableRepository.findById(field.getTableId()).orElse(null);
        assert y9Table != null;
        field.setTableName(y9Table.getTableName());
        boolean b = field.getFieldType().contains("(");
        if (!b) {
            field.setFieldType(field.getFieldType() + "(" + field.getFieldLength() + ")");
        }
        return y9TableFieldRepository.save(field);
    }

    @Override
    @Transactional
    public void updateState(String tableId) {
        y9TableFieldRepository.updateState(tableId);
    }

}
