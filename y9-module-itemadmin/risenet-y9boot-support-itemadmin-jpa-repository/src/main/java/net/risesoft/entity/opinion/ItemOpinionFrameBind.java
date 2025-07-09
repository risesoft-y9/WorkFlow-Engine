package net.risesoft.entity.opinion;

import java.io.Serializable;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

import org.hibernate.annotations.ColumnDefault;
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
@Table(name = "FF_ITEM_OPINIONFRAME")
@Comment("意见框任务绑定表")
public class ItemOpinionFrameBind implements Serializable {

    private static final long serialVersionUID = 2858871622683426060L;

    /**
     * 意见框和流程定义节点绑定唯一标示
     */
    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
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
     * 意见框唯一标示
     */
    @Comment("意见框标识")
    @Column(name = "OPINIONFRAMEMARK", length = 50, nullable = false)
    private String opinionFrameMark;

    @Comment("意见框名称")
    @Column(name = "OPINIONFRAMENAME", length = 100, nullable = false)
    private String opinionFrameName;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 200, nullable = false)
    private String itemId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255, nullable = false)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否必签意见")
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
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 最后操作的人员的Id
     */
    @Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATEDATE")
    private String createDate;

    /**
     * 最后的修改时间
     */
    @Comment("修改时间")
    @Column(name = "MODIFYDATE")
    private String modifyDate;
}
