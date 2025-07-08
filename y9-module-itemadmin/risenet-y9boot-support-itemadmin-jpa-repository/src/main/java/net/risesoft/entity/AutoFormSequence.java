package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 表单中有自增的序列编号，这个表用来保存表单中的序列号
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_AUTOFORMSEQUENCE")
@Comment("序列编号")
public class AutoFormSequence implements Serializable {

    private static final long serialVersionUID = 500808565082122549L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("标签名称")
    @Column(name = "LABELNAME")
    private String labelName;

    @Comment("序列值")
    @Column(name = "SEQUENCEVALUE")
    private Integer sequenceValue;

    @Comment("字符值")
    @Column(name = "CHARACTERVALUE")
    private String characterValue;

    @Comment("年份")
    @Column(name = "CALENDARYEAR")
    private Integer calendarYear;
}
