package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会签部门详情
 * 
 * @author qinman
 * @date 2024/12/12
 */
@NoArgsConstructor
@Data
public class SignDeptDetailModel implements Serializable {

    private static final long serialVersionUID = -7739639595142884456L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 流程执行实例Id
     */
    private String executionId;

    /**
     * 任务Id
     */
    private String taskId;

    /**
     * 会签单位唯一标示
     */
    private String deptId;

    /**
     * 会签单位名称
     */
    private String deptName;

    /**
     * 签注人
     */
    private String userName;

    /**
     * 签注人电话
     */
    private String mobile;

    /**
     * 正文文件id
     */
    private String fileStoreId;

    /**
     * 单位负责人
     */
    private String deptManager;

    /**
     * 状态:1是在办、2是正常办结、3是退回办结、4是减签
     */
    private Integer status;

    /**
     * 是否是新的
     */
    private boolean newed;

    /**
     * 生成时间
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}