package net.risesoft.entity.opinion;

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

@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINIONFRAME_ONECLICKSET")
@Comment("意见框一键设置表")
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
    private String oneSetType;// oneClickSign-一键签批 oneClickRead- 一键阅知 同意 已阅

    @Comment("对应执行动作")
    @Column(name = "EXECUTEACTION", length = 100)
    private String executeAction;

    @Comment("一键设置类型名称")
    @Column(name = "ONESETTYPE_NAME", length = 100)
    private String oneSetTypeName; // 一键设置类型名称

    @Comment("对应执行动作名称")
    @Column(name = "EXECUTEACTION_NAME", length = 100)
    private String executeActionName;// 对应执行动作名称

    @Comment("一键设置描述")
    @Column(name = "ONESET_DESCRIPTION", length = 300)
    private String description;

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
