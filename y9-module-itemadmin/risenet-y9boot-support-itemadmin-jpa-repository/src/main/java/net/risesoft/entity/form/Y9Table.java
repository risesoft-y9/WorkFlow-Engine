package net.risesoft.entity.form;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;
import net.risesoft.enums.ItemTableTypeEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Table(name = "Y9FORM_TABLE")
@Comment("业务表定义")
@NoArgsConstructor
@Data
public class Y9Table extends ItemAdminBaseEntity implements Serializable {
    private static final long serialVersionUID = 2788176269486752045L;

    @Id
    @Column(name = "ID", length = 38)
    @Comment("主键")
    private String id;

    @Column(name = "TABLENAME", length = 30)
    @Comment("表英文名称")
    private String tableName;

    @Column(name = "OLDTABLENAME", length = 30)
    @Comment("老表英文名称")
    private String oldTableName;

    @Column(name = "TABLECNNAME", length = 30)
    @Comment("表中文名称")
    private String tableCnName;

    @Column(name = "TABLEALIAS", length = 20)
    @Comment("表别名")
    private String tableAlias;

    @Column(name = "TABLEMEMO", length = 50)
    @Comment("表备注")
    private String tableMemo;

    @Column(name = "TABLETYPE")
    @Comment("表类型")
    @Convert(converter = ItemEnumConverter.ItemTableTypeEnumConverter.class)
    private ItemTableTypeEnum tableType = ItemTableTypeEnum.MAIN;

    @Column(name = "SYSTEMNAME", length = 50)
    @Comment("系统名称")
    private String systemName;

    @Column(name = "SYSTEMCNNAME", length = 50)
    @Comment("系统中文名称")
    private String systemCnName;
}