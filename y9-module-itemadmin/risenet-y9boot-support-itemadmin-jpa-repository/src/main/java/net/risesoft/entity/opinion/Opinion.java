package net.risesoft.entity.opinion;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

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
@Table(name = "FF_OPINION")
@Comment("意见信息表")
public class Opinion implements Serializable, Comparable<Opinion> {

    private static final long serialVersionUID = -611638086177612070L;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 租户Id
     */
    @Comment("租户Id")
    @Column(name = "TENANTID", length = 50, nullable = false)
    private String tenantId;

    /**
     * 意见框Id
     */
    @Comment("意见框标识")
    @Column(name = "OPINIONFRAMEMARK", length = 50, nullable = false)
    private String opinionFrameMark;

    /**
     * 流程系列号
     */
    @Comment("流程编号")
    @Column(name = "PROCESSSERIALNUMBER", length = 38, nullable = false)
    private String processSerialNumber;

    /**
     * 流程实例Id
     */
    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 64)
    private String processInstanceId;

    /**
     * 任务实例Id
     */
    @Comment("任务id")
    @Column(name = "TASKID", length = 200)
    private String taskId;

    /**
     * 意见内容
     */
    @Comment("意见内容")
    @Lob
    @Column(name = "CONTENT", nullable = false)
    private String content;

    /**
     * 填写意见人员id
     */
    @Comment("人员id")
    @Column(name = "USERID", length = 38, nullable = false)
    private String userId;

    /**
     * 填写意见的人员名称
     */
    @Comment("人员名称")
    @Column(name = "USERNAME", length = 50, nullable = false)
    private String userName;

    /**
     * 填写意见人部门id
     */
    @Comment("部门id")
    @Column(name = "DEPTID", length = 38)
    private String deptId;

    /**
     * 填写意见的人员部门名称
     */
    @Comment("部门名称")
    @Column(name = "DEPTNAME", length = 100)
    private String deptName;

    /**
     * 意见生成时间
     */
    @Comment("生成时间")
    @Column(name = "CREATEDATE", length = 50)
    private String createDate;

    /**
     * 意见最后的修改时间
     */
    @Comment("修改时间")
    @Column(name = "MODIFYDATE", length = 50)
    private String modifyDate;

    @Comment("岗位id")
    @Column(name = "POSITIONID", length = 38)
    private String positionId;

    /**
     * 岗位名称
     */
    @Comment("岗位名称")
    @Column(name = "POSITIONNAME", length = 50)
    private String positionName;

    /**
     * 自定义历程id
     */
    @Comment("自定义历程id")
    @Column(name = "PROCESSTRACKID", length = 50)
    private String processTrackId;

    @Transient
    @Deprecated
    private String orderStr;

    @Override
    public int compareTo(Opinion o) {
        return this.orderStr.compareTo(o.getOrderStr());
    }
}
