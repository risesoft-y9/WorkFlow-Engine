package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.entity.ItemButtonRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemButtonRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/vue/itemButtonRole")
public class ItemButtonRoleRestController {

    @Autowired
    private ItemButtonRoleService itemButtonRoleService;

    /**
     * 获取按钮绑定角色列表
     *
     * @param itemButtonId 绑定id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemButtonRole>> list(@RequestParam(required = true) String itemButtonId) {
        List<ItemButtonRole> list = itemButtonRoleService.findByItemButtonIdContainRoleName(itemButtonId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除按钮与角色的绑定
     *
     * @param ids 绑定id
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam(required = true) String[] ids) {
        itemButtonRoleService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 保存按钮角色
     *
     * @param itemButtonId 绑定id
     * @param roleIds 角色ids
     * @return
     */
    @ResponseBody
    @RequestMapping(value = "/saveRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveRole(@RequestParam(required = true) String itemButtonId,
        @RequestParam(required = true) String roleIds) {
        if (StringUtils.isNotEmpty(roleIds)) {
            String[] roleIdarr = roleIds.split(";");
            for (String roleId : roleIdarr) {
                itemButtonRoleService.saveOrUpdate(itemButtonId, roleId);
            }
        }
        return Y9Result.successMsg("保存成功");
    }
}
