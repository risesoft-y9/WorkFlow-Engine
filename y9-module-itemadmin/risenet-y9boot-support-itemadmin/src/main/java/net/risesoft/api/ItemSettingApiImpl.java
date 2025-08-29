package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemSettingApi;
import net.risesoft.model.itemadmin.ConfSettingModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.setting.ItemSettingService;
import net.risesoft.service.setting.impl.ConfSetting;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 办结信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemSetting", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemSettingApiImpl implements ItemSettingApi {

    private final ItemSettingService itemSettingService;

    /**
     * 获取事项配置信息
     *
     * @param tenantId 租户id
     * @return {@code Y9Result<ConfSettingModel>} 通用请求返回对象 - data 是事项配置信息
     * @since 9.6.9
     */
    @Override
    public Y9Result<ConfSettingModel> getConfSetting(@RequestParam String tenantId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ConfSetting confSetting = itemSettingService.getConfSetting();
        ConfSettingModel confSettingModel = new ConfSettingModel();
        Y9BeanUtil.copyProperties(confSetting, confSettingModel);
        return Y9Result.success(confSettingModel);
    }
}
