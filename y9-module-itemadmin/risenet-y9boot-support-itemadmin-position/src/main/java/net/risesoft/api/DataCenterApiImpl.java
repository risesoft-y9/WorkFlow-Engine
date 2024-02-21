package net.risesoft.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.risesoft.api.itemadmin.DataCenterApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Position;
import net.risesoft.service.DataCenterService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 数据中心接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequestMapping(value = "/services/rest/dataCenter")
public class DataCenterApiImpl implements DataCenterApi {

    @Autowired
    private PositionApi positionApi;

    @Autowired
    private DataCenterService dataCenterService;

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return boolean
     */
    @Override
    @PostMapping(value = "/saveToDateCenter", produces = MediaType.APPLICATION_JSON_VALUE)
    public boolean saveToDateCenter(String processInstanceId, String tenantId, String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.getPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setPosition(position);
        boolean b = dataCenterService.saveToDateCenter(processInstanceId);
        return b;
    }

}
