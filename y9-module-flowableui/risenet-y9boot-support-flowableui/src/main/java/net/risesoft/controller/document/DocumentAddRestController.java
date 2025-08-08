package net.risesoft.controller.document;

import javax.validation.constraints.NotBlank;

import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import net.risesoft.api.itemadmin.core.DocumentApi;
import net.risesoft.model.itemadmin.OpenDataModel;
import net.risesoft.model.itemadmin.core.DocumentDetailModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 新建公文
 *
 * @author zhangchongjie
 * @date 2024/06/05
 */
@Slf4j
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/vue/document/add", produces = MediaType.APPLICATION_JSON_VALUE)
public class DocumentAddRestController {

    private final DocumentApi documentApi;

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @return Y9Result<Map < String, Object>>
     */
    @GetMapping(value = "")
    public Y9Result<OpenDataModel> add(@RequestParam @NotBlank String itemId) {
        return documentApi.add(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(), itemId, false);
    }

    /**
     * 获取新建办件初始化数据
     *
     * @param itemId 事项id
     * @param startTaskDefKey 开始节点定义key
     * @return Y9Result<DocumentDetailModel>
     */
    @GetMapping(value = "/startTaskDefKey")
    public Y9Result<DocumentDetailModel> startTaskDefKey(@RequestParam @NotBlank String itemId,
        @RequestParam @NotBlank String startTaskDefKey) {
        return documentApi.addWithStartTaskDefKey(Y9LoginUserHolder.getTenantId(), Y9LoginUserHolder.getPositionId(),
            itemId, startTaskDefKey, false);
    }
}