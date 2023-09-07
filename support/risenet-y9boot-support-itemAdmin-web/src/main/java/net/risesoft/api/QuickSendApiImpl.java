package net.risesoft.api;

import javax.validation.constraints.NotBlank;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.QuickSendApi;
import net.risesoft.service.QuickSendService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Validated
@RestController
@RequestMapping(value = "/services/rest/quickSend", produces = MediaType.APPLICATION_JSON_VALUE)
public class QuickSendApiImpl implements QuickSendApi {

    @Autowired
    private QuickSendService quickSendService;

    @Override
    public String getAssignee(@NotBlank String tenantId, @NotBlank String positionId, @NotBlank String itemId, @NotBlank String taskKey) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        return quickSendService.getAssignee(itemId, taskKey);
    }

    @Override
    public void saveOrUpdate(@NotBlank String tenantId, @NotBlank String positionId, @NotBlank String itemId, @NotBlank String taskKey, String assignee) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPositionId(positionId);
        quickSendService.saveOrUpdate(itemId, taskKey, assignee);
    }

}
