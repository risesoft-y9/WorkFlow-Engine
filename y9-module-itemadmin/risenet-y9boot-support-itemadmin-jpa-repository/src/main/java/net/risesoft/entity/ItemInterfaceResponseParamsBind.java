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

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_INTERFACE_RESPONSEPARAMS_BIND")
@org.hibernate.annotations.Table(comment = "事项接口响应参数绑定表", appliesTo = "FF_ITEM_INTERFACE_RESPONSEPARAMS_BIND")
public class ItemInterfaceResponseParamsBind implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -6351147138280883815L;

    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("接口id")
    @Column(name = "INTERFACEID", length = 50)
    private String interfaceId;

    @Comment("表名称")
    @Column(name = "TABLENAME", length = 50, nullable = false)
    private String tableName;

    @Comment("列名称")
    @Column(name = "COLUMNNAME", length = 50, nullable = false)
    private String columnName;

    @Comment("参数id")
    @Column(name = "parameterId", length = 50)
    private String parameterId;

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

}
