package net.risesoft.entity.opinion;

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

/**
 * @author qinman
 * @date 2024/12/16
 */
@NoArgsConstructor
@Data
@Entity
@Table(name = "FF_OPINION_SIGN")
@org.hibernate.annotations.Table(comment = "会签意见信息表", appliesTo = "FF_OPINION_SIGN")
public class OpinionSign implements Serializable {

    private static final long serialVersionUID = 4287475181460776706L;

    /**
     * 唯一标示
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    /**
     * 会签部门信息id
     */
    @Comment("会签部门信息id")
    @Column(name = "SIGNDEPTDETAILID", length = 50, nullable = false)
    private String signDeptDetailId;

    /**
     * 意见框Id
     */
    @Comment("意见框标识")
    @Column(name = "OPINIONFRAMEMARK", length = 50, nullable = false)
    private String opinionFrameMark;

    /**
     * 任务实例Id
     */
    @Comment("任务id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    /**
     * 意见内容
     */
    @Comment("意见内容")
    @Column(name = "CONTENT", nullable = false, length = 1000)
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
    @Column(name = "DEPTID", length = 50)
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
}
