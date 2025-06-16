package net.risesoft.api;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.SecretLevelRecordApi;
import net.risesoft.api.platform.org.PersonApi;
import net.risesoft.api.platform.tenant.TenantApi;
import net.risesoft.entity.DmyjInfo;
import net.risesoft.entity.DmyjSxInfo;
import net.risesoft.entity.SecretLevelRecord;
import net.risesoft.model.itemadmin.DmyjInfoModel;
import net.risesoft.model.itemadmin.SecretLevelModel;
import net.risesoft.model.platform.Person;
import net.risesoft.model.platform.Tenant;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.DmyjInfoRepository;
import net.risesoft.repository.jpa.DmyjSxInfoRepository;
import net.risesoft.service.SecretLevelRecordService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/secretLevelRecord", produces = MediaType.APPLICATION_JSON_VALUE)
public class SecretLevelRecordApiImpl implements SecretLevelRecordApi {

    private final SecretLevelRecordService secretLevelRecordService;

    private final PersonApi personApi;

    private final TenantApi tenantApi;

    private final DmyjInfoRepository dmyjInfoRepository;

    private final DmyjSxInfoRepository dmyjSxInfoRepository;

    /**
     * 获取定密依据信息
     *
     * @param tenantId 租户id
     * @param miji 密级
     * @param dmyjmc 定密依据名称
     * @param dmyjsj 定密依据司局
     * @return {@code Y9Result<List<DmyjInfoModel>>} 通用请求返回对象
     * @since 9.6.8
     */
    @Override
    public Y9Result<List<DmyjInfoModel>> getDmyjInfo(@RequestParam String tenantId, @RequestParam String miji,
        String dmyjmc, @RequestParam String dmyjsj) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<DmyjInfoModel> list = new ArrayList<>();
        if (StringUtils.isBlank(dmyjmc)) {
            System.out.println("dmyjmc为空");
            List<DmyjInfo> dmyjInfoList = dmyjInfoRepository.getDmyjInfo(miji, "%" + dmyjsj + "%");
            for (DmyjInfo info : dmyjInfoList) {
                DmyjInfoModel model = new DmyjInfoModel();
                Y9BeanUtil.copyProperties(info, model);
                list.add(model);
            }
        } else {
            List<DmyjSxInfo> dmyjInfoList = dmyjSxInfoRepository.getDmyjSxInfo(miji, dmyjmc, "%" + dmyjsj + "%");
            for (DmyjSxInfo info : dmyjInfoList) {
                DmyjInfoModel model = new DmyjInfoModel();
                Y9BeanUtil.copyProperties(info, model);
                list.add(model);
            }
        }
        return Y9Result.success(list);
    }

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
        String secretItem, String description, @RequestParam String tableName, @RequestParam String fieldName) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPersonId(userId);
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setUserInfo(person.toUserInfo());
        Tenant tenant = tenantApi.getById(tenantId).getData();
        Y9LoginUserHolder.setTenantShortName(tenant.getShortName());
        secretLevelRecordService.save(processSerialNumber, secretLevel, secretBasis, secretItem, description, tableName,
            fieldName);
        return Y9Result.successMsg("保存成功");
    }
}
