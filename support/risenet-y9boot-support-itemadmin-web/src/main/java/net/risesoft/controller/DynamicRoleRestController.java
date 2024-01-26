package net.risesoft.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.entity.DynamicRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.DynamicRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/vue/dynamicRole")
public class DynamicRoleRestController {

    @Autowired
    private DynamicRoleService dynamicRoleService;

    /**
     * 获取动态角色列表
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/dynamicRoleList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<DynamicRole>> dynamicRoleList() {
        List<DynamicRole> drList = dynamicRoleService.findAll();
        return Y9Result.success(drList, "获取成功");
    }

    /**
     * 获取动态角色
     *
     * @param id 角色id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/getDynamicRole", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<DynamicRole> getDynamicRole(@RequestParam String id) {
        DynamicRole dynamicRole = dynamicRoleService.findOne(id);
        return Y9Result.success(dynamicRole, "获取成功");
    }

    /**
     * 获取动态角色树
     *
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<Map<String, Object>>> list() {
        List<Map<String, Object>> listMap = new ArrayList<>();
        List<DynamicRole> dynamicRoleList = dynamicRoleService.findAll();
        for (DynamicRole dynamicRole : dynamicRoleList) {
            Map<String, Object> map = new HashMap<>(16);
            map.put("id", dynamicRole.getId());
            map.put("name", dynamicRole.getName());
            map.put("isParent", "false");
            map.put("orgType", "dynamicRole");
            listMap.add(map);
        }
        return Y9Result.success(listMap, "获取成功");
    }

    /**
     * 删除
     *
     * @param dynamicRoleIds 角色ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam String[] dynamicRoleIds) {
        dynamicRoleService.removeDynamicRoles(dynamicRoleIds);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存动态角色
     *
     * @param dynamicRole
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveOrUpdate", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveOrUpdate(DynamicRole dynamicRole) {
        dynamicRoleService.saveOrUpdate(dynamicRole);
        return Y9Result.successMsg("保存成功");
    }
}
