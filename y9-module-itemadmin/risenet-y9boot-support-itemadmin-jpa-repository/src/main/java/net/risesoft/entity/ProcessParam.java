package net.risesoft.entity;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonFormat;

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
@org.hibernate.annotations.Table(comment = "流程参数信息表", appliesTo = "FF_PROCESS_PARAM")
@Table(name = "FF_PROCESS_PARAM",
    indexes = {@Index(name = "index_001_processInstanceId", columnList = "processInstanceId"),
        @Index(name = "index_002_processSerialNumber", columnList = "processSerialNumber")})
public class ProcessParam implements Serializable {

    private static final long serialVersionUID = -5245779237483037821L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("事项唯一标示")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @Comment("事项名称")
    @Column(name = "ITEMNAME", length = 50, nullable = false)
    private String itemName;

    @Comment("系统中文名称")
    @Column(name = "SYSTEMCNNAME", length = 50, nullable = false)
    private String systemCnName;

    @Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = false)
    private String systemName;

    @Comment("标题")
    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title;

    @Comment("自定义编号")
    @Column(name = "CUSTOMNUMBER", length = 100)
    private String customNumber;

    @Comment("自定义级别")
    @Column(name = "CUSTOMLEVEL", length = 100)
    private String customLevel;

    @Comment("委办局Id")
    @Column(name = "BUREAUIDS", length = 200)
    private String bureauIds;

    @Comment("科室Id")
    @Column(name = "DEPTIDS", length = 4000)
    private String deptIds;

    @Comment("办结人员")
    @Column(name = "COMPLETER", length = 50)
    private String completer;

    @Comment("统一待办url前缀")
    @Column(name = "TODOTASKURLPREFIX", length = 200)
    private String todoTaskUrlPrefix;

    @Comment("搜索词")
    @Lob
    @Column(name = "SEARCHTERM", nullable = false)
    private String searchTerm;

    @Comment("是否发送短信")
    @Column(name = "isSendSms", length = 50)
    private String isSendSms;

    @Comment("是否署名")
    @Column(name = "isShuMing", length = 50)
    private String isShuMing;

    @Comment("发送短信内容")
    @Column(name = "smsContent", length = 2000)
    private String smsContent;

    @Comment("接收短信人员id")
    @Lob
    @Column(name = "smsPersonId")
    private String smsPersonId;

    @Comment("主办人id")
    @Column(name = "sponsorGuid", length = 50)
    private String sponsorGuid;

    @Comment("主办部门或者委办局ID")
    @Column(name = "HOSTDEPTID", length = 50)
    private String hostDeptId;

    @Comment("主办部门或者委办局名称")
    @Column(name = "HOSTDEPTNAME", length = 50)
    private String hostDeptName;

    @Comment("流程的启动人员id")
    @Column(name = "STARTOR", length = 100)
    private String startor;

    @Comment("流程的启动人员姓名")
    @Column(name = "STARTORNAME", length = 100)
    private String startorName;

    @Comment("这个件是否发送过")
    @Column(name = "SENDED", length = 10)
    private String sended = "false";

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    @Type(type = "numeric_boolean")
    @Comment("是否定制流程")
    @Column(name = "CUSTOMITEM", nullable = false)
    @ColumnDefault("0")
    private Boolean customItem = false;

    /**
     * 目标，xxx使用
     */
    @Comment("目标")
    @Column(name = "TARGET", length = 100)
    private String target;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd", timezone = "GMT+8")
    @Temporal(TemporalType.DATE)
    @Column(name = "DUEDATE")
    @Comment("到期时间")
    private Date dueDate;

    @Comment("描述")
    @Column(name = "DESCRIPTION", length = 500)
    private String description;

}
