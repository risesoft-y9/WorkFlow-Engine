package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.CalendarConfig;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface CalendarConfigService {

    /**
     * 删除日历配置
     *
     * @param startDate
     * @return
     */
    Y9Result<String> delCalendar(String startDate);

    /**
     * 获取年节假期配置信息
     *
     * @param year
     * @return
     */
    CalendarConfig findByYear(String year);

    /**
     * 获取日历配置
     *
     * @param month
     * @return
     */
    List<Map<String, Object>> listCalendar(String month);

    /**
     * 保存日历配置
     *
     * @param startDate
     * @param type
     * @return
     */
    Y9Result<String> saveCalendar(String startDate, Integer type);

    /**
     * 保存日历配置
     *
     * @param calendarConfig
     */
    void saveOrUpdate(CalendarConfig calendarConfig);
}
