package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 快捷发送
 *
 * @author zhangchongjie
 * @date 2023/09/07
 */
@Validated
public interface QuickSendApi {

    /**
     * 获取快速发送设置
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是快捷发送人
     * @since 9.6.6
     */
    @GetMapping("/getAssignee")
    Y9Result<String> getAssignee(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String taskKey);

    /**
     * 保存快速发送设置
     *
     * @param itemId 事项id
     * @param taskKey 任务key
     * @param assignee 发送人
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/saveOrUpdate")
    Y9Result<String> saveOrUpdate(@RequestParam @NotBlank String itemId, @RequestParam @NotBlank String taskKey,
        @RequestParam String assignee);

}
