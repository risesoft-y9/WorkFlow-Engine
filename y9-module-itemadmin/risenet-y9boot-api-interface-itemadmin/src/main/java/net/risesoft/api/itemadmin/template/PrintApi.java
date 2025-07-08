package net.risesoft.api.itemadmin.template;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 打印模板
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface PrintApi {

    /**
     * 打开打印模板
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<String>} 通用请求返回对象 -data是模版文件ID
     * @since 9.6.6
     */
    @GetMapping("/openDocument")
    Y9Result<String> openDocument(@RequestParam("tenantId") String tenantId, @RequestParam("itemId") String itemId);

}
