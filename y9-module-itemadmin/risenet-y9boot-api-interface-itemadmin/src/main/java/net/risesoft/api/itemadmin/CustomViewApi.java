package net.risesoft.api.itemadmin;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.pojo.Y9Result;

/**
 * 自定义视图接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/19
 */
@Validated
public interface CustomViewApi {

    /**
     * 删除自定义视图
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param viewType 视图类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delCustomView")
    Y9Result<Object> delCustomView(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("userId") @NotBlank String userId, @RequestParam("viewType") String viewType);

    /**
     * 获取自定义视图列表
     *
     * @param tenantId 租户id
     * @param userId 人员id
     * @param viewType 视图类型
     * @return {@code Y9Result<List<CustomViewModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/listCustomView")
    Y9Result<List<CustomViewModel>> listCustomView(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("userId") @NotBlank String userId, @RequestParam("viewType") @NotBlank String viewType);

    /**
     * 保存自定义视图
     *
     * @param tenantId 租户id
     * @param orgUnitId 人员、岗位id
     * @param jsonData json数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/saveCustomView")
    Y9Result<Object> saveCustomView(@RequestParam("tenantId") @NotBlank String tenantId,
        @RequestParam("orgUnitId") @NotBlank String orgUnitId, @RequestParam("jsonData") String jsonData);

}
