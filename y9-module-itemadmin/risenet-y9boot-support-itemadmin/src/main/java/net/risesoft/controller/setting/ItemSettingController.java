package net.risesoft.controller.setting;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.setting.ItemSettingService;
import net.risesoft.service.setting.impl.ConfSetting;

/**
 * 系统设置
 * 
 * @author qinman
 * @date 2025/08/28
 */
@RestController
@RequestMapping(value = "/vue/itemSetting", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
@Slf4j
public class ItemSettingController {

    private final ItemSettingService itemSettingService;

    /**
     * 获取系统配置
     * 
     * @return {@code Y9Result<ConfSetting>}
     */
    @GetMapping("/getConfSetting")
    public Y9Result<ConfSetting> getConfSetting() {
        return Y9Result.success(itemSettingService.getConfSetting());
    }

    /**
     * 保存系统配置
     * 
     * @param confSetting 系统配置
     * @return {@code Y9Result<Object>}
     */
    @PostMapping("/saveConfSetting")
    public Y9Result<Object> saveConfSetting(ConfSetting confSetting) {
        itemSettingService.saveConfSetting(confSetting);
        return Y9Result.success();
    }
}
