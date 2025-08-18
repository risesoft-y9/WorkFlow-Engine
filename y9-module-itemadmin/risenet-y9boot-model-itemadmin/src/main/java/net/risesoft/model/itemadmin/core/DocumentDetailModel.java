package net.risesoft.model.itemadmin.core;

import java.io.Serializable;

import lombok.Data;

/**
 * 流程详情数据
 *
 * @author qinman
 * @date 2024/11/01
 */
@Data
public class DocumentDetailModel extends DocumentDetailBaseModel implements Serializable {

    private static final long serialVersionUID = -5471867511958169456L;

    /**
     * 关联文件数
     */
    private Integer associatedFileNum;

    /**
     * 正文数
     */
    private Integer docNum;

    /**
     * 附件数
     */
    private Integer fileNum;

    /**
     * 沟通交流数
     */
    private Integer speakInfoNum;

    /**
     * 关注
     */
    private Boolean follow;
}
