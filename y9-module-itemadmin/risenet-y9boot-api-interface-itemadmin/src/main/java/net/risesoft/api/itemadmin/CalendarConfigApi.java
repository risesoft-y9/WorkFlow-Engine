package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.CalendarConfigModel;
import net.risesoft.pojo.Y9Result;

/**
 * 日历配置接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface CalendarConfigApi {

    /**
     * 获取年节假日配置信息
     *
     * @param tenantId 租户id
     * @param year 年份
     * @return {@code Y9Result<CalendarConfigModel>} 通用请求返回对象 - data 是日历配置
     * @since 9.6.6
     */
    @GetMapping("/findByYear")
    Y9Result<CalendarConfigModel> findByYear(@RequestParam("tenantId") String tenantId,
        @RequestParam("year") String year);
}
