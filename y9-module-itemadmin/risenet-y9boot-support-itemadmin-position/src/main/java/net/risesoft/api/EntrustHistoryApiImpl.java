package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
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
@RequestMapping(value = "/services/rest/entrustHistory")
public class EntrustHistoryApiImpl implements EntrustHistoryApi {

    private final EntrustHistoryService entrustHistoryService;

    /**
     * 获取某个用户的某个事项委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员滴
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return Y9Result<List<EntrustHistoryModel>>
     */
    @Override
    @GetMapping(value = "/findByOwnerIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<EntrustHistoryModel>> findByOwnerIdAndItemId(String tenantId, String userId, String ownerId,
        String itemId) {
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
     * @return List<EntrustHistoryModel>
     */
    @Override
    @GetMapping(value = "/findOneByOwnerId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<EntrustHistoryModel>> findOneByOwnerId(String tenantId, String userId, String ownerId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId);
        List<EntrustHistoryModel> list = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return Y9Result.success(list, "获取成功");
    }
}
