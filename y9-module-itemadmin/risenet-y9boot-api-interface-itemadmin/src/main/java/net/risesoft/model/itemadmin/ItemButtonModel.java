package net.risesoft.model.itemadmin;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
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
    private Integer buttonType;

    /**
     * 按钮描述
     */
    private String description;

    /**
     * 序号
     */
    private Integer tabIndex;

    public ItemButtonModel(String key, String name, Integer buttonType) {
        this.key = key;
        this.name = name;
        this.buttonType = buttonType;
    }
}
