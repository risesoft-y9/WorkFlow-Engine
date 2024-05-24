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
@Table(name = "FF_INTERFACE_INFO")
@org.hibernate.annotations.Table(comment = "接口信息表", appliesTo = "FF_INTERFACE_INFO")
public class InterfaceInfo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 5724668342027403903L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("接口名称")
    @Column(name = "interfaceName", length = 50)
    private String interfaceName;

    @Comment("接口地址")
    @Column(name = "interfaceAddress", length = 200)
    private String interfaceAddress;

    @Comment("请求类型")
    @Column(name = "requestType", length = 20)
    private String requestType;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

}
