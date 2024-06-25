package net.risesoft.controller;

import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemOpinionFrameRoleService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemOpinionFrameRole")
public class ItemOpinionFrameRoleRestController {

    private final ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    /**
     * 意见框绑定角色
     *
     * @param roleIds 角色ids
     * @param itemOpinionFrameId 意见框标识
     * @return
     */
    @RequestMapping(value = "/bindRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> bindRole(@RequestParam String roleIds, @RequestParam String itemOpinionFrameId) {
        String[] roleIdarr = roleIds.split(";");
        for (String roleId : roleIdarr) {
            itemOpinionFrameRoleService.saveOrUpdate(itemOpinionFrameId, roleId);
        }
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 获取意见框绑定的角色
     *
     * @param itemOpinionFrameId 意见框标识
     * @return
     */
    @RequestMapping(value = "/list", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemOpinionFrameRole>> list(@RequestParam String itemOpinionFrameId) {
        List<ItemOpinionFrameRole> list =
            itemOpinionFrameRoleService.findByItemOpinionFrameIdContainRoleName(itemOpinionFrameId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除角色的绑定
     *
     * @param ids 角色ids
     * @return
     */
    @RequestMapping(value = "/remove", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> remove(@RequestParam String[] ids) {
        itemOpinionFrameRoleService.remove(ids);
        return Y9Result.successMsg("删除成功");
    }
}
