package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 抄送信息模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ChaoSongBaseModel implements Serializable {

    private static final long serialVersionUID = 1671973514146425897L;
    /**
     * 主键
     */
    private String id;

    /**
     * 租户Id
     */
    private String tenantId;

    /**
     * 抄送的标题
     */
    private String title;

    /**
     * 抄送的流程实例
     */
    private String processInstanceId;

    /**
     * 流程定义id
     */
    private String processDefinitionId;

    /**
     * 流程编号
     */
    private String processSerialNumber;

    /**
     * 抄送目标人员名称
     */
    private String userName;

    /**
     * 抄送目标人员Id
     */
    private String userId;

    /**
     * 抄送目标人员部门名称
     */
    private String userDeptName;

    /**
     * 抄送目标人员部门Id
     */
    private String userDeptId;

    /**
     * 操作人的名称
     */
    private String senderName;

    /**
     * 操作人的Id
     */
    private String senderId;

    /**
     * 操作人员部门名称
     */
    private String sendDeptName;

    /**
     * 操作人员部门Id
     */
    private String sendDeptId;

    /**
     * 抄送时间
     */
    private String createTime;

    /**
     * 抄送时间
     */
    private String readTime;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 文件编号
     */
    private String number;

    /**
     * 紧急程度
     */
    private String level;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 是否结束
     */
    private boolean banjie;

    /**
     * 是否关注
     */
    private boolean follow;

    /**
     * 序号
     */
    private int serialNumber;

}
