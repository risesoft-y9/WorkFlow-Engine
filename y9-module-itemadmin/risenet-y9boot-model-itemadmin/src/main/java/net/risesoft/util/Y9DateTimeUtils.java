package net.risesoft.util;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;

/**
 * 日期时间工具类
 * 
 * @author qinman
 * @date 2025/10/14
 */
@Validated
public class Y9DateTimeUtils {
    public static final DateTimeFormatter FORMATTER_TIME_COMPACT = DateTimeFormatter.ofPattern("HHmmss");
    public static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter FORMATTER_DATE_COMPACT = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter FORMATTER_DATETIME = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final DateTimeFormatter FORMATTER_DATETIME_COMPACT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    public static final DateTimeFormatter FORMATTER_DATETIME_MINUTE = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    public static ZoneId shanghaiZone = ZoneId.of("Asia/Shanghai");

    /**
     * 格式化当前日期
     * 
     * @return yyyy-MM-dd
     */
    public static String formatCurrentDate() {
        return LocalDate.now().format(FORMATTER_DATE);
    }

    /**
     * 格式化当前日期
     * 
     * @return yyyyMMdd
     */
    public static String formatCurrentDateCompact() {
        return LocalDate.now().format(FORMATTER_DATE_COMPACT);
    }

    /**
     * 格式化当前日期时间
     * 
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatCurrentDateTime() {
        return LocalDateTime.now().format(FORMATTER_DATETIME);
    }

    /**
     * 格式化当前日期时间
     * 
     * @return yyyyMMddHHmmss
     */
    public static String formatCurrentDateTimeCompact() {
        return LocalDateTime.now().format(FORMATTER_DATETIME_COMPACT);
    }

    /**
     * 格式化当前时间
     * 
     * @return HHmmss
     */
    public static String formatCurrentTimeCompact() {
        return LocalDateTime.now().format(FORMATTER_TIME_COMPACT);
    }

    /**
     * 格式化指定日期
     * 
     * @param date 日期
     * @return yyyy-MM-dd
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(shanghaiZone).toLocalDate();
        return localDate.format(FORMATTER_DATE);
    }

    /**
     * 格式化指定日期时间
     *
     * @param date 日期
     * @return yyyy-MM-dd HH:mm:ss
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        LocalDateTime dateTime = date.toInstant().atZone(shanghaiZone).toLocalDateTime();
        return dateTime.format(FORMATTER_DATETIME);
    }

    /**
     * 格式化指定日期时间
     *
     * @param date 日期
     * @return yyyy-MM-dd HH:mm
     */
    public static String formatDateTimeMinute(Date date) {
        if (date == null) {
            return "";
        }
        LocalDateTime dateTime = date.toInstant().atZone(shanghaiZone).toLocalDateTime();
        return dateTime.format(FORMATTER_DATETIME_MINUTE);
    }

    /**
     * 解析日期字符串
     * 
     * @param dateStr yyyy-MM-dd
     * @return Date
     */
    public static Date parseDate(String dateStr) throws Exception {
        if (!StringUtils.hasText(dateStr)) {
            throw new Exception("解析的日期时间字符串不能为空");
        }
        LocalDate localDate = LocalDate.parse(dateStr, FORMATTER_DATE);
        return Date.from(localDate.atStartOfDay(shanghaiZone).toInstant());
    }

    // 解析日期时间字符串
    public static Date parseDateTime(String dateTimeStr) throws Exception {
        if (!StringUtils.hasText(dateTimeStr)) {
            throw new Exception("解析的日期时间字符串不能为空");
        }
        LocalDateTime localDateTime = LocalDateTime.parse(dateTimeStr, FORMATTER_DATETIME);
        return Date.from(localDateTime.atZone(shanghaiZone).toInstant());
    }

    public static String getYear(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return String.valueOf(localDate.getYear());
    }
}
