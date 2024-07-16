package net.risesoft.model.itemadmin;

import java.io.Serializable;
import java.util.List;

import lombok.Data;

/**
 * 意见列表模型类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class OpinionListModel implements Serializable {

    private static final long serialVersionUID = -3096553071527880599L;

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
     * 是否可新增代录意见
     */
    private Boolean addAgent;

    /**
     * 是否可新增意见
     */
    private Boolean addable;

    /**
     * 意见实体内容
     */
    private OpinionModel opinion;

    /**
     * 是否必签意见
     */
    private Boolean signOpinion;

    /**
     * 一键设置列表
     */
    private List<OpinionFrameOneClickSetModel> oneClickSetList;
}
