package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.platform.Department;
import net.risesoft.pojo.Y9Result;

/**
 * 会签信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface SignDeptInfoApi {

    /**
     * 根据主键删除会签信息
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @PostMapping(value = "/deleteById")
    Y9Result<Object> deleteById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据流程编号获取会签信息
     *
     * @param tenantId 租户ID
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.0
     */
    @GetMapping(value = "/getSignDeptList")
    Y9Result<List<SignDeptModel>> getSignDeptList(@RequestParam("tenantId") String tenantId,
        @RequestParam("deptType") String deptType, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 获取委外会签部门选择树
     *
     * @param tenantId 租户ID
     * @return Y9Result<List<Department>>
     * @since 9.6.0
     */
    @GetMapping(value = "/getSignOutDeptTree")
    Y9Result<List<Department>> getSignOutDeptTree(@RequestParam("tenantId") String tenantId);

    /**
     * 根据流程编号和部门ID判断是否是会签部门
     *
     * @param tenantId 租户ID
     * @param deptId 部门ID
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.0
     */
    @GetMapping(value = "/isSignDept")
    Y9Result<Boolean> isSignDept(@RequestParam("tenantId") String tenantId, @RequestParam("deptId") String deptId,
        @RequestParam("deptType") String deptType, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存会签信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @param deptIds 部门ids
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @PostMapping(value = "/saveSignDept")
    Y9Result<Object> saveSignDept(@RequestParam("tenantId") String tenantId,
        @RequestParam("positionId") String positionId, @RequestParam("deptIds") String deptIds,
        @RequestParam("deptType") String deptType, @RequestParam("processSerialNumber") String processSerialNumber);

    /**
     * 保存会签签名
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @param userName 签字人姓名
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @PostMapping(value = "/saveSignDeptInfo")
    Y9Result<Object> saveSignDeptInfo(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id,
        @RequestParam(value = "userName", required = false) String userName);
}
