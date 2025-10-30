package net.risesoft.model;

import java.io.Serializable;

import lombok.Data;

@Data
public class ItemBoxAndTaskIdModel implements Serializable {
    private static final long serialVersionUID = 5455103964590209018L;
    /**
     * 列表类型 {@link net.risesoft.enums.ItemBoxTypeEnum}
     */
    private String itemBox;
    /**
     * 任务ID 当itemBox为TODO时，用于打开办件
     *
     */
    private String taskId;

    public ItemBoxAndTaskIdModel(String itemBox, String taskId) {
        this.itemBox = itemBox;
        this.taskId = taskId;
    }
}
