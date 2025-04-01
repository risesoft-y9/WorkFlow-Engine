package net.risesoft.controller.open;

import java.util.HashMap;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.FormDataApi;
import net.risesoft.consts.InitDataConsts;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WorkList4GfgService;
import net.risesoft.y9.Y9LoginUserHolder;
import net.risesoft.y9.json.Y9JsonUtil;

/**
 * 事项，统计相关
 *
 * @author zhangchongjie
 * @date 2025/03/14
 */
@Validated
@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(value = "/open", produces = MediaType.APPLICATION_JSON_VALUE)
public class OpenRestController {

    private final FormDataApi formDataApi;

    private final WorkList4GfgService workList4GfgService;

    @PostMapping(value = "/setCompleteFlow")
    public Y9Result<String> setCompleteFlow(@RequestHeader("auth-tenantId") String tenantId,
        @RequestBody String jsonData) {
        HashMap<String, Object> jsonMap = Y9JsonUtil.readHashMap(jsonData);
        assert jsonMap != null;
        if (null == jsonMap.get("guid")) {
            return Y9Result.failure("guid不能为空");
        }
        String guid = (String)jsonMap.get("guid");
        HashMap<String, Object> map = new HashMap<>();
        map.put("fw.isqlc", "是");
        return formDataApi.updateFormData(tenantId, guid, Y9JsonUtil.writeValueAsString(map));
    }

    @RequestMapping(value = "/refreshMap")
    public Y9Result<String> refreshMap(@RequestParam(required = false) String tenantId) {
        if (StringUtils.isBlank(tenantId)) {
            tenantId = InitDataConsts.TENANT_ID;
        }
        Y9LoginUserHolder.setTenantId(tenantId);
        workList4GfgService.refreshMap();
        return Y9Result.success();
    }
}
