package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 打印日志信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class PrintLogModel implements Serializable {

    private static final long serialVersionUID = -1560139993428782073L;

    private String id;

    //
    private String title;

    // 流程编号
    private String processSerialNumber;

    // 操作内容
    private String actionContent;

    // 操作类型
    private String actionType;

    // ip
    private String ip;

    // 打印人id
    private String userId;

    // 打印人
    private String userName;

    // 打印人部门id
    private String deptId;

    // 打印时间
    private String printTime;
}
