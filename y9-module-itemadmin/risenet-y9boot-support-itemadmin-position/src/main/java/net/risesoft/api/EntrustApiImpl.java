package net.risesoft.api;

import java.text.ParseException;
import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.entity.Entrust;
import net.risesoft.model.itemadmin.EntrustModel;
import net.risesoft.model.platform.Position;
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
     */
    @Override
    public void deleteEntrust(String tenantId, String id) {
        Y9LoginUserHolder.setTenantId(tenantId);
        entrustService.destroyEntrust(id);
    }

    /**
     * 获取委托列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return List<EntrustModel>
     */
    @Override
    public List<EntrustModel> getEntrustList(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustService.getEntrustList(positionId);
    }

    /**
     * 获取我的委托列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @return List<EntrustModel>
     */
    @Override
    public List<EntrustModel> getMyEntrustList(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustService.getMyEntrustList(positionId);
    }

    /**
     * 保存或更新委托
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     * @param entrustModel 实体类（EntrustModel）
     * @throws ParseException 抛出异常
     */
    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE,
        consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdate(String tenantId, String positionId, @RequestBody EntrustModel entrustModel)
        throws ParseException {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, positionId).getData();
        Y9LoginUserHolder.setPosition(position);
        Entrust entrust = new Entrust();
        Y9BeanUtil.copyProperties(entrustModel, entrust);
        entrustService.saveOrUpdate(entrust);
    }
}
