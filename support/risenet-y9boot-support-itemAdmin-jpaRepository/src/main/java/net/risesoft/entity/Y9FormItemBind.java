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
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@JsonIgnoreProperties(value = {"hibernateLazyInitializer"})
@Entity
@Table(name = "FF_ITEM_Y9FORMBIND")
@org.hibernate.annotations.Table(comment = "Y9表单与事项流程任务绑定表", appliesTo = "FF_ITEM_Y9FORMBIND")
public class Y9FormItemBind implements Serializable {

    private static final long serialVersionUID = 7852048678955381044L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @Comment("表单ID")
    @Column(name = "FORMID", length = 38, nullable = false)
    private String formId;

    @Comment("表单名称")
    @Column(name = "FORMNAME", length = 55, nullable = false)
    private String formName;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

    @Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255, nullable = false)
    private String processDefinitionId;

    @Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 255)
    private String taskDefKey;

    @Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @Type(type = "numeric_boolean")
    @Comment("是否显示意见附件")
    @Column(name = "SHOWFILETAB")
    private boolean showFileTab = true;

    @Type(type = "numeric_boolean")
    @Comment("是否显示正文")
    @Column(name = "SHOWDOCUMENTTAB")
    private boolean showDocumentTab = true;

    @Type(type = "numeric_boolean")
    @Comment("是否显示关联文件")
    @Column(name = "SHOWHISTORYTAB")
    private boolean showHistoryTab = true;

    @Comment("事项名称")
    @Transient
    private String itemName;

    @Comment("流程定义名称")
    @Transient
    private String procDefName;

    @Comment("任务名称")
    @Transient
    private String taskDefName;

}
