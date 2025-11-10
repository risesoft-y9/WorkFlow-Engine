package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 催办信息
 *
 * @author qinman
 * @date 2024/12/24
 */
@Data
public class UrgeInfoModel implements Serializable {

    private static final long serialVersionUID = 2913082851033227521L;
    /**
     * 主键
     */
    private String id;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 流程执行实例Id
     */
    private String executionId;

    /**
     * 是否是对子流程的催办信息
     */
    private boolean isSub;

    /**
     * 催办人员唯一标识
     */
    private String userId;

    /**
     * 催办人员姓名
     */
    private String userName;

    /**
     * 催办内容
     */
    private String msgContent;

    /**
     * 创建时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /**
     * 更新时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
