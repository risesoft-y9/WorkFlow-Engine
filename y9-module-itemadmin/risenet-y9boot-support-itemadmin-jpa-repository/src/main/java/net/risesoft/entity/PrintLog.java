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
@Table(name = "FF_PRINT_LOG")
@org.hibernate.annotations.Table(comment = "打印日志信息表", appliesTo = "FF_PRINT_LOG")
public class PrintLog implements Serializable {

    private static final long serialVersionUID = 6122532613338542293L;
    @Id
    @Comment("主键")
    @Column(name = "ID", length = 50, nullable = false)
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "assigned")
    private String id;

    @Comment("流程编号")
    @Column(name = "processSerialNumber", length = 50, nullable = false)
    private String processSerialNumber;

    @Comment("操作内容")
    @Column(name = "actionContent", length = 50)
    private String actionContent;

    @Comment("操作类型")
    @Column(name = "actionType", length = 50)
    private String actionType;

    @Comment("ip")
    @Column(name = "ip", length = 50)
    private String ip;

    @Comment("打印人")
    @Column(name = "userName", length = 50)
    private String userName;

    @Comment("打印人id")
    @Column(name = "userId", length = 50)
    private String userId;

    @Comment("打印人部门id")
    @Column(name = "deptId", length = 50)
    private String deptId;

    @Comment("打印时间")
    @Column(name = "printTime", length = 50)
    private String printTime;

}