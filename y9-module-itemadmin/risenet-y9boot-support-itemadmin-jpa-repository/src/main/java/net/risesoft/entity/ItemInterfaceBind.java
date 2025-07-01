package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_ITEM_INTERFACE_BIND")
@org.hibernate.annotations.Table(comment = "事项接口绑定表", appliesTo = "FF_ITEM_INTERFACE_BIND")
public class ItemInterfaceBind implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -7459152125523867706L;

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

    /**
     * 生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATETIME")
    private String createTime;

    /**
     * 接口名称
     */
    @Transient
    private String interfaceName;

    /**
     * 接口地址
     */
    @Transient
    private String interfaceAddress;

    /**
     * 事项名称
     */
    @Transient
    private String itemName;

}
