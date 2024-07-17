package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Entrust;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.Position;
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
@RequestMapping(value = "/services/rest/entrust4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustApiImpl implements Entrust4PositionApi {

    private final EntrustService entrustService;

    private final PositionApi positionApi;

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
     * 获取委托列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EntrustModel>> getEntrustList(@RequestParam String tenantId, @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustModel> list = entrustService.listEntrustByPositionId(positionId);
        return Y9Result.success(list);
    }

    /**
     * 获取我的委托列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return {@code Y9Result<List<EntrustModel>>} 通用请求返回对象 - data 是委托设置列表
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<EntrustModel>> getMyEntrustList(@RequestParam String tenantId,
        @RequestParam String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        List<EntrustModel> list = entrustService.listMyEntrust(positionId);
        return Y9Result.success(list);
    }

    /**
     * 保存或更新委托
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param entrustModel 实体类（EntrustModel）
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveOrUpdate(@RequestParam String tenantId, @RequestParam String positionId,
        @RequestBody EntrustModel entrustModel) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Entrust entrust = new Entrust();
        Y9BeanUtil.copyProperties(entrustModel, entrust);
        entrustService.saveOrUpdate(entrust);
        return Y9Result.success();
    }
}
