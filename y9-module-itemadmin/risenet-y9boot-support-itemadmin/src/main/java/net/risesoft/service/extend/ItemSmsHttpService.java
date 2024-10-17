package net.risesoft.service.extend;

import java.util.List;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface ItemSmsHttpService {

    void sendSmsHttpList(String tenantId, String userId, List<String> mobile, String smsContent, String systemName)
        throws Exception;
}
