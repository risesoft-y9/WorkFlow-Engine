package net.risesoft.api.itemadmin.entrust;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.EntrustHistoryModel;
import net.risesoft.pojo.Y9Result;

/**
 * 出差委托历史接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
public interface EntrustHistoryApi {

    /**
     * 获取某个用户的某个事项委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员滴
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return {@code Y9Result<List<EntrustHistoryModel>>} 通用请求返回对象 - data 是委托历史列表
     * @since 9.6.6
     */
    @GetMapping("/findByOwnerIdAndItemId")
    Y9Result<List<EntrustHistoryModel>> findByOwnerIdAndItemId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId,
        @RequestParam("itemId") String itemId);

    /**
     * 获取某个用户的委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @return {@code Y9Result<List<EntrustHistoryModel>>} 通用请求返回对象 - data 是委托历史列表
     * @since 9.6.6
     */
    @GetMapping("/findOneByOwnerId")
    Y9Result<List<EntrustHistoryModel>> findOneByOwnerId(@RequestParam("tenantId") String tenantId,
        @RequestParam("userId") String userId, @RequestParam("ownerId") String ownerId);

}
