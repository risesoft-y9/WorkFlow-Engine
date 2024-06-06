package net.risesoft.service.form.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9TableFieldService;
import org.apache.commons.lang3.StringUtils;
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
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableFieldServiceImpl implements Y9TableFieldService {

    private final Y9TableFieldRepository y9TableFieldRepository;

    private final Y9TableRepository y9TableRepository;

    private final TableManagerService tableManagerService;

    @Override
    @Transactional
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<>(16);
        try {
            y9TableFieldRepository.deleteById(id);
            map.put("msg", "删除成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            LOGGER.error("删除失败", e);
        }
        return map;
    }

    @Override
    public Y9TableField findById(String id) {
        return y9TableFieldRepository.findById(id).orElse(null);
    }

    @Override
    public List<Y9TableField> findSystemField(String tableId) {
        return y9TableFieldRepository.findByTableIdAndIsSystemFieldOrderByDisplayOrderAsc(tableId, 1);
    }

    @Override
    public Map<String, Object> getFieldList(String tableId) {
        Map<String, Object> resMap = new HashMap<>(16);
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
        resMap.put("rows", list);
        resMap.put(UtilConsts.SUCCESS, true);
        return resMap;
    }

    @Override
    @Transactional
    public Map<String, Object> saveOrUpdate(Y9TableField field) {
        Map<String, Object> map = new HashMap<>(16);
        try {
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
            y9TableFieldRepository.save(field);
            map.put(UtilConsts.SUCCESS, true);
            map.put("msg", "保存成功");
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "保存失败");
            LOGGER.error("保存失败", e);
        }
        return map;
    }

    @Override
    public List<Y9TableField> searchFieldsByTableId(String tableId) {
        return y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
    }

    @Override
    public List<Y9TableField> searchFieldsByTableId(String tableId, Integer state) {
        return y9TableFieldRepository.findByTableIdAndStateOrderByDisplayOrderAsc(tableId, state);
    }

    @Override
    @Transactional
    public void updateState(String tableId) {
        y9TableFieldRepository.updateState(tableId);
    }

}
