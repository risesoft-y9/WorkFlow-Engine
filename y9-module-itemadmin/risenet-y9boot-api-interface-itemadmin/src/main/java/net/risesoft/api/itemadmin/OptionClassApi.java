package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.Y9FormOptionValueModel;
import net.risesoft.pojo.Y9Result;

/**
 * 数据字典
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface OptionClassApi {

    /**
     * 获取数据字典列表
     *
     * @param tenantId 租户id
     * @param type 字典类型
     * @return {@code Y9Result<List<Y9FormOptionValueModel>>} 通用请求返回对象 -data是数据字典列表
     */
    @GetMapping(value = "/getOptionValueList")
    Y9Result<List<Y9FormOptionValueModel>> getOptionValueList(@RequestParam("tenantId") String tenantId,
        @RequestParam("type") String type);

}
