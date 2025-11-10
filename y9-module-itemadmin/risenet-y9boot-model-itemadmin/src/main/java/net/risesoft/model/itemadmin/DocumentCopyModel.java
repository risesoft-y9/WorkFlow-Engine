package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

import net.risesoft.enums.DocumentCopyStatusEnum;

/**
 * @author qinman
 * @date 2025/02/10
 */
@Data
public class DocumentCopyModel implements Serializable {

    private static final long serialVersionUID = 7005186986575115649L;

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
     * 主键
     */
    private String id;
    /**
     * 抄送的流程实例
     */
    private String processSerialNumber;
    /**
     * 抄送的流程实例
     */
    private String processInstanceId;
    /**
     * 抄送目标人员名称
     */
    private String userName;
    /**
     * 抄送目标人员Id
     */
    private String userId;
    /**
     * 操作人名称
     */
    private String senderName;
    /**
     * 操作人Id
     */
    private String senderId;
    /**
     * 传阅状态 1:待填写意见,2:已填写意见,8:取消,9:删除
     */
    private DocumentCopyStatusEnum status = DocumentCopyStatusEnum.TODO_SIGN;
    /**
     * 系统英文名称
     */
    private String systemName;
    /**
     * 事项分类中文名称
     */
    private String systemCnName;

    /**
     * 标题
     */
    private String title;

    /**
     * 主办司局
     */
    private String hostDeptName;

    /**
     * 编号
     */
    private String customNumber;
}
