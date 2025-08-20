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
public class OpinionFrameModel implements Serializable {

    private static final long serialVersionUID = -8677057712529922156L;

    /**
     * 意见框标识
     */
    private String mark;

    /**
     * 意见框名称
     */
    private String name;

    /**
     * 是否可新增意见
     */
    private Boolean addable;

    /**
     * 意见实体内容
     */
    private List<OpinionModel> opinionList;

    /**
     * 是否必签意见
     */
    private Boolean signOpinion;

    /**
     * 一键设置列表
     */
    private List<OpinionFrameOneClickSetModel> oneClickSetList;
}
