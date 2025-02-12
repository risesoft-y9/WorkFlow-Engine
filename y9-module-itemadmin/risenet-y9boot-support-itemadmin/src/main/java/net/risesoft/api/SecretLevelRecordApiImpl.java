package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SecretLevelRecordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SecretLevelRecordService;
import net.risesoft.y9.Y9LoginUserHolder;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/secretLevelRecord", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecretLevelRecordApiImpl implements SecretLevelRecordApi {

    private final SecretLevelRecordService secretLevelRecordService;

    private final PersonApi personApi;

    /**
     * 保存密级修改记录
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processSerialNumber 流程编号
     * @param secretLevel 密级
     * @param secretBasis 定密依据
     * @param secretItem 具体事项
     * @param description 说明
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> saveRecord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String secretLevel, @RequestParam String secretBasis,
        @RequestParam String secretItem, String description) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(person.toUserInfo());
        secretLevelRecordService.save(processSerialNumber, secretLevel, secretBasis, secretItem, description);
        return Y9Result.successMsg("保存成功");
    }
}
