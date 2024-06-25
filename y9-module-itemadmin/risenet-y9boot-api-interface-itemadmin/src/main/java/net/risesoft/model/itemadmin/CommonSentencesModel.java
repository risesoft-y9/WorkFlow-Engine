package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 常用语
 *
 * @author zhangchongjie
 * @date 2024/06/24
 */
@Data
public class CommonSentencesModel implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 8568677891120022088L;

    /**
     * 主键
     */
    private String id;

    /**
     * 人员guid"
     */
    private String userId;

    /**
     * 常用语内容
     */
    private String content;

    /**
     * 排序号
     */
    private Integer tabIndex;

    /**
     * 使用次数，点击次数
     */
    private Integer useNumber;

    /**
     * 租户Id
     */
    private String tenantId;
}
