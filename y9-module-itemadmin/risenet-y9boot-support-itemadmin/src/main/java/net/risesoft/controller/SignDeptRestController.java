package net.risesoft.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.SignOutDept;
import net.risesoft.pojo.Y9Page;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SignDeptInfoService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/signDept", produces = MediaType.APPLICATION_JSON_VALUE)
public class SignDeptRestController {

    private final SignDeptInfoService signDeptInfoService;

    /**
     * 获取委外会签部门列表
     * 
     * @param name 部门名称
     * @param page 页码
     * @param rows 条数
     * @return Y9Page<SignOutDept>
     */
    @GetMapping(value = "/getList")
    public Y9Page<SignOutDept> getList(@RequestParam String name, @RequestParam Integer page,
        @RequestParam Integer rows) {
        Page<SignOutDept> pageList = signDeptInfoService.listAll(name, page, rows);
        return Y9Page.success(page, pageList.getTotalPages(), pageList.getTotalElements(), pageList.getContent(),
            "获取列表成功");
    }

    /**
     * 删除委外会签部门
     *
     * @param ids
     * @return Y9Result<String>
     */
    @PostMapping(value = "/remove")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        signDeptInfoService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存或更新委外会签部门
     *
     * @param info
     * @return Y9Result<String>
     */
    @PostMapping(value = "/saveOrUpdate")
    public Y9Result<String> saveOrUpdate(SignOutDept info) {
        signDeptInfoService.saveOrUpdate(info);
        return Y9Result.successMsg("保存成功");
    }

}
