package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 事项模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ItemModel implements Serializable {

    private static final long serialVersionUID = -2211904374246635797L;

    /**
     * 主键
     */
    private String id;
    /**
     * 事项名称
     */
    private String name;
    /**
     * 事项类型
     */
    private String type;
    /**
     * 事项责任制
     */
    private String accountability;
    /**
     * 事项管理员
     */
    private String nature;
    /**
     * 系统级别
     */
    private String sysLevel;
    /**
     * 法定期限
     */
    private Integer legalLimit;
    /**
     * 承诺时限
     */
    private Integer expired;
    /**
     * 工作流GUID
     */
    private String workflowGuid;
    /**
     * 是否网上申办，1“是”、0“否”
     */
    private String isOnline;
    /**
     * 是否对接，1“是”、0“否”
     */
    private String isDocking;
    /**
     * 对接外部系统标识
     */
    private String dockingSystem;
    /**
     * 对接事项id
     */
    private String dockingItemId;
    /**
     * 系统名称
     */
    private String systemName;
    /**
     * 待办任务链接前缀
     */
    private String todoTaskUrlPrefix;
    /**
     * 图标内容
     */
    private String iconData;
    /**
     * 是否可定制事项
     */
    private Boolean customItem;

}