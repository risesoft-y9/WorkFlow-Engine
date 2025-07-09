package net.risesoft.entity.tab;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "FF_ITEM_TABBIND")
@Comment("页签与事项绑定表")
public class ItemTabBind implements Serializable {

    private static final long serialVersionUID = -1461950199809202921L;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
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
     * 页签唯一标示
     */
    @Comment("页签唯一标示")
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
    @Comment("事项Id")
    @Column(name = "ITEMID", length = 100, nullable = false)
    private String itemId;

    /**
     * 流程定义Id
     */
    @Comment("流程定义Id")
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
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 创建/修改的人员的唯一标示
     */
    @Comment("人员id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 序号
     */
    @Comment("序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    /**
     * 更新时间
     */
    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;
}
