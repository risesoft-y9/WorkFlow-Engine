package net.risesoft.api.itemadmin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
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
@Validated
public interface AssociatedFileApi {

    /**
     * 关联流程计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是关联流程计数
     * @since 9.6.6
     */
    @GetMapping("/countAssociatedFile")
    Y9Result<Integer> countAssociatedFile(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("processSerialNumber") @NotBlank String processSerialNumber);

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteAllAssociatedFile")
    Y9Result<Object> deleteAllAssociatedFile(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("processSerialNumber") @NotBlank String processSerialNumber,
        @RequestParam("delIds") String delIds);

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteAssociatedFile")
    Y9Result<Object> deleteAssociatedFile(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("processSerialNumber") @NotBlank String processSerialNumber, @RequestParam("delId") String delId);

    /**
     * 获取关联流程列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<AssociatedFileModel>>} 通用请求返回对象 - data是关联流程列表
     * @since 9.6.6
     */
    @GetMapping("/getAssociatedFileAllList")
    Y9Result<List<AssociatedFileModel>> getAssociatedFileAllList(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存关联流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/saveAssociatedFile")
    Y9Result<Object> saveAssociatedFile(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("positionId") @NotBlank String positionId,
        @RequestParam("processSerialNumber") String processSerialNumber,
        @RequestParam("processInstanceIds") String processInstanceIds);

}
