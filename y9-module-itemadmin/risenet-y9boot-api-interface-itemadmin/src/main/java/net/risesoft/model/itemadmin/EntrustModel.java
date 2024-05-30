package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 委托模型类
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class EntrustModel implements Serializable {

    private static final long serialVersionUID = 1227671437196285577L;

    public static final String ALL = "ALL";

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 委托人Id
     */
    private String ownerId;

    /**
     * 委托人姓名
     */
    private String ownerName;

    /**
     * 委托对象Id
     */
    private String assigneeId;

    /**
     * 委托对象姓名
     */
    private String assigneeName;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 是否启用
     */
    private Integer used;

    /**
     * 委托开始时间
     */
    private String startTime;

    /**
     * 委托结束时间
     */
    private String endTime;

    /**
     * 委托事项的生成时间
     */
    private String creatTime;

    /**
     * 委托事项编辑时间
     */
    private String updateTime;

}
