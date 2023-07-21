package net.risesoft.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.EntrustHistoryApi;
import net.risesoft.entity.EntrustHistory;
import net.risesoft.model.itemadmin.EntrustHistoryModel;
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
@RequestMapping(value = "/services/rest/entrustHistory")
public class EntrustHistoryApiImpl implements EntrustHistoryApi {

    @Autowired
    private EntrustHistoryService entrustHistoryService;

    /**
     * 获取某个用户的某个事项委托历史对象集合
     *
     * @param tenantId 租户id
     * @param userId 人员滴
     * @param ownerId 委托人id
     * @param itemId 事项id
     * @return List<EntrustHistoryModel>
     */
    @Override
    @GetMapping(value = "/findByOwnerIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<EntrustHistoryModel> findByOwnerIdAndItemId(String tenantId, String userId, String ownerId, String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId, itemId);
        List<EntrustHistoryModel> ehModelList = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return ehModelList;
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
    public List<EntrustHistoryModel> findOneByOwnerId(String tenantId, String userId, String ownerId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId);
        List<EntrustHistoryModel> ehModelList = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return ehModelList;
    }
}
