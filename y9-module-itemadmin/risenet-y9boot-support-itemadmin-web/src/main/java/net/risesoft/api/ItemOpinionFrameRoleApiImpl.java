package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemOpinionFrameRoleApi;
import net.risesoft.entity.ItemOpinionFrameRole;
import net.risesoft.model.itemadmin.ItemOpinionFrameRoleModel;
import net.risesoft.service.ItemOpinionFrameRoleService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/itemOpinionFrameRole")
public class ItemOpinionFrameRoleApiImpl implements ItemOpinionFrameRoleApi {

    @Autowired
    private ItemOpinionFrameRoleService itemOpinionFrameRoleService;

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
