package net.risesoft.service.extend;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemSmsHttpService {

    /**
     * 发送短信
     *
     * @param tenantId 租户ID
     * @param userId 用户ID
     * @param mobile 手机号
     * @param smsContent 短信内容
     * @param systemName 系统名称
     * @throws Exception 异常
     */
    void sendSmsHttpList(String tenantId, String userId, List<String> mobile, String smsContent, String systemName)
        throws Exception;
}
