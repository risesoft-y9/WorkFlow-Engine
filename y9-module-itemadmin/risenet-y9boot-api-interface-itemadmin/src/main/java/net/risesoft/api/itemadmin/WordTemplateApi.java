package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.GetMapping;

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
     *
     * Description: 根据唯一标示获取模板辣眼睛
     *
     * @param id 模板id
     * @return String
     */
    @GetMapping(value = "/getFilePathById")
    Y9Result<String> getFilePathById(String id);
}
