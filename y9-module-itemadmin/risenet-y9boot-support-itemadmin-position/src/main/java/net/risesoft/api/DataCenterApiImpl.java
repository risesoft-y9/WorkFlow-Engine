package net.risesoft.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.DataCenterApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.platform.Position;
import net.risesoft.pojo.Y9Result;
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
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/dataCenter")
public class DataCenterApiImpl implements DataCenterApi {

    private final PositionApi positionApi;

    private final DataCenterService dataCenterService;

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param userId 人员id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveToDateCenter(@RequestParam String processInstanceId, @RequestParam String tenantId,
        @RequestParam String userId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        Position position = positionApi.get(tenantId, userId).getData();
        Y9LoginUserHolder.setPosition(position);
        dataCenterService.saveToDateCenter(processInstanceId);
        return Y9Result.success();
    }

}
