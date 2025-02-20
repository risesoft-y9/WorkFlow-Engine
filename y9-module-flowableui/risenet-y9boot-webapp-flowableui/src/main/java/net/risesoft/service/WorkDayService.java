package net.risesoft.service;

import java.text.ParseException;
import java.util.Date;

import net.risesoft.model.itemadmin.TaskRelatedModel;

/**
 * @author qinman
 */
public interface WorkDayService {

    int getDay(Date startDate, Date endDate);

    /**
     * 计算某个日期几个工作日后的日期
     * 
     * @param date 起始日期
     * @param days 几个工作日
     * @return 计算后的日期字符串 YYYY-MM-DD
     * @throws ParseException
     */
    String getDate(Date date, int days) throws ParseException;

    /**
     *
     * @param startDate
     * @param endDate
     * @return
     */
    TaskRelatedModel getLightColor(Date startDate, Date endDate);
}
