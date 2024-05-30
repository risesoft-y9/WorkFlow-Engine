package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 发言信息模型
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
     * 发言时间
     */
    private String createTime;

    /**
     * 发言时间
     */
    private String updateTime;

}
