package net.risesoft.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINIONFRAME_ONECLICKSET")
@org.hibernate.annotations.Table(comment = "意见框一键设置表", appliesTo = "FF_OPINIONFRAME_ONECLICKSET")
public class OpinionFrameOneClickSet implements Serializable {

    private static final long serialVersionUID = -8789316082610929817L;

    /**
     * 唯一标示
     */
    @Comment("主键")
    @Id
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("一键设置类型")
    @Column(name = "ONESET_TYPE", length = 100)
    private String oneSetType;//oneClickSign-一键签批   oneClickRead- 一键阅知   同意  已阅

    @Comment("对应执行动作")
    @Column(name = "EXECUTEACTION", length = 100)
    private String executeAction;

    @Comment("一键设置类型名称")
    @Column(name = "ONESETTYPE_NAME", length = 100)
    private String oneSetTypeName; //一键设置类型名称

    @Comment("对应执行动作名称")
    @Column(name = "EXECUTEACTION_NAME", length = 100)
    private String executeActionName;//对应执行动作名称

    @Comment("绑定id")
    @Column(name = "BIND_ID", length = 50)
    private String bindId;

    @Comment("操作人员id")
    @Column(name = "USER_ID", length = 50)
    private String userId;

    @Comment("创建时间")
    @Column(name = "CREATE_DATE")
    private String createDate;


}
