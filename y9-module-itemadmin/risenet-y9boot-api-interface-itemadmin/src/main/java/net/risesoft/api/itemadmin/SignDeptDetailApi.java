package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.SignDeptDetailModel;
import net.risesoft.pojo.Y9Result;

/**
 * 会签信息接口
 *
 * @author qinman
 * @date 2024/12/13
 */
public interface SignDeptDetailApi {

    /**
     * 根据主键删除会签信息
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程实例id获取会签信息
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程实例id
     * @param deptId 单位ID
     * @return Y9Result<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/findByProcessSerialNumberAndDeptId4Latest")
    Y9Result<SignDeptDetailModel> findByProcessSerialNumberAndDeptId4Latest(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("deptId") String deptId);

    /**
     * 根据流程实例id获取会签信息
     *
     * @param tenantId 租户ID
     * @param id 会签部门详情id
     * @return Y9Result<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/findById")
    Y9Result<SignDeptDetailModel> findById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程实例id获取会签信息
     *
     * @param tenantId 租户ID
     * @param processSerialNumber 流程序列号
     * @param status 会签状态 {@link net.risesoft.enums.SignDeptDetailStatusEnum}
     * @return Y9Result<List<SignDeptDetailModel>
     * @since 9.6.8
     */
    @GetMapping(value = "/findByProcessSerialNumberAndStatus")
    Y9Result<List<SignDeptDetailModel>> findByProcessSerialNumberAndStatus(@RequestParam("tenantId") String tenantId,
        @RequestParam("processSerialNumber") String processSerialNumber, @RequestParam("status") int status);

    /**
     * 保存会签信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @param signDeptDetailModel 会签部门信息
     * @return Y9Result<Object>
     * @since 9.6.8
     */
    @PostMapping(value = "/saveOrUpdate")
    Y9Result<Object> saveOrUpdate(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestBody SignDeptDetailModel signDeptDetailModel);
}
