package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.dictionary.OptionValueApi;
import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.controller.vo.NodeTreeVO;
import net.risesoft.entity.DynamicRole;
import net.risesoft.enums.DynamicRoleKindsEnum;
import net.risesoft.model.platform.OptionValue;
import net.risesoft.model.platform.Role;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DynamicRoleService;
import net.risesoft.util.PackageClassFinder;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/dynamicRole", produces = MediaType.APPLICATION_JSON_VALUE)
public class DynamicRoleRestController {

    private final DynamicRoleService dynamicRoleService;

    private final OptionValueApi optionValueApi;

    private final RoleApi roleApi;

    /**
     * 获取动态角色列表
     *
     * @return Y9Result<List<DynamicRole>>
     */
    @GetMapping(value = "/dynamicRoleList")
    public Y9Result<List<DynamicRole>> dynamicRoleList() {
        List<DynamicRole> drList = dynamicRoleService.listAll();
        List<OptionValue> dpcList =
            optionValueApi.listByType(Y9LoginUserHolder.getTenantId(), "departmentPropCategory").getData();
        List<Role> roleList = roleApi.listRoleByParentId(InitDataConsts.TOP_PUBLIC_ROLE_ID).getData();
        drList.stream().filter(dynamicRole -> null != dynamicRole.getKinds()).forEach(dynamicRole -> {
            if (dynamicRole.getKinds().equals(DynamicRoleKindsEnum.DEPT_PROP_CATEGORY.getValue())) {
                List<OptionValue> dpcListFilter = dpcList.stream()
                    .filter(dpc -> dpc.getCode().equals(String.valueOf(dynamicRole.getDeptPropCategory())))
                    .collect(Collectors.toList());
                if (dpcListFilter.isEmpty()) {
                    dynamicRole.setDeptPropCategoryName("已删除");
                } else {
                    dynamicRole.setDeptPropCategoryName(dpcListFilter.get(0).getName());
                }
            } else if (dynamicRole.getKinds().equals(DynamicRoleKindsEnum.ROLE.getValue())) {
                List<Role> roleListFilter = roleList.stream()
                    .filter(role -> role.getId().equals(dynamicRole.getRoleId())).collect(Collectors.toList());
                if (roleListFilter.isEmpty()) {
                    dynamicRole.setRoleName("已删除");
                } else {
                    dynamicRole.setRoleName(roleListFilter.get(0).getName());
                }
            }
        });
        return Y9Result.success(drList, "获取成功");
    }

    /**
     * 获取动态角色
     *
     * @param id 角色id
     * @return Y9Result<DynamicRole>
     */
    @GetMapping(value = "/getDynamicRole")
    public Y9Result<DynamicRole> getDynamicRole(@RequestParam String id) {
        DynamicRole dynamicRole = dynamicRoleService.getById(id);
        return Y9Result.success(dynamicRole, "获取成功");
    }

    /**
     * 获取动态角色树
     *
     * @return Y9Result<List<NodeTreeVO>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<NodeTreeVO>> list() {
        List<NodeTreeVO> listMap = new ArrayList<>();
        List<DynamicRole> dynamicRoleList = dynamicRoleService.listAll();
        for (DynamicRole dynamicRole : dynamicRoleList) {
            NodeTreeVO map = new NodeTreeVO();
            map.setId(dynamicRole.getId());
            map.setName(dynamicRole.getName());
            map.setIsParent(false);
            map.setOrgType("dynamicRole");
            listMap.add(map);
        }
        return Y9Result.success(listMap, "获取成功");
    }

    @GetMapping(value = "/deptPropCategory")
    public Y9Result<List<OptionValue>> deptPropCategory() {
        return optionValueApi.listByType(Y9LoginUserHolder.getTenantId(), "departmentPropCategory");
    }

    @GetMapping(value = "/publicRole")
    public Y9Result<List<Role>> publicRole() {
        return roleApi.listRoleByParentId(InitDataConsts.TOP_PUBLIC_ROLE_ID);
    }

    @GetMapping(value = "/getClasses")
    public Y9Result<List<String>> getClasses(String packageName) {
        try {
            List<Class<?>> classes = PackageClassFinder.getClasses(packageName);
            List<String> list = new ArrayList<>();
            for (Class<?> clazz : classes) {
                list.add(clazz.getName());
            }
            return Y9Result.success(list, "获取动态角色类路径成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Y9Result.failure("获取动态角色类路径失败");
    }

    /**
     * 删除
     *
     * @param dynamicRoleIds 角色ids
     * @return Y9Result<String>
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] dynamicRoleIds) {
        dynamicRoleService.removeDynamicRoles(dynamicRoleIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存动态角色
     *
     * @param dynamicRole 动态角色
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(DynamicRole dynamicRole) {
        dynamicRoleService.saveOrUpdate(dynamicRole);
        return Y9Result.successMsg("保存成功");
    }
}
