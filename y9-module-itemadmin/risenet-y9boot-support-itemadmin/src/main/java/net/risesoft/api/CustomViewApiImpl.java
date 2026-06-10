package net.risesoft.api;

import java.util.List;

import jakarta.validation.constraints.NotBlank;


import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.view.CustomViewApi;
import net.risesoft.api.platform.org.OrgUnitApi;
import net.risesoft.api.platform.org.PositionApi;
import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.view.CustomViewService;

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

    private final PositionApi positionApi;

    /**
     * 删除自定义视图
     * 
     * @param viewType 视图类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> delCustomView(@RequestParam String viewType) {
        customViewService.delCustomView(viewType);
        return Y9Result.success();
    }

    /**
     * 获取自定义视图
     *
     * @param viewType 视图类型
     * @return {@code Y9Result<List<CustomViewModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<List<CustomViewModel>> listCustomView(@RequestParam @NotBlank String viewType) {
        return customViewService.listCustomView(viewType);
    }

    /**
     * 保存自定义视图
     *
     * @param jsonData 视图数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<Object> saveCustomView(@RequestParam String jsonData) {
        customViewService.saveCustomView(jsonData);
        return Y9Result.success();
    }
}
