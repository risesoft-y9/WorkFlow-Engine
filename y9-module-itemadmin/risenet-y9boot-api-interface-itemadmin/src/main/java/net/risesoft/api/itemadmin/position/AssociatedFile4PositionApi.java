package net.risesoft.api.itemadmin.position;

import java.util.List;

import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.pojo.Y9Result;

/**
 * 关联文件接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface AssociatedFile4PositionApi {

    /**
     * 关联文件计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Integer>
     */
    Y9Result<Integer> countAssociatedFile(String tenantId, String processSerialNumber);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return Y9Result<Object>
     */
    Y9Result<Object> deleteAllAssociatedFile(String tenantId, String processSerialNumber, String delIds);

    /**
     * 删除关联文件
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return Y9Result<Object>
     */
    Y9Result<Object> deleteAssociatedFile(String tenantId, String processSerialNumber, String delId);

    /**
     * 获取关联文件列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<AssociatedFileModel>> 关联文件列表
     */
    Y9Result<List<AssociatedFileModel>> getAssociatedFileAllList(String tenantId, String positionId, String processSerialNumber);

    /**
     * 保存关联文件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return Y9Result<Object>
     */
    Y9Result<Object> saveAssociatedFile(String tenantId, String positionId, String processSerialNumber, String processInstanceIds);
}
