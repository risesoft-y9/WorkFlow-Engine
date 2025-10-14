package net.risesoft.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.entity.CalendarConfig;
import net.risesoft.id.IdType;
import net.risesoft.id.Y9IdGenerator;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.CalendarConfigRepository;
import net.risesoft.service.CalendarConfigService;
import net.risesoft.util.Y9DateTimeUtils;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(value = "rsTenantTransactionManager", readOnly = true)
public class CalendarConfigServiceImpl implements CalendarConfigService {

    private final CalendarConfigRepository calendarConfigRepository;

    @Override
    @Transactional
    public Y9Result<String> delCalendar(String startDate) {
        try {
            String[] str = startDate.split("-");
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(str[0]);
            if (calendarConfig != null) {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                String everyYearHoliday = calendarConfig.getEveryYearHoliday();

                List<String> workingDay2HolidayList = StringUtils.isNotBlank(workingDay2Holiday)
                    ? new ArrayList<>(Arrays.asList(workingDay2Holiday.split(","))) : new ArrayList<>();
                List<String> weekend2WorkingDayList = StringUtils.isNotBlank(weekend2WorkingDay)
                    ? new ArrayList<>(Arrays.asList(weekend2WorkingDay.split(","))) : new ArrayList<>();
                List<String> everyYearHolidayList = StringUtils.isNotBlank(everyYearHoliday)
                    ? new ArrayList<>(Arrays.asList(everyYearHoliday.split(","))) : new ArrayList<>();
                /*
                 * 删除休假
                 */
                if (workingDay2HolidayList.contains(startDate)) {
                    if (workingDay2HolidayList.contains(startDate)) {
                        /*
                         * 若有，删除日期
                         */
                        workingDay2HolidayList = this.remove(workingDay2HolidayList, startDate);
                    }
                    /*
                     * 如不是周末，删除休假，需从全年节假日期中删除
                     */
                    if (!isWeekend(startDate)) {
                        if (everyYearHolidayList.contains(startDate)) {
                            /*
                             * 若有，删除日期
                             */
                            everyYearHolidayList = this.remove(everyYearHolidayList, startDate);
                        }
                    }
                }
                /*
                 * 删除补班
                 */
                if (weekend2WorkingDayList.contains(startDate)) {
                    /*
                     * 若有，删除补班日期
                     */
                    weekend2WorkingDayList = this.remove(weekend2WorkingDayList, startDate);
                    /*
                     * 如是周末，删除补班，需添加至全年节假日期中
                     */
                    if (isWeekend(startDate)) {
                        if (!everyYearHolidayList.contains(startDate)) {
                            everyYearHolidayList.add(startDate);
                        }
                    } else {
                        /*
                         * 如不是周末，删除补班，需从全年节假日期中删除
                         */
                        if (everyYearHolidayList.contains(startDate)) {
                            /*
                             * 若有，删除日期
                             */
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
            return Y9Result.success("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
            return Y9Result.failure("删除失败");
        }
    }

    @Override
    public CalendarConfig findByYear(String year) {
        return calendarConfigRepository.findByYear(year);
    }

    /**
     * 获取一年所有的周末
     *
     * @return List<String>
     */
    public List<String> getYearHoliday(String years) {
        // 返回的日期集合
        List<String> days = new ArrayList<>();
        try {
            int year = Integer.parseInt(years);
            Calendar calendar = new GregorianCalendar(year, Calendar.JANUARY, 1);
            int i = 1;
            while (calendar.get(Calendar.YEAR) < year + 1) {
                calendar.set(Calendar.WEEK_OF_YEAR, i++);
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
                if (calendar.get(Calendar.YEAR) == year) {
                    LOGGER.info("星期天:{}", Y9DateTimeUtils.formatDate(calendar.getTime()));
                    days.add(Y9DateTimeUtils.formatDate(calendar.getTime()));
                }
                calendar.set(Calendar.DAY_OF_WEEK, Calendar.SATURDAY);
                if (calendar.get(Calendar.YEAR) == year) {
                    LOGGER.info("星期六:{}", Y9DateTimeUtils.formatDate(calendar.getTime()));
                    days.add(Y9DateTimeUtils.formatDate(calendar.getTime()));
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取一年所有的周末失败", e);
        }
        return days;
    }

    public boolean isWeekend(String date) throws Exception {
        Date bdate = Y9DateTimeUtils.parseDate(date);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bdate);
        return cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }

    @Override
    public List<Map<String, Object>> listCalendar(String month) {
        List<Map<String, Object>> resList = new ArrayList<>();
        try {
            String[] str = month.split("-");
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(str[0]);
            if (calendarConfig != null) {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                if (StringUtils.isNotBlank(weekend2WorkingDay)) {
                    String[] list = weekend2WorkingDay.split(",");
                    for (String date : list) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("date", date);
                        map.put("type", 2);
                        resList.add(map);
                    }
                }
                if (StringUtils.isNotBlank(workingDay2Holiday)) {
                    String[] list1 = workingDay2Holiday.split(",");
                    for (String date : list1) {
                        Map<String, Object> map = new HashMap<>(16);
                        map.put("date", date);
                        map.put("type", 1);
                        resList.add(map);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.error("获取日历失败", e);
        }
        return resList;
    }

    public List<String> remove(List<String> list, String date) {
        list.removeIf(date::equals);
        return list;
    }

    @Override
    @Transactional
    public Y9Result<String> saveCalendar(String startDate, Integer type) {
        try {
            String year = startDate.split("-")[0];
            CalendarConfig calendarConfig = calendarConfigRepository.findByYear(year);
            if (calendarConfig == null) {
                List<String> yearHoliday;
                yearHoliday = this.getYearHoliday(year);
                calendarConfig = new CalendarConfig();
                calendarConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
                calendarConfig.setYear(String.valueOf(year));
                if (type == 1) {
                    calendarConfig.setWorkingDay2Holiday(startDate);
                    if (!yearHoliday.contains(startDate)) {
                        yearHoliday.add(startDate);
                    }
                } else {
                    calendarConfig.setWeekend2WorkingDay(startDate);
                    if (yearHoliday.contains(startDate)) {
                        yearHoliday = this.remove(yearHoliday, startDate);
                    }
                }
                // 获取前一年的配置
                CalendarConfig calendarConfig0 =
                    calendarConfigRepository.findByYear(String.valueOf((Integer.parseInt(year) - 1)));
                String yearHolidayStr = StringUtils.join(yearHoliday, ",");
                /*
                 * 每年休假日期累加,方便跨年计算
                 */
                if (calendarConfig0 != null) {
                    yearHolidayStr = calendarConfig0.getEveryYearHoliday() + yearHolidayStr;
                }
                calendarConfig.setEveryYearHoliday(yearHolidayStr);
                calendarConfigRepository.save(calendarConfig);
            } else {
                String weekend2WorkingDay = calendarConfig.getWeekend2WorkingDay();
                String workingDay2Holiday = calendarConfig.getWorkingDay2Holiday();
                String everyYearHoliday = calendarConfig.getEveryYearHoliday();

                List<String> workingDay2HolidayList = StringUtils.isNotBlank(workingDay2Holiday)
                    ? new ArrayList<>(Arrays.asList(workingDay2Holiday.split(","))) : new ArrayList<>();
                List<String> weekend2WorkingDayList = StringUtils.isNotBlank(weekend2WorkingDay)
                    ? new ArrayList<>(Arrays.asList(weekend2WorkingDay.split(","))) : new ArrayList<>();
                List<String> everyYearHolidayList = StringUtils.isNotBlank(everyYearHoliday)
                    ? new ArrayList<>(Arrays.asList(everyYearHoliday.split(","))) : new ArrayList<>();
                /*
                 * 休假
                 */
                if (type == 1) {
                    if (!workingDay2HolidayList.contains(startDate)) {
                        workingDay2HolidayList.add(startDate);
                    }
                    if (!everyYearHolidayList.contains(startDate)) {
                        everyYearHolidayList.add(startDate);
                    }
                    if (weekend2WorkingDayList.contains(startDate)) {
                        weekend2WorkingDayList = this.remove(weekend2WorkingDayList, startDate);
                    }
                } else {
                    if (!weekend2WorkingDayList.contains(startDate)) {
                        weekend2WorkingDayList.add(startDate);
                    }
                    if (workingDay2HolidayList.contains(startDate)) {
                        workingDay2HolidayList = this.remove(workingDay2HolidayList, startDate);
                    }
                    if (everyYearHolidayList.contains(startDate)) {
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
            return Y9Result.success("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            return Y9Result.failure("保存失败");
        }
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
            } else {
                calendarConfigRepository.save(calendarConfig);
            }
            return;
        }

        CalendarConfig newcc = new CalendarConfig();
        newcc.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newcc.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
        newcc.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
        newcc.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
        newcc.setYear(calendarConfig.getYear());
        calendarConfigRepository.save(newcc);
    }
}
