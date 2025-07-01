package net.risesoft.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CalendarConfigService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/calendar", produces = MediaType.APPLICATION_JSON_VALUE)
public class CalendarRestController {

    private final CalendarConfigService calendarConfigService;

    /**
     * 删除日历配置
     *
     * @param startDate 日期
     * @return Y9Result<String>
     */
    @PostMapping(value = "/delCalendar")
    public Y9Result<String> delCalendar(@RequestParam String startDate) {
        return calendarConfigService.delCalendar(startDate);
    }

    /**
     * 获取日历配置
     *
     * @param month 月份
     * @return Y9Result<List<Map<String, Object>>>
     */
    @GetMapping(value = "/getCalendar")
    public Y9Result<List<Map<String, Object>>> getCalendar(@RequestParam String month) {
        List<Map<String, Object>> list = calendarConfigService.listCalendar(month);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存日历配置
     *
     * @param startDate 日期
     * @param type 类型
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveCalendar")
    public Y9Result<String> saveCalendar(@RequestParam String startDate, @RequestParam Integer type) {
        return calendarConfigService.saveCalendar(startDate, type);
    }
}
