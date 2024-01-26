package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

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
public class QuickSend implements Serializable {

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

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Comment("生成时间")
    @Column(name = "UPDATETIME", length = 50)
    private Date updateTime;

}