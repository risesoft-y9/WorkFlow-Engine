package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 正文详情
 *
 * @author mengjuhua
 * @date 2024/06/24
 */
@Data
public class Y9WordInfo implements Serializable {
    private static final long serialVersionUID = -8867458227431637429L;

    /** 候选人组 */
    private String activitiUser;

    /** 正文模板ID */
    private String fileDocumentId;

    /** 流程编号 */
    private String processSerialNumber;

    /** 用户名称 */
    private String userName;

    /**
     * 保存时间
     */
    private String saveDate;
    /**
     * 打开方式
     */
    private String openWordOrPdf;

    /** 是否只读 */
    private String wordReadOnly;

    /** 事项id */
    private String itemId;

    /** 办件状态：todo（待办），doing（在办），done（办结） */
    private String itembox;

    /** 任务id */
    private String taskId;

    /** 是否套红、1为套红word，0为word */
    private String isTaoHong;

    /** 文件类型 */
    private String fileType;

    /** 正文类型 */
    private String docCategory;

    /** 文件仓库Id （uid） */
    private String fileStoreId;

    /** 文件Id （uid） */
    private String uid;

    /** 正文标题 */
    private String documentTitle;

    /** 浏览器类型 */
    private String browser;

    /** 流程实例id */
    private String processInstanceId;

    /** 租户ID */
    private String tenantId;

    /** 人员ID */
    private String userId;

    /** 岗位id */
    private String positionId;

    /** 委办局ID */
    private String currentBureauGuid;
}
