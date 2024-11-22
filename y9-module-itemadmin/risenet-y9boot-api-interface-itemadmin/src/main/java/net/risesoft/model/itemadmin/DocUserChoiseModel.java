package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 发送选人获取数据
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class DocUserChoiseModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 3535052079590863497L;

    /**
     * 是否存在岗位
     */
    private boolean existPosition;

    /**
     * 是否存在部门
     */
    private boolean existDepartment;

    /**
     * 是否存在用户组
     */
    private boolean existCustomGroup;

    /**
     * 任务节点多实例类型
     */
    private String multiInstance;

    /**
     * 节点类型
     */
    private String type;

    /**
     * 流程定义Id
     */
    private String processDefinitionId;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 事项id
     */
    private String itemId;

    /**
     * 是否具有主协办状态
     */
    private boolean sponsorStatus;

    /**
     * 任务路由
     */
    private String routeToTask;

}
