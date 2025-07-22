package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 附件配置表
 *
 * @author yihong
 * @version 1.0
 * @see AttachmentConfModel
 */
@Data
public class AttachmentConfModel implements Serializable {

    private static final long serialVersionUID = 2438901849849486770L;

    /**
     * 主键
     */
    private String id;

    /**
     * 附件类型
     */
    private String attachmentType;

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 显示名称
     */
    private String disPlayName;

    /**
     * 显示宽度
     */
    private String disPlayWidth;

    /**
     * 排列方式
     */
    private String disPlayAlign;

    /**
     * 配置类型
     */
    private Integer configType;

    /**
     * 输入框类型
     */
    private String inputBoxType;

    /**
     * 输入框宽度
     */
    private String spanWidth;

    /**
     * 标签名称
     */
    private String labelName;

    /**
     * 绑定数据字典
     */
    private String optionClass;

    /**
     * 是否必填
     */
    private Integer isRequired;

    /**
     * 排序
     */
    private Integer tabIndex;

    /**
     * 更新时间
     */
    private String updateTime;
}
