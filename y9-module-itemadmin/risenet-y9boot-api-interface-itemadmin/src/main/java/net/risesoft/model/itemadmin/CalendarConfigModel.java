package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 日历配置模型
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class CalendarConfigModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -5625830014785041762L;

    /**
     * 主键
     */
    private String id;

    /**
     * 工作日休假日期
     */
    private String workingDay2Holiday;

    /**
     * 周末补班日期
     */
    private String weekend2WorkingDay;

    /**
     * 全年节假日期，包括工作日休假，排除周末补班日期，存储多年的节假日
     */
    private String everyYearHoliday;

    /**
     * 年份
     */
    private String year;
}
