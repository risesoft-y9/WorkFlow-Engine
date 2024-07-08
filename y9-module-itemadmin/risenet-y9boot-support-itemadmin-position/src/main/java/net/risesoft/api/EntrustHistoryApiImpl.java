package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.EntrustHistoryApi;
import net.risesoft.entity.EntrustHistory;
import net.risesoft.model.itemadmin.EntrustHistoryModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EntrustHistoryService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 出差委托历史接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/entrustHistory", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustHistoryApiImpl implements EntrustHistoryApi {

    private final EntrustHistoryService entrustHistoryService;

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
    @Override
    public Y9Result<List<EntrustHistoryModel>> findByOwnerIdAndItemId(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String ownerId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId, itemId);
        List<EntrustHistoryModel> list = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return Y9Result.success(list, "获取成功");
    }

    /**
     * 获取某个用户的委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param ownerId 委托人id
     * @return {@code Y9Result<List<EntrustHistoryModel>>} 通用请求返回对象 - data 是委托历史列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EntrustHistoryModel>> findOneByOwnerId(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String ownerId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId);
        List<EntrustHistoryModel> list = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return Y9Result.success(list, "获取成功");
    }
}
