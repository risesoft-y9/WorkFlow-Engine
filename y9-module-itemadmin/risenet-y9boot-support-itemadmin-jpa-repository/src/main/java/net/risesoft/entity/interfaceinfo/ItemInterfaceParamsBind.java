package net.risesoft.entity.interfaceinfo;

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

import net.risesoft.entity.base.ItemAdminBaseEntity;
import net.risesoft.enums.ItemInterfaceTypeEnum;
import net.risesoft.persistence.ItemEnumConverter;

/**
 *
 * @author zhangchongjie
 * @date 2024/05/24
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_INTERFACE_PARAMS_BIND")
@Comment("事项接口参数绑定表")
public class ItemInterfaceParamsBind extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = -2886788624623913907L;

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

    @Comment("参数名称")
    @Column(name = "parameterName", length = 255)
    private String parameterName;

    @Comment("参数类型")
    @Column(name = "parameterType", length = 100)
    @Convert(converter = ItemEnumConverter.ItemInterfaceTypeEnumConverter.class)
    private ItemInterfaceTypeEnum parameterType = ItemInterfaceTypeEnum.PARAMS;

    @Comment("绑定类型") // 响应参数Response,请求参数Request
    @Column(name = "bindType", length = 50)
    @Convert(converter = ItemEnumConverter.ItemInterfaceTypeEnumConverter.class)
    private ItemInterfaceTypeEnum bindType = ItemInterfaceTypeEnum.INTERFACE_REQUEST;

    @Comment("表名称")
    @Column(name = "TABLENAME", length = 50)
    private String tableName;

    @Comment("表类型")
    @Column(name = "TABLETYPE", length = 20)
    private String tableType;

    @Comment("列名称")
    @Column(name = "COLUMNNAME", length = 100)
    private String columnName;
}
