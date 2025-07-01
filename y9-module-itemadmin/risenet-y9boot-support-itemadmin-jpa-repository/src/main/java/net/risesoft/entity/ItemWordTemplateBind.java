package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_ITEM_WORDTEMPLATE_BIND")
@org.hibernate.annotations.Table(comment = "事项正文模板绑定表", appliesTo = "FF_ITEM_WORDTEMPLATE_BIND")
public class ItemWordTemplateBind implements Serializable {

    private static final long serialVersionUID = -7420288864269881175L;

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

    @Comment("正文模板Id")
    @Column(name = "TEMPLATEID", length = 50)
    private String templateId;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    // 0未绑定，1已绑定
    @Comment("绑定状态")
    @Column(name = "BINDSTATUS")
    private Integer bindStatus = 0;

    @Comment("绑定值")
    @Column(name = "BINDVALUE", length = 100)
    private String bindValue;
}
