package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_CANCEL_NUMBER")
@org.hibernate.annotations.Table(comment = "取消编号的表", appliesTo = "FF_CANCEL_NUMBER")
public class CancelNumber implements Serializable {

    private static final long serialVersionUID = -3873935047012829167L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("编号")
    @Column(name = "CONTENT", length = 100)
    private String content;

    /**
     * @see net.risesoft.enums.CancelNumberStatusEnum
     */
    @Comment("状态")
    @Column(name = "STATUS", length = 100)
    private Integer status;

    @Comment("编号人员")
    @Column(name = "USERID", length = 50)
    private String userId;

    @Comment("编号人员姓名")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 100)
    private String createTime;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 100)
    private String updateTime;
}