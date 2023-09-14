package net.risesoft.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.position.Entrust4PositionApi;
import net.risesoft.api.org.PositionApi;
import net.risesoft.entity.Entrust;
import net.risesoft.model.Position;
import net.risesoft.model.itemadmin.EntrustModel;
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
@RequestMapping(value = "/services/rest/entrust4Position", produces = MediaType.APPLICATION_JSON_VALUE)
public class EntrustApiImpl implements Entrust4PositionApi {

    @Autowired
    private EntrustService entrustService;

    @Autowired
    private PositionApi positionApi;

    @Override
    public void deleteEntrust(String tenantId, String id) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        entrustService.destroyEntrust(id);
    }

    @Override
    public List<EntrustModel> getEntrustList(String tenantId, String positionId) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustService.getEntrustList(positionId);
    }

    @Override
    public List<EntrustModel> getMyEntrustList(String tenantId, String positionId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        return entrustService.getMyEntrustList(positionId);
    }

    @Override
    @PostMapping(value = "/saveOrUpdate", produces = MediaType.APPLICATION_JSON_VALUE, consumes = MediaType.APPLICATION_JSON_VALUE)
    public void saveOrUpdate(String tenantId, String positionId, @RequestBody EntrustModel entrustModel) throws Exception {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.getPosition(tenantId, positionId);
        Y9LoginUserHolder.setPosition(position);
        Entrust entrust = new Entrust();
        Y9BeanUtil.copyProperties(entrustModel, entrust);
        entrustService.saveOrUpdate(entrust);
    }
}
