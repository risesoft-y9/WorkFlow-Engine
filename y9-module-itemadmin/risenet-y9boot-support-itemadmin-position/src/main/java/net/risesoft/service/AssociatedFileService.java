package net.risesoft.service;

import java.util.List;

import net.risesoft.model.itemadmin.AssociatedFileModel;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface AssociatedFileService {

    /**
     * 关联文件计数
     *
     * @param processSerialNumber
     * @return
     */
    int countAssociatedFile(String processSerialNumber);

    /**
     * 删除多个关联文件
     *
     * @param processSerialNumber
     * @param delIds
     * @return
     */
    boolean deleteAllAssociatedFile(String processSerialNumber, String delIds);

    /**
     * 删除关联文件
     *
     * @param processSerialNumber
     * @param delId
     * @return
     */
    boolean deleteAssociatedFile(String processSerialNumber, String delId);

    /**
     * 获取关联文件列表，包括未办结件
     *
     * @param processSerialNumber
     * @return
     */
    List<AssociatedFileModel> getAssociatedFileAllList(String processSerialNumber);

    /**
     * 保存关联文件
     *
     * @param processSerialNumber
     * @param processInstanceIds
     * @return
     */
    boolean saveAssociatedFile(String processSerialNumber, String processInstanceIds);
}
