package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_MAPPINGCONF")
@Comment("事项对接字段映射配置")
public class ItemMappingConf extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = 6023418927806462716L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    /**
     * 1为内部系统，2为外部系统
     */
    @Comment("对接系统类型")
    @Column(name = "SYSTYPE", length = 1, nullable = false)
    private String sysType = "2";

    @Comment("表名称")
    @Column(name = "TABLENAME", length = 50, nullable = false)
    private String tableName;

    @Comment("列名称")
    @Column(name = "COLUMNNAME", length = 50, nullable = false)
    private String columnName;

    /**
     * 内部系统为事项id，外部系统为自定义标识
     */
    @Comment("映射系统标识")
    @Column(name = "MAPPINGID", length = 50, nullable = false)
    private String mappingId;

    /**
     * sysType为1时使用
     */
    @Comment("映射表名称")
    @Column(name = "MAPPINGTABLENAME", length = 50)
    private String mappingTableName;

    @Comment("映射字段名")
    @Column(name = "MAPPINGNAME", length = 50, nullable = false)
    private String mappingName;
}
