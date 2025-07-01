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
@Table(name = "FF_AFTERWORK_INFO")
@org.hibernate.annotations.Table(comment = "核稿后工作事项信息表", appliesTo = "FF_AFTERWORK_INFO")
public class AfterWorkInfo implements Serializable {

    private static final long serialVersionUID = 5583565476984453824L;
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

    @Comment("核稿后工作事项")
    @Column(name = "HGHSXNAME")
    private String hghsxName;

    @Comment("开始时间")
    @Column(name = "STARTTIME", length = 50)
    private String startTime;

    @Comment("结束时间")
    @Column(name = "ENDTIME", length = 50)
    private String endTime;

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

}
