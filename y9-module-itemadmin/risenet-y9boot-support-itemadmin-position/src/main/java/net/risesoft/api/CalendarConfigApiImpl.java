package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 日历配置接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/calendarConfig")
public class CalendarConfigApiImpl implements CalendarConfigApi {

    private final CalendarConfigService calendarConfigService;

    /**
     * 获取年节假日配置信息
     *
     * @param tenantId 租户id
     * @param year     年份
     * @return Y9Result<CalendarConfigModel>
     */
    @Override
    @GetMapping(value = "/findByYear", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<CalendarConfigModel> findByYear(String tenantId, String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CalendarConfig calendarConfig = calendarConfigService.findByYear(year);
        CalendarConfigModel calendarConfigModel = ItemAdminModelConvertUtil.calendarConfig2CalendarConfigModel(calendarConfig);
        return Y9Result.success(calendarConfigModel);
    }

}
