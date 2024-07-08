package net.risesoft.api;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.AssociatedFile4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.AssociatedFileModel;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.AssociatedFileService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 关联流程接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/associatedFile4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class AssociatedFileApiImpl implements AssociatedFile4PositionApi {

    private final AssociatedFileService associatedFileService;

    private final PositionApi positionManager;

    /**
     * 关联流程计数
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data是关联流程计数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> countAssociatedFile(@RequestParam @NotBlank String tenantId,
        @RequestParam @NotBlank String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        int num = associatedFileService.countAssociatedFile(processSerialNumber);
        return Y9Result.success(num);
    }

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delIds 关联流程实例id(,隔开)
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteAllAssociatedFile(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String delIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        associatedFileService.deleteAllAssociatedFile(processSerialNumber, delIds);
        return Y9Result.success();
    }

    /**
     * 删除关联流程
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @param delId 关联流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteAssociatedFile(@RequestParam String tenantId,
        @RequestParam String processSerialNumber, @RequestParam String delId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        associatedFileService.deleteAssociatedFile(processSerialNumber, delId);
        return Y9Result.success();
    }

    /**
     * 获取关联流程列表,包括未办结件
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<AssociatedFileModel>>} 通用请求返回对象 - data是关联流程列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<AssociatedFileModel>> getAssociatedFileAllList(@RequestParam String tenantId,
        @RequestParam String positionId, @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        List<AssociatedFileModel> list = associatedFileService.getAssociatedFileAllList(processSerialNumber);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 保存关联流程
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param processSerialNumber 流程编号
     * @param processInstanceIds 关联的流程实例ids
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveAssociatedFile(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String processSerialNumber, @RequestParam String processInstanceIds) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionManager.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        associatedFileService.saveAssociatedFile(processSerialNumber, processInstanceIds);
        return Y9Result.success();
    }
}
