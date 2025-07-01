package net.risesoft.service.form.impl;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.form.Y9Form;
import net.risesoft.entity.form.Y9FormField;
import net.risesoft.entity.form.Y9TableField;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.form.Y9FormFieldRepository;
import net.risesoft.repository.form.Y9FormRepository;
import net.risesoft.repository.form.Y9TableFieldRepository;
import net.risesoft.service.form.Y9FormFieldService;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9BeanUtil;

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
    private final Y9FormRepository y9FormRepository;
    private final Y9TableFieldRepository y9TableFieldRepository;

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
    @Transactional(readOnly = false)
    public Y9Result<String> deleteByFormId(String formId) {
        try {
            y9FormFieldRepository.deleteByFormId(formId);
            return Y9Result.successMsg("清空表单绑定字段成功");
        } catch (Exception e) {
            LOGGER.error("清空表单绑定字段失败", e);
            return Y9Result.failure("清空表单绑定字段失败");
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

    @Override
    @Transactional
    public Y9Result<Object> copyFormAndFieldBind(String systemName, String systemCnName, String copyFormId,
        String tableName) {
        try {
            // 复制表单
            Y9Form copyForm = y9FormRepository.findById(copyFormId).orElse(null);
            if (null != copyForm) {
                Y9Form y9Form = new Y9Form();
                Y9BeanUtil.copyProperties(copyForm, y9Form);

                y9Form.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                boolean isExists = y9FormRepository.existsBySystemNameAndFormName(systemName, copyForm.getFormName());
                if (isExists) {
                    String newFormName = copyForm.getFormName() + "_副本";
                    y9Form.setFormName(newFormName);
                }
                y9Form.setSystemName(systemName);
                y9Form.setSystemCnName(systemCnName);
                y9Form = y9FormRepository.save(y9Form);
                List<Y9FormField> list = y9FormFieldRepository.findByFormId(copyFormId);
                for (Y9FormField element : list) {
                    Y9TableField tableField =
                        y9TableFieldRepository.findByTableNameAndFieldNameIgnoreCase(tableName, element.getFieldName());
                    if (null != tableField) {
                        Y9FormField newElement = new Y9FormField();
                        Y9BeanUtil.copyProperties(element, newElement);
                        newElement.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        newElement.setFormId(y9Form.getId());
                        newElement.setTableName(tableName);
                        y9FormFieldRepository.save(newElement);
                    }
                }
            }
            return Y9Result.success("复制表单设计及字段绑定成功");
        } catch (Exception e) {
            LOGGER.error("复制表单字段失败", e);
            return Y9Result.failure("复制表单字段失败");
        }
    }

    @Override
    @Transactional
    public Y9Result<String> saveFormFieldBind(String formId, String tableId, String tableName, Boolean isAppend,
        String fieldJson) {
        List<Y9FormField> addFieldList = new ArrayList<>();
        String updatedJson = "";
        try {
            List<Map<String, Object>> listMap = Y9JsonUtil.readListOfMap(fieldJson, String.class, Object.class);
            Y9Form y9Form = y9FormRepository.findById(formId).orElse(null);
            if (null != y9Form && StringUtils.isNotBlank(y9Form.getFormJson())) {
                for (Map<String, Object> map : listMap) {
                    String fieldName = (String)map.get("fieldName");
                    List<Y9FormField> fieldList = y9FormFieldRepository.findByFormIdAndFieldName(formId, fieldName);
                    if (fieldList.size() == 0) {
                        Y9FormField formField = new Y9FormField();
                        formField.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                        formField.setFieldCnName((String)map.get("fieldCnName"));
                        formField.setFieldName(fieldName);
                        formField.setFieldType((String)map.get("fieldType"));
                        formField.setFormId(formId);
                        formField.setTableId((String)map.get("tableId"));
                        formField.setTableName((String)map.get("tableName"));
                        formField.setQuerySign((String)map.get("querySign"));
                        formField.setQueryType((String)map.get("queryType"));
                        formField.setOptionValue((String)map.get("optionValue"));
                        y9FormFieldRepository.save(formField);
                        addFieldList.add(formField);
                    }
                }
                if (isAppend) { // 追加字段，解析表单设计formJson,在list后面插入追加字段的组件信息
                    if (null != addFieldList && !addFieldList.isEmpty()) {
                        String formJson = y9Form.getFormJson();
                        Map<String, Object> jsonMap = Y9JsonUtil.readValue(formJson, Map.class);
                        List<Map<String, Object>> formList = (List<Map<String, Object>>)jsonMap.get("list");

                        for (Y9FormField field : addFieldList) {
                            String fieldName = field.getFieldName();
                            Map<String, Object> newField = appendJson(fieldName, field.getFieldCnName(), tableName);
                            formList.add(newField); // 添加到最后
                        }
                        // 更新 formJson
                        jsonMap.put("list", formList);
                        updatedJson = Y9JsonUtil.writeValueAsString(jsonMap);
                        y9Form.setFormJson(updatedJson);
                        LOGGER.info("################################################更新表单设计数据成功:" + updatedJson);
                        y9FormRepository.save(y9Form);
                    }
                }
            } else {
                return Y9Result.failure("表单不存在或者表单设计数据为空");
            }
        } catch (Exception e) {
            LOGGER.error("保存表单字段以及自动插入表单组件失败", e);
            return Y9Result.failure("保存表单字段以及自动插入表单组件失败");
        }
        return Y9Result.success("保存表单字段以及自动插入表单组件");
    }

    private Map<String, Object> appendJson(String fieldName, String fieldCName, String tableName) {
        String randomKey = generateRandomKey();
        Map<String, Object> json = new HashMap<>();
        json.put("type", "input");
        json.put("icon", "icon-input");

        Map<String, Object> optionsJson = new HashMap<>();
        optionsJson.put("width", "");
        optionsJson.put("defaultValue", "");
        optionsJson.put("fieldPermission", "");
        optionsJson.put("bindDatabase", true);
        optionsJson.put("readonly", false);
        optionsJson.put("querySign", false);
        optionsJson.put("required", false);
        optionsJson.put("requiredMessage", "");
        optionsJson.put("dataType", "");
        optionsJson.put("dataTypeCheck", false);
        optionsJson.put("dataTypeMessage", "");
        optionsJson.put("pattern", "");
        optionsJson.put("patternCheck", false);
        optionsJson.put("patternMessage", "");
        optionsJson.put("validatorCheck", false);
        optionsJson.put("validator", "");
        optionsJson.put("placeholder", "");
        optionsJson.put("customClass", "");
        optionsJson.put("disabled", false);
        optionsJson.put("labelWidth", 100);
        optionsJson.put("isLabelWidth", false);
        optionsJson.put("hidden", false);
        optionsJson.put("dataBind", true);
        optionsJson.put("showPassword", false);
        optionsJson.put("clearable", false);
        optionsJson.put("maxlength", "");
        optionsJson.put("showWordLimit", false);
        optionsJson.put("customProps", new HashMap<String, Object>());
        optionsJson.put("tip", "");
        optionsJson.put("extendProps", new HashMap<String, Object>());
        optionsJson.put("remoteFunc", "func_" + randomKey);
        optionsJson.put("remoteOption", "option_" + randomKey);
        optionsJson.put("tableColumn", false);
        optionsJson.put("subform", false);
        json.put("options", optionsJson);

        Map<String, Object> eventsJson = new HashMap<>();
        eventsJson.put("onMounted", "");
        eventsJson.put("onChange", "");
        eventsJson.put("onFocus", "");
        eventsJson.put("onBlur", "");
        json.put("events", eventsJson);
        json.put("name", fieldCName);
        json.put("key", randomKey);
        json.put("model", fieldName);
        json.put("rules", "");
        json.put("bindTable", tableName);
        return json;
    }

    /**
     * 生成随机的key 获取字母和数字组合的随机字符串
     *
     * @return
     */
    private String generateRandomKey() {
        int length = 10;
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder sb = new StringBuilder(length);
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

}
