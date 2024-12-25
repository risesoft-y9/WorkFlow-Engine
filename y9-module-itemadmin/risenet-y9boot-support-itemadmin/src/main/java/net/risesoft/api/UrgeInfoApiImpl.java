package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.UrgeInfoApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.UrgeInfo;
import net.risesoft.model.itemadmin.UrgeInfoModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.UrgeInfoService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * @author : qinman
 * @date : 2024-12-24
 * @since 9.6.8
 **/
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/urgeInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class UrgeInfoApiImpl implements UrgeInfoApi {

    private final PersonApi personApi;

    private final UrgeInfoService urgeInfoService;

    @Override
    public Y9Result<Object> save(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String msgContent) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(personApi.get(tenantId, userId).getData());
        urgeInfoService.save(processSerialNumber, msgContent);
        return Y9Result.success();
    }

    @Override
    public Y9Result<List<UrgeInfoModel>> findByProcessSerialNumber(String tenantId, String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<UrgeInfo> urgeInfoList = urgeInfoService.findByProcessSerialNumber(processSerialNumber);
        List<UrgeInfoModel> modelList = new ArrayList<>();
        urgeInfoList.forEach(urgeInfo -> {
            UrgeInfoModel urgeInfoModel = new UrgeInfoModel();
            Y9BeanUtil.copyProperties(urgeInfo, urgeInfoModel);
            modelList.add(urgeInfoModel);
        });
        return Y9Result.success(modelList);
    }

    @Override
    public Y9Result<Object> deleteById(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        urgeInfoService.deleteById(id);
        return Y9Result.success();
    }
}
