package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_CALENDARCONFIG")
@Comment("日历配置表")
public class CalendarConfig implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 放假日期
     */
    @Comment("放假日期")
    @Column(name = "workingDay2Holiday", length = 2000)
    private String workingDay2Holiday;

    /**
     * 周末补班日期
     */
    @Comment("周末补班日期")
    @Column(name = "weekend2WorkingDay", length = 1000)
    private String weekend2WorkingDay;

    /**
     * 全年节假日期，包括工作日休假，排除周末补班日期，存储多年的节假日
     */
    @Comment("每年休假日期")
    @Lob
    @Column(name = "everyYearHoliday")
    private String everyYearHoliday;

    /**
     * 年份
     */
    @Comment("年份")
    @Column(name = "year", length = 10)
    private String year;

}
