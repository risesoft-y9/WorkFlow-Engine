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
 * 协作状态详情表
 *
 * @author Think
 *
 */
@Entity
@Table(name = "FF_PROCESSINSTANCE_DETAILS")
@org.hibernate.annotations.Table(comment = "协作状态详情表", appliesTo = "FF_PROCESSINSTANCE_DETAILS")
@NoArgsConstructor
@Data
public class ProcessInstanceDetails implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 6794538661278078490L;

    @Id
    @Column(name = "ID", length = 38)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    @Comment("主键")
    private String id;

    @Comment("流程实例id")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("任务id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    @Comment("任务名称")
    @Column(name = "TASKNAME", length = 50)
    private String taskName;

    @Comment("意见内容")
    @Column(name = "OPINIONCONTENT", length = 500)
    private String opinionContent;

    @Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 100)
    private String systemName;

    @Comment("系统中文名称")
    @Column(name = "SYSTEMCNNAME", length = 100)
    private String systemCnName;

    @Comment("事项id")
    @Column(name = "ITEMID", length = 50)
    private String itemId;

    @Comment("应用名称")
    @Column(name = "APPNAME", length = 50)
    private String appName;

    @Comment("应用中文名称")
    @Column(name = "APPCNNAME", length = 100)
    private String appCnName;

    @Comment("发送人id")
    @Column(name = "SENDERID", length = 50)
    private String senderId;

    @Comment("发送人名称")
    @Column(name = "SENDERNAME", length = 50)
    private String senderName;

    @Comment("办理人id")
    @Column(name = "ASSIGNEEID", length = 50)
    private String assigneeId;

    @Comment("办理人名称")
    @Column(name = "ASSIGNEENAME", length = 50)
    private String assigneeName;

    @Comment("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "STARTTIME")
    private Date startTime;

    @Comment("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ENDTIME")
    private Date endTime;

    @Comment("编号")
    @Column(name = "SERIALNUMBER", length = 50)
    private String serialNumber;

    @Comment("文件标题")
    @Column(name = "TITLE", length = 1000)
    private String title;

    @Comment("发起人")
    @Column(name = "USERNAME", length = 100)
    private String userName;

    @Comment("办件状态")
    @Column(name = "ITEMBOX", length = 20)
    private String itembox;// todo待办，doing在办，done办结

    @Comment("详情链接")
    @Column(name = "URL", length = 200)
    private String url;

}
