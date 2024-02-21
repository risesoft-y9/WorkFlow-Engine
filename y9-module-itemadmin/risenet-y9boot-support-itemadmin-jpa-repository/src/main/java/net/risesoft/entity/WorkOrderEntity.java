package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

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
@Table(name = "y9_form_workorder")
@org.hibernate.annotations.Table(comment = "工单信息表", appliesTo = "y9_form_workorder")
public class WorkOrderEntity implements Serializable {

    private static final long serialVersionUID = -2196986462380652871L;

    @Id
    @Comment("主键")
    @Column(name = "GUID")
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String guid;

    /**
     * 流程编号，与Y9表单一致，存放流程编号,与guid一样
     */
    @Comment("流程编号")
    @Column(name = "processInstanceId", length = 50)
    private String processInstanceId;

    /**
     * 工作流实例id
     */
    @Comment("工作流实例id")
    @Column(name = "realProcessInstanceId", length = 50)
    private String realProcessInstanceId;

    /**
     * 工单编号
     */
    @Comment("工单编号")
    @Column(name = "number", length = 50)
    private String number;

    /**
     * 文件标题
     */
    @Comment("文件标题")
    @Column(name = "title", length = 500)
    private String title;

    /**
     * 工单类型
     */
    @Comment("工单类型")
    @Column(name = "workOrderType", length = 20)
    private String workOrderType;

    /**
     * 紧急程度
     */
    @Comment("紧急程度")
    @Column(name = "urgency", length = 10)
    private String urgency;

    /**
     * 创建日期
     */
    @Comment("创建日期")
    @Column(name = "createDate", length = 20)
    private String createDate;

    /**
     * 创建时间
     */
    @Comment("创建时间")
    @Column(name = "createTime", length = 20)
    private String createTime;

    /**
     * 创建人Id
     */
    @Comment("创建人Id")
    @Column(name = "userId", length = 50)
    private String userId;

    /**
     * 创建人
     */
    @Comment("创建人")
    @Column(name = "userName", length = 50)
    private String userName;

    /**
     * 联系电话
     */
    @Comment("联系电话")
    @Column(name = "mobile", length = 15)
    private String mobile;

    /**
     * qq
     */
    @Comment("QQ")
    @Column(name = "QQ", length = 15)
    private String qq;

    /**
     * 提交租户id
     */
    @Comment("提交租户id")
    @Column(name = "tenantId", length = 50)
    private String tenantId;

    /**
     * 提交租户名称
     */
    @Comment("提交租户名称")
    @Column(name = "tenantName", length = 100)
    private String tenantName;

    /**
     * 纯文本描述
     */
    @Lob
    @Comment("纯文本描述")
    @Column(name = "description")
    private String description;

    /**
     * 带html格式描述
     */
    @Lob
    @Comment("带html格式描述")
    @Column(name = "htmlDescription")
    private String htmlDescription;

    /**
     * 建议
     */
    @Comment("建议")
    @Column(name = "suggest", length = 2000)
    private String suggest;

    /**
     * 结果反馈
     */
    @Comment("结果反馈")
    @Column(name = "resultFeedback", length = 2000)
    private String resultFeedback;

    /**
     * 处理类型，0为草稿，1为未处理，2为处理中，3为已处理
     */
    @Comment("处理类型")
    @Column(name = "handleType", length = 5)
    private String handleType;

    /**
     * 处理人
     */
    @Comment("处理人")
    @Column(name = "handler", length = 50)
    private String handler;

    /**
     * 处理人联系方式
     */
    @Comment("处理人联系方式")
    @Column(name = "handlerMobile", length = 20)
    private String handlerMobile;

}
