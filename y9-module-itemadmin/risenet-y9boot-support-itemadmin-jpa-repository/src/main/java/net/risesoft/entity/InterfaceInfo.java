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
@Table(name = "FF_INTERFACE_INFO")
@Comment("接口信息表")
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
