package net.risesoft.entity.form;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Entity
@Table(name = "Y9FORM_TABLE_FIELD")
@org.hibernate.annotations.Table(comment = "业务表字段定义", appliesTo = "Y9FORM_TABLE_FIELD")
@Accessors(chain = true)
@NoArgsConstructor
@Data
public class Y9TableField implements Serializable {

    private static final long serialVersionUID = -8257542480755687177L;

    @Id
    @Column(name = "ID", length = 38)
    @org.hibernate.annotations.Comment("主键")
    private String id;

    @Column(name = "FIELDNAME", length = 30)
    @org.hibernate.annotations.Comment("字段英文名称")
    private String fieldName;

    @Column(name = "OLDFIELDNAME", length = 30)
    @org.hibernate.annotations.Comment("旧字段英文名称")
    private String oldFieldName;

    @Column(name = "FIELDCNNAME", length = 30)
    @org.hibernate.annotations.Comment("字段中文名称")
    private String fieldCnName;

    @Column(name = "TABLEID", length = 50)
    @org.hibernate.annotations.Comment("表Id")
    private String tableId;

    @Column(name = "TABLENAME", length = 50)
    @org.hibernate.annotations.Comment("表名称")
    private String tableName;

    @Column(name = "FIELDTYPE", length = 50)
    @org.hibernate.annotations.Comment("字段类型")
    private String fieldType;

    @Column(name = "FIELDLENGTH")
    @org.hibernate.annotations.Comment("字段长度")
    private Integer fieldLength = 0;

    /**
     * // 1为是，0为否
     */
    @Column(name = "ISMAYNULL")
    @org.hibernate.annotations.Comment("是否允许为空")
    private Integer isMayNull = 1;

    @Column(name = "ISSYSTEMFIELD")
    @org.hibernate.annotations.Comment("是否系统字段")
    private Integer isSystemField = 0;

    /**
     * // -1未生成表字段，1为已经生成表字段
     */
    @Column(name = "STATE")
    @org.hibernate.annotations.Comment("字段状态")
    private Integer state = -1;

    @Column(name = "DISPLAYORDER")
    @org.hibernate.annotations.Comment("显示排序")
    private Integer displayOrder;

}
