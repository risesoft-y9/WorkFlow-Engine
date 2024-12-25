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
     * @param tenantId 租户id
     * @param id 模板id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping(value = "/getFilePathById")
    Y9Result<String> getFilePathById(@RequestParam("tenantId") String tenantId, @RequestParam("id") String id);

    /**
     * 根据正文模板id获取正文模板绑定信息
     *
     * @param tenantId 租户id
     * @param itemId 正文模板id
     * @param wordType 正文模板类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping(value = "/getWordTemplateBind")
    Y9Result<String> getWordTemplateBind(@RequestParam("tenantId") String tenantId,
        @RequestParam("itemId") String itemId, @RequestParam(value = "wordType", required = false) String wordType);
}
