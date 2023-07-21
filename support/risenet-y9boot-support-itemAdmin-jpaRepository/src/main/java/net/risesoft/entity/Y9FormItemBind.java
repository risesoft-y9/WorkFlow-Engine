package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.GenericGenerator;

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
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    @org.hibernate.annotations.Comment("表单ID")
    @Column(name = "FORMID", length = 38, nullable = false)
    private String formId;

    @org.hibernate.annotations.Comment("表单名称")
    @Column(name = "FORMNAME", length = 55, nullable = false)
    private String formName;

    @org.hibernate.annotations.Comment("事项Id")
    @Column(name = "ITEMID", length = 55, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("流程定义Id")
    @Column(name = "PROCESSDEFINITIONID", length = 255, nullable = false)
    private String processDefinitionId;

    @org.hibernate.annotations.Comment("任务key")
    @Column(name = "TASKDEFKEY", length = 255)
    private String taskDefKey;

    @org.hibernate.annotations.Comment("排序号")
    @Column(name = "TABINDEX", length = 10)
    private Integer tabIndex;

    @org.hibernate.annotations.Comment("是否显示意见附件")
    @Column(name = "SHOWFILETAB")
    private boolean showFileTab = true;

    @org.hibernate.annotations.Comment("是否显示正文")
    @Column(name = "SHOWDOCUMENTTAB")
    private boolean showDocumentTab = true;

    @org.hibernate.annotations.Comment("是否显示关联文件")
    @Column(name = "SHOWHISTORYTAB")
    private boolean showHistoryTab = true;

    @org.hibernate.annotations.Comment("事项名称")
    @Transient
    private String itemName;

    @org.hibernate.annotations.Comment("流程定义名称")
    @Transient
    private String procDefName;

    @org.hibernate.annotations.Comment("任务名称")
    @Transient
    private String taskDefName;

}
