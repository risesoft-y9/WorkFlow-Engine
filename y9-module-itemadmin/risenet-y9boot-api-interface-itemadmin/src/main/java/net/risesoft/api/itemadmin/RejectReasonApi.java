package net.risesoft.api.itemadmin;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 驳回原因管理
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface RejectReasonApi {

    /**
     * 保存驳回原因
     *
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam Integer action, @RequestParam String taskId,
        @RequestParam(required = false) String reason);
}
