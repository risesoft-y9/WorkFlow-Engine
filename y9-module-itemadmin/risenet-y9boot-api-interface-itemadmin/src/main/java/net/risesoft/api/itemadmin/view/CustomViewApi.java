package net.risesoft.api.itemadmin.view;

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
     * @param viewType 视图类型
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @PostMapping("/delCustomView")
    Y9Result<Object> delCustomView(@RequestParam("viewType") String viewType);

    /**
     * 获取自定义视图列表
     *
     * @param viewType 视图类型
     * @return {@code Y9Result<List<CustomViewModel>>} 通用请求返回对象
     * @since 9.6.6
     */
    @GetMapping("/listCustomView")
    Y9Result<List<CustomViewModel>> listCustomView(@RequestParam("viewType") @NotBlank String viewType);

    /**
     * 保存自定义视图
     *
     * @param jsonData json数据
     * @return {@code Y9Result<Object>} 通用请求返回对象
     */
    @PostMapping("/saveCustomView")
    Y9Result<Object> saveCustomView(@RequestParam("jsonData") String jsonData);

}
