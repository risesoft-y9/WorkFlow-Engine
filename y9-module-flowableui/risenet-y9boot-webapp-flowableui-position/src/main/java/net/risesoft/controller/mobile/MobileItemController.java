package net.risesoft.controller.mobile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.ItemApi;
import net.risesoft.consts.UtilConsts;
import net.risesoft.model.itemadmin.ItemListModel;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;
import net.risesoft.y9.util.Y9Util;

/**
 * 获取app应用接口
 *
 * @author 10858
 */
@Validated
@RequiredArgsConstructor
@RestController
@Slf4j
@RequestMapping("/mobile/item")
public class MobileItemController {

    private final ItemApi itemApi;

    /**
     * 获取事项列表
     *
     * @param tenantId 租户id
     * @param positionId 岗位id
     */
    @ResponseBody
    @RequestMapping(value = "/getItemList")
    public void getItemList(@RequestHeader("auth-tenantId") String tenantId,
        @RequestHeader("auth-positionId") String positionId, HttpServletResponse response) {
        Map<String, Object> resMap = new HashMap<>(16);
        try {
            Y9LoginUserHolder.setTenantId(tenantId);
            List<ItemListModel> listMap = itemApi.getItemList(tenantId, positionId).getData();
            resMap.put(UtilConsts.SUCCESS, true);
            resMap.put("msg", "获取数据成功");
            resMap.put("itemList", listMap);
        } catch (Exception e) {
            resMap.put(UtilConsts.SUCCESS, false);
            resMap.put("msg", "获取数据异常");
            LOGGER.error("获取数据异常", e);
        }
        Y9Util.renderJson(response, Y9JsonUtil.writeValueAsString(resMap));
    }
}
