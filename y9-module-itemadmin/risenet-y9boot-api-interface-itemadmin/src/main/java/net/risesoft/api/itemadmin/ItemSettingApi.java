package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ConfSettingModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项配置信息接口
 *
 * @author qinman
 * @date 2025/08/28
 */
public interface ItemSettingApi {

    /**
     * 获取事项配置信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<ConfSettingModel>} 通用请求返回对象 - data 是事项配置信息
     * @since 9.6.9
     */
    @GetMapping("/getConfSetting")
    Y9Result<ConfSettingModel> getConfSetting(@RequestParam("tenantId") String tenantId);
}