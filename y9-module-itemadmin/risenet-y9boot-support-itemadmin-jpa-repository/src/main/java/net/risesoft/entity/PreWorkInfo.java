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
@Table(name = "FF_PREWORK_INFO")
@org.hibernate.annotations.Table(comment = "前期工作事项信息表", appliesTo = "FF_PREWORK_INFO")
public class PreWorkInfo implements Serializable {

    private static final long serialVersionUID = 6352179134431455153L;
    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50)
    private String processSerialNumber;

    @Comment("前期工作事项名称")
    @Column(name = "QQSXNAME")
    private String qqsxName;

    @Comment("开始时间")
    @Column(name = "STARTTIME", length = 50)
    private String startTime;

    @Comment("结束时间(收文：预计结束时间)")
    @Column(name = "ENDTIME", length = 50)
    private String endTime;

    @Comment("签报号")
    @Column(name = "QBH")
    private String qbh;

    @Comment("委托评估文号")
    @Column(name = "WTPGWH")
    private String wtpgwh;

    @Comment("办理结果")
    @Column(name = "BLJG", length = 500)
    private String bljg;

    @Comment("录入人")
    @Column(name = "INPUTPERSON")
    private String inputPerson;

    @Comment("录入时间")
    @Column(name = "RECORDTIME", length = 50)
    private String recordTime;

    @Comment("录入人ID")
    @Column(name = "INPUTPERSONID", length = 50)
    private String inputPersonId;

    @Comment("事项状态(0,不计算督办时限,1:计算)")
    @Column(name = "STATUS", length = 5)
    private String status;

    @Comment("添加人的id")
    @Column(name = "ADDPERSON", length = 50)
    private String addPerson;

    @Comment("添加人的姓名")
    @Column(name = "ADDPERSONNAME", length = 50)
    private String addPersonName;

    @Comment("事项类型（0：办文 1：收文）")
    @Column(name = "SXTYPE", length = 10)
    private String sxType = "0";

    @Comment("是否已同步过0未同步过，1已同步过")
    @Column(name = "SYNC_FLAG", length = 10)
    private String syncFlag = "0";

    @Comment("实际结束时间（收文用）")
    @Column(name = "REALENDTIME", length = 50)
    private String realEndTime;

    @Comment("记录下并案时主件添加的工作事项的id")
    @Column(name = "MASTERQQSXUID", length = 100)
    private String masterQqsxUid;

}
