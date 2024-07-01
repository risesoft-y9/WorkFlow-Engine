package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ItemOpinionFrameRoleApi {

    /**
     * 获取意见框绑定角色
     *
     * @param tenantId 租户id
     * @param itemOpinionFrameId 意见框绑定id
     * @return Y9Result<List<ItemOpinionFrameRoleModel>>
     */
    @GetMapping("/findByItemOpinionFrameId")
    Y9Result<List<ItemOpinionFrameRoleModel>> findByItemOpinionFrameId(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemOpinionFrameId") String itemOpinionFrameId);
}
