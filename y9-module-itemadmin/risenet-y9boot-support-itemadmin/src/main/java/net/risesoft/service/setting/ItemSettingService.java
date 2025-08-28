package net.risesoft.service.setting;

import net.risesoft.entity.settings.ItemSetting;
import net.risesoft.service.setting.impl.ConfSetting;

/**
 *
 * @author qinman
 * @date 2025/08/28
 */
public interface ItemSettingService {

    String get(String key);

    ItemSetting saveOrUpdate(ItemSetting itemSetting);

    ConfSetting getConfSetting();

    void saveConfSetting(ConfSetting tenantSetting);
}
