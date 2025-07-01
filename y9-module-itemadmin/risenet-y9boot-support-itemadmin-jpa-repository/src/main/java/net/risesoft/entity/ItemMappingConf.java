package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_ITEM_MAPPINGCONF")
@org.hibernate.annotations.Table(comment = "事项对接字段映射配置", appliesTo = "FF_ITEM_MAPPINGCONF")
public class ItemMappingConf implements Serializable {

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

    @Comment("生成时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
