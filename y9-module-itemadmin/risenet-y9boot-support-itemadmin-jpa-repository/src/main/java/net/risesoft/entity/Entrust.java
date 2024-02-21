package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "FF_Entrust")
@org.hibernate.annotations.Table(comment = "出差委托表", appliesTo = "FF_Entrust")
public class Entrust implements Serializable {

    private static final long serialVersionUID = 6623504879552636137L;

    public static final String ITEMID4ALL = "ALL";

    public static final String ITEMNAME4ALL = "所有事项";

    /**
     * 未使用
     */
    public static final Integer NOTUSED = 0;
    /**
     * 使用中
     */
    public static final Integer USING = 1;
    /**
     * 已使用
     */
    public static final Integer USED = 2;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 委托人Id
     */
    @Comment("委托人Id")
    @Column(name = "OWNERID", length = 100, nullable = false)
    private String ownerId;

    /**
     * 委托人姓名
     */
    @Comment("委托人姓名")
    @Transient
    private String ownerName;

    /**
     * 委托对象Id
     */
    @Comment("委托对象Id")
    @Column(name = "ASSIGNEEID", length = 100, nullable = false)
    private String assigneeId;

    /**
     * 委托对象姓名
     */
    @Comment("委托对象姓名")
    @Transient
    private String assigneeName;

    /**
     * 事项Id
     */
    @Comment("事项Id")
    @Column(name = "ITEMID", nullable = false)
    private String itemId;

    /**
     * 事项名称
     */
    @Comment("事项名称")
    @Transient
    private String itemName;

    /**
     * 委托开始时间
     */
    @Comment("委托开始时间")
    @Column(name = "STARTTIME", nullable = false, length = 30)
    private String startTime;

    /**
     * 委托结束时间
     */
    @Comment("委托结束时间")
    @Column(name = "ENDTIME", nullable = false, length = 30)
    private String endTime;

    /**
     * 是否已经使用,判断依据是委托的开始时间小于系统当前时间
     */
    @Comment("是否已经使用")
    @Transient
    private Integer used;

    /**
     * 委托事项的生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATTIME", nullable = false)
    private String creatTime;

    /**
     * 委托事项编辑时间
     */
    @Comment("编辑时间")
    @Column(name = "UPDATETIME", nullable = false)
    private String updateTime;
}
