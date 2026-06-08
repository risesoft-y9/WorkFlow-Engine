package net.risesoft.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SignDeptInfoApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.SignDeptInfo;
import net.risesoft.entity.SignOutDept;
import net.risesoft.entity.SignOutDeptType;
import net.risesoft.enums.platform.org.OrgTypeEnum;
import net.risesoft.model.itemadmin.SignDeptModel;
import net.risesoft.model.platform.org.Department;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.SignOutDeptRepository;
import net.risesoft.repository.jpa.SignOutDeptTypeRepository;
import net.risesoft.service.SignDeptInfoService;
import net.risesoft.service.SignDeptOutService;
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

    private final PositionApi positionApi;

    private final SignDeptOutService signDeptOutService;

    private final SignOutDeptTypeRepository signOutDeptTypeRepository;

    private final SignOutDeptRepository signOutDeptRepository;

    /**
     * 添加会签信息
     *
     * @param deptIds 部门ids
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> addSignDept(@RequestParam String deptIds, @RequestParam String deptType,
        @RequestParam String processSerialNumber) {
        signDeptInfoService.addSignDept(processSerialNumber, deptType, deptIds);
        return Y9Result.success();
    }

    /**
     * 根据主键删除会签信息
     *
     * @param id 主键
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String id) {
        signDeptInfoService.deleteById(id);
        return Y9Result.success();
    }

    @Override
    public Y9Result<Map<String, String>> findByDeptNameMax(@RequestParam String deptNameMax) {
        Map<String, String> map = new HashMap<String, String>();

        SignOutDept signOutDept = signDeptOutService.findByDeptNameMax(deptNameMax);
        if (signOutDept != null) {
            String fullDeptName = signOutDept.getFullDeptName();
            String ldcw = signOutDept.getLdcw();
            String deptName = signOutDept.getDeptName();
            String deptSuffix = signOutDept.getDeptSuffix();
            map.put("fullDeptName", fullDeptName);
            map.put("ldcw", ldcw);
            map.put("deptName", deptName);
            map.put("deptSuffix", deptSuffix);
        }
        return Y9Result.success(map);
    }

    /**
     * 根据流程编号获取会签信息
     *
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<List < SignDeptModel>
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<SignDeptModel>> getSignDeptList(@RequestParam String deptType,
        @RequestParam String processSerialNumber) {
        List<SignDeptInfo> list = signDeptInfoService.getSignDeptList(processSerialNumber, deptType);
        List<SignDeptModel> modelList = new ArrayList<>();
        for (SignDeptInfo signDeptInfo : list) {
            SignDeptModel model = new SignDeptModel();
            Y9BeanUtil.copyProperties(signDeptInfo, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

    /**
     * 获取委外会签单位树
     *
     * @return Y9Result<List < Department>>
     * @since 9.6.0
     */
    @Override
    public Y9Result<List<Department>> getSignOutDeptTree(@RequestParam(required = false) String id) {
        List<Department> modelList = new ArrayList<>();
        if (StringUtils.isBlank(id)) {
            List<SignOutDeptType> typelist = signOutDeptTypeRepository.findByIsForbiddenOrderByTabIndexAsc(0);
            for (SignOutDeptType info : typelist) {
                Department department = new Department();
                department.setId(String.valueOf(info.getDeptTypeId()));
                department.setOrgType(OrgTypeEnum.DEPARTMENT);
                department.setName(info.getDeptType());
                department.setParentId("");
                modelList.add(department);
            }
        } else {
            List<SignOutDept> list = signOutDeptRepository.findByDeptTypeIdAndIsForbiddenOrderByDeptOrderAsc(id, 0);
            for (SignOutDept signOutDept : list) {
                Department department = new Department();
                department.setId(signOutDept.getDeptId());
                department.setOrgType(OrgTypeEnum.DEPARTMENT);
                department.setName(signOutDept.getDeptName());
                department.setParentId(signOutDept.getDeptTypeId());
                modelList.add(department);
            }
        }
        return Y9Result.success(modelList);
    }

    /**
     * 根据流程编号和部门ID判断是否是会签部门
     * 
     * @param deptId 部门ID
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @return Y9Result<Boolean>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Boolean> isSignDept(@RequestParam String deptId, @RequestParam String deptType,
        @RequestParam String processSerialNumber) {
        List<SignDeptInfo> list = signDeptInfoService.getSignDeptList(processSerialNumber, deptType);
        if (list.isEmpty()) {
            return Y9Result.success(false);
        }
        return Y9Result.success(list.stream().anyMatch(signDeptInfo -> signDeptInfo.getDeptId().equals(deptId)));
    }

    /**
     * 保存会签信息
     *
     * @param deptIds 部门ids
     * @param deptType 单位类型（0：委内，1：委外）
     * @param processSerialNumber 流程编号
     * @param tzsDeptId 部门id，不为空，表示需要将显示名称改为原司局单位名称
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveSignDept(@RequestParam String deptIds, @RequestParam String deptType,
        @RequestParam String processSerialNumber, String tzsDeptId) {
        signDeptInfoService.saveSignDept(processSerialNumber, deptType, deptIds, tzsDeptId);
        return Y9Result.success();
    }

    /**
     * 保存会签签名
     *
     * @param id 主键
     * @param userName 签字人姓名
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> saveSignDeptInfo(@RequestParam String id, String userName) {
        signDeptInfoService.saveSignDeptInfo(id, userName);
        return Y9Result.success();
    }

    /**
     * 插入或更新会签部门，更新显示名称
     *
     * @param processSerialNumber 流程编号
     * @param type 中央预算内投资计划下达类文件类型,1为是，0为否
     * @param tzsDeptId 司局部门id
     * @return Y9Result<Object>
     * @since 9.6.0
     */
    @Override
    public Y9Result<Object> updateSignDept(@RequestParam String processSerialNumber, @RequestParam String type,
        @RequestParam String tzsDeptId) {
        signDeptInfoService.updateSignDept(processSerialNumber, Y9LoginUserHolder.getPositionId(), type, tzsDeptId);
        return Y9Result.success();
    }
}
