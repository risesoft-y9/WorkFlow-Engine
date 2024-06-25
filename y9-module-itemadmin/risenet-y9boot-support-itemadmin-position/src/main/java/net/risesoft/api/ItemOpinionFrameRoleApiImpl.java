package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemOpinionFrameRoleApi;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;
import net.risesoft.service.ItemOpinionFrameRoleService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 意见框绑定角色接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemOpinionFrameRole")
public class ItemOpinionFrameRoleApiImpl implements ItemOpinionFrameRoleApi {

    private final ItemOpinionFrameRoleService itemOpinionFrameRoleService;

    /**
     * 获取意见框绑定角色
     *
     * @param tenantId 租户id
     * @param itemOpinionFrameId 意见框绑定id
     * @return List<ItemOpinionFrameRoleModel>
     */
    @Override
    @GetMapping(value = "/findByItemOpinionFrameId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemOpinionFrameRoleModel> findByItemOpinionFrameId(String tenantId, String itemOpinionFrameId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameRoleModel> modelList = new ArrayList<>();
        List<ItemOpinionFrameRole> list = itemOpinionFrameRoleService.findByItemOpinionFrameId(itemOpinionFrameId);
        for (ItemOpinionFrameRole role : list) {
            ItemOpinionFrameRoleModel model = new ItemOpinionFrameRoleModel();
            Y9BeanUtil.copyProperties(role, model);
            modelList.add(model);
        }
        return modelList;
    }

}
