package net.risesoft.model.itemadmin;

import lombok.Data;

import java.io.Serializable;

/**
 * 列表视图配置模型
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class ItemViewConfModel implements Serializable {

    private static final long serialVersionUID = -7907684948565390289L;

    /**
     * 主键
     */
    private String id;
    /**
     *  事项ID
     */
    private String itemId;
    /**
     *   视图类型
     */
    private String viewType;
    /**
     *   表名
     */
    private String tableName;
    /**
     *       字段名
     */
    private String columnName;
    /**
     *  显示名称
     */
    private String disPlayName;
    /**
     *  显示宽度
     */
    private String disPlayWidth;
    /**
     *  显示对齐方式
     */
    private String disPlayAlign;
    /**
     *  排序
     */
    private Integer tabIndex;
    /**
     * 用户ID
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 创建时间
     */
    private String createTime;
    /**
     * 更新时间
     */
    private String updateTime;
    /**
     *是否开启搜索条件,绑定数据库表和字段时，可开启搜索条件
     */
    private Integer openSearch;
    /**
     *输入框类型,search-带图标前缀的搜索框,input,select,date
     */
    private String inputBoxType;
    /**
     *搜索框宽度
     */
    private String spanWidth;
    /**
     *搜索名称,不填写则使用disPlayName显示名称
     */
    private String labelName;
    /**
     *绑定数据字典,输入框类型select时使用
     */
    private String optionClass;
}
