package net.risesoft.controller;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemOrganWordRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOrganWordRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemOrganWordRole", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemOrganWordRoleController {

    private final ItemOrganWordRoleService itemOrganWordRoleService;

    /**
     * 将意见框绑定到角色上
     *
     * @param roleIds 角色id
     * @param itemOrganWordBindId 绑定id
     * @return
     */
    @RequestMapping("/bindRole")
    public Y9Result<String> bindRole(String roleIds, String itemOrganWordBindId) {
        if (StringUtils.isNotEmpty(roleIds)) {
            String[] roleIdarr = roleIds.split(";");
            for (String roleId : roleIdarr) {
                itemOrganWordRoleService.saveOrUpdate(itemOrganWordBindId, roleId);
            }
        }
        return Y9Result.successMsg("保存成功");
    }

    @GetMapping(value = "/list")
    public Y9Result<List<ItemOrganWordRole>> list(@RequestParam String itemOrganWordBindId) {
        List<ItemOrganWordRole> list =
            itemOrganWordRoleService.listByItemOrganWordBindIdContainRoleName(itemOrganWordBindId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除意见框与角色的绑定
     *
     * @param ids 绑定的id
     * @return
     */
    @RequestMapping("/remove")
    public Y9Result<String> remove(String[] ids) {
        itemOrganWordRoleService.remove(ids);
        return Y9Result.successMsg("删除绑定的角色失败");
    }
}
