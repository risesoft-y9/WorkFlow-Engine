package net.risesoft.model.processadmin;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 执行模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
@AllArgsConstructor
public class Y9BpmnModel implements Serializable {

    private static final long serialVersionUID = 2725030569008818532L;
    /**
     * flowNodeModelList
     */
    private List<FlowNodeModel> flowNodeModelList;
    /**
     * linkNodeModelList
     */
    private List<LinkNodeModel> linkNodeModelList;

    /**
     * txtFlowPath
     */
    private String txtFlowPath;

    /**
     * 是否办结
     */
    private boolean completed;
}
