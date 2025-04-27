package net.risesoft.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Comment;
import org.hibernate.annotations.GenericGenerator;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author : qinman
 * @date : 2025-04-24
 * @since 9.6.8
 **/
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@Builder
@Table(name = "FF_TODO3RD")
@org.hibernate.annotations.Table(comment = "第三方那个待办表", appliesTo = "FF_TODO3RD")
public class Todo3rd implements Serializable {

    private static final long serialVersionUID = -7296757089028689519L;

    /**
     * 主键
     */
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 38, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    @JsonProperty("vendorId")
    private String id;

    /**
     * 系统标识
     */
    @Comment("系统标识")
    @Column(name = "APPCODE", length = 50, nullable = false)
    private String appCode;
    /**
     * 系统名称
     */
    @Comment("系统名称")
    @Column(name = "APPNAME", length = 50, nullable = false)
    private String appName;

    /**
     * 标题
     */
    @Comment("标题")
    @Column(name = "TITLE", length = 500, nullable = false)
    @JsonProperty("bt")
    private String title;
    /**
     * 办理提示
     */
    @Comment("办理提示")
    @Column(name = "CONTENT", length = 500)
    private String content;

    /**
     * 紧急程度
     */
    @Comment("紧急程度")
    @Column(name = "URGENT", length = 10)
    private Integer urgent;
    /**
     * 紧急程度
     */
    @Comment("紧急程度")
    @Column(name = "URGENTTEXT", length = 10)
    private String urgentText;
    /**
     * 处理链接
     */
    @Comment("处理链接")
    @Column(name = "URL", length = 500, nullable = false)
    private String url;
    /**
     * 发送人主键
     */
    @Comment("发送人主键")
    @Column(name = "SENDUSERID", length = 50, nullable = false)
    private String sendUserId;
    /**
     * 发送人姓名
     */
    @Comment("发送人姓名")
    @Column(name = "SENDUSERNAME", length = 50, nullable = false)
    private String sendUserName;
    /**
     * 发送人部门主键
     */
    @Comment("发送人部门主键")
    @Column(name = "SENDDEPTID", length = 50, nullable = false)
    private String sendDeptId;
    /**
     * 发送人部门名称
     */
    @Comment("发送人部门名称")
    @Column(name = "SENDDEPTNAME", length = 50, nullable = false)
    private String sendDeptName;
    /**
     * 接收人主键
     */
    @Comment("接收人主键")
    @Column(name = "RECEIVEUSERID", length = 50, nullable = false)
    private String receiveUserId;
    /**
     * 接收人姓名
     */
    @Comment("接收人姓名")
    @Column(name = "RECEIVEUSERNAME", length = 50, nullable = false)
    private String receiveUserName;
    /**
     * 接收人部门主键
     */
    @Comment("接收人部门主键")
    @Column(name = "RECEIVEDEPTID", length = 50, nullable = false)
    private String receiveDeptId;
    /**
     * 接收人部门名称
     */
    @Comment("接收人部门名称")
    @Column(name = "RECEIVEDEPTNAME", length = 50, nullable = false)
    private String receiveDeptName;
    /**
     * 接收时间
     */
    @Comment("接收时间")
    @Column(name = "RECEIVETIME", length = 50, nullable = false)
    private String receiveTime;

    @JsonIgnore
    @Comment("流程序列号")
    @Column(name = "PROCESSSERIALNUMBER", length = 50, nullable = false)
    private String processSerialNumber;

    @JsonIgnore
    @Comment("待办类型")
    @Column(name = "TODOTYPE", length = 10, nullable = false)
    private Integer todoType;

    /**
     * 1新增操作、2修改操作、3删除操作
     */
    @JsonIgnore
    @Comment("操作类型")
    @Column(name = "OPTTYPE", length = 10, nullable = false)
    private Integer optType;

    @JsonIgnore
    @Comment("是否成功")
    @Column(name = "SUCCESS", length = 50, nullable = false)
    private boolean success;

    @JsonIgnore
    @Comment("是否删除")
    @Column(name = "DELETED", length = 50, nullable = false)
    private boolean deleted;

    /**
     * 返回消息
     */
    @JsonIgnore
    @Comment("返回消息")
    @Column(name = "MESSAGE", length = 500)
    private String message;

    /**
     * 请求参数
     */
    @JsonIgnore
    @Comment("请求参数")
    @Column(name = "PARAMS", length = 500, nullable = false)
    private String params;
}