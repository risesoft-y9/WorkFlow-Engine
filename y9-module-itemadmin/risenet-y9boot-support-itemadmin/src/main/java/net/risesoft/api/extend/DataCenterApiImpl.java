package net.risesoft.api.extend;

import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.extend.DataCenterApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.extend.DataCenterService;
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
@RequestMapping(value = "/services/rest/dataCenter", produces = MediaType.APPLICATION_JSON_VALUE)
@Primary
public class DataCenterApiImpl implements DataCenterApi {

    private final OrgUnitApi orgUnitApi;

    private final DataCenterService dataCenterService;

    /**
     * 根据流程实例id删除流程数据
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> deleteOfficeInfo(String tenantId, String processInstanceId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        dataCenterService.deleteOfficeInfo(processInstanceId);
        return Y9Result.success();
    }

    /**
     * 保存办结数据到数据中心
     *
     * @param processInstanceId 流程实例id
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveToDataCenter(@RequestParam String processInstanceId, @RequestParam String tenantId,
        @RequestParam String orgUnitId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        dataCenterService.saveToDataCenter(processInstanceId);
        return Y9Result.success();
    }

}
