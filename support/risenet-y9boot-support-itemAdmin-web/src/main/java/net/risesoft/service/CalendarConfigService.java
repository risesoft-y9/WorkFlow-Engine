package net.risesoft.service;

import java.util.List;
import java.util.Map;

import net.risesoft.entity.CalendarConfig;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface CalendarConfigService {

    /**
     * 删除日历配置
     * 
     * @param startDate
     * @return
     */
    Map<String, Object> delCalendar(String startDate);

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
    List<Map<String, Object>> getCalendar(String month);

    /**
     * 保存日历配置
     * 
     * @param startDate
     * @param type
     * @return
     */
    Map<String, Object> saveCalendar(String startDate, Integer type);

    /**
     * 保存日历配置
     * 
     * @param calendarConfig
     */
    void saveOrUpdate(CalendarConfig calendarConfig);
}
