package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import net.risesoft.entity.ItemOrganWordRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOrganWordRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Controller
@RequestMapping(value = "/vue/itemOrganWordRole")
public class ItemOrganWordRoleController {

    @Autowired
    private ItemOrganWordRoleService itemOrganWordRoleService;

    /**
     * 将意见框绑定到角色上
     *
     * @param returnvalue
     * @param id
     * @return
     */
    @RequestMapping("/bindRole")
    @ResponseBody
    public Y9Result<String> bindRole(String roleIds, String itemOrganWordBindId) {
        if (StringUtils.isNotEmpty(roleIds)) {
            String[] roleIdarr = roleIds.split(";");
            for (String roleId : roleIdarr) {
                itemOrganWordRoleService.saveOrUpdate(itemOrganWordBindId, roleId);
            }
        }
        return Y9Result.successMsg("保存成功");
    }

    @ResponseBody
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemOrganWordRole>> list(@RequestParam(required = true) String itemOrganWordBindId) {
        List<ItemOrganWordRole> list =
            itemOrganWordRoleService.findByItemOrganWordBindIdContainRoleName(itemOrganWordBindId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除意见框与角色的绑定
     *
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("/remove")
    public Y9Result<String> remove(String[] ids) {
        itemOrganWordRoleService.remove(ids);
        return Y9Result.successMsg("删除绑定的角色失败");
    }
}
