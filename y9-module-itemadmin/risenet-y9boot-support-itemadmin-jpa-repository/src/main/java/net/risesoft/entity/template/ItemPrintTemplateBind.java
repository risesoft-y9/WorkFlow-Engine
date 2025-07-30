package net.risesoft.entity.template;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

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
@Table(name = "FF_ITEM_PRINTTEMPLATE_BIND")
@org.hibernate.annotations.Table(comment = "打印表单绑定表", appliesTo = "FF_ITEM_PRINTTEMPLATE_BIND")
public class ItemPrintTemplateBind implements Serializable {

    private static final long serialVersionUID = 6829462901084070306L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("模板ID")
    @Column(name = "TEMPLATEID", length = 38, nullable = false)
    private String templateId;

    @Comment("模板名称")
    @Column(name = "TEMPLATENAME", length = 100, nullable = false)
    private String templateName;

    @Comment("模板Url")
    @Column(name = "TEMPLATEURL", length = 100, nullable = true)
    private String templateUrl;

    /**
     * 模板类型，1为word模板,2为表单模板
     */
    @Comment("模板类型")
    @Column(name = "TEMPLATETYPE", length = 10, nullable = false)
    private String templateType;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

}
