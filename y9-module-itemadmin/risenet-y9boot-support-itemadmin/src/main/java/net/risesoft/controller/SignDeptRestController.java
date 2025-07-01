package net.risesoft.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SignOutDept;
import net.risesoft.entity.SignOutDeptType;
import net.risesoft.model.platform.Department;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SignDeptOutService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/signDept", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignDeptRestController {

    private final SignDeptOutService signDeptOutService;

    /**
     * 禁用/启用部门
     *
     * @param ids 部门ids
     * @param status 0:启用 1:禁用
     * @return Y9Result<String>
     */
    @PostMapping(value = "/disableDept")
    public Y9Result<String> disableDept(@RequestParam String[] ids, @RequestParam Integer status) {
        signDeptOutService.disableDept(ids, status);
        return Y9Result.successMsg("禁用成功");
    }

    /**
     * 禁用/启用部门类型
     *
     * @param id 部门类型id
     * @param status 0:启用 1:禁用
     * @return Y9Result<String>
     */
    @PostMapping(value = "/disableType")
    public Y9Result<String> disableType(@RequestParam String id, @RequestParam Integer status) {
        signDeptOutService.disableType(id, status);
        return Y9Result.successMsg("禁用成功");
    }

    /**
     * 获取委外会签部门列表
     *
     * @param deptTypeId 部门类型id
     * @param name 部门名称
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<SignOutDept>
     */
    @GetMapping(value = "/getDeptList")
    public Y9Result<List<SignOutDept>> getDeptList(@RequestParam String deptTypeId,
        @RequestParam(required = false) String name, @RequestParam(required = false) Integer page,
        @RequestParam(required = false) Integer rows) {
        List<SignOutDept> list = signDeptOutService.listAll(deptTypeId, name, page, rows);
        return Y9Result.success(list);
    }

    /**
     * 获取委外会签类型
     *
     * @param name 类型名称
     * @return Y9Result<List<SignOutDeptType>>
     */
    @GetMapping(value = "/getDeptTypeList")
    public Y9Result<List<Department>> getDeptTypeList(@RequestParam(required = false) String name) {
        List<SignOutDeptType> list = signDeptOutService.getDeptTypeList(name);
        List<Department> modelList = new ArrayList<>();
        for (SignOutDeptType type : list) {
            Department model = new Department();
            model.setId(type.getDeptTypeId());
            model.setName(type.getDeptType());
            model.setTabIndex(type.getTabIndex());
            model.setDisabled(type.getIsForbidden() == 1);
            modelList.add(model);
        }
        return Y9Result.success(modelList, "获取列表成功");
    }

    /**
     * 删除委外会签部门
     *
     * @param ids
     * @return Y9Result<String>
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        signDeptOutService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 删除委外会签部门类型
     *
     * @param id
     * @return Y9Result<String>
     */
    @PostMapping(value = "/removeDetpType")
    public Y9Result<String> removeDetpType(@RequestParam String id) {
        signDeptOutService.removeDetpType(id);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存部门排序
     *
     * @param idAndTabIndexs
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveDeptOrder")
    public Y9Result<String> saveDeptOrder(@RequestParam String[] idAndTabIndexs) {
        signDeptOutService.saveDeptOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存或更新委外会签部门类型
     *
     * @param info
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveDetpType")
    public Y9Result<String> saveDetpType(SignOutDeptType info) {
        signDeptOutService.saveDetpType(info);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存或更新委外会签部门
     *
     * @param info
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(SignOutDept info) {
        signDeptOutService.saveOrUpdate(info);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存部门类型排序
     *
     * @param idAndTabIndexs
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveTypeOrder")
    public Y9Result<String> saveTypeOrder(@RequestParam String[] idAndTabIndexs) {
        signDeptOutService.saveTypeOrder(idAndTabIndexs);
        return Y9Result.successMsg("保存成功");
    }

}
