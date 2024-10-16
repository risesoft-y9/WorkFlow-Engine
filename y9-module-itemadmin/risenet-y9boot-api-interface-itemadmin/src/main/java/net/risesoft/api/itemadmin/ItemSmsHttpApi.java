package net.risesoft.api.itemadmin;

import java.util.List;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.pojo.Y9Result;

/**
 * 消息提醒相关
 *
 * @author zhangchongjie
 * @author mengjuhua
 *
 * @date 2022/12/28
 */
public interface ItemSmsHttpApi {

    /**
     * 向多人发送短信(http形式)
     *
     * @param tenantId 租户id
     * @param userId 发送人id
     * @param mobile 接收人手机号
     * @param smsContent 短信内容
     * @param systemName 系统名称
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping(value = "/sendSmsHttpList")
    Y9Result<Boolean> sendSmsHttpList(@RequestParam("tenantId") String tenantId, @RequestParam("userId") String userId,
        @RequestParam("mobile") List<String> mobile, @RequestParam("smsContent") String smsContent,
        @RequestParam(value = "systemName", required = false) String systemName) throws Exception;

}
