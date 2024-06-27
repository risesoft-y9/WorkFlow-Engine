package net.risesoft.service;

import java.util.Map;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
public interface AssociatedFileService {

    /**
     * 关联流程计数
     * 
     * @param processSerialNumber
     * @return
     */
    int countAssociatedFile(String processSerialNumber);

    /**
     * 删除多个关联流程
     * 
     * @param processSerialNumber
     * @param delIds
     * @return
     */
    boolean deleteAllAssociatedFile(String processSerialNumber, String delIds);

    /**
     * 删除关联流程
     * 
     * @param processSerialNumber
     * @param delId
     * @return
     */
    boolean deleteAssociatedFile(String processSerialNumber, String delId);

    /**
     * 获取关联流程列表，包括未办结件
     * 
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> getAssociatedFileAllList(String processSerialNumber);

    /**
     * 获取关联流程列表
     * 
     * @param processSerialNumber
     * @return
     */
    Map<String, Object> getAssociatedFileList(String processSerialNumber);

    /**
     * 保存关联流程
     * 
     * @param processSerialNumber
     * @param processInstanceIds
     * @return
     */
    boolean saveAssociatedFile(String processSerialNumber, String processInstanceIds);
}
