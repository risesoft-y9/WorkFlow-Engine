package net.risesoft.controller.config;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.platform.permission.RoleApi;
import net.risesoft.api.processadmin.ProcessDefinitionApi;
import net.risesoft.controller.vo.ItemNodeLinkBindVO;
import net.risesoft.entity.ItemLinkRole;
import net.risesoft.entity.ItemNodeLinkBind;
import net.risesoft.entity.LinkInfo;
import net.risesoft.model.platform.Role;
import net.risesoft.model.processadmin.TargetModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.ItemLinkRoleRepository;
import net.risesoft.repository.jpa.LinkInfoRepository;
import net.risesoft.service.config.ItemNodeLinkBindService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9Util;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/vue/itemNodeLinkBind", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemNodeLinkBindController {

    private final ItemNodeLinkBindService itemNodeLinkBindService;

    private final ProcessDefinitionApi processDefinitionApi;

    private final LinkInfoRepository linkInfoRepository;

    private final ItemLinkRoleRepository itemLinkRoleRepository;

    private final RoleApi roleApi;

    /**
     * 复制上一版本链接
     *
     * @param itemId 事项id
     * @return
     */
    @PostMapping(value = "/copyBind")
    public Y9Result<String> copyBind(@RequestParam String itemId, @RequestParam String processDefinitionId) {
        itemNodeLinkBindService.copyBind(itemId, processDefinitionId);
        return Y9Result.successMsg("复制成功");
    }

    /**
     * 获取任务节点信息和流程定义信息
     *
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @return
     */
    @GetMapping(value = "/getBpmList")
    public Y9Result<List<ItemNodeLinkBindVO>> getBpmList(@RequestParam String processDefinitionId,
        @RequestParam String itemId) {
        List<TargetModel> list;
        List<ItemNodeLinkBindVO> res_list = new ArrayList<>();
        String tenantId = Y9LoginUserHolder.getTenantId();
        list = processDefinitionApi.getNodes(tenantId, processDefinitionId, false).getData();
        ItemNodeLinkBindVO vo;
        for (TargetModel targetModel : list) {
            vo = new ItemNodeLinkBindVO();
            vo.setTaskDefKey(targetModel.getTaskDefKey());
            vo.setTaskDefName(targetModel.getTaskDefName());
            ItemNodeLinkBind bind = itemNodeLinkBindService.listByItemIdAndProcessDefinitionIdAndTaskDefKey(itemId,
                processDefinitionId, targetModel.getTaskDefKey());
            if (bind != null) {
                LinkInfo linkInfo = linkInfoRepository.findById(bind.getLinkId()).orElse(null);
                String name = linkInfo != null ? linkInfo.getLinkName() : "链接不存在";
                String url = linkInfo != null ? linkInfo.getLinkUrl() : "链接地址不存在";
                List<ItemLinkRole> rolelist = itemLinkRoleRepository.findByItemLinkId(bind.getId());
                String roleNames = "";
                String roleBindIds = "";
                for (ItemLinkRole bindrole : rolelist) {
                    roleBindIds = Y9Util.genCustomStr(roleBindIds, bindrole.getId(), ",");
                    Role role = roleApi.getRole(bindrole.getRoleId()).getData();
                    if (StringUtils.isEmpty(roleNames)) {
                        roleNames = null == role ? "角色不存在" : role.getName();
                    } else {
                        roleNames += "、" + (null == role ? "角色不存在" : role.getName());
                    }
                }
                vo.setLinkName(name + "(" + url + ")");
                vo.setRoleNames(roleNames);
                vo.setRoleBindIds(roleBindIds);
                vo.setLinkBindId(bind.getId());
            }
            res_list.add(vo);
        }
        return Y9Result.success(res_list, "获取成功");
    }

    /**
     * 移除绑定
     *
     * @param bindId 绑定id
     * @return
     */
    @PostMapping(value = "/removeBind")
    public Y9Result<String> removeBind(@RequestParam String bindId) {
        itemNodeLinkBindService.removeBind(bindId);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 移除角色的绑定
     *
     * @param ids 绑定ids
     * @return
     */
    @PostMapping(value = "/removeRole")
    public Y9Result<String> removeRole(@RequestParam String[] ids) {
        itemNodeLinkBindService.removeRole(ids);
        return Y9Result.successMsg("删除成功");
    }

    /**
     * 链接绑定角色
     *
     * @param roleIds 角色ids
     * @param itemLinkId 绑定关系id
     * @return
     */
    @PostMapping(value = "/saveBindRole")
    public Y9Result<String> saveBindRole(@RequestParam String roleIds, @RequestParam String itemLinkId) {
        itemNodeLinkBindService.saveBindRole(itemLinkId, roleIds);
        return Y9Result.successMsg("保存成功");
    }

    /**
     * 保存绑定
     *
     * @param linkId 链接id
     * @param itemId 事项id
     * @param processDefinitionId 流程定义id
     * @param taskDefKey 节点key
     * @return
     */
    @PostMapping(value = "/saveItemLinkBind")
    public Y9Result<String> saveItemLinkBind(@RequestParam String linkId, @RequestParam String processDefinitionId,
        @RequestParam(required = false) String taskDefKey, @RequestParam String itemId) {
        itemNodeLinkBindService.saveItemNodeLinkBind(itemId, linkId, processDefinitionId, taskDefKey);
        return Y9Result.successMsg("保存成功");
    }
}
