package net.risesoft.api;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import net.risesoft.api.itemadmin.WordTemplateApi;
import net.risesoft.entity.WordTemplate;
import net.risesoft.pojo.Y9Result;
import net.risesoft.service.WordTemplateService;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/22
 */
@RestController
@RequestMapping(value = "/services/rest/wordTemplate", produces = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class WordTemplateApiImpl implements WordTemplateApi {

    private final WordTemplateService wordTemplateService;

    @Override
    public Y9Result<String> getFilePathById(String id) {
        String y9FilePathId = "";
        WordTemplate wordTemplate = wordTemplateService.findById(id);
        if (null != wordTemplate) {
            y9FilePathId = wordTemplate.getFilePath();
        }
        return Y9Result.success(y9FilePathId);
    }

}
