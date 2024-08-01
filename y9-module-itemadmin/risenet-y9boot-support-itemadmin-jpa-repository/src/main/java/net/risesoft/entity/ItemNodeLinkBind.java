package net.risesoft.entity;

import java.io.Serializable;
import java.util.List;

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
@Table(name = "FF_ITEM_NODE_LINKBIND")
@org.hibernate.annotations.Table(comment = "事项节点链接绑定表", appliesTo = "FF_ITEM_NODE_LINKBIND")
public class ItemNodeLinkBind implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 7561318776920064470L;

    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 200, nullable = false)
    private String itemId;

    @Comment("链接id")
    @Column(name = "LINKID", length = 100)
    private String linkId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

    /**
     * 链接名称
     */
    @Transient
    private String linkName;

    /**
     * 链接地址
     */
    @Transient
    private String linkUrl;

    /**
     * 事项名称
     */
    @Transient
    private String itemName;

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

}
