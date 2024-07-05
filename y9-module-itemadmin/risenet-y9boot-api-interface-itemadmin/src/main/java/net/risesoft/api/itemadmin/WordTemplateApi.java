package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 正文模板信息管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface WordTemplateApi {

    /**
     * 根据id获取正文模板文件路径
     *
     * @param id 模板id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping(value = "/getFilePathById")
    Y9Result<String> getFilePathById(@RequestParam("id") String id);
}
