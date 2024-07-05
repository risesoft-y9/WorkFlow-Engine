package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.RejectReasonApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.RejectReasonService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 驳回原因接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/rejectReason", produces = MediaType.APPLICATION_JSON_VALUE)
public class RejectReasonApiImpl implements RejectReasonApi {

    private final RejectReasonService rejectReasonService;

    private final PersonApi personManager;

    /**
     * 保存驳回原因
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> save(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam Integer action, @RequestParam String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        rejectReasonService.save(reason, taskId, action);
        return Y9Result.success();
    }
}
