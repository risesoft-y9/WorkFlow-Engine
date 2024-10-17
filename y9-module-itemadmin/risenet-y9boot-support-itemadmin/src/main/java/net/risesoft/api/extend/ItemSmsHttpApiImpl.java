package net.risesoft.api.extend;

import java.util.List;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.extend.ItemSmsHttpApi;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.extend.ItemSmsHttpService;

/**
 * 短信接口
 *
 * @author zhangchongjie
 * @author mengjuhua
 *
 * @date 2022/12/28
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemSmsHttp", produces = MediaType.APPLICATION_JSON_VALUE)
@Primary
public class ItemSmsHttpApiImpl implements ItemSmsHttpApi {

    private final ItemSmsHttpService itemSmsHttpService;

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
    @Override
    public Y9Result<Boolean> sendSmsHttpList(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam List<String> mobile, @RequestParam String smsContent, String systemName) throws Exception {
        itemSmsHttpService.sendSmsHttpList(tenantId, userId, mobile, smsContent, systemName);
        return Y9Result.success(true);
    }
}
