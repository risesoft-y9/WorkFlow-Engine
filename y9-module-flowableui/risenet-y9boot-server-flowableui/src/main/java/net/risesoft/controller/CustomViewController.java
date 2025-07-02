package net.risesoft.controller;

import java.util.List;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.CustomViewApi;
import net.risesoft.model.itemadmin.CustomViewModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 自定义视图信息
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/customView", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class CustomViewController {

    private final CustomViewApi customViewApi;

    /**
     * 删除视图
     *
     * @param viewType 视图类型
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/delCustomView")
    public Y9Result<Object> delCustomView(@RequestParam String viewType) {
        return customViewApi.delCustomView(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), viewType);
    }

    /**
     * 获取视图列表
     *
     * @param viewType 视图类型
     * @return Y9Result<List<CustomViewModel>>
     */
    @GetMapping(value = "/listCustomView")
    public Y9Result<List<CustomViewModel>> listCustomView(@RequestParam String viewType) {
        return customViewApi.listCustomView(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), viewType);
    }

    /**
     * 保存视图信息
     *
     * @param jsonData 数据信息
     * @return Y9Result<Object>
     */
    @PostMapping(value = "/saveCustomView")
    public Y9Result<Object> saveCustomView(@RequestParam String jsonData) {
        return customViewApi.saveCustomView(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPersonId(), jsonData);
    }

}
