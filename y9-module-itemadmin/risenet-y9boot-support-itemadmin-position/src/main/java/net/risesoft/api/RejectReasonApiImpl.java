package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.RejectReasonApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.service.RejectReasonService;
import net.risesoft.y9.Y9LoginUserHolder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 驳回原因接口
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/rejectReason")
public class RejectReasonApiImpl implements RejectReasonApi {

    private final RejectReasonService rejectReasonService;

    private final PersonApi personManager;

    /**
     * 保存驳回原因
     * @param tenantId 租户id
     * @param userId 人员id
     * @param action action
     * @param taskId 任务id
     * @param reason 理由
     */
    @Override
    @PostMapping(value = "/save", produces = MediaType.APPLICATION_JSON_VALUE)
    public void save(String tenantId, String userId, Integer action, String taskId, String reason) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPerson(person);

        rejectReasonService.save(reason, taskId, action);
    }
}
