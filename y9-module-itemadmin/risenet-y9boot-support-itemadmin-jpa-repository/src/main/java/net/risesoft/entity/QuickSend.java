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

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_QUICK_SEND")
@org.hibernate.annotations.Table(comment = "快捷发送信息表", appliesTo = "FF_QUICK_SEND")
public class QuickSend extends ItemAdminBaseEntity implements Serializable {

    public static final Integer DOING = 1;
    private static final long serialVersionUID = -2075804520003967924L;
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("事项Id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @Comment("任务key")
    @Column(name = "TASKKEY", length = 50)
    private String taskKey;

    @Comment("岗位id")
    @Column(name = "POSITIONID", length = 50)
    private String positionId;

    @Comment("快捷发送办理人")
    @Column(name = "ASSIGNEE", length = 2000)
    private String assignee;
}