package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.template.WordTemplateApi;
import net.risesoft.api.processadmin.RepositoryApi;
import net.risesoft.entity.Item;
import net.risesoft.entity.template.ItemWordTemplateBind;
import net.risesoft.entity.template.WordTemplate;
import net.risesoft.model.processadmin.ProcessDefinitionModel;
import net.risesoft.pojo.Y9Result;
import net.risesoft.repository.template.ItemWordTemplateBindRepository;
import net.risesoft.service.WordTemplateService;
import net.risesoft.service.core.ItemService;
import net.risesoft.y9.Y9LoginUserHolder;

/**
 * 正文模板接口
 *
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/wordTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
public class WordTemplateApiImpl implements WordTemplateApi {

    private final WordTemplateService wordTemplateService;

    private final ItemService itemService;

    private final ItemWordTemplateBindRepository itemWordTemplateBindRepository;

    private final RepositoryApi repositoryApi;

    /**
     * 根据id获取正文模板文件路径
     *
     * @param tenantId 租户id
     * @param id 模板id
     * @return {@code Y9Result<Object>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getFilePathById(@RequestParam String tenantId, @RequestParam String id) {
        String y9FilePathId = "";
        Y9LoginUserHolder.setTenantId(tenantId);
        WordTemplate wordTemplate = wordTemplateService.findById(id);
        if (null != wordTemplate) {
            y9FilePathId = wordTemplate.getFilePath();
        }
        return Y9Result.success(y9FilePathId);
    }

    /**
     * 根据流程定义id和正文类型获取绑定正文模板文件id
     *
     * @param tenantId 租户id
     * @param itemId 事项
     * @param wordType 正文类型
     * @return {@code Y9Result<String>} 通用请求返回对象
     * @since 9.6.6
     */
    @Override
    public Y9Result<String> getWordTemplateBind(@RequestParam String tenantId, @RequestParam String itemId,
        @RequestParam String wordType) {
        String y9FilePathId = null;
        Y9LoginUserHolder.setTenantId(tenantId);
        Item item = itemService.findById(itemId);
        String processDefinitionKey = item.getWorkflowGuid();
        ProcessDefinitionModel processDefinition =
            repositoryApi.getLatestProcessDefinitionByKey(tenantId, processDefinitionKey).getData();
        String processDefinitionId = processDefinition.getId();
        ItemWordTemplateBind bind = itemWordTemplateBindRepository
            .findByItemIdAndProcessDefinitionIdAndBindValue(itemId, processDefinitionId, wordType);
        if (null != bind) {
            WordTemplate wordTemplate = wordTemplateService.findById(bind.getTemplateId());
            if (null != wordTemplate) {
                y9FilePathId = wordTemplate.getFilePath();
            }
        }
        return Y9Result.success(y9FilePathId);
    }

}
