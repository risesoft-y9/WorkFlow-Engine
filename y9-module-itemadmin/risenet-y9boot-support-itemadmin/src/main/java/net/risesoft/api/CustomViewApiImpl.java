package net.risesoft.api;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.CustomViewApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.model.platform.OrgUnit;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.CustomViewService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 自定义视图接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/customView", produces = MediaType.APPLICATION_JSON_VALUE)
public class CustomViewApiImpl implements CustomViewApi {

    private final CustomViewService customViewService;

    private final OrgUnitApi orgUnitApi;

    /**
     * 删除自定义视图
     * 
     * @param tenantId 租户id
     * @param userId 人员id
     * @param viewType 视图类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delCustomView(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String viewType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customViewService.delCustomView(viewType);
        return Y9Result.success();
    }

    /**
     * 获取自定义视图
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param viewType 视图类型
     * @return {@code Y9Result<List<CustomViewModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<CustomViewModel>> listCustomView(@RequestParam String tenantId, @RequestParam String userId,
        @RequestParam String viewType) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, userId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        return customViewService.listCustomView(viewType);
    }

    /**
     * 保存自定义视图
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员id
     * @param jsonData 视图数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveCustomView(@RequestParam String tenantId, @RequestParam String orgUnitId,
        @RequestParam String jsonData) {
        Y9LoginUserHolder.setTenantId(tenantId);
        OrgUnit orgUnit = orgUnitApi.getOrgUnitPersonOrPosition(tenantId, orgUnitId).getData();
        Y9LoginUserHolder.setOrgUnit(orgUnit);
        customViewService.saveCustomView(jsonData);
        return Y9Result.success();
    }
}
