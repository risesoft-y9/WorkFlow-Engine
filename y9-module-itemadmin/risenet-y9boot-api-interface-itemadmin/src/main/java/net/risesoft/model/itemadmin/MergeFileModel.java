package net.risesoft.model.itemadmin;

import java.io.Serializable;

import lombok.Data;

/**
 * 合并文件信息
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Data
public class MergeFileModel implements Serializable, Comparable<MergeFileModel> {

    private static final long serialVersionUID = 8424588252309828549L;

    private String id;

    // 文件名称
    private String fileName;

    // 源文件id,版式文件的源文件id,由哪个文件转的版式文件
    private String sourceFileId;

    // 文件类型,1为合并文件,2为合并版式文件
    private String fileType;

    // 列表类型,1为附件合并,2为文件合并
    private String listType;

    // 文件仓库Id
    private String fileStoreId;

    // 流程实例编号
    private String processSerialNumber;

    // 合并人
    private String personName;

    // 合并人员Id
    private String personId;

    // 合并时间
    private String createTime;

    @Override
    public int compareTo(MergeFileModel mf) {
        return this.createTime.compareTo(mf.getCreateTime());
    }
}