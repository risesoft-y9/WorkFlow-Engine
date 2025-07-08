package net.risesoft.entity;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
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
@Table(name = "FF_SPEAKINFO")
@Comment("沟通交流信息表")
public class SpeakInfo implements Serializable {

    private static final long serialVersionUID = 4264601224577400231L;

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
     * 流程实例Id
     */
    @Comment("流程实例Id")
    @Column(name = "PROCESSINSTANCEID", length = 50, nullable = false)
    private String processInstanceId;

    /**
     * 发言信息
     */
    @Comment("发言信息")
    @Column(name = "CONTENT", length = 1000)
    private String content;

    /**
     * 发言人名称
     */
    @Comment("发言人名称")
    @Column(name = "USERNAME", length = 50)
    private String userName;

    /**
     * 发言人Id
     */
    @Comment("发言人Id")
    @Column(name = "USERID", length = 50)
    private String userId;

    /**
     * 是否可以编辑
     */
    @Transient
    private boolean edited;

    /**
     * 是否删除
     */
    @Convert(converter = org.hibernate.type.NumericBooleanConverter.class)
    @Comment("是否删除")
    @Column(name = "DELETED", length = 50)
    private boolean deleted;

    /**
     * 发言时间
     */
    @Comment("发言时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    /**
     * 发言时间
     */
    @Comment("修改时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;

    /**
     * 已看人员id
     */
    @Comment("已看人员id")
    @Column(name = "READUSERID", length = 2000)
    private String readUserId;

}
