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
@Table(name = "FF_ORGANWORD_DETAIL")
@org.hibernate.annotations.Table(comment = "公文编号详细表", appliesTo = "FF_ORGANWORD_DETAIL")
public class OrganWordDetail implements Serializable {

    private static final long serialVersionUID = -223673649868267898L;

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
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 是否独立的,独立的话保存当前事项的Id，公共的值为common
     */
    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    /**
     * 机关代字标识
     */
    @Comment("机关代字标识")
    @Column(name = "CUSTOM", length = 50, nullable = false)
    private String custom;

    /**
     * 机关代字
     */
    @Comment("机关代字")
    @Column(name = "CHARACTERVALUE", length = 50, nullable = false)
    private String characterValue;

    /**
     * 文号年份
     */
    @Comment("文号年份")
    @Column(name = "YEARS", length = 10, nullable = false)
    private Integer year;

    /**
     * 当前值
     */
    @Comment("当前值")
    @Column(name = "CURRENTNUMBER", length = 10, nullable = false)
    private Integer number;

    /**
     * 生成、修改时间
     */
    @Comment("修改时间")
    @Column(name = "CREATETIME", nullable = false)
    private String createTime;

}
