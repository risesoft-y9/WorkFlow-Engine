package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.ItemViewConfApi;
import net.risesoft.entity.ItemViewConf;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.service.ItemViewConfService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 列表视图接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemViewConf")
public class ItemViewConfApiImpl implements ItemViewConfApi {

    private final ItemViewConfService itemViewConfService;

    /**
     * 获取事项视图配置列表
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return List<ItemViewConfModel>
     */
    @Override
    @GetMapping(value = "/findByItemIdAndViewType", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<ItemViewConfModel> findByItemIdAndViewType(String tenantId, String itemId, String viewType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<ItemViewConfModel> modelList = new ArrayList<ItemViewConfModel>();
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
