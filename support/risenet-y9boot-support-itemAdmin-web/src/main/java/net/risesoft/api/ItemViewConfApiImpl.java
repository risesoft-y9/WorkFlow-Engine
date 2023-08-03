package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.entity.ItemViewConf;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/itemViewConf")
public class ItemViewConfApiImpl implements ItemViewConfApi {

    @Autowired
    private ItemViewConfService itemViewConfService;

    @Override
    @GetMapping(value = "/findByItemIdAndViewType", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemViewConfModel> findByItemIdAndViewType(String tenantId, String itemId, String viewType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemViewConfModel> modelList = new ArrayList<>();
        List<ItemViewConf> list = itemViewConfService.findByItemIdAndViewType(itemId, viewType);
        ItemViewConfModel model = null;
        for (ItemViewConf itemViewConf : list) {
            model = new ItemViewConfModel();
            Y9BeanUtil.copyProperties(itemViewConf, model);
            modelList.add(model);
        }
        return modelList;
    }
}
