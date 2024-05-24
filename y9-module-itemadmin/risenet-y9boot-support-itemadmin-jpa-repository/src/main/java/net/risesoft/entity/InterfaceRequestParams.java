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
 * @date 2024/05/23
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_INTERFACE_REQUEST_PARAMS")
@org.hibernate.annotations.Table(comment = "接口请求参数信息表", appliesTo = "FF_INTERFACE_REQUEST_PARAMS")
public class InterfaceRequestParams implements Serializable {

    /**
     *
     */
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

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
