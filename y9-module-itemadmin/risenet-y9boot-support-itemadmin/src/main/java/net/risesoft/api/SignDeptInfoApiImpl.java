package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;


import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 会签信息接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/signDept", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignDeptInfoApiImpl implements SignDeptInfoApi {

    private final SignDeptInfoService signDeptInfoService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 根据主键删除会签信息
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        signDeptInfoService.deleteById(id);
        return Y9Result.success();
    }

    /**
     * 根据流程实例id获取会签信息
     *
     * @param tenantId 租户ID
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processInstanceId 流程实例id
     * @return Y9Result<List<SignDeptModel>
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<SignDeptModel>> getSignDeptList(@RequestParam String tenantId, @RequestParam String deptType,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SignDeptInfo> list = signDeptInfoService.getSignDeptList(processInstanceId, deptType);
        List<SignDeptModel> modelList = new ArrayList<>();
        for (SignDeptInfo signDeptInfo : list) {
            SignDeptModel model = new SignDeptModel();
            Y9BeanUtil.copyProperties(signDeptInfo, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 保存会签信息
     *
     * @param tenantId 租户ID
     * @param positionId 岗位id
     * @param deptIds 部门ids
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processInstanceId 流程实例id
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveSignDept(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String deptIds, @RequestParam String deptType, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, positionId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        signDeptInfoService.saveSignDept(processInstanceId, deptType, deptIds);
        return Y9Result.success();
    }

    /**
     * 保存会签签名
     *
     * @param tenantId 租户ID
     * @param id 主键
     * @param userName 签字人姓名
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveSignDeptInfo(@RequestParam String tenantId, @RequestParam String id,
        @RequestParam String userName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        signDeptInfoService.saveSignDeptInfo(id, userName);
        return Y9Result.success();
    }
}
