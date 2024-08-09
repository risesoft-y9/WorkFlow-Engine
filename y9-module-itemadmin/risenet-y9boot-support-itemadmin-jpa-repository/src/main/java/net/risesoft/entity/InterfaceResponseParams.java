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

/**
 *
 * @author zhangchongjie
 * @date 2024/05/23
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_INTERFACE_RESPONSE_PARAMS")
@Comment("接口响应参数信息表")
public class InterfaceResponseParams implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 2349336106886222608L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("接口id")
    @Column(name = "interfaceId", length = 50)
    private String interfaceId;

    @Comment("参数名称")
    @Column(name = "parameterName", length = 50)
    private String parameterName;

    @Comment("是否文件类型")
    @Column(name = "isFile", length = 10)
    private String isFile = "0";

    @Comment("参数备注")
    @Column(name = "remark", length = 100)
    private String remark;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
