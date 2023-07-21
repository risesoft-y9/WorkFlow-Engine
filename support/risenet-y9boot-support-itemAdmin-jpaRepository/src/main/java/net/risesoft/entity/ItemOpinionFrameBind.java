package net.risesoft.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_ITEM_OPINIONFRAME")
@org.hibernate.annotations.Table(comment = "意见框任务绑定表", appliesTo = "FF_ITEM_OPINIONFRAME")
public class ItemOpinionFrameBind implements Serializable {

    private static final long serialVersionUID = 2858871622683426060L;

    /**
     * 意见框和流程定义节点绑定唯一标示
     */
    @org.hibernate.annotations.Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 租户Id
     */
    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 意见框唯一标示
     */
    @org.hibernate.annotations.Comment("意见框标识")
    @Column(name = "OPINIONFRAMEMARK", length = 50, nullable = false)
    private String opinionFrameMark;

    @org.hibernate.annotations.Comment("意见框名称")
    @Column(name = "OPINIONFRAMENAME", length = 100, nullable = false)
    private String opinionFrameName;

    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 200, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255, nullable = false)
    private String processDefinitionId;

    @org.hibernate.annotations.Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @org.hibernate.annotations.Comment("是否必签意见")
    @Column(name = "SIGNOPINION", length = 100)
    @ColumnDefault("true")
    private boolean signOpinion;

    /**
     * 流程定义节点key
     */
    @Transient
    private String taskDefName;

    /**
     * 角色名称
     */
    @Transient
    private String roleNames;

    /**
     * 角色Id集合
     */
    @Transient
    private List<String> roleIds;

    /**
     * 操作的人员的名称
     */
    @org.hibernate.annotations.Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 最后操作的人员的Id
     */
    @org.hibernate.annotations.Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 生成时间
     */
    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATEDATE")
    private String createDate;

    /**
     * 最后的修改时间
     */
    @org.hibernate.annotations.Comment("修改时间")
    @Column(name = "MODIFYDATE")
    private String modifyDate;
}
