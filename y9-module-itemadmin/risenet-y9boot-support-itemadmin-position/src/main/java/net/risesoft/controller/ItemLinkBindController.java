package net.risesoft.controller;

import lombok.RequiredArgsConstructor;
import net.risesoft.entity.ItemLinkBind;
import net.risesoft.entity.ItemLinkRole;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.ItemLinkBindService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemLinkBind")
public class ItemLinkBindController {

    private final ItemLinkBindService itemLinkBindService;

    /**
     * 获取绑定链接列表
     *
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/getBindList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemLinkBind>> getBindList(@RequestParam String itemId) {
        List<ItemLinkBind> list = itemLinkBindService.findByItemId(itemId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取链接绑定的角色
     *
     * @param itemLinkId 绑定关系id
     * @return
     */
    @RequestMapping(value = "/getBindRoleList", method = RequestMethod.GET, produces = "application/json")
    public Y9Result<List<ItemLinkRole>> getBindRoleList(@RequestParam String itemLinkId) {
        List<ItemLinkRole> list = itemLinkBindService.getBindRoleList(itemLinkId);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param ids 绑定ids
     * @return
     */
    @RequestMapping(value = "/removeBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeBind(@RequestParam String[] ids) {
        itemLinkBindService.removeBind(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 移除角色的绑定
     *
     * @param ids 绑定ids
     * @return
     */
    @RequestMapping(value = "/removeRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> removeRole(@RequestParam String[] ids) {
        itemLinkBindService.removeRole(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 链接绑定角色
     *
     * @param roleIds 角色ids
     * @param itemLinkId 绑定关系id
     * @return
     */
    @RequestMapping(value = "/saveBindRole", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveBindRole(@RequestParam String roleIds, @RequestParam String itemLinkId) {
        itemLinkBindService.saveBindRole(itemLinkId, roleIds);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存绑定
     *
     * @param linkIds 链接id
     * @param itemId 事项id
     * @return
     */
    @RequestMapping(value = "/saveItemLinkBind", method = RequestMethod.POST, produces = "application/json")
    public Y9Result<String> saveItemLinkBind(@RequestParam String[] linkIds, @RequestParam String itemId) {
        itemLinkBindService.saveItemLinkBind(itemId, linkIds);
        return Y9Result.successMsg("保存成功");
    }
}
