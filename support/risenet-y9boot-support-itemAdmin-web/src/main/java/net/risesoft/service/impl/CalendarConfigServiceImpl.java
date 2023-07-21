package net.risesoft.service.impl;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;

import net.risesoft.consts.UtilConsts;
import net.risesoft.entity.CalendarConfig;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.repository.jpa.CalendarConfigRepository;
import net.risesoft.service.CalendarConfigService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@Service(value = "calendarConfigService")
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
@Slf4j
public class CalendarConfigServiceImpl implements CalendarConfigService {

    @Autowired
    private CalendarConfigRepository calendarConfigRepository;

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> delCalendar(String startDate) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "删除成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            String[] str = startDate.split("-");
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(str[0]);
            if (calendarConfig != null) {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                String everyYearHoliday = calendarConfig.getEveryYearHoliday();

                List<String> workingDay2HolidayList = StringUtils.isNotBlank(workingDay2Holiday) ? new ArrayList<String>(Arrays.asList(workingDay2Holiday.split(","))) : new ArrayList<String>();
                List<String> weekend2WorkingDayList = StringUtils.isNotBlank(weekend2WorkingDay) ? new ArrayList<String>(Arrays.asList(weekend2WorkingDay.split(","))) : new ArrayList<String>();
                List<String> everyYearHolidayList = StringUtils.isNotBlank(everyYearHoliday) ? new ArrayList<String>(Arrays.asList(everyYearHoliday.split(","))) : new ArrayList<String>();
                // 删除休假
                if (workingDay2HolidayList.contains(startDate)) {
                    if (workingDay2HolidayList.contains(startDate)) {
                        // 若有，删除日期
                        workingDay2HolidayList = this.remove(workingDay2HolidayList, startDate);
                    }
                    if (!isWeekend(startDate)) {
                        // 如不是周末，删除休假，需从全年节假日期中删除
                        if (everyYearHolidayList.contains(startDate)) {
                            // 若有，删除日期
                            everyYearHolidayList = this.remove(everyYearHolidayList, startDate);
                        }
                    }
                }
                // 删除补班
                if (weekend2WorkingDayList.contains(startDate)) {
                    // 若有，删除补班日期
                    weekend2WorkingDayList = this.remove(weekend2WorkingDayList, startDate);
                    if (isWeekend(startDate)) {
                        // 如是周末，删除补班，需添加至全年节假日期中
                        if (!everyYearHolidayList.contains(startDate)) {
                            everyYearHolidayList.add(startDate);
                        }
                    } else {
                        // 如不是周末，删除补班，需从全年节假日期中删除
                        if (everyYearHolidayList.contains(startDate)) {
                            // 若有，删除日期
                            everyYearHolidayList = this.remove(everyYearHolidayList, startDate);
                        }
                    }
                }
                String workingDay2HolidayTemp = StringUtils.join(workingDay2HolidayList, ",");
                calendarConfig.setWorkingDay2Holiday(workingDay2HolidayTemp);

                String weekend2WorkingDayTemp = StringUtils.join(weekend2WorkingDayList, ",");
                calendarConfig.setWeekend2WorkingDay(weekend2WorkingDayTemp);

                calendarConfig.setEveryYearHoliday(StringUtils.join(everyYearHolidayList, ","));
                calendarConfigRepository.save(calendarConfig);
            }
        } catch (Exception e) {
            map.put("message", "删除失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public CalendarConfig findByYear(String year) {
        return calendarConfigRepository.findByYear(year);
    }

    @Override
    public List<Map<String, Object>> getCalendar(String month) {
        List<Map<String, Object>> resList = new ArrayList<Map<String, Object>>();
        try {
            String[] str = month.split("-");
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(str[0]);
            if (calendarConfig != null) {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                if (StringUtils.isNotBlank(weekend2WorkingDay)) {
                    String[] list = weekend2WorkingDay.split(",");
                    for (String date : list) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        map.put("date", date);
                        map.put("type", 2);
                        resList.add(map);
                    }
                }
                if (StringUtils.isNotBlank(workingDay2Holiday)) {
                    String[] list1 = workingDay2Holiday.split(",");
                    for (String date : list1) {
                        Map<String, Object> map = new HashMap<String, Object>(16);
                        map.put("date", date);
                        map.put("type", 1);
                        resList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return resList;
    }

    /**
     * 获取一年所有的周末
     * 
     * @return
     */
    public List<String> getYearHoliday(String years) {
        // 返回的日期集合
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        List<String> days = new ArrayList<String>();
        try {
            int year = Integer.valueOf(years);
            Calendar calendar = new GregorianCalendar(year, 0, 1);
            int i = 1;
            while (calendar.get(Calendar.YEAR) < year + 1) {
                calendar.set(Calendar.WEEK_OF_YEAR, i++);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                if (calendar.get(Calendar.YEAR) == year) {
                    LOGGER.info("星期天:{}", sdf.format(calendar.getTime()));
                    days.add(sdf.format(calendar.getTime()));
                }
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                if (calendar.get(Calendar.YEAR) == year) {
                    LOGGER.info("星期六:{}", sdf.format(calendar.getTime()));
                    days.add(sdf.format(calendar.getTime()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return days;
    }

    public boolean isWeekend(String date) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date bdate = sdf.parse(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bdate);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return true;
        } else {
            return false;
        }

    }

    public List<String> remove(List<String> list, String date) {
        Iterator<String> iterator = list.iterator();
        while (iterator.hasNext()) {
            String str = iterator.next();
            if (date.equals(str)) {
                iterator.remove();
            }
        }
        return list;
    }

    @Override
    @Transactional(readOnly = false)
    public Map<String, Object> saveCalendar(String startDate, Integer type) {
        Map<String, Object> map = new HashMap<String, Object>(16);
        map.put("message", "保存成功");
        map.put(UtilConsts.SUCCESS, true);
        try {
            String year = startDate.split("-")[0];
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(year);
            if (calendarConfig == null) {
                List<String> yearHoliday = new ArrayList<String>();
                yearHoliday = this.getYearHoliday(year);
                calendarConfig = new CalendarConfig();
                calendarConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                calendarConfig.setYear(String.valueOf(year));
                // 休假日期
                if (type == 1) {
                    calendarConfig.setWorkingDay2Holiday(startDate);
                    if (!yearHoliday.contains(startDate)) {
                        yearHoliday.add(startDate);
                    }
                } else {
                    // 补班日期
                    calendarConfig.setWeekend2WorkingDay(startDate);
                    if (yearHoliday.contains(startDate)) {
                        yearHoliday = this.remove(yearHoliday, startDate);
                    }
                }
                // 获取前一年的配置
                CalendarConfig calendarConfig0 = calendarConfigRepository.findByYear(String.valueOf((Integer.valueOf(year) - 1)));
                String yearHolidayStr = StringUtils.join(yearHoliday, ",");
                if (calendarConfig0 != null) {
                    // 每年休假日期累加,方便跨年计算
                    yearHolidayStr = calendarConfig0.getEveryYearHoliday() + yearHolidayStr;
                }
                calendarConfig.setEveryYearHoliday(yearHolidayStr);
                calendarConfigRepository.save(calendarConfig);
            } else {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                String everyYearHoliday = calendarConfig.getEveryYearHoliday();

                List<String> workingDay2HolidayList = StringUtils.isNotBlank(workingDay2Holiday) ? new ArrayList<String>(Arrays.asList(workingDay2Holiday.split(","))) : new ArrayList<String>();
                List<String> weekend2WorkingDayList = StringUtils.isNotBlank(weekend2WorkingDay) ? new ArrayList<String>(Arrays.asList(weekend2WorkingDay.split(","))) : new ArrayList<String>();
                List<String> everyYearHolidayList = StringUtils.isNotBlank(everyYearHoliday) ? new ArrayList<String>(Arrays.asList(everyYearHoliday.split(","))) : new ArrayList<String>();
                if (type == 1) {
                    // 休假
                    if (!workingDay2HolidayList.contains(startDate)) {
                        // 不包含则添加
                        workingDay2HolidayList.add(startDate);
                    }
                    if (!everyYearHolidayList.contains(startDate)) {
                        // 不包含则添加
                        everyYearHolidayList.add(startDate);
                    }
                    if (weekend2WorkingDayList.contains(startDate)) {
                        // 若有，删除补班日期
                        weekend2WorkingDayList = this.remove(weekend2WorkingDayList, startDate);
                    }
                } else {
                    // 补班，去除假日
                    if (!weekend2WorkingDayList.contains(startDate)) {
                        // 不包含则添加
                        weekend2WorkingDayList.add(startDate);
                    }
                    if (workingDay2HolidayList.contains(startDate)) {
                        // 若有，删除当前补班日期
                        workingDay2HolidayList = this.remove(workingDay2HolidayList, startDate);
                    }
                    if (everyYearHolidayList.contains(startDate)) {
                        // 若有，删除当前补班日期
                        everyYearHolidayList = this.remove(everyYearHolidayList, startDate);
                    }
                }

                String workingDay2HolidayTemp = StringUtils.join(workingDay2HolidayList, ",");
                calendarConfig.setWorkingDay2Holiday(workingDay2HolidayTemp);

                String weekend2WorkingDayTemp = StringUtils.join(weekend2WorkingDayList, ",");
                calendarConfig.setWeekend2WorkingDay(weekend2WorkingDayTemp);

                calendarConfig.setEveryYearHoliday(StringUtils.join(everyYearHolidayList, ","));
                calendarConfigRepository.save(calendarConfig);
            }
        } catch (Exception e) {
            map.put("message", "保存失败");
            map.put(UtilConsts.SUCCESS, false);
            e.printStackTrace();
        }
        return map;
    }

    @Override
    public void saveOrUpdate(CalendarConfig calendarConfig) {
        String id = calendarConfig.getId();
        if (StringUtils.isNotEmpty(id)) {
            CalendarConfig oldcc = calendarConfigRepository.findById(id).orElse(null);
            if (null != oldcc) {
                oldcc.setId(id);
                oldcc.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
                oldcc.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
                oldcc.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
                oldcc.setYear(calendarConfig.getYear());

                calendarConfigRepository.save(oldcc);
                return;
            } else {
                calendarConfigRepository.save(calendarConfig);
                return;
            }
        }

        CalendarConfig newcc = new CalendarConfig();
        newcc.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newcc.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
        newcc.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
        newcc.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
        newcc.setYear(calendarConfig.getYear());
        calendarConfigRepository.save(newcc);
        return;
    }
}
