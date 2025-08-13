package net.risesoft.controller;

import java.util.List;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.view.ItemViewConfApi;
import net.risesoft.model.itemadmin.ItemViewConfModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 视图类型
 *
 * @author qinman
 * @date 2025/08/13
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/viewConf", produces = MediaType.APPLICATION_JSON_VALUE)
public class ViewConfRestController {

    private final ItemViewConfApi itemViewConfApi;

    /**
     * 根据事项id和视图类型获取视图配置
     *
     * @param itemId 事项id
     * @param viewType 视图类型
     * @return Y9Result<List < ItemViewConfModel>>
     */
    @GetMapping(value = "/list")
    public Y9Result<List<ItemViewConfModel>> list(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String viewType) {
        List<ItemViewConfModel> itemViewConfList =
            this.itemViewConfApi.findByItemIdAndViewType(Y9LoginUserHolder.getTenantId(), itemId, viewType).getData();
        return Y9Result.success(itemViewConfList, "获取成功");
    }
}