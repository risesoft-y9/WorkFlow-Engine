package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.EntrustApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.entity.entrust.Entrust;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.EntrustService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.util.Y9BeanUtil;

/**
 * 出差委托接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/entrust", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustApiImpl implements EntrustApi {

    private final EntrustService entrustService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 删除委托
     *
     * @param tenantId 租户id
     * @param id 委托id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteEntrust(@RequestParam String tenantId, @RequestParam String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        entrustService.destroyEntrust(id);
        return Y9Result.success();
    }

    /**
     * 获取我委托的列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EntrustModel>> getEntrustList(@RequestParam String tenantId, @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustModel> list = entrustService.listEntrustByUserId(orgUnitId);
        return Y9Result.success(list);
    }

    /**
     * 获取委托我的列表
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EntrustModel>> getMyEntrustList(@RequestParam String tenantId,
        @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustModel> list = entrustService.listMyEntrust(orgUnitId);
        return Y9Result.success(list);
    }

    /**
     * 保存或更新委托
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param entrustModel 实体类（EntrustModel）
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestBody EntrustModel entrustModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        Entrust entrust = new Entrust();
        Y9BeanUtil.copyProperties(entrustModel, entrust);
        entrustService.saveOrUpdate(entrust);
        return Y9Result.success();
    }
}
