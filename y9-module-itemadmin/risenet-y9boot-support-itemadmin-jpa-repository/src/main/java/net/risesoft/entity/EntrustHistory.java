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
@Table(name = "FF_Entrust_History")
@org.hibernate.annotations.Table(comment = "出差委托历史表", appliesTo = "FF_Entrust_History")
public class EntrustHistory implements Serializable {

    private static final long serialVersionUID = 5575438162078024036L;

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
    @Comment("受托人Id")
    @Column(name = "ASSIGNEEID", length = 100, nullable = false)
    private String assigneeId;

    /**
     * 委托对象姓名
     */
    @Comment("受托人姓名")
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
     * 委托事项的生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATTIME", nullable = false)
    private String creatTime;

    /**
     * 委托事项编辑时间
     */
    @Comment("编辑时间")
    @Column(name = "updateTime", nullable = false)
    private String updateTime;
}
