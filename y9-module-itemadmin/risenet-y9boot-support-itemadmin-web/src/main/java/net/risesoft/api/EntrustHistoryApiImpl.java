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
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EntrustHistoryService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/entrustHistory")
public class EntrustHistoryApiImpl implements EntrustHistoryApi {

    @Autowired
    private EntrustHistoryService entrustHistoryService;

    @Override
    @GetMapping(value = "/findByOwnerIdAndItemId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<EntrustHistoryModel>> findByOwnerIdAndItemId(String tenantId, String userId, String ownerId,
        String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId, itemId);
        List<EntrustHistoryModel> ehModelList = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return Y9Result.success(ehModelList);
    }

    @Override
    @GetMapping(value = "/findOneByOwnerId", produces = MediaType.APPLICATION_JSON_VALUE)
    public Y9Result<List<EntrustHistoryModel>> findOneByOwnerId(String tenantId, String userId, String ownerId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustHistory> ehList = entrustHistoryService.list(ownerId);
        List<EntrustHistoryModel> ehModelList = ItemAdminModelConvertUtil.entrustHistoryList2ModelList(ehList);
        return Y9Result.success(ehModelList);
    }
}
