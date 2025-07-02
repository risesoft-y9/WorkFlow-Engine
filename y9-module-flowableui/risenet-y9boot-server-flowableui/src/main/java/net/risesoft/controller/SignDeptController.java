package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.log.FlowableOperationTypeEnum;
import net.risesoft.log.annotation.FlowableLog;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.platform.Department;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 会签信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/signDept", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class SignDeptController {

    private final SignDeptInfoApi signDeptInfoApi;

    /**
     * 删除会签信息
     *
     * @param id 主键id
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "删除会签信息", operationType = FlowableOperationTypeEnum.DELETE)
    @PostMapping(value = "/deleteById")
    public Y9Result<Object> deleteById(@RequestParam String id) {
        return signDeptInfoApi.deleteById(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), id);
    }

    /**
     * 获取会签信息
     *
     * @param processSerialNumber 流程编号
     * @param deptType 部门类型
     * @return Y9Result<List<SignDeptModel>>
     */
    @FlowableLog(operationName = "获取会签信息")
    @GetMapping(value = "/getSignDeptList")
    public Y9Result<List<SignDeptModel>> getSignDeptList(@RequestParam String processSerialNumber,
        @RequestParam String deptType) {
        return signDeptInfoApi.getSignDeptList(Y9LoginUserHolder.getTenantId(), deptType, processSerialNumber);
    }

    /**
     * 获取委外会签部门树
     * 
     * @param id 部门id
     * @return Y9Result<List<Department>>
     */
    @FlowableLog(operationName = "获取委外会签部门树")
    @GetMapping(value = "/getSignOutDeptTree")
    public Y9Result<List<Department>> getSignOutDeptTree(@RequestParam(required = false) String id) {
        return signDeptInfoApi.getSignOutDeptTree(Y9LoginUserHolder.getTenantId(), id);
    }

    /**
     * 保存会签部门
     *
     * @param processSerialNumber 流程编号
     * @param deptType 部门类型
     * @param deptIds 部门id
     * @param tzsDeptId 部门id，不为空，表示需要将显示名称改为原司局单位名称
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "保存会签部门", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveSignDept")
    public Y9Result<Object> saveSignDept(@RequestParam String processSerialNumber, @RequestParam String deptType,
        @RequestParam String deptIds, @RequestParam(required = false) String tzsDeptId) {
        return signDeptInfoApi.saveSignDept(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), deptIds,
            deptType, processSerialNumber, tzsDeptId);
    }

    /**
     * 保存会签签名
     *
     * @param id 主键id
     * @param userName 用户名
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "保存会签签名", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/saveSignDeptInfo")
    public Y9Result<Object> saveSignDeptInfo(@RequestParam String id, @RequestParam(required = false) String userName) {
        return signDeptInfoApi.saveSignDeptInfo(Y9LoginUserHolder.getTenantId(), id, userName);
    }

    /**
     * 插入或更新会签部门，更新显示名称
     *
     * @param processSerialNumber 流程编号
     * @param type 中央预算内投资计划下达类文件类型,1为是，0为否
     * @param tzsDeptId 司局部门id
     * @return Y9Result<Object>
     */
    @FlowableLog(operationName = "插入或更新会签部门，更新显示名称", operationType = FlowableOperationTypeEnum.SAVE)
    @PostMapping(value = "/updateSignDept")
    public Y9Result<Object> updateSignDept(@RequestParam String processSerialNumber, @RequestParam String type,
        @RequestParam String tzsDeptId) {
        return signDeptInfoApi.updateSignDept(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
            processSerialNumber, type, tzsDeptId);
    }

}
