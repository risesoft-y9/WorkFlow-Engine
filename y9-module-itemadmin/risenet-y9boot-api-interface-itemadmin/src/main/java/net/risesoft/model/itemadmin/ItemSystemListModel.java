package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 事项系统列表
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ItemSystemListModel implements Serializable {

    private static final long serialVersionUID = 6908045098273484292L;
    /**
     * 系统英文名称
     */
    private String systemName;

    /**
     * 系统名称
     */
    private String sysLevel;

}
