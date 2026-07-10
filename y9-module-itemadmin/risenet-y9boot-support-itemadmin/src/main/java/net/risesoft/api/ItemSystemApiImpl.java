package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.ItemSystemApi;
import net.risesoft.entity.ItemSystem;
import net.risesoft.model.itemadmin.ItemSystemModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.config.ItemSystemService;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 事项系统接口
 *
 * @author qinman
 * @date 2026/07/10
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemSystem", produces = MediaType.APPLICATION_JSON_VALUE)
public class ItemSystemApiImpl implements ItemSystemApi {

    private final ItemSystemService itemSystemService;

    /**
     * 获取事项系统
     *
     * @return {@code Y9Result<List<ItemSystemModel>>} 通用请求返回对象 - data 是事项系统集合
     * @since 9.6.9
     */
    @Override
    public Y9Result<List<ItemSystemModel>> listAll() {
        List<ItemSystem> itemSystems = itemSystemService.listAll();
        List<ItemSystemModel> modelList = new ArrayList<>();
        itemSystems.forEach(itemSystem -> {
            ItemSystemModel model = new ItemSystemModel();
            Y9BeanUtil.copyProperties(itemSystem, model);
            modelList.add(model);
        });
        return Y9Result.success(modelList);
    }
}
