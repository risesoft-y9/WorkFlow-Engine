package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.CalendarConfigApi;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/calendarConfig")
public class CalendarConfigApiImpl implements CalendarConfigApi {

    @Autowired
    private CalendarConfigService calendarConfigService;

    @Override
    @GetMapping(value = "/findByYear", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<CalendarConfigModel> findByYear(String tenantId, String year) {
        Y9LoginUserHolder.setTenantId(tenantId);
        CalendarConfig calendarConfig = calendarConfigService.findByYear(year);
        CalendarConfigModel calendarConfigModel =
            ItemAdminModelConvertUtil.calendarConfig2CalendarConfigModel(calendarConfig);
        return Y9Result.success(calendarConfigModel);
    }

}
