package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.PrintApi;
import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.jpa.PrintTemplateItemBindRepository;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 打印模板接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/print", produces = MediaType.APPLICATION_JSON_VALUE)
public class PrintApiImpl implements PrintApi {

    private final PrintTemplateItemBindRepository printTemplateItemBindRepository;

    /**
     * 获取打印模板文件存储ID（打开打印模板使用）
     *
     * @param tenantId 租户id
     * @param itemId 事项id
     * @return {@code Y9Result<String>} 通用请求返回对象 -data是模版文件ID
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> openDocument(@RequestParam String tenantId, @RequestParam String itemId) {
        Y9LoginUserHolder.setTenantId(tenantId);
        ItemPrintTemplateBind bind = printTemplateItemBindRepository.findByItemId(itemId);
        String fileStoreId = bind.getTemplateUrl();
        return Y9Result.success(fileStoreId);
    }

}
