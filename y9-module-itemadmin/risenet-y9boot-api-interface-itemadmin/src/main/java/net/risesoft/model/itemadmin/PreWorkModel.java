package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 前期工作事项信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class PreWorkModel implements Serializable {

    private static final long serialVersionUID = -5539950937757851911L;
    /**
     * 主键
     */
    private String id;

    // 流程实例编号
    private String processSerialNumber;

    // 前期工作事项名称
    private String qqsxName;

    // 开始时间
    private String startTime;

    // 结束时间(收文：预计结束时间)
    private String endTime;

    // 签报号
    private String qbh;

    // 委托评估文号
    private String wtpgwh;

    // 办理结果
    private String bljg;

    // 录入人
    private String inputPerson;

    // 录入时间
    private String recordTime;

    // 录入人ID
    private String inputPersonId;

    // 事项状态(0,不计算督办时限,1:计算)
    private String status;

    // 添加人的id
    private String addPerson;

    // 添加人的姓名
    private String addPersonName;

    // 事项类型（0：办文 1：收文）
    private String sxType = "0";

    // 是否已同步过0未同步过，1已同步过
    private String syncFlag = "0";

    // 实际结束时间（收文用）
    private String realEndTime;

    // 记录下并案时主件添加的工作事项的id
    private String masterQqsxUid;

}