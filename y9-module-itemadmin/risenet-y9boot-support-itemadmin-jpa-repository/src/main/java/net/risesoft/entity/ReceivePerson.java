package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@Comment("收发管理人员表")
@Table(name = "FF_ReceivePerson")
public class ReceivePerson implements Serializable {
    private static final long serialVersionUID = -8497612611429902341L;

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
     * 收发部门Id
     */
    @Comment("收发部门Id")
    @Column(name = "DEPTID", length = 50, nullable = false)
    private String deptId;

    /**
     * 收发部门名称
     */
    @Comment("收发部门名称")
    @Column(name = "DEPTNAME", length = 200, nullable = false)
    private String deptName;

    /**
     * 收发人员Id
     */
    @Comment("收发人员Id")
    @Column(name = "PERSONID", length = 50)
    private String personId;

    @Comment("收发人员部门Id")
    @Column(name = "PERSONDEPTID", length = 50)
    private String personDeptId;

    @Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否可以发送")
    @Column(name = "SEND")
    private boolean send = true;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否可以接收")
    @Column(name = "RECEIVE")
    private boolean receive = true;
}
