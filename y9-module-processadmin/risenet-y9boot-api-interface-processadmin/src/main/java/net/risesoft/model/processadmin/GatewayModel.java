package net.risesoft.model.processadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 
 * 并行网关节点信息
 * 
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class GatewayModel implements Serializable {

    private static final long serialVersionUID = 3903601005583132464L;
    /**
     * 节点key
     */
    private String taskDefKey;
    /**
     * 任务名称（线上名称存在这里就是线的名字）
     */
    private String taskDefName;
}
