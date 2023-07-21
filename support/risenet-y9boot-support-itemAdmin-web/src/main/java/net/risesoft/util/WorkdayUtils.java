package net.risesoft.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

/**
 * 工作日计算工具,该类主要用于计算两个日期之间的工作日期总数，或则计算多少个工作日之后的日期。 使用方法
 * 
 * <pre>
 * 1、首先设置法定工作日列表和法定假日列表 2、计算工作日总数 例如 WorkdayUtils workdayUtils = new WorkdayUtils();
 * workdayUtils.setlegalWorkday(legalWorkday); workdayUtils.setLegalHoliday(LegalHoliday); Date date =
 * workdayUtils.getWorkday(new Date(), 15);//计算15个工作日之后的日期 int count = workdayUtils.getWorkdayCount(new Date(),
 * format.parse("2013-08-13"));//计算两个日期之间的工作日总数
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public class WorkdayUtils {
    /**
     * @fields LEGAL_WORKDAY 法定工作日
     */
    public static final int LEGAL_WORKDAY = 1;
    /**
     * @fields LEGAL_HOLIDAY 法定节假日
     */
    public static final int LEGAL_HOLIDAY = 2;
    /**
     * @fields WORKDAY 普通工作日
     */
    public static final int WORKDAY = 3;
    /**
     * @fields HOLIDAY 普通假日
     */
    public static final int HOLIDAY = 4;

    public static void main(String[] args) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        WorkdayUtils workdayUtils = new WorkdayUtils();
        Date date = workdayUtils.getWorkday(new Date(), 15);
        System.out.println(format.format(date));
        System.out.println(workdayUtils.getWorkdayCount(new Date(), format.parse("2013-08-13")));
    }

    /**
     * @fields 日期格式化类型，默认是“yyyy-MM-dd”
     */
    private String datePattern = "yyyy-MM-dd";
    /**
     * @fields legalWorkday 法定工作日列表，日期格式 yyyy-MM-dd
     */
    private String legalWorkday;

    /**
     * @fields LegalHoliday 法定节假日列表，日期格式yyyy-MM-dd
     */
    private String legalHoliday;

    public WorkdayUtils() {
        super();
    }

    /**
     * @description 构造方法
     * @param legalWorkday 法定工作日列表
     * @param LegalHoliday 法定假日列表
     */
    public WorkdayUtils(String legalWorkday, String legalHoliday) {
        super();
        this.legalWorkday = legalWorkday;
        this.legalHoliday = legalHoliday;
    }

    /**
     * @description 构造方法
     * @param legalWorkday 法定工作日列表
     * @param LegalHoliday 法定假日列表
     * @param datePattern 日期模式，默认为“yyyy-MM-dd”
     */
    public WorkdayUtils(String legalWorkday, String legalHoliday, String datePattern) {
        super();
        this.legalWorkday = legalWorkday;
        this.legalHoliday = legalHoliday;
        this.datePattern = datePattern;
    }

    /**
     * 断言指定的对象不为null，如果为null则抛出IllegalArgumentException异常
     * 
     * @param obj
     */
    private void assertNotNull(Object obj) {
        if (obj == null) {
            throw new IllegalArgumentException("The date must not be null!");
        }
    }

    /**
     * 获取日期格式化类型，默认为“yyyy-MM-dd”
     * 
     * @return String
     */
    public String getDatePattern() {
        return datePattern;
    }

    /**
     * 获取日类型，日类型包括法定工作日、法定节假日、普通工作日、普通假日。
     * 
     * @param date
     * @return int 日类型
     */
    public int getDayType(Date date) {
        assertNotNull(date);
        String dateStr = DateFormatUtils.format(date, this.datePattern);
        if (legalWorkday != null && !legalWorkday.isEmpty()) {
            if (legalWorkday.contains(dateStr)) {
                return LEGAL_WORKDAY;
            }
        }
        if (legalHoliday != null && !legalHoliday.isEmpty()) {
            if (legalHoliday.contains(dateStr)) {
                return LEGAL_HOLIDAY;
            }
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int day = c.get(Calendar.DAY_OF_WEEK);
        if (Calendar.MONDAY <= day && day <= Calendar.FRIDAY) {
            return WORKDAY;
        }
        return HOLIDAY;
    }

    /**
     * 获取临近的工作日
     * 
     * @param date 日期，不能为null
     * @param neighbour 如果为1表示下一个工作日，-1表示上一个工作日
     * @return Date 新的日期对象
     */
    private Date getNeighbourWorkday(Date date, int neighbour) {
        assertNotNull(date);
        Date nextWorkday = date;
        do {
            nextWorkday = DateUtils.addDays(nextWorkday, neighbour);
        } while (!isWorkday(nextWorkday));
        return nextWorkday;
    }

    /**
     * 获取指定日期之后的下一个工作日，该方法会返回一个新的日期对象
     * 
     * @param date Date 不能为null
     * @return Date 下个一个工作日
     */
    public Date getNextWorkday(Date date) {
        return getNeighbourWorkday(date, 1);
    }

    /**
     * 获取指定日期之后的上一个工作日，该方法会返回一个新的日期对象
     * 
     * @param date Date 不能为null
     * @return Date 上一个工作日
     */
    public Date getPrevWorkday(Date date) {
        return getNeighbourWorkday(date, -1);
    }

    /**
     * 获取多少个工作日之后的工作日期
     * 
     * @param date 指定日期,不能为null
     * @param amount 多少个工作日,如果为负数则表示多少工作日之前的工作日，为正数则表示多少个工作日之后的工作日
     * @return Date 新的日期对象
     */
    public Date getWorkday(Date date, int amount) {
        assertNotNull(date);
        Date workday = date;
        if (amount > 0) {
            for (int i = 0; i < amount; i++) {
                workday = getNextWorkday(workday);
            }
        }
        if (amount < 0) {
            for (int i = amount; i < 0; i++) {
                workday = getPrevWorkday(workday);
            }
        }
        return workday;
    }

    /**
     * 获取指定日期之间的工作日总数
     * 
     * @param startDate 开始日期不能为null
     * @param endDate 结束日期不能为null，结束日期必须大于等于开始日期
     * @return 工作日天数
     */
    public int getWorkdayCount(Date startDate, Date endDate) {
        assertNotNull(startDate);
        assertNotNull(endDate);
        int count = 0;
        if (DateUtils.isSameDay(startDate, endDate)) {
            if (isWorkday(startDate)) {
                count = 1;
            }
            return count;
        }
        if (endDate.before(startDate)) {
            throw new IllegalArgumentException("The endDate must be greater than startDate");
        }
        Date day = startDate;
        while (!DateUtils.isSameDay(day, endDate)) {
            day = DateUtils.addDays(day, 1);
            if (isWorkday(day)) {
                count++;
            }
        }
        return count;
    }

    /**
     * 判断给定的日期是否是工作日或法定工作日
     * 
     * @param date 日期不能为null
     * @return 如果是工作日或法定工作日则返回true，否则返回false
     */
    public boolean isWorkday(Date date) {
        int dayType = getDayType(date);
        if (dayType == LEGAL_WORKDAY || dayType == WORKDAY) {
            return true;
        }
        return false;
    }

    /**
     * 设置日期格式化类型，如果参数为空或null则使用默认格式“yyyy-MM-dd”
     * 
     * @param datePattern
     */
    public void setDatePattern(String datePattern) {
        if (StringUtils.isNotBlank(datePattern)) {
            this.datePattern = datePattern;
        }
    }

    /**
     * 设置法定节假日列表
     * 
     * @param LegalHoliday
     */
    public void setLegalHoliday(String legalHoliday) {
        this.legalHoliday = legalHoliday;
    }

    /**
     * 设置法定工作日列表
     * 
     * @param legalWorkday
     */
    public void setLegalWorkday(String legalWorkday) {
        this.legalWorkday = legalWorkday;
    }
}
