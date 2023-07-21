package net.risesoft.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.multipart.MultipartFile;

import net.risesoft.entity.ItemPrintTemplateBind;
import net.risesoft.entity.PrintTemplate;

/**
 * @author qinman
 * @author zhangchongjie
 * @date 2022/12/20
 */
public interface PrintTemplateService {

    /**
     * 删除绑定的打印模板
     *
     * @param id
     * @return
     */
    public Map<String, Object> deleteBindPrintTemplate(String id);

    /**
     * 删除打印模板
     *
     * @param id
     * @return
     */
    public Map<String, Object> deletePrintTemplate(String id);

    /**
     * 下载模板
     *
     * @param id
     * @param response
     * @param request
     */
    public void download(String id, HttpServletResponse response, HttpServletRequest request);

    /**
     * Description:
     * 
     * @return
     */
    List<PrintTemplate> findAll();

    /**
     * 查询绑定打印模板的列表
     *
     * @param itemId
     * @return
     */
    public List<ItemPrintTemplateBind> getTemplateBindList(String itemId);

    /**
     * Description: 保存绑定的模板
     * 
     * @param itemId
     * @param templateId
     * @param templateName
     * @param templateUrl
     * @param templateType
     * @return
     */
    public Map<String, Object> saveBindTemplate(String itemId, String templateId, String templateName, String templateUrl, String templateType);

    /**
     * Description:
     * 
     * @param printTemplate
     */
    void saveOrUpdate(PrintTemplate printTemplate);

    /**
     * 上传打印模板
     *
     * @param file
     * @return
     */
    public Map<String, Object> uploadTemplate(MultipartFile file);
}
