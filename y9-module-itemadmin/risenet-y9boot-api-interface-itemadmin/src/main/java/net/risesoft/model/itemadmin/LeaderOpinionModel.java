package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 领导批示
 * 
 * @author qinman
 * @date 2024/11/11
 */
@Data
public class LeaderOpinionModel implements Serializable {

    private static final long serialVersionUID = -5689476613176053832L;

    /**
     * 主键
     */
    private String id;

    /**
     * 流程序列号
     */
    private String processSerialNumber;

    /**
     * 批示领导岗位名称
     */
    private String positionName;

    /**
     * 批示内容
     */
    private String opinionContent;

    /**
     * 批示日期
     */
    private String opinionDate;

    /**
     * 文件仓库Id
     */
    private String fileStoreId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 上传人
     */
    private String personName;

    /**
     * 上传人员Id
     */
    private String personId;

    /**
     * 生成时间
     */
    private String createDate;

    /**
     * 修改时间
     */
    private String updateDate;
}
