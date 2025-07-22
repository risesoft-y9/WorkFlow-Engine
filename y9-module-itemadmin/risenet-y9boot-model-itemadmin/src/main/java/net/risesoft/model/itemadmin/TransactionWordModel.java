package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 正文信息
 *
 * @author mengjuhua
 * @date 2024/06/24
 */
@Data
public class TransactionWordModel implements Serializable {
    private static final long serialVersionUID = -3649708329591414562L;

    /**
     * 主键
     */
    private String id;

    /** 附件上传ID */
    private String fileStoreId;

    /**
     * 标题
     */
    private String title;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 包括文件名+后缀
     */
    private String fileName;

    /**
     * 文件大小
     */
    private String fileSize;

    /**
     * 上传人员id
     */
    private String userId;

    /**
     * 上传人员名称
     */
    private String userName;

    /**
     * 保存时间
     */
    private String saveDate;

    /**
     * 流程序号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 是否套红、1为套红word，0为word
     */
    private String isTaoHong;

}
