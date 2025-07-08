package net.risesoft.entity.interfaceinfo;

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
@Table(name = "FF_INTERFACE_RESPONSE_PARAMS")
@org.hibernate.annotations.Table(comment = "接口响应参数信息表", appliesTo = "FF_INTERFACE_RESPONSE_PARAMS")
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

    @Comment("是否文件")
    @Column(name = "fileType", length = 10)
    private String fileType;

    @Comment("参数备注")
    @Column(name = "remark", length = 100)
    private String remark;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
