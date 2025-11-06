package net.risesoft.entity.documentword;

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

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_WORD_CONF")
@Comment("事项正文组件权限配置")
public class ItemWordConf extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = -3985582253198013194L;

    @Id
    @Comment("主键")
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 100, nullable = false)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 100)
    private String taskDefKey;

    @Comment("流程节点名称")
    @Column(name = "TASKDEFNAME", length = 100)
    private String taskDefName;

    @Comment("正文类型")
    @Column(name = "WORDTYPE", length = 50)
    private String wordType;

    @Comment("角色ids")
    @Column(name = "ROLEIDS", length = 500)
    private String roleIds;

    /**
     * 绑定的角色名称
     */
    @Transient
    private String roleNames;
}
