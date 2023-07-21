package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

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
@Table(name = "FF_ITEM_TABBIND")
@org.hibernate.annotations.Table(comment = "页签与事项绑定表", appliesTo = "FF_ITEM_TABBIND")
public class ItemTabBind implements Serializable {

    private static final long serialVersionUID = -1461950199809202921L;

    /**
     * 唯一标示
     */
    @Id
    @org.hibernate.annotations.Comment("主键")
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
     * 页签唯一标示
     */
    @org.hibernate.annotations.Comment("页签唯一标示")
    @Column(name = "TABID", length = 38, nullable = false)
    private String tabId;

    /**
     * 页签名称
     */
    @Transient
    private String tabName;

    /**
     * 事项Id
     */
    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 100, nullable = false)
    private String itemId;

    /**
     * 流程定义Id
     */
    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100, nullable = false)
    private String processDefinitionId;

    /**
     * 页签url
     */
    @Transient
    private String tabUrl;

    /**
     * 创建/修改人员的名称
     */
    @org.hibernate.annotations.Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 创建/修改的人员的唯一标示
     */
    @org.hibernate.annotations.Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 序号
     */
    @org.hibernate.annotations.Comment("序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    /**
     * 生成时间
     */
    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    /**
     * 更新时间
     */
    @org.hibernate.annotations.Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
