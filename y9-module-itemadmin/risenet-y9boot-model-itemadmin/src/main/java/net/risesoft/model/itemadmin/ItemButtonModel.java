package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.risesoft.enums.ItemButtonTypeEnum;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemButtonModel implements Serializable {

    private static final long serialVersionUID = -695604520841204814L;

    /**
     * 按钮唯一标示
     */
    private String key;

    /**
     * 按钮名称
     */
    private String name;

    /**
     * 按钮类型
     */
    private ItemButtonTypeEnum buttonType  =ItemButtonTypeEnum.COMMON;

    /**
     * 组织id
     */
    private List<String> orgUnitIds;

    /**
     * 按钮描述
     */
    private String description;

    /**
     * 序号
     */
    private Integer tabIndex;

    public ItemButtonModel(String key, String name, ItemButtonTypeEnum buttonType) {
        this.key = key;
        this.name = name;
        this.buttonType = buttonType;
    }

    public ItemButtonModel(String key, String name, ItemButtonTypeEnum buttonType, Integer tabIndex) {
        this.key = key;
        this.name = name;
        this.buttonType = buttonType;
        this.tabIndex = tabIndex;
    }
}
