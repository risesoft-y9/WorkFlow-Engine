package net.risesoft.entity;

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

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_RELATEDPROCESS")
@org.hibernate.annotations.Table(comment = "事项配置关联流程表", appliesTo = "FF_ITEM_RELATEDPROCESS")
public class RelatedProcess implements Serializable {

    private static final long serialVersionUID = 6372379519852059380L;

    @Id
    @Comment("主键")
    @Column(name = "ID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("租户Id")
    @Column(name = "TENANT_ID", length = 50)
    private String tenantId;

    @Comment("关联流程的事项Id")
    @Column(name = "PARENT_ITEMID", length = 55, nullable = false)
    private String parentItemId;

    @Comment("被关联的流程的事项ID")
    @Column(name = "ITEM_ID", length = 55, nullable = false)
    private String itemId;

    @Comment("被关联的流程的事项名称")
    @Column(name = "ITEM_NAME", length = 100, nullable = false)
    private String itemName;

    @Comment("创建时间")
    @Column(name = "CREATE_DATE", length = 50)
    private String createDate;

}
