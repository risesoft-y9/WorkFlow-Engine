package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 流程实例模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@AllArgsConstructor
public class Y9FlowableCountModel implements Serializable {

    private static final long serialVersionUID = 2787409946198547130L;
    /**
     * 待办数量
     */
    private long todoCount;
    /**
     * 在办数量
     */
    private long doingCount;
    /**
     * 办结数量
     */
    private long doneCount;
}
