package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;

import net.risesoft.model.itemadmin.ItemSystemModel;
import net.risesoft.pojo.Y9Result;

/**
 * 事项系统接口
 *
 * @author qinman
 * @date 2026/07/10
 */
public interface ItemSystemApi {

    /**
     * 获取事项系统
     *
     * @return {@code Y9Result<List<ItemSystemModel>>} 通用请求返回对象 - data 是事项系统集合
     * @since 9.6.9
     */
    @GetMapping("/listAll")
    Y9Result<List<ItemSystemModel>> listAll();
}