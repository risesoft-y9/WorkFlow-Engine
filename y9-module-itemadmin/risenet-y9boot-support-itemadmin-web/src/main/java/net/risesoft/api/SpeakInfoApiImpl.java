package net.risesoft.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.SpeakInfoApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.SpeakInfo;
import net.risesoft.model.itemadmin.SpeakInfoModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SpeakInfoService;
import net.risesoft.util.ItemAdminModelConvertUtil;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/speakInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpeakInfoApiImpl implements SpeakInfoApi {

    @Autowired
    private SpeakInfoService speakInfoService;

    @Autowired
    private PersonApi personManager;

    @Override
    public Y9Result<Object> deleteById(String tenantId, String userId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return speakInfoService.deleteById(id);
    }

    @Override
    public Y9Result<SpeakInfoModel> findById(String tenantId, String userId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SpeakInfo speakInfo = speakInfoService.findById(id);
        return Y9Result.success(ItemAdminModelConvertUtil.speakInfo2Model(speakInfo));
    }

    @Override
    public Y9Result<List<SpeakInfoModel>> findByProcessInstanceId(String tenantId, String userId,
        String processInstanceId) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);
        List<SpeakInfo> siList = speakInfoService.findByProcessInstanceId(processInstanceId);
        return Y9Result.success(ItemAdminModelConvertUtil.speakInfoList2ModelList(siList));
    }

    @Override
    public Y9Result<Integer> getNotReadCount(String tenantId, String userId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(speakInfoService.getNotReadCount(processInstanceId, userId));
    }

    @Override
    public Y9Result<String> saveOrUpdate(String tenantId, String userId, @RequestBody SpeakInfoModel speakInfoModel) {
        Person person = personManager.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        SpeakInfo speakInfo = ItemAdminModelConvertUtil.speakInfoModel2SpeakInfo(speakInfoModel);
        return Y9Result.success(speakInfoService.saveOrUpdate(speakInfo));
    }
}
