package net.risesoft.controller.mobile.v1;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.risesoft.api.itemadmin.position.Item4PositionApi;
import net.risesoft.model.itemadmin.ItemModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;
import java.util.Map;

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
     * @return Y9Result<List < Map < String, Object>>>
     */
    @RequestMapping(value = "/getItemList")
    public Y9Result<List<Map<String, Object>>> getItemList() {
        try {
            String tenantId = Y9LoginUserHolder.getTenantId();
            String positionId = Y9LoginUserHolder.getPositionId();
            List<Map<String, Object>> listMap = item4PositionApi.getItemList(tenantId, positionId);
            for (Map<String, Object> app : listMap) {
                app.put("itemId", app.get("url"));
                app.put("itemName", app.get("name"));
                ItemModel itemModel = item4PositionApi.getByItemId(tenantId, (String) app.get("url"));
                app.put("appIcon", itemModel != null && StringUtils.isNotBlank(itemModel.getIconData()) ? itemModel.getIconData() : "");
                app.put("processDefinitionKey", itemModel != null ? itemModel.getWorkflowGuid() : "");
            }
            return Y9Result.success(listMap, "获取数据成功");
        } catch (Exception e) {
            LOGGER.error("获取数据异常", e);
        }
        return Y9Result.failure("获取数据异常");
    }
}
