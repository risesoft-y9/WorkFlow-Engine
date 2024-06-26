package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 新建事项列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class AddItemListModel implements Serializable {

    private static final long serialVersionUID = 2889028913656422110L;
    /**
     * 资源id
     */
    private String id;

    /**
     * 事项Id
     */
    private String itemId;

    /**
     * 事项名称
     */
    private String itemName;

    /**
     * 事项Id
     */
    private String url;

    /**
     * 事项图标
     */
    private String appIcon;

    /**
     * 待办数量
     */
    private Integer todoCount;

    /**
     * 事项名称
     */
    private String name;

    /**
     * 流程定义key
     */
    private String processDefinitionKey;

}
