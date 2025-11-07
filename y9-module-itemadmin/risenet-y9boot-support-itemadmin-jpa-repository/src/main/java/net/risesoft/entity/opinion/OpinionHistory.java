package net.risesoft.entity.opinion;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import lombok.Data;
import lombok.NoArgsConstructor;

import net.risesoft.entity.base.ItemAdminBaseEntity;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINION_HISTORY")
@Comment("意见历史表")
public class OpinionHistory extends ItemAdminBaseEntity implements Serializable {

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
     * 操作、保存时间
     */
    @Comment("操作、保存时间")
    @Column(name = "SAVEDATE", length = 50)
    private String saveDate;

    /**
     * 意见类型，1为修改，2为删除
     */
    @Comment("意见类型")
    @Column(name = "OPINIONTYPE", length = 5)
    private String opinionType = "1";
}
