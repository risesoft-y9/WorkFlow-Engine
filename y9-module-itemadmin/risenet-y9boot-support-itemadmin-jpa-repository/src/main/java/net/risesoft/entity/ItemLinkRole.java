package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_ITEM_LINK_ROLE")
@org.hibernate.annotations.Table(comment = "事项和链接绑定关系对应的角色", appliesTo = "FF_ITEM_LINK_ROLE")
public class ItemLinkRole implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = -1823432459488463001L;

    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项和链接绑定关系id")
    @Column(name = "ITEMLINKID", length = 50, nullable = false)
    private String itemLinkId;

    @Comment("角色Id")
    @Column(name = "ROLEID", length = 50, nullable = false)
    private String roleId;

    /**
     * 角色名称
     */
    @Transient
    private String roleName;
}
