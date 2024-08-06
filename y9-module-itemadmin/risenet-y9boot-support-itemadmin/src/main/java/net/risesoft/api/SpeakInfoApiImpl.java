package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

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
 * 沟通交流接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/speakInfo", produces = MediaType.APPLICATION_JSON_VALUE)
public class SpeakInfoApiImpl implements SpeakInfoApi {

    private final SpeakInfoService speakInfoService;

    private final PersonApi personApi;

    /**
     * 逻辑删除发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param id 主键id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteById(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return speakInfoService.deleteById(id);
    }

    /**
     * 根据唯一标示获取发言信息
     *
     * @param tenantId 租户id
     * @param id 主键id
     * @return {@code Y9Result<SpeakInfoModel>} 通用请求返回对象 - data 是发言信息
     * @since 9.6.6
     */
    @Override
    public Y9Result<SpeakInfoModel> findById(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        SpeakInfo speakInfo = speakInfoService.findById(id);
        return Y9Result.success(ItemAdminModelConvertUtil.speakInfo2Model(speakInfo));
    }

    /**
     * 根据流程实例查找某一个流程的所有发言信息，根据时间倒叙排列
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<List<SpeakInfoModel>>} 通用请求返回对象 - data 是发言信息列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<SpeakInfoModel>> findByProcessInstanceId(@RequestParam String tenantId,
        @RequestParam String userId, @RequestParam String processInstanceId) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        List<SpeakInfo> siList = speakInfoService.findByProcessInstanceId(processInstanceId);
        return Y9Result.success(ItemAdminModelConvertUtil.speakInfoList2ModelList(siList));
    }

    /**
     * 获取未读消息计数
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param processInstanceId 流程实例id
     * @return {@code Y9Result<Integer>} 通用请求返回对象 - data 是未读消息计数
     * @since 9.6.6
     */
    @Override
    public Y9Result<Integer> getNotReadCount(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return Y9Result.success(speakInfoService.getNotReadCount(processInstanceId, userId));
    }

    /**
     * 保存或者更新发言信息
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param speakInfoModel 发言信息
     * @return {@code Y9Result<String>} 通用请求返回对象 - data 是发言ID
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> saveOrUpdate(@RequestParam String tenantId, @RequestParam String userId,
        @RequestBody SpeakInfoModel speakInfoModel) {
        Person person = personApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setTenantId(tenantId);
        Y9LoginUserHolder.setPerson(person);

        SpeakInfo speakInfo = ItemAdminModelConvertUtil.speakInfoModel2SpeakInfo(speakInfoModel);
        return Y9Result.success(speakInfoService.saveOrUpdate(speakInfo));
    }
}
