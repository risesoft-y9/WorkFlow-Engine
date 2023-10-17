package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Validated
public interface QuickSendApi {

    /**
     * 获取快捷发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return String
     */
    @GetMapping("/getAssignee")
    String getAssignee(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("positionId") @NotBlank String positionId, @RequestParam("itemId") @NotBlank String itemId, @RequestParam("taskKey") @NotBlank String taskKey);

    /**
     * 保存快捷发送人
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     */
    @PostMapping("/saveOrUpdate")
    void saveOrUpdate(@RequestParam("tenantId") @NotBlank String tenantId, @RequestParam("positionId") @NotBlank String positionId, @RequestParam("itemId") @NotBlank String itemId, @RequestParam("taskKey") @NotBlank String taskKey, @RequestParam("assignee") String assignee);

}
