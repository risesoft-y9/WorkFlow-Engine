package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.*;

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
@Table(name = "FF_ITEM_OPINIONFRAME_ROLE")
@org.hibernate.annotations.Table(comment = "事项和意见框绑定关系对应的角色", appliesTo = "FF_ITEM_OPINIONFRAME_ROLE")
public class ItemOpinionFrameRole implements Serializable {

    private static final long serialVersionUID = 7079860101823150509L;

    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项和意见框的绑定关系")
    @Column(name = "ITEMOPINIONFRAMEID", length = 50, nullable = false)
    private String itemOpinionFrameId;

    @Comment("角色Id")
    @Column(name = "ROLEID", length = 50, nullable = false)
    private String roleId;

    /**
     * 角色名称
     */
    @Transient
    private String roleName;
}
