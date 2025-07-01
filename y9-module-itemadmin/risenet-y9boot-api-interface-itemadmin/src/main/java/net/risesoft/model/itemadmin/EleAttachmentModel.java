package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * @author qinman
 * @date 2024/11/11
 */
@Data
public class EleAttachmentModel implements Serializable {

    private static final long serialVersionUID = -6966320620917025550L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程实例编号
     */
    private String processSerialNumber;

    /**
     * 文件仓库Id
     */
    private String fileStoreId;

    /**
     * 文件名称
     */
    private String name;

    /**
     * 密级
     */
    private String miJi;

    /**
     * 附件类型
     */
    private String attachmentType;

    /**
     * 上传时间
     */
    private String uploadTime;

    /**
     * 上传人
     */
    private String personName;

    /**
     * 上传人员Id
     */
    private String personId;

    /**
     * 文件索引
     */
    private Integer tabIndex;
}
