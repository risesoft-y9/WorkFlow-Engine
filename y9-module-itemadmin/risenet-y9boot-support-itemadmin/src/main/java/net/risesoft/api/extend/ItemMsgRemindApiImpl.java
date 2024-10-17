package net.risesoft.api.extend;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.extend.ItemMsgRemindApi;
import net.risesoft.model.itemadmin.ItemMsgRemindModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.extend.ItemMsgRemindService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 消息提醒接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/itemMsgRemind", produces = MediaType.APPLICATION_JSON_VALUE)
@Primary
public class ItemMsgRemindApiImpl implements ItemMsgRemindApi {

    private final ItemMsgRemindService itemMsgRemindService;

    /**
     * 根据processInstanceId删除消息提醒信息
     *
     * @param tenantId 租户id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteMsgRemindInfo(@RequestParam String tenantId, @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        itemMsgRemindService.deleteMsgRemindInfo(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 根据userId,type获取消息提醒配置
     *
     * @param tenantId 租户id
     * @param userId 用户id
     * @param type 类型
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getRemindConfig(@RequestParam String tenantId, @RequestParam String userId, String type) {
        Y9LoginUserHolder.setTenantId(tenantId);
        String str = itemMsgRemindService.getRemindConfig(userId, type);
        return Y9Result.success(str);
    }

    /**
     * 保存消息提醒信息
     *
     * @param tenantId 租户id
     * @param info 消息提醒信息
     * @return {@code Y9Result<Boolean>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Boolean> saveMsgRemindInfo(@RequestParam String tenantId, @RequestBody ItemMsgRemindModel info) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Boolean b = itemMsgRemindService.saveMsgRemindInfo(info);
        return Y9Result.success(b);
    }
}
