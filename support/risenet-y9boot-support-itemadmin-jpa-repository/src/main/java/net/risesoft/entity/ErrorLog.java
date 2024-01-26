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
@Table(name = "FF_ERRORLOG")
@org.hibernate.annotations.Table(comment = "错误日志记录表", appliesTo = "FF_ERRORLOG")
public class ErrorLog implements Serializable {
    private static final long serialVersionUID = 2537599274208903877L;

    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程实例")
    @Column(name = "PROCESSINSTANCEID", length = 50)
    private String processInstanceId;

    @Comment("任务id")
    @Column(name = "TASKID", length = 50)
    private String taskId;

    @Comment("错误类型")
    @Column(name = "ERRORTYPE", length = 100, nullable = false)
    private String errorType;

    @Comment("错误标识")
    @Column(name = "ERRORFLAG", length = 100, nullable = false)
    private String errorFlag;

    @Comment("扩展字段")
    @Column(name = "EXTENDFIELD", length = 255)
    private String extendField;

    @Comment("错误日志信息")
    @Lob
    @Column(name = "TEXT")
    private String text;

    @Comment("创建时间")
    @Column(name = "CREATETIME", length = 50)
    private String createTime;

    @Comment("更新时间")
    @Column(name = "UPDATETIME", length = 50)
    private String updateTime;

}
