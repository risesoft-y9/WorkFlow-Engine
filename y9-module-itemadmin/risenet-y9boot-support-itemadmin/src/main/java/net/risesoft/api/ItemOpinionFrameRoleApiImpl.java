package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemOpinionFrameRoleApi;
import net.risesoft.entity.opinion.ItemOpinionFrameRole;
import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemOpinionFrameRoleService;
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
     * 获取意见框绑定角色列表
     *
     * @param tenantId 租户id
     * @param itemOpinionFrameId 意见框绑定id
     * @return {@code Y9Result<List<ItemOpinionFrameRoleModel>>} 通用请求返回对象 - data 是意见框绑定角色列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<ItemOpinionFrameRoleModel>> findByItemOpinionFrameId(@RequestParam String tenantId,
        @RequestParam String itemOpinionFrameId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemOpinionFrameRoleModel> modelList = new ArrayList<>();
        List<ItemOpinionFrameRole> list = itemOpinionFrameRoleService.listByItemOpinionFrameId(itemOpinionFrameId);
        for (ItemOpinionFrameRole role : list) {
            ItemOpinionFrameRoleModel model = new ItemOpinionFrameRoleModel();
            Y9BeanUtil.copyProperties(role, model);
            modelList.add(model);
        }
        return Y9Result.success(modelList);
    }

}
