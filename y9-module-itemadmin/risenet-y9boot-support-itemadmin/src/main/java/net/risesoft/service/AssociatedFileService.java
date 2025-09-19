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
     * 关联流程计数
     *
     * @param processSerialNumber 流程序列号
     * @return int
     */
    int countAssociatedFile(String processSerialNumber);

    /**
     * 删除多个关联流程
     *
     * @param processSerialNumber 流程序列号
     * @param delIds 删除的关联流程ID
     */
    void deleteAllAssociatedFile(String processSerialNumber, String delIds);

    /**
     * 删除关联流程
     *
     * @param processSerialNumber 流程序列号
     * @param delId 删除的关联流程ID
     */
    void deleteAssociatedFile(String processSerialNumber, String delId);

    /**
     * 获取关联流程列表，包括未办结件
     *
     * @param processSerialNumber 流程序列号
     * @return List<AssociatedFileModel>
     */
    List<AssociatedFileModel> listAssociatedFileAll(String processSerialNumber);

    /**
     * 保存关联流程
     *
     * @param processSerialNumber 流程序列号
     * @param processInstanceIds 流程实例ID
     */
    void saveAssociatedFile(String processSerialNumber, String processInstanceIds);
}
