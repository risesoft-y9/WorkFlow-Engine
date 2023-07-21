package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

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
@org.hibernate.annotations.Table(comment = "收发管理部门表", appliesTo = "FF_ReceiveDepartment")
@Table(name = "FF_ReceiveDepartment")
public class ReceiveDepartment implements Serializable {
    private static final long serialVersionUID = -8497612611429902341L;

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
     * 委办局Id
     */
    @org.hibernate.annotations.Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    /**
     * 父节点Id
     */
    @org.hibernate.annotations.Comment("父节点Id")
    @Column(name = "PARENTID", length = 50)
    private String parentId;

    /**
     * 收发部门Id
     */
    @org.hibernate.annotations.Comment("收发部门Id")
    @Column(name = "DEPTID", length = 50, nullable = false)
    private String deptId;

    /**
     * 收发部门名称
     */
    @org.hibernate.annotations.Comment("收发部门名称")
    @Column(name = "DEPTNAME", length = 200, nullable = false)
    private String deptName;

    @org.hibernate.annotations.Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @org.hibernate.annotations.Comment("x序号")
    @Column(name = "TABINDEX", length = 11)
    private Integer tabIndex;

}
