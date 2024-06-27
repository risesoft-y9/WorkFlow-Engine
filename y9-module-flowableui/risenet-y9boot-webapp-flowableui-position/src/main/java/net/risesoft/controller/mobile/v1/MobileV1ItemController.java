package net.risesoft.controller.mobile.v1;

import java.util.List;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 事项接口
 *
 * @author zhangchongjie
 * @date 2024/01/17
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/mobile/v1/item")
public class MobileV1ItemController {

    private final Item4PositionApi item4PositionApi;

    /**
     * 获取事项列表
     *
     * @return Y9Result<List < ItemListModel>>
     */
    @RequestMapping(value = "/getItemList")
    public Y9Result<List<ItemListModel>> getItemList() {
        String tenantId = Y9LoginUserHolder.getTenantId();
        String positionId = Y9LoginUserHolder.getPositionId();
        return item4PositionApi.getItemList(tenantId, positionId);
    }
}
