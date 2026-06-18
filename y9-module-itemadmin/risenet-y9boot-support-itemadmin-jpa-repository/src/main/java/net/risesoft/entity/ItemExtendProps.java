package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
@Table(name = "FF_ITEM_EXTENDPROPS")
@Comment("事项扩展属性信息表")
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

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否显示意见附件")
    @Column(name = "SHOWFILETAB")
    private boolean showFileTab = false;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否显示正文")
    @Column(name = "SHOWDOCUMENTTAB")
    private boolean showDocumentTab = false;

    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否显示关联流程")
    @Column(name = "SHOWHISTORYTAB")
    private boolean showHistoryTab = false;
}