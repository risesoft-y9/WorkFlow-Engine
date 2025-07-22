package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

@Data
public class OpinionFrameOneClickSetModel implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 唯一标示
     */
    private String id;

    private String oneSetType;// oneClickSign-一键签批 oneClickRead- 一键阅知 同意 已阅

    private String executeAction;// 对应执行动作

    private String oneSetTypeName; // 一键设置类型名称

    private String executeActionName;// 对应执行动作名称

    private String description;// 描述

    private String bindId;// 绑定的id

    private String userId;// 用户id

    private String createDate;// 创建日期

}
