package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 事项意见框架角色模型类
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ItemOpinionFrameRoleModel implements Serializable {

    private static final long serialVersionUID = 7079860101823150509L;

    /**
     * 主键
     */
    private String id;

    /**
     * 事项意见框绑定ID
     */
    private String itemOpinionFrameId;
    /**
     * 角色ID
     */
    private String roleId;
    /**
     * 角色名称
     */
    private String roleName;

}
