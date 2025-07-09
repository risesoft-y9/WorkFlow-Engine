package net.risesoft.entity.receive;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
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
@Comment("收发管理部门表")
@Table(name = "FF_RECEIVEDEPARTMENT")
public class ReceiveDepartment implements Serializable {
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
     * 委办局Id
     */
    @Comment("委办局Id")
    @Column(name = "BUREAUID", length = 50)
    private String bureauId;

    /**
     * 父节点Id
     */
    @Comment("父节点Id")
    @Column(name = "PARENTID", length = 50)
    private String parentId;

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

    @Comment("创建时间")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "CREATEDATE")
    private Date createDate;

    @Comment("x序号")
    @Column(name = "TABINDEX", length = 11)
    private Integer tabIndex;

}
