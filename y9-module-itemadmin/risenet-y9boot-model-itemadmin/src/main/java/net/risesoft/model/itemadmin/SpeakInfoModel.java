package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.Data;

/**
 * 发言信息模型
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class SpeakInfoModel implements Serializable {

    private static final long serialVersionUID = 9176582654675102844L;

    /**
     * 唯一标示
     */
    private String id;

    /**
     * 流程实例Id
     */
    private String processInstanceId;

    /**
     * 发言信息
     */
    private String content;

    /**
     * 发言人名称
     */
    private String userName;

    /**
     * 发言人Id
     */
    private String userId;

    /**
     * 是否可以编辑
     */
    private boolean edited;

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
