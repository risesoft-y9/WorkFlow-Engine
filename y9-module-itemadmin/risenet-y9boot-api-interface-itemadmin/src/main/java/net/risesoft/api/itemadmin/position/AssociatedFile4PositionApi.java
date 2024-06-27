package net.risesoft.api.itemadmin.position;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.pojo.Y9Result;

/**
 * 关联流程接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface AssociatedFile4PositionApi {

    /**
     * 关联流程计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return Y9Result<Integer>
     */
    @GetMapping("/countAssociatedFile")
    Y9Result<Integer> countAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return Y9Result<Object>
     */
    @PostMapping("/deleteAllAssociatedFile")
    Y9Result<Object> deleteAllAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delIds") String delIds);

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return Y9Result<Object>
     */
    @PostMapping("/deleteAssociatedFile")
    Y9Result<Object> deleteAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("delId") String delId);

    /**
     * 获取关联流程列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<AssociatedFileModel>> 关联流程列表
     */
    @GetMapping("/getAssociatedFileAllList")
    Y9Result<List<AssociatedFileModel>> getAssociatedFileAllList(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存关联流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return Y9Result<Object>
     */
    @PostMapping("/saveAssociatedFile")
    Y9Result<Object> saveAssociatedFile(@RequestParam("tenantId") String tenantId, @RequestParam("positionId") String positionId, @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("processInstanceIds") String processInstanceIds);

}
