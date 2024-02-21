package net.risesoft.api.itemadmin;

import net.risesoft.model.itemadmin.CalendarConfigModel;

/**
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
     * @return CalendarConfigModel
     */
    CalendarConfigModel findByYear(String tenantId, String year);

}
