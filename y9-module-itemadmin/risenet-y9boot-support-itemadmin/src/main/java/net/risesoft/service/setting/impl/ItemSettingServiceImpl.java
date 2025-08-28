package net.risesoft.service.setting.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Optional;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.settings.ItemSetting;
import net.risesoft.repository.setting.ItemSettingRepository;
import net.risesoft.service.setting.ItemSettingService;

/**
 *
 * @author qinman
 * @date 2025/08/28
 */
@Slf4j
@Service
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@RequiredArgsConstructor
public class ItemSettingServiceImpl implements ItemSettingService {

    private final ItemSettingRepository itemSettingRepository;
    private final Environment environment;

    private Object convert(String stringValue, Class<?> tClass) {
        if (tClass == Integer.class || tClass == int.class) {
            return Integer.valueOf(stringValue);
        } else if (tClass == Long.class || tClass == long.class) {
            return Long.valueOf(stringValue);
        } else if (tClass == Boolean.class || tClass == boolean.class) {
            return Boolean.valueOf(stringValue);
        } else if (tClass == Float.class || tClass == float.class) {
            return Float.valueOf(stringValue);
        } else if (tClass == Double.class || tClass == double.class) {
            return Double.valueOf(stringValue);
        }
        return stringValue;
    }

    @Override
    public String get(String key) {
        Optional<ItemSetting> itemSettingOptional = itemSettingRepository.findById(key);
        if (itemSettingOptional.isPresent()) {
            return itemSettingOptional.get().getValue();
        }
        return environment.getProperty(key);
    }

    private AbstractSetting fillObjectFiledWithSettingItem(Class<? extends AbstractSetting> tClass) {
        AbstractSetting setting = null;
        try {
            setting = tClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        Field[] declaredFields = tClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (!Modifier.isStatic(declaredField.getModifiers())) {
                String value = this.get(setting.getPrefix() + declaredField.getName());
                try {
                    if (value != null) {
                        declaredField.setAccessible(true);
                        declaredField.set(setting, convert(value, declaredField.getType()));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return setting;
    }

    @Transactional
    private void saveObjectFiledAsSettingItem(AbstractSetting setting) {
        Field[] declaredFields = setting.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);

            if (!Modifier.isStatic(declaredField.getModifiers())) {
                ItemSetting itemSetting = new ItemSetting();
                itemSetting.setKey(setting.getPrefix() + declaredField.getName());
                try {
                    itemSetting.setValue(String.valueOf(declaredField.get(setting)));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                this.saveOrUpdate(itemSetting);
            }
        }
    }

    @Override
    @Transactional
    public ItemSetting saveOrUpdate(ItemSetting itemSetting) {
        return itemSettingRepository.save(itemSetting);
    }

    @Override
    public ConfSetting getConfSetting() {
        return (ConfSetting)this.fillObjectFiledWithSettingItem(ConfSetting.class);
    }

    @Override
    @Transactional
    public void saveConfSetting(ConfSetting tenantSetting) {
        this.saveObjectFiledAsSettingItem(tenantSetting);
    }
}
