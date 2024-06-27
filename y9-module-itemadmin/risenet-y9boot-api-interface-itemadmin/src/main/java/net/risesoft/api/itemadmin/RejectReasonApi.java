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
     * 保存退回/收回的原因
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/save")
    Y9Result<Object> save(@RequestParam("userId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("action") Integer action, @RequestParam("taskId") String taskId,
        @RequestParam("reason") String reason);
}
