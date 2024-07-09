package net.risesoft.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.io.Serializable;

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
	@Column(name = "TENANTID", length = 50)
	private String tenantId;

	@Comment("主事项Id")
	@Column(name = "PARENTITEMID", length = 55, nullable = false)
	private String parentItemId;

	@Comment("事项Id")
	@Column(name = "ITEMID", length = 55, nullable = false)
	private String itemId;

	@Comment("事项名称")
	@Column(name = "ITEMNAME", length = 100, nullable = false)
	private String itemName;

	@Comment("生成时间")
	@Column(name = "CREATDATE", length = 50)
	private String creatDate;

}
