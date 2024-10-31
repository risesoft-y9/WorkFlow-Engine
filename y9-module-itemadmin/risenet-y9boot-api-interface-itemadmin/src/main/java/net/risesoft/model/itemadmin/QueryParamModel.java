package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;
import lombok.NonNull;

/**
 * 所有待办查询参数模型
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class QueryParamModel implements Serializable {

    private static final long serialVersionUID = 616504649565222078L;

    /**
     * 系统名称
     */
    private String systemName;

    /**
     * 事项唯一标示
     */
    private String itemId;

    /**
     * 标题
     */
    private String title;

    /**
     * 编号
     */
    private String customNumber;

    /**
     * 委办局id
     */
    private String bureauIds;

    /**
     * 当前页码
     */
    @NonNull
    Integer page;

    /**
     * 每页行数
     */
    @NonNull
    Integer rows;
}
