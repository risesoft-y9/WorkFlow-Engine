package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

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
@Table(name = "FF_ITEM_EXTENDPROPS")
@org.hibernate.annotations.Table(comment = "事项扩展属性信息表", appliesTo = "FF_ITEM_EXTENDPROPS")
public class ItemExtendProps implements Serializable {

    private static final long serialVersionUID = 2822386808572556307L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项id")
    @Column(name = "ITEMID", length = 100, nullable = false)
    private String itemId;

    @Type(type = "numeric_boolean")
    @Comment("是否显示意见附件")
    @Column(name = "SHOWFILETAB")
    private boolean showFileTab = false;

    @Type(type = "numeric_boolean")
    @Comment("是否显示正文")
    @Column(name = "SHOWDOCUMENTTAB")
    private boolean showDocumentTab = false;

    @Type(type = "numeric_boolean")
    @Comment("是否显示关联流程")
    @Column(name = "SHOWHISTORYTAB")
    private boolean showHistoryTab = false;
}