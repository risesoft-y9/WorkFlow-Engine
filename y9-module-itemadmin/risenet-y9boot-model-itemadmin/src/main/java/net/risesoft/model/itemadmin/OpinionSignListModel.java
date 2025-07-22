package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 会签意见列表模型类
 *
 * @author qinman
 * @date 2024/12/16
 */
@Data
public class OpinionSignListModel implements Serializable {

    private static final long serialVersionUID = -5693064126688354858L;
    /**
     * 意见框标识
     */
    private String opinionFrameMark;

    /**
     * 意见是否编辑过
     */
    private Boolean isEdit;

    /**
     * 意见是否可编辑
     */
    private Boolean editable;

    /**
     * 是否可新增意见
     */
    private Boolean addable;

    /**
     * 意见实体内容
     */
    private OpinionSignModel opinionSignModel;

    /**
     * 是否必签意见
     */
    private Boolean signOpinion;

    /**
     * 一键设置列表
     */
    private List<OpinionFrameOneClickSetModel> oneClickSetList;
}
