package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/21
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "act_de_model_new")
@Comment("流程设计模型信息表")
public class ActDeModel implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -163908227555530854L;

	@Id
	@Comment("主键")
	@Column(name = "id", length = 255, nullable = false)
	@GeneratedValue(generator = "uuid")
	@GenericGenerator(name = "uuid", strategy = "assigned")
	private String id;

	@Comment("模型名称")
	@Column(name = "name", length = 400)
	private String name;

	@Comment("模型key")
	@Column(name = "model_key", length = 400)
	private String modelKey;

	@Comment("描述")
	@Column(name = "description", length = 4000)
	private String description;

	@Comment("创建时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created", nullable = false)
	private Date created;

	@Comment("创建人")
	@Column(name = "created_by", length = 255)
	private String createdBy;

	@Comment("最后修改时间")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_updated")
	private Date lastUpdated;

	@Comment("最后修改人")
	@Column(name = "last_updated_by", length = 255)
	private String lastUpdatedBy;

	@Comment("版本")
	@Column(name = "version")
	private Integer version;

	@Comment("流程图数据")
	@Column(name = "modelByte")
	@Lob
	private byte[] modelByte;

	@Comment("租户id")
	@Column(name = "tenantId", length = 255)
	private String tenantId;

}