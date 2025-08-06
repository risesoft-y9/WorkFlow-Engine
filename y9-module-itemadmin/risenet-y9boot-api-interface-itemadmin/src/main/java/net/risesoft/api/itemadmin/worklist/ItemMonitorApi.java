package net.risesoft.api.itemadmin.worklist;

import javax.validation.constraints.NotBlank;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.core.ActRuDetailModel;
import net.risesoft.pojo.Y9Page;

/**
 * 监控接口
 *
 * @author qinman
 * @date 2024/12/19
 */
public interface ItemMonitorApi {

    /**
     * 根据系统名称查询所有件列表(以发送时间排序)
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param systemName 系统名称
     * @param page page
     * @param rows rows
     * @return {@code Y9Page<ActRuDetailModel>} 通用分页请求返回对象 -rows 是待办任务
     * @since 9.6.6
     */
    @GetMapping("/findBySystemName")
    Y9Page<ActRuDetailModel> findBySystemName(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("systemName") @NotBlank String systemName,
        @RequestParam("page") Integer page, @RequestParam("rows") Integer rows);
}
