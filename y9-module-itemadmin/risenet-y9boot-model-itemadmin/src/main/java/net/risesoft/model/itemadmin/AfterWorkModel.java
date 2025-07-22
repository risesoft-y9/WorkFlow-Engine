package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 核稿后工作事项信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class AfterWorkModel implements Serializable {

    /**
     * 主键
     */
    private String id;

    // 流程实例编号
    private String processSerialNumber;

    // 核稿后工作事项
    private String hghsxName;

    // 开始时间
    private String startTime;

    // 结束时间
    private String endTime;

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

}