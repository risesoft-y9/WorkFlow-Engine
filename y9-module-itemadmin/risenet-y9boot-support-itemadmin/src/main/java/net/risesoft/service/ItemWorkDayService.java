package net.risesoft.service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

/**
 * 工作日计算
 * 
 * @author qinman
 */
public interface ItemWorkDayService {

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

    List<String> getDb(int days);
}
