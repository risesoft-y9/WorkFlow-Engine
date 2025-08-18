package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Builder;
import lombok.Data;

/**
 * 意见框
 * 
 * @author qinman
 * @date 2025/08/18
 */
@Data
@Builder
public class OpinionFrameModel implements Serializable {

    private static final long serialVersionUID = 2877090725036243745L;

    /**
     * 意见框唯一标示
     */
    private String mark;

    /**
     * 意见框名称
     */
    private String name;

    /**
     * 意见列表
     */
    private List<OpinionListModel> opinionList;
}
