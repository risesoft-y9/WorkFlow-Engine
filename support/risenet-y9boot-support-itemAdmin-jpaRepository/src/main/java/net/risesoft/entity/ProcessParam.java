package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.ColumnDefault;
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
@org.hibernate.annotations.Table(comment = "流程参数信息表", appliesTo = "FF_PROCESS_PARAM")
@Table(name = "FF_PROCESS_PARAM",
    indexes = {@Index(name = "index_001_processInstanceId", columnList = "processInstanceId"),
        @Index(name = "index_002_processSerialNumber", columnList = "processSerialNumber")})
public class ProcessParam implements Serializable {

    private static final long serialVersionUID = -5245779237483037821L;

    @Id
    @org.hibernate.annotations.Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @org.hibernate.annotations.Comment("流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @org.hibernate.annotations.Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @org.hibernate.annotations.Comment("事项唯一标示")
    @Column(name = "ITEMID", length = 50, nullable = false)
    private String itemId;

    @org.hibernate.annotations.Comment("事项名称")
    @Column(name = "ITEMNAME", length = 50, nullable = false)
    private String itemName;

    @org.hibernate.annotations.Comment("系统中文名称")
    @Column(name = "SYSTEMCNNAME", length = 50, nullable = false)
    private String systemCnName;

    @org.hibernate.annotations.Comment("系统英文名称")
    @Column(name = "SYSTEMNAME", length = 50, nullable = false)
    private String systemName;

    @org.hibernate.annotations.Comment("标题")
    @Column(name = "TITLE", length = 1000, nullable = false)
    private String title;

    @org.hibernate.annotations.Comment("自定义编号")
    @Column(name = "CUSTOMNUMBER", length = 100)
    private String customNumber;

    @org.hibernate.annotations.Comment("自定义级别")
    @Column(name = "CUSTOMLEVEL", length = 100)
    private String customLevel;

    @org.hibernate.annotations.Comment("委办局Id")
    @Column(name = "BUREAUIDS", length = 200)
    private String bureauIds;

    @org.hibernate.annotations.Comment("科室Id")
    @Column(name = "DEPTIDS", length = 4000)
    private String deptIds;

    @org.hibernate.annotations.Comment("办结人员")
    @Column(name = "COMPLETER", length = 50)
    private String completer;

    @org.hibernate.annotations.Comment("统一待办url前缀")
    @Column(name = "TODOTASKURLPREFIX", length = 200)
    private String todoTaskUrlPrefix;

    @org.hibernate.annotations.Comment("搜索词")
    @Lob
    @Column(name = "SEARCHTERM", nullable = false)
    private String searchTerm;

    @org.hibernate.annotations.Comment("是否发送短信")
    @Column(name = "isSendSms", length = 50)
    private String isSendSms;

    @org.hibernate.annotations.Comment("是否署名")
    @Column(name = "isShuMing", length = 50)
    private String isShuMing;

    @org.hibernate.annotations.Comment("发送短信内容")
    @Column(name = "smsContent", length = 2000)
    private String smsContent;

    @org.hibernate.annotations.Comment("接收短信人员id")
    @Lob
    @Column(name = "smsPersonId")
    private String smsPersonId;

    @org.hibernate.annotations.Comment("主办人id")
    @Column(name = "sponsorGuid", length = 50)
    private String sponsorGuid;

    @org.hibernate.annotations.Comment("流程的启动人员id")
    @Column(name = "STARTOR", length = 100)
    private String startor;

    @org.hibernate.annotations.Comment("流程的启动人员姓名")
    @Column(name = "STARTORNAME", length = 100)
    private String startorName;

    @org.hibernate.annotations.Comment("这个件是否发送过")
    @Column(name = "SENDED", length = 10)
    private String sended = "false";

    @org.hibernate.annotations.Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    @org.hibernate.annotations.Comment("是否定制流程")
    @Column(name = "CUSTOMITEM", nullable = false)
    @ColumnDefault("0")
    private Boolean customItem = false;

}
