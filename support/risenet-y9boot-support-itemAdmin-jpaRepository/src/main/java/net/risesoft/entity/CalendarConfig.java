package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
@Table(name = "FF_CalendarConfig")
@org.hibernate.annotations.Table(comment = "日历配置表", appliesTo = "FF_CalendarConfig")
public class CalendarConfig implements Serializable {

    private static final long serialVersionUID = -4235779237483037821L;

    /**
     * 主键
     */
    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 放假日期
     */
    @org.hibernate.annotations.Comment("放假日期")
    @Column(name = "workingDay2Holiday", length = 2000)
    private String workingDay2Holiday;

    /**
     * 周末补班日期
     */
    @org.hibernate.annotations.Comment("周末补班日期")
    @Column(name = "weekend2WorkingDay", length = 1000)
    private String weekend2WorkingDay;

    /**
     * 全年节假日期，包括工作日休假，排除周末补班日期，存储多年的节假日
     */
    @org.hibernate.annotations.Comment("每年休假日期")
    @Lob
    @Column(name = "everyYearHoliday")
    private String everyYearHoliday;

    /**
     * 年份
     */
    @org.hibernate.annotations.Comment("年份")
    @Column(name = "year", length = 10)
    private String year;

}
