package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 自定义列表视图信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class CustomViewModel implements Serializable {

    private static final long serialVersionUID = -483436371202350656L;

    // 主键
    private String id;

    // 事项Id
    private String itemId;

    // 表单Id
    private String formId;

    // 视图类型
    private String viewType;

    // 字段id
    private String fieldId;

    // 字段名称
    private String columnName;

    // 字段显示名称
    private String disPlayName;

    // 序号
    private Integer tabIndex;

    // 人员id
    private String userId;

    // 人员名称
    private String userName;

    // 生成时间
    private String createTime;
}
