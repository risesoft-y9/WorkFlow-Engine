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
                // 初始化配置列表
                CalendarConfigLists configLists = initializeConfigLists(calendarConfig);
                // 处理休假删除逻辑
                handleWorkingDayRemoval(configLists, startDate);
                // 处理补班删除逻辑
                handleWorkingDayAdditionRemoval(configLists, startDate);
                // 更新并保存配置
                updateCalendarConfig(calendarConfig, configLists);
            }
            return Y9Result.success("删除成功");
        } catch (Exception e) {
            LOGGER.error("删除失败", e);
            return Y9Result.failure("删除失败");
        }
    }

    /**
     * 初始化配置列表
     */
    private CalendarConfigLists initializeConfigLists(CalendarConfig calendarConfig) {
        CalendarConfigLists lists = new CalendarConfigLists();
        lists.workingDay2HolidayList = StringUtils.isNotBlank(calendarConfig.getWorkingDay2Holiday())
            ? new ArrayList<>(Arrays.asList(calendarConfig.getWorkingDay2Holiday().split(","))) : new ArrayList<>();
        lists.weekend2WorkingDayList = StringUtils.isNotBlank(calendarConfig.getWeekend2WorkingDay())
            ? new ArrayList<>(Arrays.asList(calendarConfig.getWeekend2WorkingDay().split(","))) : new ArrayList<>();
        lists.everyYearHolidayList = StringUtils.isNotBlank(calendarConfig.getEveryYearHoliday())
            ? new ArrayList<>(Arrays.asList(calendarConfig.getEveryYearHoliday().split(","))) : new ArrayList<>();
        return lists;
    }

    /**
     * 处理休假删除逻辑
     */
    private void handleWorkingDayRemoval(CalendarConfigLists configLists, String startDate) throws Exception {
        if (configLists.workingDay2HolidayList.contains(startDate)) {
            // 删除休假日期
            configLists.workingDay2HolidayList = this.remove(configLists.workingDay2HolidayList, startDate);
            // 如果不是周末，需要从全年节假日期中删除
            if (!isWeekend(startDate)) {
                if (configLists.everyYearHolidayList.contains(startDate)) {
                    configLists.everyYearHolidayList = this.remove(configLists.everyYearHolidayList, startDate);
                }
            }
        }
    }

    /**
     * 处理补班删除逻辑
     */
    private void handleWorkingDayAdditionRemoval(CalendarConfigLists configLists, String startDate) {
        if (configLists.weekend2WorkingDayList.contains(startDate)) {
            // 删除补班日期
            configLists.weekend2WorkingDayList = this.remove(configLists.weekend2WorkingDayList, startDate);

            try {
                // 根据是否为周末决定全年节假日的处理方式
                if (isWeekend(startDate)) {
                    // 是周末，删除补班需添加至全年节假日期中
                    if (!configLists.everyYearHolidayList.contains(startDate)) {
                        configLists.everyYearHolidayList.add(startDate);
                    }
                } else {
                    // 不是周末，删除补班需从全年节假日期中删除
                    if (configLists.everyYearHolidayList.contains(startDate)) {
                        configLists.everyYearHolidayList = this.remove(configLists.everyYearHolidayList, startDate);
                    }
                }
            } catch (Exception e) {
                LOGGER.error("判断日期类型失败", e);
            }
        }
    }

    /**
     * 更新并保存日历配置
     */
    private void updateCalendarConfig(CalendarConfig calendarConfig, CalendarConfigLists configLists) {
        calendarConfig.setWorkingDay2Holiday(StringUtils.join(configLists.workingDay2HolidayList, ","));
        calendarConfig.setWeekend2WorkingDay(StringUtils.join(configLists.weekend2WorkingDayList, ","));
        calendarConfig.setEveryYearHoliday(StringUtils.join(configLists.everyYearHolidayList, ","));
        calendarConfigRepository.save(calendarConfig);
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
                handleNewCalendarConfig(startDate, year, type);
            } else {
                handleExistingCalendarConfig(calendarConfig, startDate, type);
            }
            return Y9Result.success("保存成功");
        } catch (Exception e) {
            LOGGER.error("保存失败", e);
            return Y9Result.failure("保存失败");
        }
    }

    /**
     * 处理新的日历配置
     */
    private void handleNewCalendarConfig(String startDate, String year, Integer type) {
        List<String> yearHoliday = this.getYearHoliday(year);
        CalendarConfig calendarConfig = new CalendarConfig();
        calendarConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        calendarConfig.setYear(year);
        if (type == 1) {
            // 休假类型
            calendarConfig.setWorkingDay2Holiday(startDate);
            if (!yearHoliday.contains(startDate)) {
                yearHoliday.add(startDate);
            }
        } else {
            // 补班类型
            calendarConfig.setWeekend2WorkingDay(startDate);
            if (yearHoliday.contains(startDate)) {
                yearHoliday = this.remove(yearHoliday, startDate);
            }
        }
        // 处理全年节假日配置
        String yearHolidayStr = processYearHolidayConfiguration(year, yearHoliday);
        calendarConfig.setEveryYearHoliday(yearHolidayStr);
        calendarConfigRepository.save(calendarConfig);
    }

    /**
     * 处理已存在的日历配置
     */
    private void handleExistingCalendarConfig(CalendarConfig calendarConfig, String startDate, Integer type) {
        // 初始化配置列表
        CalendarConfigLists configLists = initializeConfigLists(calendarConfig);
        // 根据类型处理配置
        if (type == 1) {
            processHolidayType(configLists, startDate);
        } else {
            processWorkingDayType(configLists, startDate);
        }
        // 更新并保存配置
        updateCalendarConfig(calendarConfig, configLists);
    }

    /**
     * 处理休假类型配置
     */
    private void processHolidayType(CalendarConfigLists configLists, String startDate) {
        if (!configLists.workingDay2HolidayList.contains(startDate)) {
            configLists.workingDay2HolidayList.add(startDate);
        }
        if (!configLists.everyYearHolidayList.contains(startDate)) {
            configLists.everyYearHolidayList.add(startDate);
        }
        if (configLists.weekend2WorkingDayList.contains(startDate)) {
            configLists.weekend2WorkingDayList = this.remove(configLists.weekend2WorkingDayList, startDate);
        }
    }

    /**
     * 处理补班类型配置
     */
    private void processWorkingDayType(CalendarConfigLists configLists, String startDate) {
        if (!configLists.weekend2WorkingDayList.contains(startDate)) {
            configLists.weekend2WorkingDayList.add(startDate);
        }
        if (configLists.workingDay2HolidayList.contains(startDate)) {
            configLists.workingDay2HolidayList = this.remove(configLists.workingDay2HolidayList, startDate);
        }
        if (configLists.everyYearHolidayList.contains(startDate)) {
            configLists.everyYearHolidayList = this.remove(configLists.everyYearHolidayList, startDate);
        }
    }

    /**
     * 处理全年节假日配置
     */
    private String processYearHolidayConfiguration(String year, List<String> yearHoliday) {
        // 获取前一年的配置
        CalendarConfig previousYearConfig =
            calendarConfigRepository.findByYear(String.valueOf((Integer.parseInt(year) - 1)));
        String yearHolidayStr = StringUtils.join(yearHoliday, ",");
        // 每年休假日期累加,方便跨年计算
        if (previousYearConfig != null) {
            yearHolidayStr = previousYearConfig.getEveryYearHoliday() + yearHolidayStr;
        }
        return yearHolidayStr;
    }

    @Override
    public void saveOrUpdate(CalendarConfig calendarConfig) {
        String id = calendarConfig.getId();
        if (StringUtils.isNotEmpty(id)) {
            CalendarConfig existCalendarConfig = calendarConfigRepository.findById(id).orElse(null);
            if (null != existCalendarConfig) {
                existCalendarConfig.setId(id);
                existCalendarConfig.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
                existCalendarConfig.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
                existCalendarConfig.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
                existCalendarConfig.setYear(calendarConfig.getYear());
                calendarConfigRepository.save(existCalendarConfig);
            } else {
                calendarConfigRepository.save(calendarConfig);
            }
            return;
        }
        CalendarConfig newCalendarConfig = new CalendarConfig();
        newCalendarConfig.setId(Y9IdGenerator.genId(IdType.SNOWFLAKE));
        newCalendarConfig.setEveryYearHoliday(calendarConfig.getEveryYearHoliday());
        newCalendarConfig.setWeekend2WorkingDay(calendarConfig.getWeekend2WorkingDay());
        newCalendarConfig.setWorkingDay2Holiday(calendarConfig.getWorkingDay2Holiday());
        newCalendarConfig.setYear(calendarConfig.getYear());
        calendarConfigRepository.save(newCalendarConfig);
    }

    /**
     * 配置列表容器类
     */
    private static class CalendarConfigLists {
        List<String> workingDay2HolidayList;
        List<String> weekend2WorkingDayList;
        List<String> everyYearHolidayList;
    }
}
