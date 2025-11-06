package net.risesoft.entity.interfaceinfo;

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
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_INTERFACE_REQUEST_PARAMS")
@Comment("接口请求参数信息表")
public class InterfaceRequestParams extends ItemAdminBaseEntity implements Serializable {

    private static final long serialVersionUID = 8522345464974859818L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("接口id")
    @Column(name = "interfaceId", length = 50)
    private String interfaceId;

    @Comment("参数类型")
    @Column(name = "parameterType", length = 20)
    private String parameterType;

    @Comment("参数名称")
    @Column(name = "parameterName", length = 50)
    private String parameterName;

    @Comment("参数备注")
    @Column(name = "remark", length = 100)
    private String remark;
}
