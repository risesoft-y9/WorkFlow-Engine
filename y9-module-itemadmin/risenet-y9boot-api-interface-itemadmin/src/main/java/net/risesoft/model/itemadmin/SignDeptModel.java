package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import lombok.Data;

/**
 * 会签信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class SignDeptModel implements Serializable {

    private String id;

    // 部门Id
    private String deptId;

    // 部门名称
    private String deptName;

    // 显示部门Id
    private String displayDeptId;

    // 显示部门名称
    private String displayDeptName;

    // 流程编号
    private String processSerialNumber;

    // 签字人姓名
    private String userName;

    // 签字日期
    private String signDate;

    // 单位类型（0：委内，1：委外，2：联合发文）
    private String deptType;

    // 录入时间
    private Date recordTime;

    // 录入人
    private String inputPerson;

    // 录入人id
    private String inputPersonId;

    // 部门排序
    private Integer orderIndex;
}
