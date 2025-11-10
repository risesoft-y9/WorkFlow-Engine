package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 委托历史模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class EntrustHistoryModel implements Serializable {

    private static final long serialVersionUID = -5361810092732234663L;
    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date createTime;
    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    protected Date updateTime;
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
     * 委托开始时间
     */
    private String startTime;
    /**
     * 委托结束时间
     */
    private String endTime;

}
