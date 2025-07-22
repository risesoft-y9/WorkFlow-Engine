package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qinman
 * @date 2025/02/12
 */
@Data
public class OpinionCopyModel implements Serializable {

    private static final long serialVersionUID = 8472577174564331346L;
    /**
     *
     */
    private String id;

    /**
     * 抄送的流程实例
     */
    private String processSerialNumber;

    /**
     * 意见内容
     */
    private String content;

    /**
     * 是否是发送意见
     */
    private boolean send;

    /**
     * 抄送目标人员Id
     */
    private String userId;

    /**
     * 人员名称
     */
    private String userName;

    /**
     * 生成时间
     */
    private String createTime;

    /**
     * 更新时间
     */
    private String updateTime;

    /**
     * 如果是发送意见时，这里为发送目标所有的未取消和删除的人员名称
     */
    private String userNames;
}
