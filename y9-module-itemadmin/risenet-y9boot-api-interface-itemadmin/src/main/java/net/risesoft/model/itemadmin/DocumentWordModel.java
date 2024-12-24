package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * word 文档实体类
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class DocumentWordModel implements Serializable {

    private static final long serialVersionUID = 7218166988367955306L;

    /**
     * 主键
     */

    private String id;

    /**
     * 文件仓库Id
     */
    private String fileStoreId;

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
     * 数据类型，1：word，2：套红word，3：pdf，4：odf
     */
    private Integer type;

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
     * 正文类别,1:办文要报，2：发文稿纸，3：发文单,4：签注意见
     */
    private String wordType;

    /**
     * 任务id
     */
    private String taskId;

    /**
     * 更新时间
     */
    private String updateDate;

    /**
     * 当前委办局id
     */
    private String currentBureauId;

    /**
     * 当前人员名称
     */
    private String currentUserName;

}
