package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_AutoFormSequence")
@org.hibernate.annotations.Table(comment = "序列编号", appliesTo = "FF_AutoFormSequence")
public class AutoFormSequence implements Serializable {

    private static final long serialVersionUID = 500808565082122549L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("租户id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @org.hibernate.annotations.Comment("标签名称")
    @Column(name = "LABELNAME")
    private String labelName;

    @org.hibernate.annotations.Comment("序列值")
    @Column(name = "SEQUENCEVALUE")
    private Integer sequenceValue;

    @org.hibernate.annotations.Comment("字符值")
    @Column(name = "CHARACTERVALUE")
    private String characterValue;

    @org.hibernate.annotations.Comment("年份")
    @Column(name = "CALENDARYEAR")
    private Integer calendarYear;
}
