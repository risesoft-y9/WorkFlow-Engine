package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 日历配置接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/calendarConfig", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarConfigApiImpl implements CalendarConfigApi {

    private final CalendarConfigService calendarConfigService;

    /**
     * 获取指定年节假日配置信息
     *
     * @param tenantId 租户id
     * @param year 年份
     * @return {@code Y9Result<CalendarConfigModel>} 通用请求返回对象 - data 是日历配置
     * @since 9.6.6
     */
    @Override
    public Y9Result<CalendarConfigModel> findByYear(@RequestParam String tenantId, @RequestParam String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CalendarConfig calendarConfig = calendarConfigService.findByYear(year);
        CalendarConfigModel calendarConfigModel =
            ItemAdminModelConvertUtil.calendarConfig2CalendarConfigModel(calendarConfig);
        return Y9Result.success(calendarConfigModel);
    }

}
