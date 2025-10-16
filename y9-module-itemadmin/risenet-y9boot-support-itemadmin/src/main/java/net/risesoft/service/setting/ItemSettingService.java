package net.risesoft.service.setting;

import net.risesoft.service.setting.impl.ConfSetting;

/**
 *
 * @author qinman
 * @date 2025/08/28
 */
public interface ItemSettingService {

    String get(String key);

    ConfSetting getConfSetting();

    void saveConfSetting(ConfSetting tenantSetting);
}
