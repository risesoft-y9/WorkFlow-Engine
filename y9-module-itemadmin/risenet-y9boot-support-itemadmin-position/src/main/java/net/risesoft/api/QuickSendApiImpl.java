package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.QuickSendApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.QuickSendService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 快速发送设置接口
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/quickSend", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuickSendApiImpl implements QuickSendApi {

    private final QuickSendService quickSendService;

    /**
     * 获取快速发送设置
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是快捷发送人
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getAssignee(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itemId, @RequestParam String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return Y9Result.success(quickSendService.getAssignee(itemId, taskKey));
    }

    /**
     * 保存快速发送设置
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveOrUpdate(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestParam String itemId, @RequestParam String taskKey, @RequestParam String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        quickSendService.saveOrUpdate(itemId, taskKey, assignee);
        return Y9Result.success();
    }

}
