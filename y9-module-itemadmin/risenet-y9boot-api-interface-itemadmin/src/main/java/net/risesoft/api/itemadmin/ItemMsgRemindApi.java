package net.risesoft.api.itemadmin;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.ItemMsgRemindModel;
import net.risesoft.pojo.Y9Result;

/**
 * 消息提醒相关
 *
 * @author zhangchongjie
 * @author mengjuhua
 *
 * @date 2022/12/28
 */
public interface ItemMsgRemindApi {

    /**
     * 根据processInstanceId删除消息提醒信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/deleteMsgRemindInfo")
    Y9Result<Object> deleteMsgRemindInfo(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("processInstanceId") @NotBlank String processInstanceId);

    /**
     * 根据userId,type获取消息提醒配置
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param type 类型
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/getRemindConfig")
    Y9Result<String> getRemindConfig(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("userId") @NotBlank String userId, @RequestParam(value = "type", required = false) String type);

    /**
     * 保存消息提醒信息
     *
     * @param tenantId 租户id
     * @param info 消息提醒信息
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/saveMsgRemindInfo", consumes = MediaType.APPLICATION_JSON_VALUE)
    Y9Result<Boolean> saveMsgRemindInfo(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestBody ItemMsgRemindModel info);

}
