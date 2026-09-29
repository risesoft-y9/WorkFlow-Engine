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

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @date 2026/07/09
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_SYSTEM")
@org.hibernate.annotations.Table(comment = "事项系统定义信息表", appliesTo = "FF_ITEM_SYSTEM")
public class ItemSystem extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = 378481509477193522L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("系统名称")
    @Column(name = "NAME", length = 200, nullable = false, unique = true)
    private String name;

    @Comment("系统中文名称")
    @Column(name = "CNNAME", length = 50, nullable = false, unique = true)
    private String cnName;

    @Comment("排序")
    @Column(name = "TABINDEX")
    private Integer tabIndex;
}