package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 历史正文信息
 *
 * @author mengjuhua
 * @date 2024/06/24
 */
@Data
public class TransactionHistoryWordModel implements Serializable {
    private static final long serialVersionUID = -6656204742636094799L;

    /**
     * 主键
     */
    private String id;

    /** 文件仓库Id */
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
     * 是否套红,1为套红word，0为word
     */
    private String isTaoHong;

    /**
     * 保存时间
     */
    private String saveDate;

    /**
     * 任务ID
     */
    private String taskId;

    /**
     * 流程序号
     */
    private String processSerialNumber;

    /**
     * 流程实例id
     */
    private String processInstanceId;

    /**
     * 打开方式
     */
    private String openWordOrPdf;
}
