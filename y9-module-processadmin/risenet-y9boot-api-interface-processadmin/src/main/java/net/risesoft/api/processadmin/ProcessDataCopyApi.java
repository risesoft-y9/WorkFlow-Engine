package net.risesoft.api.processadmin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 流程定义数据复制接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface ProcessDataCopyApi {

    /**
     * 复制拷贝流程定义数据
     *
     * @param sourceTenantId 源租户id
     * @param targetTenantId 目标租户id
     * @param modelKey 定义key
     * @return {@code Y9Result<Object>} 通用请求返回对象 - success 属性判断操作是否成功
     * @since 9.6.6
     */
    @PostMapping("/copyModel")
    Y9Result<Object> copyModel(@RequestParam("sourceTenantId") String sourceTenantId,
        @RequestParam("targetTenantId") String targetTenantId, @RequestParam("modelKey") String modelKey);

}
