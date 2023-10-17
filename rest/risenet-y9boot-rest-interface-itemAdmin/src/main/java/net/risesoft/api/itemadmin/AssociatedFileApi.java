package net.risesoft.api.itemadmin;

import java.util.Map;

/**
 * 关联文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface AssociatedFileApi {

    /**
     * 关联文件计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return int
     */
    int countAssociatedFile(String tenantId, String processSerialNumber);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return boolean 是否删除成功
     */
    boolean deleteAllAssociatedFile(String tenantId, String userId, String processSerialNumber, String delIds);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return boolean 是否删除成功
     */
    boolean deleteAssociatedFile(String tenantId, String userId, String processSerialNumber, String delId);

    /**
     * 获取关联文件列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getAssociatedFileAllList(String tenantId, String userId, String processSerialNumber);

    /**
     * 获取关联文件列表
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @return Map&lt;String, Object&gt;
     */
    Map<String, Object> getAssociatedFileList(String tenantId, String userId, String processSerialNumber);

    /**
     * 保存关联文件
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return boolean 是否保存成功
     */
    boolean saveAssociatedFile(String tenantId, String userId, String processSerialNumber, String processInstanceIds);
}
