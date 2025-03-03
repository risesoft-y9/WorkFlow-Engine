package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SecretLevelRecordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.entity.SecretLevelRecord;
import net.risesoft.model.itemadmin.SecretLevelModel;
import net.risesoft.model.platform.Person;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.SecretLevelRecordService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/secretLevelRecord", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecretLevelRecordApiImpl implements SecretLevelRecordApi {

    private final SecretLevelRecordService secretLevelRecordService;

    private final PersonApi personApi;

    /**
     * 获取密级修改记录
     *
     * @param tenantId 租户id
     * @param processSerialNumber 流程编号
     * @return {@code Y9Result<List<SecretLevelModel>>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<SecretLevelModel>> getRecord(@RequestParam String tenantId,
        @RequestParam String processSerialNumber) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<SecretLevelModel> list = new ArrayList<>();
        List<SecretLevelRecord> secretLevelRecordList = secretLevelRecordService.getRecord(processSerialNumber);
        for (SecretLevelRecord secretLevelRecord : secretLevelRecordList) {
            SecretLevelModel secretLevelModel = new SecretLevelModel();
            Y9BeanUtil.copyProperties(secretLevelRecord, secretLevelModel);
            list.add(secretLevelModel);
        }
        return Y9Result.success(list);
    }

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
     * @param tableName 表名
     * @param fieldName 字段名
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<Object> saveRecord(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processSerialNumber, @RequestParam String secretLevel, @RequestParam String secretBasis,
        @RequestParam String secretItem, String description, @RequestParam String tableName,
        @RequestParam String fieldName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(person.toUserInfo());
        secretLevelRecordService.save(processSerialNumber, secretLevel, secretBasis, secretItem, description, tableName,
            fieldName);
        return Y9Result.successMsg("保存成功");
    }
}
