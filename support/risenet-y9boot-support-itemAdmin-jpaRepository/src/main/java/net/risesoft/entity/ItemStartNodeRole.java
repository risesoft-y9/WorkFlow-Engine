package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_ITEM_STARTNODE_ROLE")
@org.hibernate.annotations.Table(comment = "事项和按钮绑定关系对应的角色", appliesTo = "FF_ITEM_STARTNODE_ROLE")
public class ItemStartNodeRole implements Serializable {

    private static final long serialVersionUID = 5753820699147405666L;

    @org.hibernate.annotations.Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 100, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100, nullable = false)
    private String processDefinitionId;

    @org.hibernate.annotations.Comment("流程节点Key")
    @Column(name = "TASKDEFKEY", length = 100, nullable = false)
    private String taskDefKey;

    @org.hibernate.annotations.Comment("流程节点名称")
    @Column(name = "TASKDEFNAME", length = 100)
    private String taskDefName;

    @org.hibernate.annotations.Comment("序号")
    @Column(name = "TABINDEX", length = 3)
    private Integer tabIndex;

    @org.hibernate.annotations.Comment("角色Id")
    @Column(name = "ROLEIDS", length = 500)
    private String roleIds;

    @org.hibernate.annotations.Comment("创建人名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    @org.hibernate.annotations.Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;
}