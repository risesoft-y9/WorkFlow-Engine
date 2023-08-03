package net.risesoft.service.form.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.form.Y9Table;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.enums.ItemFieldTypeEnum;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.repository.form.Y9TableRepository;
import net.risesoft.service.form.TableManagerService;
import net.risesoft.service.form.Y9TableFieldService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "y9TableFieldService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class Y9TableFieldServiceImpl implements Y9TableFieldService {

    @Autowired
    private Y9TableFieldRepository y9TableFieldRepository;

    @Autowired
    private Y9TableRepository y9TableRepository;

    @Autowired
    private TableManagerService tableManagerService;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delete(String id) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            y9TableFieldRepository.deleteById(id);
            map.put("msg", "删除成功");
            map.put(UtilConsts.SUCCESS, true);
        } catch (Exception e) {
            map.put(UtilConsts.SUCCESS, false);
            map.put("msg", "删除失败");
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public Y9TableField findById(String id) {
        Y9TableField c = y9TableFieldRepository.findById(id).orElse(null);
        return c;
    }

    @Override
    public List<Y9TableField> findSystemField(String tableId) {
        List<Y9TableField> list =
            y9TableFieldRepository.findByTableIdAndIsSystemFieldOrderByDisplayOrderAsc(tableId, 1);
        return list;
    }

    public String getCnFieldType(String fieldType) {
        ItemFieldTypeEnum[] ftArr = ItemFieldTypeEnum.values();
        for (ItemFieldTypeEnum ft : ftArr) {
            if (fieldType.indexOf(ft.getValue()) >= 0) {
                fieldType = fieldType.replaceFirst(ft.getValue(), ft.getName());
            }
        }
        return fieldType;
    }

    @Override
    public Map<String, Object> getFieldList(String tableId) {
        Map<String, Object> resMap = new HashMap<String, Object>(16);
        List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
        Map<String, Object> map = tableManagerService.getExistTableFields(tableId);
        for (Y9TableField field : list) {
            // 指定表中是否存在该字段，0为否，1为是
            field.setState(0);
            if (map != null && map.size() > 0) {
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
    @Transactional(readOnly = false)
    public Map<String, Object> saveOrUpdate(Y9TableField field) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        try {
            if (StringUtils.isBlank(field.getId())) {
                field.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
            }
            if (field.getDisplayOrder() == null) {
                Integer order = y9TableFieldRepository.getMaxDisplayOrder(field.getTableId());
                field.setDisplayOrder(order == null ? 1 : order + 1);
            }
            Y9Table y9Table = y9TableRepository.findById(field.getTableId()).orElse(null);
            field.setTableName(y9Table.getTableName());
            boolean b = !field.getFieldType().contains("(");
            if (b) {
                field.setFieldType(field.getFieldType() + "(" + field.getFieldLength() + ")");
            }
            y9TableFieldRepository.save(field);
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
    public List<Y9TableField> searchFieldsByTableId(String tableId) {
        List<Y9TableField> list = y9TableFieldRepository.findByTableIdOrderByDisplayOrderAsc(tableId);
        return list;
    }

    @Override
    public List<Y9TableField> searchFieldsByTableId(String tableId, Integer state) {
        List<Y9TableField> list = y9TableFieldRepository.findByTableIdAndStateOrderByDisplayOrderAsc(tableId, state);
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public void updateState(String tableId) {
        y9TableFieldRepository.updateState(tableId);
    }

}
