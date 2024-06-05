package net.risesoft.api;

import lombok.RequiredArgsConstructor;
import net.risesoft.api.itemadmin.WordTemplateApi;
import net.risesoft.entity.WordTemplate;
import net.risesoft.service.WordTemplateService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 正文模板接口
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/services/rest/wordTemplate")
public class WordTemplateApiImpl implements WordTemplateApi {

    private final WordTemplateService wordTemplateService;

    /**
     * 根据id获取正文模板文件路径
     *
     * @param id 正文模板id
     * @return String
     */
    @Override
    @GetMapping(value = "/getFilePathById", produces = MediaType.APPLICATION_JSON_VALUE)
    public String getFilePathById(String id) {
        String y9FilePathId = "";
        WordTemplate wordTemplate = wordTemplateService.findById(id);
        if (null != wordTemplate) {
            y9FilePathId = wordTemplate.getFilePath();
        }
        return y9FilePathId;
    }

}
