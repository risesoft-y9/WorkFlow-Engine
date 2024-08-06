package net.risesoft.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.entity.WordTemplate;
import net.risesoft.pojo.Y9Result;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface WordTemplateService {

    /**
     * 删除正文模板
     *
     * @param id
     * @return
     */
    Y9Result<String> deleteWordTemplate(String id);

    /**
     * 下载模板
     *
     * @param id
     * @param response
     * @param request
     */
    void download(String id, HttpServletResponse response, HttpServletRequest request);

    /**
     * Description:
     *
     * @param id
     * @return
     */
    WordTemplate findById(String id);

    /**
     * Description:
     *
     * @return
     */
    List<WordTemplate> listAll();

    /**
     * 获取书签
     *
     * @param id
     * @return
     */
    /**
     * Description:
     *
     * @param wordTemplateId
     * @param wordTemplateType
     * @return
     */
    List<Map<String, Object>> listBookMarkByWordTemplateIdAndWordTemplateType(String wordTemplateId,
        String wordTemplateType);

    /**
     * 获取当前委办局下所有的正文模板(可根据正文模板名称查询)
     *
     * @param bureauId
     * @param fileName
     * @return
     */
    List<WordTemplate> listByBureauIdAndFileNameContainingOrderByUploadTimeDesc(String bureauId, String fileName);

    /**
     * Description:
     *
     * @param bureauId
     * @return
     */
    List<WordTemplate> listByBureauIdOrderByUploadTimeDesc(String bureauId);

    /**
     * Description: 保存正文模板
     *
     * @param wordTemplate
     */
    void saveOrUpdate(WordTemplate wordTemplate);

    /**
     * 上传正文模板
     *
     * @param file
     * @return
     */
    Y9Result<String> upload(MultipartFile file);
}
